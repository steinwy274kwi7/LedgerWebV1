<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>가계부 - 로그인</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- 커스텀 CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/user/loginForm.css">
</head>
<body class="bg-light d-flex align-items-center vh-100">

    <div class="container">
        <div class="row justify-content-center">
            <div class="col-md-6 col-lg-5">
                
                <!-- 🌟 1. 회원가입 성공 알림 영역 -->
                <c:if test="${not empty joinSuccess}">
                    <div class="alert alert-success text-center mb-4 shadow-sm custom-alert" role="alert">
                        <h4 class="alert-heading fw-bold">🎉 회원가입을 환영합니다!</h4>
                        <p class="mb-0 mt-2">이제 가입하신 계정으로 바로 로그인해 보세요.</p>
                    </div>
                </c:if>

                <div class="card shadow-sm border-0 custom-card">
                    <div class="card-body p-5">
                        
                        <h2 class="text-center fw-bold mb-4 text-dark">로그인</h2>

                        <!-- 🌟 2. 로그인 폼 -->
                        <form id="loginForm" action="${pageContext.request.contextPath}/user/login.do" method="post">
                            
                            <div class="mb-3">
                                <label for="userId" class="form-label fw-bold text-secondary">아이디</label>
                                <input type="text" class="form-control form-control-lg" id="userId" name="userId" placeholder="아이디를 입력하세요" required>
                            </div>
                            
                            <div class="mb-4">
                                <label for="userPw" class="form-label fw-bold text-secondary">비밀번호</label>
                                <input type="password" class="form-control form-control-lg" id="userPw" name="userPw" placeholder="비밀번호를 입력하세요" required>
                            </div>
                            
                            <div class="d-grid mb-4">
                                <button type="submit" class="btn btn-primary btn-lg fw-bold">로그인</button>
                            </div>
                            
                        </form>

                        <!-- 🌟 3. 하단 네비게이션 링크 -->
                        <div class="text-center mt-4 border-top pt-4">
                            <p class="text-muted mb-2">아직 계정이 없으신가요?</p>
                            <a href="${pageContext.request.contextPath}/user/registerForm.do" class="btn btn-outline-success fw-bold w-100 mb-3">회원가입 하러가기</a>
                            
                            <div class="d-flex justify-content-center gap-3">
                                <a href="${pageContext.request.contextPath}/user/findIdForm.do" class="text-decoration-none text-secondary hover-primary">아이디 찾기</a>
                                <span class="text-secondary">|</span>
                                <a href="${pageContext.request.contextPath}/user/findPwForm.do" class="text-decoration-none text-secondary hover-primary">비밀번호 찾기</a>
                            </div>
                        </div>

                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- 🌟 4. JS로 넘겨줄 서버 메시지 세팅 -->
    <script>
        window.AppConfig = {
            serverMessage: '${msg}'
        };
    </script>
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <!-- 분리된 커스텀 JS -->
    <script src="${pageContext.request.contextPath}/assets/js/user/loginForm.js"></script>

</body>
</html>