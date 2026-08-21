<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>가계부 - 회원가입</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- 커스텀 CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/user/registerForm.css">
</head>
<body class="bg-light d-flex align-items-center min-vh-100 py-5">

    <div class="container">
        <div class="row justify-content-center">
            <div class="col-md-8 col-lg-6">
                <div class="card shadow-sm border-0 custom-card">
                    <div class="card-body p-4 p-md-5">
                        
                        <h2 class="text-center fw-bold mb-4 text-dark">회원가입</h2>

                        <!-- 🌟 1. 회원가입 폼 -->
                        <form id="registerForm" action="${pageContext.request.contextPath}/user/register.do" method="post">
                            
                            <div class="mb-3">
                                <label for="userId" class="form-label fw-bold text-secondary">아이디</label>
                                <input type="text" class="form-control" id="userId" name="userId" placeholder="사용할 아이디를 입력하세요" required>
                            </div>
                            
                            <div class="mb-3">
                                <label for="userPw" class="form-label fw-bold text-secondary">비밀번호</label>
                                <input type="password" class="form-control" id="userPw" name="userPw" placeholder="비밀번호를 입력하세요" required>
                            </div>
                            
                            <div class="mb-3">
                                <label for="userNickname" class="form-label fw-bold text-secondary">닉네임</label>
                                <input type="text" class="form-control" id="userNickname" name="userNickname" placeholder="사용할 닉네임을 입력하세요" required>
                            </div>
                            
                            <div class="mb-3">
                                <label for="userEmail" class="form-label fw-bold text-secondary">이메일</label>
                                <input type="email" class="form-control" id="userEmail" name="userEmail" placeholder="example@email.com" required>
                            </div>
                            
                            <div class="mb-3">
                                <label for="userPhone" class="form-label fw-bold text-secondary">전화번호</label>
                                <input type="text" class="form-control" id="userPhone" name="userPhone" placeholder="예: 010-1234-5678" required>
                            </div>
                            
                            <div class="mb-4">
                                <label for="userBirth" class="form-label fw-bold text-secondary">생년월일</label>
                                <input type="date" class="form-control" id="userBirth" name="userBirth" required>
                            </div>
                            
                            <!-- 🌟 2. 하단 버튼 영역 -->
                            <div class="d-grid gap-2 mt-4">
                                <button type="submit" class="btn btn-primary fw-bold py-2">가입하기</button>
                                <button type="reset" class="btn btn-light border fw-bold py-2 text-secondary">다시 쓰기</button>
                            </div>
                            
                        </form>

                        <!-- 🌟 3. 로그인 화면으로 돌아가기 -->
                        <div class="text-center mt-4 pt-3 border-top">
                            <p class="text-muted mb-0">
                                이미 계정이 있으신가요? 
                                <a href="${pageContext.request.contextPath}/user/loginForm.do" class="text-decoration-none fw-bold">로그인</a>
                            </p>
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
    <!-- 더블클릭 방지 모듈 -->
    <script src="${pageContext.request.contextPath}/assets/js/common/uiUtil.js"></script>
    <!-- 분리된 커스텀 JS -->
    <script src="${pageContext.request.contextPath}/assets/js/user/registerForm.js"></script>

</body>
</html>