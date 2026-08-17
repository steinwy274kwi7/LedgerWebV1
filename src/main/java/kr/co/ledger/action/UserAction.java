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
            case "findIdForm" 	-> findIdForm(request, response);
            case "findId"       -> findId(request, response);
            case "findPwForm" 	-> findPwForm(request, response);
            case "findPw"     	-> findPw(request, response);
            case "myPage" 		-> myPage(request, response);
            case "updateForm" 	-> updateForm(request, response);
            case "updateInfo" 	-> updateInfo(request, response);
            case "withdraw" 	-> withdraw(request, response);
            case "wakeup" 		-> wakeup(request, response);
            default -> throw new IllegalArgumentException("UserAction에 없는 기능: " + command);
        };
    }
    
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
        if (birth != null) {
            birth = birth.replace("-", ""); 
        }
        dto.setUserBirth(birth);
        
        boolean isSuccess = UserService.getInstance().registerUser(dto);
        
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

        if (userBirth != null) {
            userBirth = userBirth.replace("-", "");
        }

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
        
        if (userBirth != null) {
            userBirth = userBirth.replace("-", "");
        }

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
 
        if (loginUser == null) {
            request.setAttribute("msg", "로그인이 필요한 서비스입니다.");
            return "/views/user/loginForm.jsp";
        }
        UserDTO userInfo = UserService.getInstance().getUserInfo(loginUser.getUserId());
        request.setAttribute("userInfo", userInfo);
        
        return "/views/user/myPage.jsp";
    }
    
    // 개인정보 수정 폼
    private String updateForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) return "/views/user/loginForm.jsp";
        
        UserDTO userInfo = UserService.getInstance().getUserInfo(loginUser.getUserId());
        request.setAttribute("userInfo", userInfo);
        return "/views/user/updateForm.jsp";
    }

    // 개인정보 수정
    private String updateInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) return "/views/user/loginForm.jsp";

        UserDTO dto = new UserDTO();
        dto.setUserId(loginUser.getUserId());
        dto.setUserPw(request.getParameter("userPw"));
        dto.setUserNickname(request.getParameter("userNickname"));
        dto.setUserEmail(request.getParameter("userEmail"));
        dto.setUserPhone(request.getParameter("userPhone"));
        
        String birth = request.getParameter("userBirth");
        if (birth != null) {
            birth = birth.replace("-", ""); 
        }
        dto.setUserBirth(birth);

        boolean isSuccess = UserService.getInstance().updateUserInfo(dto);

        if (isSuccess) {
            loginUser.setUserNickname(dto.getUserNickname()); 
            
            request.setAttribute("msg", "정보가 성공적으로 수정되었습니다.");
            return myPage(request, response);
        } else {
            request.setAttribute("msg", "정보 수정에 실패했습니다.");
            return "/views/user/updateForm.jsp";
        }
    }
    
    // 회원 탈퇴
    private String withdraw(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) return "/views/user/loginForm.jsp";

        boolean isSuccess = UserService.getInstance().withdrawUser(loginUser.getUserId());

        if (isSuccess) {
            request.getSession().invalidate();
            
            request.setAttribute("msg", "회원 탈퇴가 완료되었습니다. 그동안 이용해 주셔서 감사합니다.");
            return "/views/user/loginForm.jsp"; 
        } else {
            request.setAttribute("msg", "회원 탈퇴 처리에 실패했습니다.");
            return "/views/user/myPage.jsp";
        }
    }
    
    // 휴면 해제
    private String wakeup(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String userId = request.getParameter("userId");
        
        boolean isSuccess = UserService.getInstance().wakeupUser(userId);

        if (isSuccess) {
            request.setAttribute("msg", "휴면 상태가 해제되었습니다. 다시 로그인해 주세요!");
        } else {
            request.setAttribute("msg", "휴면 해제에 실패했습니다.");
        }
        return "/views/user/loginForm.jsp";
    }
    
}