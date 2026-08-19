package kr.co.ledger.action;

import java.io.PrintWriter;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.ledger.dto.GroupDTO;
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

    // 실제 그룹 생성 처리
    private String createGroup(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:" + request.getContextPath() + "/user/loginForm.do";
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

            GroupManageService.getInstance().createGroup(dto);

            return "redirect:" + request.getContextPath() + "/group/list.do";
            
        } catch (Exception e) {
            request.setAttribute("msg", e.getMessage());
            return "/views/group_manage/createForm.jsp";
        }
    }
    
}