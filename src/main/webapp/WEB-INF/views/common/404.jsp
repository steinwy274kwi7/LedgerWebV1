<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>가계부 - 페이지를 찾을 수 없습니다</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light d-flex align-items-center vh-100">
    <div class="container text-center">
        <h1 class="display-1 fw-bold text-secondary mb-3">404</h1>
        <h3 class="fw-bold text-dark mb-4">원하시는 페이지를 찾을 수 없습니다.</h3>
        <p class="text-muted mb-5">
            입력하신 주소가 잘못되었거나,<br>
            페이지가 삭제되어 이동되었을 수 있습니다.
        </p>
        <a href="${pageContext.request.contextPath}/main.do" class="btn btn-primary fw-bold px-4 py-2 shadow-sm">메인으로 돌아가기</a>
        <button onclick="history.back()" class="btn btn-light border fw-bold px-4 py-2 ms-2 text-secondary">이전 페이지</button>
    </div>
</body>
</html>