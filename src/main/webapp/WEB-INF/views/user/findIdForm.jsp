<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>아이디 찾기</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- 커스텀 CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/user/findIdForm.css">
</head>
<body class="bg-light d-flex align-items-center vh-100">

    <div class="container">
        <div class="row justify-content-center">
            <div class="col-md-6 col-lg-5">
                <div class="card shadow-sm border-0 custom-card">
                    <div class="card-body p-5">
                        
                        <h2 class="text-center fw-bold mb-4 text-dark">아이디 찾기</h2>

                        <!-- 🌟 1. 찾은 아이디 결과 영역 (부트스트랩 Alert 사용) -->
                        <c:if test="${not empty foundId}">
                            <div class="alert alert-success text-center mb-4 shadow-sm" role="alert">
                                회원님의 아이디는 <strong class="fs-5">${foundId}</strong> 입니다.
                                <hr>
                                <a href="${pageContext.request.contextPath}/user/loginForm.do" class="btn btn-success fw-bold w-100 mt-2">로그인하러 가기</a>
                            </div>
                        </c:if>

                        <!-- 🌟 2. 아이디 찾기 입력 폼 -->
                        <form id="findIdForm" action="${pageContext.request.contextPath}/user/findId.do" method="post">
                            
                            <div class="mb-3">
                                <label for="userEmail" class="form-label fw-bold text-secondary">이메일</label>
                                <input type="email" class="form-control" id="userEmail" name="userEmail" placeholder="example@email.com" required>
                            </div>
                            
                            <div class="mb-3">
                                <label for="userPhone" class="form-label fw-bold text-secondary">전화번호</label>
                                <input type="text" class="form-control" id="userPhone" name="userPhone" placeholder="'-' 없이 숫자만 입력 (예: 01012345678)" required>
                            </div>
                            
                            <div class="mb-5">
                                <label for="userBirth" class="form-label fw-bold text-secondary">생년월일</label>
                                <input type="date" class="form-control" id="userBirth" name="userBirth" required>
                            </div>
                            
                            <div class="d-grid gap-2">
                                <button type="submit" class="btn btn-primary fw-bold py-2">아이디 찾기</button>
                                <a href="${pageContext.request.contextPath}/user/loginForm.do" class="btn btn-light border fw-bold py-2 text-secondary">취소</a>
                            </div>
                            
                        </form>

                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- 🌟 3. JS로 넘겨줄 서버 메시지 세팅 -->
    <script>
        window.AppConfig = {
            serverMessage: '${msg}'
        };
    </script>
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <!-- 더블클릭 방지 모듈 -->
    <script src="${pageContext.request.contextPath}/assets/js/common/uiUtil.js"></script>
    <!-- 분리된 커스텀 JS -->
    <script src="${pageContext.request.contextPath}/assets/js/user/findIdForm.js"></script>

</body>
</html>