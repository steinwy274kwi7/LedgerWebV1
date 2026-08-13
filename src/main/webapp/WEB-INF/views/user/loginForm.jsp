<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>가계부 - 로그인</title>
</head>
<body>
    <h2>가계부 로그인</h2>

    <c:if test="${not empty msg}">
        <script>
            alert('${msg}');
        </script>
    </c:if>

    <form action="${pageContext.request.contextPath}/user/login.do" method="post">
        <table border="1">
            <tr>
                <td>아이디</td>
                <td><input type="text" name="userId" required></td>
            </tr>
            <tr>
                <td>비밀번호</td>
                <td><input type="password" name="userPw" required></td>
            </tr>
            <tr>
                <td colspan="2" align="center">
                    <button type="submit">로그인</button>
                </td>
            </tr>
        </table>
    </form>
    
    <p>아직 계정이 없으신가요? 
        <a href="${pageContext.request.contextPath}/user/registerForm.do">회원가입 하러가기</a>
    </p>
</body>
</html>