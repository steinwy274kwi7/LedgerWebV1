<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="kr.co.ledger.dto.UserDTO" %>
<%
    UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");

    if (loginUser == null) {
        response.sendRedirect(request.getContextPath() + "/user/loginForm.do");
        return;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <title>가계부 메인 화면</title>
</head>
<body>
    <h1>환영합니다, ${sessionScope.loginUser.userId}님!</h1>
    
	<a href="${pageContext.request.contextPath}/user/logout.do">로그아웃</a>
</body>
</html>