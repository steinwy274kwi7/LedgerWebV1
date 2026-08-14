<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>비밀번호 찾기</title>
</head>
<body>
    <h2>비밀번호 찾기</h2>

    <c:if test="${not empty msg}">
        <script>alert('${msg}');</script>
    </c:if>

    <c:if test="${not empty tempPw}">
        <div style="background-color: #e8f5e9; padding: 15px; margin-bottom: 15px;">
            <p>임시 비밀번호가 발급되었습니다: <strong>${tempPw}</strong></p>
            <a href="${pageContext.request.contextPath}/user/loginForm.do">로그인하러 가기</a>
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/user/findPw.do" method="post">
        <table>
            <tr>
                <td>아이디:</td>
                <td><input type="text" name="userId" required></td>
            </tr>
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
                    <button type="submit">임시 비밀번호 발급</button>
                    <a href="${pageContext.request.contextPath}/user/loginForm.do">취소</a>
                </td>
            </tr>
        </table>
    </form>
</body>
</html>