<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>가계부 - 휴면 계정 안내</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- 커스텀 CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/user/recoveryForm.css">
</head>
<body class="bg-light d-flex align-items-center vh-100">

    <div class="container">
        <div class="row justify-content-center">
            <div class="col-md-6 col-lg-5">
                <div class="card shadow-sm border-0 custom-card">
                    <div class="card-body p-5 text-center">

                        <!-- 🌟 1. 직관적인 아이콘 및 제목 -->
                        <div class="mb-4">
                            <span class="display-1">🔒</span>
                        </div>
                        <h3 class="fw-bold mb-3 text-danger">휴면 계정 전환 안내</h3>

                        <!-- 🌟 2. 안내 메시지 -->
                        <p class="text-muted mb-4">
                            회원님의 계정은 1년 이상 로그인하지 않아<br>
                            안전을 위해 <strong class="text-dark">휴면(D) 상태</strong>로 전환되었습니다.
                        </p>
                        <p class="mb-4 text-secondary" style="font-size: 0.95rem;">
                            계속 서비스를 이용하시려면 아래 버튼을 눌러<br>휴면 상태를 해제해 주세요.
                        </p>

                        <!-- 🌟 3. 해제 요청 폼 (액션 주소는 wakeup.do 그대로 유지!) -->
                        <form id="recoveryForm" action="${pageContext.request.contextPath}/user/wakeup.do" method="post">
                            
                            <!-- [핵심] 서버로 넘겨줄 휴면 대상 아이디 -->
                            <input type="hidden" name="userId" value="${dormantId}">
                            
                            <div class="d-grid gap-2">
                                <button type="submit" class="btn btn-primary fw-bold py-2">휴면 해제하기</button>
                                <button type="button" id="btnCancel" class="btn btn-light border fw-bold py-2 text-secondary">
                                    취소 <small>(로그인으로 돌아가기)</small>
                                </button>
                            </div>
                        </form>

                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- 🌟 4. JS로 넘겨줄 서버 경로 세팅 -->
    <script>
        window.AppConfig = {
            contextPath: '${pageContext.request.contextPath}'
        };
    </script>
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <!-- 분리된 커스텀 JS -->
    <script src="${pageContext.request.contextPath}/assets/js/user/recoveryForm.js"></script>

</body>
</html>