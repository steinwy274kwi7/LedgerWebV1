<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>휴면 계정 안내</title>
</head>
<body>
    <div style="text-align: center; margin-top: 100px;">
        <h2 style="color: red;">휴면 계정 전환 안내</h2>
        <p>
            회원님의 계정은 1년 이상 로그인하지 않아<br>
            안전을 위해 <b>휴면(D) 상태</b>로 전환되었습니다.
        </p>
        <p>계속 서비스를 이용하시려면 아래 버튼을 눌러 휴면 상태를 해제해 주세요.</p>
        
        <!-- 해제 요청 폼 -->
        <form action="${pageContext.request.contextPath}/user/wakeup.do" method="post">
            
            <input type="hidden" name="userId" value="${dormantId}">
            
            <button type="submit" style="padding: 10px 20px; font-size: 16px; cursor: pointer;">
                휴면 해제하기
            </button>
            <button type="button" style="padding: 10px 20px; font-size: 16px; cursor: pointer;" 
                    onclick="location.href='${pageContext.request.contextPath}/user/loginForm.do'">
                취소 (로그인으로 돌아가기)
            </button>
        </form>
    </div>
</body>
</html>