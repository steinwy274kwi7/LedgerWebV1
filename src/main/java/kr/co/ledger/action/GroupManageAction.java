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
            case "list" 		  -> getMyGroupList(request, response);
            case "createForm" 	  -> createForm(request, response);
            case "create"         -> createGroup(request, response);
            case "updateSettings" -> updateGroupSettings(request, response);
            case "delete" 		  -> deleteGroup(request, response);
            case "sendInvite" 	  -> sendInvite(request, response);
            case "getMemberList"  -> getMemberList(request, response);
            case "kickMember"     -> kickMember(request, response);
            case "leaveGroup"     -> leaveGroup(request, response);
            case "transferOwner"  -> transferOwner(request, response);
            case "searchPublic"   -> searchPublicGroups(request, response);
            default -> throw new IllegalArgumentException("GroupManageAction에 없는 기능: " + command);
        };
    }

    // 초대알림 목록 보기
    private String getInvitations(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
            return null;
        }
        
        int myUserNum = loginUser.getUserNum(); 
        List<InvitationDTO> inviteList = GroupManageService.getInstance().getPendingInvitations(myUserNum);
        
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        StringBuilder json = new StringBuilder();
        json.append("[");
        
        for (int i = 0; i < inviteList.size(); i++) {
            InvitationDTO dto = inviteList.get(i);
            
            json.append("{");
            json.append("\"inviteNum\":").append(dto.getInviteNum()).append(",");
            json.append("\"groupName\":\"").append(dto.getGroupName()).append("\",");
            json.append("\"inviterName\":\"").append(dto.getInviterName()).append("\"");
            json.append("}");
            
            if (i < inviteList.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        
        out.print(json.toString()); 
        out.flush();
        
        return null;
    }
    
 	// 초대 수락, 거절
    private String respondInvite(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
            return null;
        }
        
        int inviteNum = Integer.parseInt(request.getParameter("inviteNum"));
        String status = request.getParameter("status"); 
        int myUserNum = loginUser.getUserNum(); 
        
        boolean isSuccess = GroupManageService.getInstance().respondToInvitation(inviteNum, status, myUserNum);
        
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print("{\"success\": " + isSuccess + "}"); 
        out.flush();
        
        return null;
    }
    
    // 내가 속한 그룹 목록 로드
    private String getMyGroupList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        
        if (loginUser == null) {
            return "redirect:" + request.getContextPath() + "/user/loginForm.do";
        }

        List<GroupDTO> groupList = GroupManageService.getInstance().getMyGroupList(loginUser.getUserNum());
        
        request.setAttribute("groupList", groupList);
        
        return "/views/group_manage/groupList.jsp";
    }
    
    // 그룹 생성 폼 이동
    private String createForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getSession().getAttribute("loginUser") == null) {
            return "redirect:" + request.getContextPath() + "/user/loginForm.do";
        }
        return "/views/group_manage/createForm.jsp";
    }

    // 실제 그룹 생성 처리 (AJAX 전용으로 수정)
    private String createGroup(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        
        // 1. 응답 타입을 JSON으로 설정 (다른 AJAX 메서드들과 동일)
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // 2. 비로그인 처리 (JSON 에러 반환)
        if (loginUser == null) {
            out.print("{\"success\": false, \"message\": \"로그인이 필요합니다.\"}");
            out.flush();
            return null; // FrontController 화면 이동 막음
        }

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

            // 3. 서비스 호출
            // 💡 TIP: 만약 Service의 createGroup이 생성된 방 번호(int)를 반환하도록 설계되어 있다면
            // int groupNum = GroupManageService.getInstance().createGroup(dto);
            // 처럼 받아서 JSON에 같이 넘겨주면 아주 좋습니다. (현재는 반환값이 없다고 가정하고 작성했습니다)
            GroupManageService.getInstance().createGroup(dto);

            // 4. 성공 응답 JSON 출력
            out.print("{\"success\": true}");
            
            // (참고) 방 번호를 리턴받을 수 있다면 위 코드를 지우고 아래처럼 응답하세요.
            // out.print("{\"success\": true, \"groupNum\": " + groupNum + "}");

        } catch (Exception e) {
            // 5. 실패(예외) 응답 JSON 출력
            out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        } finally {
            out.flush();
        }
        
        // 6. View 경로 대신 null 반환 (Redirect, Forward 무시)
        return null;
    }
    
    // 그룹 설정 업데이트
    private String updateGroupSettings(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
       
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        if (loginUser == null) {
            out.print("{\"success\": false, \"message\": \"로그인이 필요합니다.\"}");
            return null;
        }

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
            out.print("{\"success\": true, \"message\": \"설정이 성공적으로 변경되었습니다.\"}");
            
        } catch (Exception e) {
            out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        } finally {
            out.flush();
        }
        
        return null;
    }
    
    // 그룹 삭제 ( 방 소프트 딜리트 )
    private String deleteGroup(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        if (loginUser == null) {
            out.print("{\"success\": false, \"message\": \"로그인이 필요합니다.\"}");
            return null;
        }

        try {
            int groupNum = Integer.parseInt(request.getParameter("groupNum"));
            
            GroupManageService.getInstance().deleteGroup(groupNum, loginUser.getUserNum());
            
            out.print("{\"success\": true, \"message\": \"그룹이 성공적으로 삭제되었습니다.\"}");
            
        } catch (Exception e) {
            out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        } finally {
            out.flush();
        }
        
        return null; 
    }
    
    // 그룹 멤버 초대
    private String sendInvite(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        if (loginUser == null) {
            out.print("{\"success\": false, \"message\": \"로그인이 필요합니다.\"}");
            return null;
        }

        try {
            int groupNum = Integer.parseInt(request.getParameter("groupNum"));
            String inviteeId = request.getParameter("inviteeId").trim();
            
            if (inviteeId.isEmpty()) {
                throw new IllegalArgumentException("초대할 아이디를 입력해 주세요.");
            }

            GroupManageService.getInstance().sendInvite(groupNum, loginUser.getUserNum(), inviteeId);
            
            out.print("{\"success\": true, \"message\": \"초대장이 성공적으로 발송되었습니다!\"}");
            
        } catch (Exception e) {
            out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        } finally {
            out.flush();
        }
        return null; 
    }
    
    // 멤버 목록 로드
    private String getMemberList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int groupNum = Integer.parseInt(request.getParameter("groupNum"));
        
        List<GroupMemberDTO> list = GroupManageService.getInstance().getGroupMemberList(groupNum);
        
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            GroupMemberDTO m = list.get(i);
            json.append("{");
            json.append("\"userNum\":").append(m.getUserNum()).append(",");
            json.append("\"userId\":\"").append(m.getUserId()).append("\",");
            json.append("\"userNickname\":\"").append(m.getUserNickname()).append("\",");
            json.append("\"joinDate\":\"").append(m.getJoinDate()).append("\"");
            json.append("}");
            
            if (i < list.size() - 1) json.append(",");
        }
        json.append("]");
        
        out.print(json.toString());
        out.flush();
        return null;
    }

    // 멤버 강퇴 처리 (방장 전용)
    private String kickMember(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        if (loginUser == null) {
            out.print("{\"success\": false, \"message\": \"로그인이 필요합니다.\"}");
            return null;
        }
        
        try {
            int groupNum = Integer.parseInt(request.getParameter("groupNum"));
            int targetUserNum = Integer.parseInt(request.getParameter("targetUserNum"));
            
            GroupManageService.getInstance().kickMember(groupNum, targetUserNum, loginUser.getUserNum());
            
            out.print("{\"success\": true, \"message\": \"멤버를 강퇴했습니다.\"}");
        } catch (Exception e) {
            out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        } finally { 
            out.flush(); 
        }
        return null;
    }

    // 자진 탈퇴 처리 (일반 멤버 전용)
    private String leaveGroup(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        if (loginUser == null) {
            out.print("{\"success\": false, \"message\": \"로그인이 필요합니다.\"}");
            return null;
        }
        
        try {
            int groupNum = Integer.parseInt(request.getParameter("groupNum"));
            
            GroupManageService.getInstance().leaveGroup(groupNum, loginUser.getUserNum());
            
            out.print("{\"success\": true, \"message\": \"그룹에서 성공적으로 탈퇴했습니다.\"}");
        } catch (Exception e) {
            out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        } finally { 
            out.flush(); 
        }
        return null;
    }
    
    // 방장 수동 위임 (AJAX)
    private String transferOwner(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        if (loginUser == null) {
            out.print("{\"success\": false, \"message\": \"로그인이 필요합니다.\"}");
            return null;
        }
        
        try {
            int groupNum = Integer.parseInt(request.getParameter("groupNum"));
            int targetUserNum = Integer.parseInt(request.getParameter("targetUserNum"));
            
            GroupManageService.getInstance().transferOwner(groupNum, targetUserNum, loginUser.getUserNum());
            
            out.print("{\"success\": true, \"message\": \"방장 권한이 성공적으로 위임되었습니다.\"}");
        } catch (Exception e) {
            out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        } finally { 
            out.flush(); 
        }
        return null;
    }
    
    // 공개 방 검색 (AJAX)
    private String searchPublicGroups(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
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
            out.print(json.toString());
        } catch (Exception e) {
            out.print("[]");
        } finally {
            out.flush();
        }
        return null;
    }
    
}