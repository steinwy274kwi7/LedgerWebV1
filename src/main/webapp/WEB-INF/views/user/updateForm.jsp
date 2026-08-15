<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>정보 수정</title>
</head>
<body>
    <h2>회원 정보 수정</h2>
    
    <c:if test="${not empty msg}">
        <script>alert('${msg}');</script>
    </c:if>

    <form action="${pageContext.request.contextPath}/user/updateInfo.do" method="post">
        <table>
            <tr>
                <td>아이디</td>
                <td>${userInfo.userId} <span style="color:gray; font-size:12px;">(변경 불가)</span></td>
            </tr>
            <tr>
                <td>새 비밀번호</td>
                <td><input type="password" name="userPw" placeholder="새로운 비밀번호 입력" required></td>
            </tr>
            <tr>
                <td>닉네임</td>
                <td><input type="text" name="userNickname" value="${userInfo.userNickname}" required></td>
            </tr>
            <tr>
                <td>이메일</td>
                <td><input type="email" name="userEmail" value="${userInfo.userEmail}" required></td>
            </tr>
            <tr>
                <td>전화번호</td>
                <td><input type="text" name="userPhone" value="${userInfo.userPhone}" required></td>
            </tr>
            <tr>
                <td>생년월일</td>
                <!-- DB에 20040328 형태로 저장되므로 text 사용 -->
                <td><input type="text" name="userBirth" value="${userInfo.userBirth}" placeholder="예: 20000101" required></td>
            </tr>
            <tr>
                <td colspan="2">
                    <button type="submit">수정 완료</button>
                    <button type="button" onclick="history.back()">취소</button>
                </td>
            </tr>
        </table>
    </form>
</body>
</html>