<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>마이페이지</title>
<script>
    function withdrawUser() {
        if (confirm("정말로 탈퇴하시겠습니까? 탈퇴 시 모든 개인정보가 즉시 파기되며 복구할 수 없습니다.")) {
            location.href = '${pageContext.request.contextPath}/user/withdraw.do';
        }
    }
</script>
</head>
<body>
    <h2>내 정보</h2>
    
    <table border="1" style="border-collapse: collapse; padding: 10px;">
        <tr>
            <th style="padding: 8px;">아이디</th>
            <td style="padding: 8px;">${userInfo.userId}</td>
        </tr>
        <tr>
            <th style="padding: 8px;">닉네임</th>
            <td style="padding: 8px;">${userInfo.userNickname}</td>
        </tr>
        <tr>
            <th style="padding: 8px;">이메일</th>
            <td style="padding: 8px;">${userInfo.userEmail}</td>
        </tr>
        <tr>
            <th style="padding: 8px;">전화번호</th>
            <td style="padding: 8px;">${userInfo.userPhone}</td>
        </tr>
        <tr>
            <th style="padding: 8px;">생년월일</th>
            <td style="padding: 8px;">${userInfo.userBirth}</td>
        </tr>
    </table>
    
    <br>
    <button type="button" onclick="location.href='${pageContext.request.contextPath}/user/updateForm.do'">정보 수정</button>
    <button type="button" onclick="location.href='${pageContext.request.contextPath}/main.do'">메인으로</button>
    <button type="button" style="color:white; background-color:red; padding:5px 10px; border:none; border-radius:3px; cursor:pointer;" onclick="withdrawUser()">회원 탈퇴</button>
    
</body>

<c:if test="${not empty msg}">
    <!-- 모달창 어두운 배경 -->
    <div id="modalBackdrop" style="position:fixed; top:0; left:0; width:100%; height:100%; background:rgba(0,0,0,0.5); z-index:999;"></div>
    
    <!-- 모달창 본체 -->
    <div id="successModal" style="position:fixed; top:50%; left:50%; transform:translate(-50%, -50%); background:white; padding:30px; border-radius:10px; box-shadow:0 4px 15px rgba(0,0,0,0.2); z-index:1000; text-align:center; min-width:300px;">
        <h3 style="color:#2e7d32; margin-top:0;">✅ 수정 완료</h3>
        <p style="font-size:16px; margin-bottom:20px;">${msg}</p>
        <button style="padding:8px 20px; cursor:pointer;" onclick="closeModal()">확인</button>
    </div>

    <!-- 모달 닫기 스크립트 -->
    <script>
        function closeModal() {
            document.getElementById('successModal').style.display = 'none';
            document.getElementById('modalBackdrop').style.display = 'none';
        }
    </script>
</c:if>

</html>