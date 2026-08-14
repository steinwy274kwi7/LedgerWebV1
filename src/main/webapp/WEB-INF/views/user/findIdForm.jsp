<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>아이디 찾기</title>
</head>
<body>
    <h2>아이디 찾기</h2>

    <c:if test="${not empty msg}">
        <script>alert('${msg}');</script>
    </c:if>

    <c:if test="${not empty foundId}">
        <div style="background-color: #e8f5e9; padding: 15px; margin-bottom: 15px;">
            <p>회원님의 아이디는 <strong>${foundId}</strong> 입니다.</p>
            <a href="${pageContext.request.contextPath}/user/loginForm.do">로그인하러 가기</a>
        </div>
    </c:if>

    <!-- 아이디 찾기 입력 폼 -->
    <form action="${pageContext.request.contextPath}/user/findId.do" method="post">
        <table>
            <tr>
                <td>이메일:</td>
                <td><input type="email" name="userEmail" required></td>
            </tr>
            <tr>
                <td>전화번호:</td>
                <td><input type="text" name="userPhone" placeholder="01012345678" required></td>
            </tr>
            <tr>
                <td>생년월일:</td>
                <td><input type="date" name="userBirth" required></td>
            </tr>
            <tr>
                <td colspan="2">
                    <button type="submit">아이디 찾기</button>
                    <a href="${pageContext.request.contextPath}/user/loginForm.do">취소</a>
                </td>
            </tr>
        </table>
    </form>
</body>
</html>