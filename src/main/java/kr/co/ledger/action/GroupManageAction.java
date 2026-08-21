package kr.co.ledger.action;

import java.io.PrintWriter;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.ledger.dto.GroupDTO;
import kr.co.ledger.dto.GroupMemberDTO;
import kr.co.ledger.dto.InvitationDTO;
import kr.co.ledger.dto.UserDTO;
import kr.co.ledger.service.GroupManageService;
import kr.co.ledger.util.UriUtil;

public class GroupManageAction implements Action {

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        String command = UriUtil.getCommand(request);
        String methodName = command.substring(command.lastIndexOf("/") + 1, command.lastIndexOf("."));
        
        return switch (methodName) {
            case "getInvitations" -> getInvitations(request, response); 
            case "respondInvite"  -> respondInvite(request, response);
            case "list"           -> getMyGroupList(request, response);
            case "createForm"     -> createForm(request, response);
            case "create"         -> createGroup(request, response);
            case "updateSettings" -> updateGroupSettings(request, response);
            case "delete"         -> deleteGroup(request, response);
            case "sendInvite"     -> sendInvite(request, response);
            case "getMemberList"  -> getMemberList(request, response);
            case "kickMember"     -> kickMember(request, response);
            case "leaveGroup"     -> leaveGroup(request, response);
            case "transferOwner"  -> transferOwner(request, response);
            case "searchPublic"   -> searchPublicGroups(request, response);
            default -> throw new IllegalArgumentException("GroupManageAction에 없는 기능: " + command);
        };
    }

    // ==========================================================
    // 🌟 [리팩토링] 공통 JSON 응답 헬퍼 메서드
    // ==========================================================
    private String sendJson(HttpServletResponse response, String jsonString) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print(jsonString != null ? jsonString : "[]");
        out.flush();
        return null;
    }

    private String sendAjaxResult(HttpServletResponse response, boolean success, String message) throws Exception {
        String safeMessage = message != null ? message.replace("\"", "\\\"").replace("\n", " ") : "";
        String jsonString = "{\"success\": " + success + ", \"message\": \"" + safeMessage + "\"}";
        return sendJson(response, jsonString);
    }
    // ==========================================================

    // 초대알림 목록 보기
    private String getInvitations(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
            return sendJson(response, "[]");
        }
        
        List<InvitationDTO> inviteList = GroupManageService.getInstance().getPendingInvitations(loginUser.getUserNum());
        
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < inviteList.size(); i++) {
            InvitationDTO dto = inviteList.get(i);
            json.append(String.format("{\"inviteNum\":%d, \"groupName\":\"%s\", \"inviterName\":\"%s\"}", 
                        dto.getInviteNum(), dto.getGroupName(), dto.getInviterName()));
            if (i < inviteList.size() - 1) json.append(",");
        }
        json.append("]");
        
        return sendJson(response, json.toString());
    }
    
    // 초대 수락, 거절
    private String respondInvite(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            return sendAjaxResult(response, false, "로그인이 필요합니다.");
        }
        
        try {
            int inviteNum = Integer.parseInt(request.getParameter("inviteNum"));
            String status = request.getParameter("status"); 
            
            boolean isSuccess = GroupManageService.getInstance().respondToInvitation(inviteNum, status, loginUser.getUserNum());
            return sendAjaxResult(response, isSuccess, isSuccess ? "처리 완료" : "처리 실패");
        } catch (Exception e) {
            return sendAjaxResult(response, false, e.getMessage());
        }
    }
    
    // 내가 속한 그룹 목록 로드 (화면 이동)
    private String getMyGroupList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:" + request.getContextPath() + "/user/loginForm.do";
        }

        List<GroupDTO> groupList = GroupManageService.getInstance().getMyGroupList(loginUser.getUserNum());
        request.setAttribute("groupList", groupList);
        
        return "/views/group_manage/groupList.jsp";
    }
    
    // 그룹 생성 폼 이동 (화면 이동)
    private String createForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getSession().getAttribute("loginUser") == null) {
            return "redirect:" + request.getContextPath() + "/user/loginForm.do";
        }
        return "/views/group_manage/createForm.jsp";
    }

    // 실제 그룹 생성 처리 (AJAX)
    private String createGroup(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) return sendAjaxResult(response, false, "로그인이 필요합니다.");

        try {
            String groupName = request.getParameter("groupName");
            String groupDesc = request.getParameter("groupDesc");
            String groupType = request.getParameter("groupType");
            String groupOpenYn = request.getParameter("groupOpenYn");

            if (groupName == null || groupName.trim().isEmpty() || groupName.length() > 20) {
                throw new IllegalArgumentException("방 이름은 필수이며 최대 20자까지 가능합니다.");
            }

            GroupDTO dto = new GroupDTO();
            dto.setGroupName(groupName.trim());
            dto.setGroupDesc(groupDesc);
            dto.setGroupType(groupType);
            dto.setGroupOpenYn(groupOpenYn);
            dto.setGroupOwnerNum(loginUser.getUserNum());

            GroupManageService.getInstance().createGroup(dto);
            return sendAjaxResult(response, true, "공동 가계부가 성공적으로 생성되었습니다.");
        } catch (Exception e) {
            return sendAjaxResult(response, false, e.getMessage());
        }
    }
    
    // 그룹 설정 업데이트 (AJAX)
    private String updateGroupSettings(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) return sendAjaxResult(response, false, "로그인이 필요합니다.");

        try {
            GroupDTO dto = new GroupDTO();
            dto.setGroupNum(Integer.parseInt(request.getParameter("groupNum")));
            dto.setGroupName(request.getParameter("groupName").trim());
            dto.setGroupDesc(request.getParameter("groupDesc"));
            dto.setGroupOpenYn(request.getParameter("groupOpenYn"));
            dto.setSettleUseYn(request.getParameter("settleUseYn")); 
            dto.setGroupOwnerNum(loginUser.getUserNum());

            if (dto.getGroupName().isEmpty() || dto.getGroupName().length() > 20) {
                throw new IllegalArgumentException("방 이름은 1~20자 사이여야 합니다.");
            }

            GroupManageService.getInstance().updateGroupSettings(dto);
            return sendAjaxResult(response, true, "설정이 성공적으로 변경되었습니다.");
        } catch (Exception e) {
            return sendAjaxResult(response, false, e.getMessage());
        }
    }
    
    // 그룹 삭제 (방 소프트 딜리트) (AJAX)
    private String deleteGroup(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) return sendAjaxResult(response, false, "로그인이 필요합니다.");

        try {
            int groupNum = Integer.parseInt(request.getParameter("groupNum"));
            GroupManageService.getInstance().deleteGroup(groupNum, loginUser.getUserNum());
            return sendAjaxResult(response, true, "그룹이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            return sendAjaxResult(response, false, e.getMessage());
        }
    }
    
    // 그룹 멤버 초대 (AJAX)
    private String sendInvite(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) return sendAjaxResult(response, false, "로그인이 필요합니다.");

        try {
            int groupNum = Integer.parseInt(request.getParameter("groupNum"));
            String inviteeId = request.getParameter("inviteeId").trim();
            
            if (inviteeId.isEmpty()) {
                throw new IllegalArgumentException("초대할 아이디를 입력해 주세요.");
            }

            GroupManageService.getInstance().sendInvite(groupNum, loginUser.getUserNum(), inviteeId);
            return sendAjaxResult(response, true, "초대장이 성공적으로 발송되었습니다!");
        } catch (Exception e) {
            return sendAjaxResult(response, false, e.getMessage());
        }
    }
    
    // 멤버 목록 로드 (AJAX)
    private String getMemberList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            int groupNum = Integer.parseInt(request.getParameter("groupNum"));
            List<GroupMemberDTO> list = GroupManageService.getInstance().getGroupMemberList(groupNum);
            
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                GroupMemberDTO m = list.get(i);
                json.append(String.format("{\"userNum\":%d, \"userId\":\"%s\", \"userNickname\":\"%s\", \"joinDate\":\"%s\"}", 
                            m.getUserNum(), m.getUserId(), m.getUserNickname(), m.getJoinDate()));
                if (i < list.size() - 1) json.append(",");
            }
            json.append("]");
            return sendJson(response, json.toString());
        } catch (Exception e) {
            return sendJson(response, "[]");
        }
    }

    // 멤버 강퇴 처리 (방장 전용) (AJAX)
    private String kickMember(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) return sendAjaxResult(response, false, "로그인이 필요합니다.");
        
        try {
            int groupNum = Integer.parseInt(request.getParameter("groupNum"));
            int targetUserNum = Integer.parseInt(request.getParameter("targetUserNum"));
            
            GroupManageService.getInstance().kickMember(groupNum, targetUserNum, loginUser.getUserNum());
            return sendAjaxResult(response, true, "멤버를 강퇴했습니다.");
        } catch (Exception e) {
            return sendAjaxResult(response, false, e.getMessage());
        }
    }

    // 자진 탈퇴 처리 (일반 멤버 전용) (AJAX)
    private String leaveGroup(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) return sendAjaxResult(response, false, "로그인이 필요합니다.");
        
        try {
            int groupNum = Integer.parseInt(request.getParameter("groupNum"));
            GroupManageService.getInstance().leaveGroup(groupNum, loginUser.getUserNum());
            return sendAjaxResult(response, true, "그룹에서 성공적으로 탈퇴했습니다.");
        } catch (Exception e) {
            return sendAjaxResult(response, false, e.getMessage());
        }
    }
    
    // 방장 수동 위임 (AJAX)
    private String transferOwner(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) return sendAjaxResult(response, false, "로그인이 필요합니다.");
        
        try {
            int groupNum = Integer.parseInt(request.getParameter("groupNum"));
            int targetUserNum = Integer.parseInt(request.getParameter("targetUserNum"));
            
            GroupManageService.getInstance().transferOwner(groupNum, targetUserNum, loginUser.getUserNum());
            return sendAjaxResult(response, true, "방장 권한이 성공적으로 위임되었습니다.");
        } catch (Exception e) {
            return sendAjaxResult(response, false, e.getMessage());
        }
    }
    
    // 공개 방 검색 (AJAX)
    private String searchPublicGroups(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String keyword = request.getParameter("keyword");
            List<GroupDTO> list = GroupManageService.getInstance().searchPublicGroups(keyword);

            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                GroupDTO g = list.get(i);
                json.append(String.format("{\"groupNum\":%d, \"groupName\":\"%s\", \"groupDesc\":\"%s\"}", 
                            g.getGroupNum(), g.getGroupName(), g.getGroupDesc()));
                if (i < list.size() - 1) json.append(",");
            }
            json.append("]");
            return sendJson(response, json.toString());
        } catch (Exception e) {
            return sendJson(response, "[]");
        }
    }
}