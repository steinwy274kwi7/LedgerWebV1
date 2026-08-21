<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>가계부 - 정보 수정</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- 커스텀 CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/user/updateForm.css">
</head>
<body class="bg-light d-flex align-items-center min-vh-100 py-5">

    <div class="container">
        <div class="row justify-content-center">
            <div class="col-md-8 col-lg-6">
                <div class="card shadow-sm border-0 custom-card">
                    <div class="card-body p-4 p-md-5">
                        
                        <h2 class="text-center fw-bold mb-4 text-dark">회원 정보 수정</h2>

                        <!-- 🌟 정보 수정 폼 -->
                        <form id="updateForm" action="${pageContext.request.contextPath}/user/updateInfo.do" method="post">
                            
                            <div class="mb-3">
                                <label class="form-label fw-bold text-secondary">아이디 <small class="text-danger">(변경 불가)</small></label>
                                <!-- 서버 전송 불필요하므로 name 생략, disabled 처리 -->
                                <input type="text" class="form-control bg-light text-muted" value="${userInfo.userId}" disabled>
                            </div>
                            
                            <div class="mb-3">
                                <label for="userPw" class="form-label fw-bold text-secondary">새 비밀번호</label>
                                <input type="password" class="form-control" id="userPw" name="userPw" placeholder="새로운 비밀번호 입력" required>
                            </div>
                            
                            <div class="mb-3">
                                <label for="userNickname" class="form-label fw-bold text-secondary">닉네임</label>
                                <input type="text" class="form-control" id="userNickname" name="userNickname" value="${userInfo.userNickname}" required>
                            </div>
                            
                            <div class="mb-3">
                                <label for="userEmail" class="form-label fw-bold text-secondary">이메일</label>
                                <input type="email" class="form-control" id="userEmail" name="userEmail" value="${userInfo.userEmail}" required>
                            </div>
                            
                            <div class="mb-3">
                                <label for="userPhone" class="form-label fw-bold text-secondary">전화번호</label>
                                <input type="text" class="form-control" id="userPhone" name="userPhone" value="${userInfo.userPhone}" placeholder="예: 01012345678" required>
                            </div>
                            
                            <div class="mb-4">
                                <label for="userBirth" class="form-label fw-bold text-secondary">생년월일</label>
                                <!-- DB에 20040328 형태로 저장되므로 type="text" 유지 -->
                                <input type="text" class="form-control" id="userBirth" name="userBirth" value="${userInfo.userBirth}" placeholder="예: 20000101 (8자리 숫자)" required>
                            </div>
                            
                            <div class="d-grid gap-2 mt-4">
                                <button type="submit" class="btn btn-primary fw-bold py-2">수정 완료</button>
                                <button type="button" class="btn btn-light border fw-bold py-2 text-secondary" onclick="history.back()">취소</button>
                            </div>
                            
                        </form>

                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- 🌟 JS로 넘겨줄 서버 메시지 세팅 -->
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
    <script src="${pageContext.request.contextPath}/assets/js/user/updateForm.js"></script>

</body>
</html>