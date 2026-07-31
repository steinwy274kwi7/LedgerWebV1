package kr.co.ledger.util;

import jakarta.servlet.http.HttpServletRequest;

public class UriUtil {
	public static String getCommand(HttpServletRequest request) {
		String uri = request.getRequestURI();
		String contextPath = request.getContextPath();
		return uri.substring(contextPath.length());
	}
}
