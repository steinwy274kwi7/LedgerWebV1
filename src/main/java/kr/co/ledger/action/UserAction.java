package kr.co.ledger.action;

import java.io.PrintWriter;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.ledger.dto.UserDTO;
import kr.co.ledger.service.UserService;
import kr.co.ledger.util.UriUtil;

public class UserAction implements Action {

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        String command = UriUtil.getCommand(request);
        String methodName = command.substring(command.lastIndexOf("/") + 1, command.lastIndexOf("."));
        
        return switch (methodName) {
            case "registerForm"         -> registerForm(request, response);
            case "register"             -> register(request, response);
            case "loginForm"            -> loginForm(request, response);
            case "login"                -> login(request, response);
            case "logout"               -> logout(request, response);
            case "main"                 -> mainDashboard(request, response);
            case "findIdForm"           -> findIdForm(request, response);
            case "findId"               -> findId(request, response);
            case "findPwForm"           -> findPwForm(request, response);
            case "findPw"               -> findPw(request, response);
            case "myPage"               -> myPage(request, response);
            case "updateForm"           -> updateForm(request, response);
            case "updateInfo"           -> updateInfo(request, response);
            case "withdraw"             -> withdraw(request, response);
            case "wakeup"               -> wakeup(request, response);
            case "searchPublicUser"     -> searchPublicUser(request, response);
            default -> throw new IllegalArgumentException("UserAction에 없는 기능: " + command);
        };
    }

    // ==========================================================
    // 🌟 [리팩토링] 공통 응답 헬퍼 메서드 (JSON & Alert)
    // ==========================================================
    private String sendJson(HttpServletResponse response, String jsonString) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print(jsonString != null ? jsonString : "[]");
        out.flush();
        return null;
    }

    private String sendAlert(HttpServletRequest request, String msg, String url) {
        request.setAttribute("msg", msg);
        if (url != null) request.setAttribute("url", url);
        return "/views/common/alert.jsp"; // 만능 공통 알림창으로 전송
    }
    // ==========================================================
    
    // 메인 대시보드
    private String mainDashboard(HttpServletRequest request, HttpServletResponse response) {
        return "/views/main.jsp";
    }
    
    // 회원가입 폼
    private String registerForm(HttpServletRequest request, HttpServletResponse response) {
        return "/views/user/registerForm.jsp";
    }
    
    // 회원가입
    private String register(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO dto = new UserDTO();
        dto.setUserId(request.getParameter("userId"));
        dto.setUserPw(request.getParameter("userPw"));
        dto.setUserNickname(request.getParameter("userNickname"));
        dto.setUserEmail(request.getParameter("userEmail"));
        dto.setUserPhone(request.getParameter("userPhone"));
        
        String birth = request.getParameter("userBirth");
        if (birth != null) birth = birth.replace("-", ""); 
        dto.setUserBirth(birth);
        
        boolean isSuccess = UserService.getInstance().registerUser(dto);
        
        // 💡 주의: UI의 '성공 박스(joinSuccess)' 유지를 위해 여기서는 alert.jsp 대신 폼으로 직접 이동
        if (isSuccess) {
            request.setAttribute("msg", "회원가입이 완료되었습니다. 환영합니다!");
            request.setAttribute("joinSuccess", "true");
            return "/views/user/loginForm.jsp";
        } else {
            request.setAttribute("msg", "회원가입에 실패했습니다.");
            return "/views/user/registerForm.jsp";
        }
    }
    
    // 로그인 폼
    private String loginForm(HttpServletRequest request, HttpServletResponse response) {
        return "/views/user/loginForm.jsp";
    }
    
    // 로그인
    private String login(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String userId = request.getParameter("userId");
        String userPw = request.getParameter("userPw");
        
        UserDTO loginUser = UserService.getInstance().login(userId, userPw);
        
        if (loginUser != null) {
            if ("D".equals(loginUser.getUserStatus())) {
                request.setAttribute("dormantId", loginUser.getUserId());
                return "/views/user/recoveryForm.jsp"; 
            }
            request.getSession().setAttribute("loginUser", loginUser);
            return "redirect:" + request.getContextPath() + "/main.do";
        } else {
            request.setAttribute("msg", "아이디 또는 비밀번호가 틀렸습니다.");
            return "/views/user/loginForm.jsp";
        }
    }

    // 로그아웃
    private String logout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate();
        return "redirect:" + request.getContextPath() + "/user/loginForm.do";
    }
    
    // 아이디 찾기 폼
    private String findIdForm(HttpServletRequest request, HttpServletResponse response) {
        return "/views/user/findIdForm.jsp";
    }

    // 아이디 찾기
    private String findId(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String userEmail = request.getParameter("userEmail");
        String userPhone = request.getParameter("userPhone");
        String userBirth = request.getParameter("userBirth");
        if (userBirth != null) userBirth = userBirth.replace("-", "");

        String foundId = UserService.getInstance().findUserId(userEmail, userPhone, userBirth);

        if (foundId != null) {
            request.setAttribute("foundId", foundId);
        } else {
            request.setAttribute("msg", "일치하는 회원 정보가 없습니다.");
        }
        return "/views/user/findIdForm.jsp";
    }
    
    // 임시 비밀번호 발급 폼
    private String findPwForm(HttpServletRequest request, HttpServletResponse response) {
        return "/views/user/findPwForm.jsp";
    }
    
    // 임시 비밀번호 발급
    private String findPw(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String userId = request.getParameter("userId");
        String userEmail = request.getParameter("userEmail");
        String userPhone = request.getParameter("userPhone");
        String userBirth = request.getParameter("userBirth");
        if (userBirth != null) userBirth = userBirth.replace("-", "");

        String tempPw = UserService.getInstance().issueTempPassword(userId, userEmail, userPhone, userBirth);

        if (tempPw != null) {
            request.setAttribute("tempPw", tempPw);
        } else {
            request.setAttribute("msg", "입력하신 정보와 일치하는 회원이 없습니다.");
        }
        return "/views/user/findPwForm.jsp";
    }
    
    // 마이페이지
    private String myPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) return sendAlert(request, "로그인이 필요한 서비스입니다.", "/user/loginForm.do");

        UserDTO userInfo = UserService.getInstance().getUserInfo(loginUser.getUserId());
        request.setAttribute("userInfo", userInfo);
        return "/views/user/myPage.jsp";
    }
    
    // 개인정보 수정 폼
    private String updateForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) return sendAlert(request, "로그인이 필요합니다.", "/user/loginForm.do");
        
        UserDTO userInfo = UserService.getInstance().getUserInfo(loginUser.getUserId());
        request.setAttribute("userInfo", userInfo);
        return "/views/user/updateForm.jsp";
    }

    // 개인정보 수정
    private String updateInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) return sendAlert(request, "로그인이 필요합니다.", "/user/loginForm.do");

        UserDTO dto = new UserDTO();
        dto.setUserId(loginUser.getUserId());
        dto.setUserPw(request.getParameter("userPw"));
        dto.setUserNickname(request.getParameter("userNickname"));
        dto.setUserEmail(request.getParameter("userEmail"));
        dto.setUserPhone(request.getParameter("userPhone"));
        
        String birth = request.getParameter("userBirth");
        if (birth != null) birth = birth.replace("-", ""); 
        dto.setUserBirth(birth);

        boolean isSuccess = UserService.getInstance().updateUserInfo(dto);

        // 💡 주의: UI의 '수정 완료 모달창' 유지를 위해 alert.jsp 대신 myPage 메서드로 직접 Forward
        if (isSuccess) {
            loginUser.setUserNickname(dto.getUserNickname()); 
            request.setAttribute("msg", "정보가 성공적으로 수정되었습니다.");
            return myPage(request, response);
        } else {
            request.setAttribute("msg", "정보 수정에 실패했습니다.");
            return "/views/user/updateForm.jsp";
        }
    }
    
    // 🌟 회원 탈퇴 (alert.jsp 완벽 적용)
    private String withdraw(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) return sendAlert(request, "로그인이 필요합니다.", "/user/loginForm.do");

        boolean isSuccess = UserService.getInstance().withdrawUser(loginUser.getUserId());

        if (isSuccess) {
            request.getSession().invalidate();
            return sendAlert(request, "회원 탈퇴가 완료되었습니다. 그동안 이용해 주셔서 감사합니다.", "/user/loginForm.do");
        } else {
            return sendAlert(request, "회원 탈퇴 처리에 실패했습니다.", "/user/myPage.do");
        }
    }
    
    // 🌟 휴면 해제 (alert.jsp 완벽 적용)
    private String wakeup(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String userId = request.getParameter("userId");
        boolean isSuccess = UserService.getInstance().wakeupUser(userId);

        if (isSuccess) {
            return sendAlert(request, "휴면 상태가 해제되었습니다. 다시 로그인해 주세요!", "/user/loginForm.do");
        } else {
            // URL을 null로 주면 alert.jsp가 알아서 이전 페이지(history.back)로 돌려보냅니다.
            return sendAlert(request, "휴면 해제에 실패했습니다.", null); 
        }
    }
    
    // 타인 가계부 검색 (AJAX)
    private String searchPublicUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String keyword = request.getParameter("keyword");
            List<UserDTO> list = UserService.getInstance().searchPublicUsersById(keyword);
            
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                UserDTO dto = list.get(i);
                json.append(String.format("{\"userNum\":%d, \"userId\":\"%s\", \"userNickname\":\"%s\"}", 
                            dto.getUserNum(), dto.getUserId(), dto.getUserNickname()));
                if (i < list.size() - 1) json.append(",");
            }
            json.append("]");
            
            return sendJson(response, json.toString());
        } catch (Exception e) {
            return sendJson(response, "[]");
        }
    }
}