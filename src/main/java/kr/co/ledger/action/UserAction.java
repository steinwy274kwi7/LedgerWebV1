package kr.co.ledger.action;

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
            case "registerForm" -> registerForm(request, response);
            case "register"     -> register(request, response);
            case "loginForm"    -> loginForm(request, response);
            case "login"        -> login(request, response);
            case "logout"       -> logout(request, response);
            case "main"         -> mainDashboard(request, response);
            default -> throw new IllegalArgumentException("UserAction에 없는 기능: " + command);
        };
    }
    
    // 0. 메인 대시보드
    private String mainDashboard(HttpServletRequest request, HttpServletResponse response) {
        return "/views/main.jsp";
    }
    
    // 1. 회원가입 폼
    private String registerForm(HttpServletRequest request, HttpServletResponse response) {
        return "/views/user/registerForm.jsp";
    }
    
    // 2. 회원가입
    private String register(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO dto = new UserDTO();
        dto.setUserId(request.getParameter("userId"));
        dto.setUserPw(request.getParameter("userPw"));
        dto.setUserNickname(request.getParameter("userNickname"));
        dto.setUserEmail(request.getParameter("userEmail"));
        dto.setUserPhone(request.getParameter("userPhone"));
        String birth = request.getParameter("userBirth");
        if (birth != null) {
            birth = birth.replace("-", ""); 
        }
        dto.setUserBirth(birth);
        
        boolean isSuccess = UserService.getInstance().registerUser(dto);
        
        if (isSuccess) {
            return "/views/user/loginForm.jsp";
        } else {
            request.setAttribute("msg", "회원가입에 실패했습니다.");
            return "/views/user/registerForm.jsp";
        }
    }
    
    // 3. 로그인 폼
    private String loginForm(HttpServletRequest request, HttpServletResponse response) {
        return "/views/user/loginForm.jsp";
    }
    
    // 4. 로그인
    private String login(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String userId = request.getParameter("userId");
        String userPw = request.getParameter("userPw");
        
        UserDTO loginUser = UserService.getInstance().login(userId, userPw);
        
        if (loginUser != null) {
            request.getSession().setAttribute("loginUser", loginUser);
            return "redirect:" + request.getContextPath() + "/main.do";
        } else {
            request.setAttribute("msg", "아이디 또는 비밀번호가 틀렸습니다.");
            return "/views/user/loginForm.jsp";
        }
    }

    // 5. 로그아웃
    private String logout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate();
        return "redirect:" + request.getContextPath() + "/user/loginForm.do";
    }
}