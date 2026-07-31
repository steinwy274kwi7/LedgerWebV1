package kr.co.ledger.action;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.ledger.util.UriUtil;

public class UserAction implements Action{

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String command = UriUtil.getCommand(request);
		String methodName = command.substring(command.lastIndexOf("/")+1, command.lastIndexOf("."));
		return switch (methodName) {
			case "registerForm" -> registerForm(request, response);
			case "register"     -> register(request, response);
			case "loginForm"    -> loginForm(request, response);
			case "login"        -> login(request, response);
			
			default -> throw new IllegalArgumentException("UserAction에 없는 기능"+command);
		};
	}
	
	private String registerForm(HttpServletRequest request, HttpServletResponse response) {
		return "/views/user/registerForm.jsp";
	}
	
	private String register(HttpServletRequest request, HttpServletResponse response) throws Exception{
		System.out.println("회원가입 로직 실행됨");
		return "/views/user/loginForm.jsp";
	}
	
	private String loginForm(HttpServletRequest request, HttpServletResponse response) {
		return "/views/user/loginForm.jsp";
	}
	
	private String login(HttpServletRequest request, HttpServletResponse response)throws Exception {
		System.out.println("로그인 로직 실행");
		return "/views/index.jsp";
	}
}
