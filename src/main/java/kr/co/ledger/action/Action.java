package kr.co.ledger.action;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface Action {
	//request 요청 바구니, response 응답 도구
	public String execute(HttpServletRequest request,HttpServletResponse response)throws Exception;
}
