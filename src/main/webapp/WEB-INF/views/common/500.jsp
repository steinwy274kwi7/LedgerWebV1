<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>가계부 - 시스템 오류</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light d-flex align-items-center vh-100">
    <div class="container text-center">
        <h1 class="display-1 fw-bold text-danger mb-3">500</h1>
        <h3 class="fw-bold text-dark mb-4">서버 처리 중 오류가 발생했습니다.</h3>
        <p class="text-muted mb-5">
            이용에 불편을 드려 죄송합니다.<br>
            일시적인 시스템 장애일 수 있으니 잠시 후 다시 시도해 주세요.
        </p>
        <a href="${pageContext.request.contextPath}/main.do" class="btn btn-danger fw-bold px-4 py-2 shadow-sm">메인으로 돌아가기</a>
    </div>
</body>
</html>s