<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>가계부 - 회원가입</title>
</head>
<body>
    <h2>가계부 회원가입</h2>

    <c:if test="${not empty msg}">
    	<script>
       		alert('${msg}');
    	</script>
	</c:if>

    <form action="${pageContext.request.contextPath}/user/register.do" method="post">
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
                <td>닉네임</td>
                <td><input type="text" name="userNickname" required></td>
            </tr>
            <tr>
                <td>이메일</td>
                <td><input type="email" name="userEmail" required></td>
            </tr>
            <tr>
                <td>전화번호</td>
                <td><input type="text" name="userPhone" placeholder="010-1234-5678" required></td>
            </tr>
            <tr>
                <td>생년월일</td>
                <td><input type="date" name="userBirth" required></td>
            </tr>
            <tr>
                <td colspan="2" align="center">
                    <button type="submit">가입하기</button>
                    <button type="reset">다시 쓰기</button>
                </td>
            </tr>
        </table>
    </form>
</body>
</html>