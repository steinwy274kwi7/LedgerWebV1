<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="kr.co.ledger.dto.UserDTO" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    // 로그인 체크 로직 유지
    UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");
    if (loginUser == null) {
        response.sendRedirect(request.getContextPath() + "/user/loginForm.do");
        return;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>가계부 메인 대시보드</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- 커스텀 CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
</head>
<body class="bg-light">
    <jsp:useBean id="now" class="java.util.Date" />

    <div class="container my-5" style="max-width: 900px;">
        
        <!-- 1. 상단 환영 메시지 및 알림 버튼 -->
        <div class="d-flex justify-content-between align-items-center mb-4 p-4 bg-white rounded shadow-sm border-start border-5 border-primary">
            <h2 class="fw-bold m-0 text-dark">
                환영합니다, <span class="text-primary">${sessionScope.loginUser.userId}</span>님! 🎉
            </h2>
            <button class="btn btn-warning fw-bold shadow-sm position-relative" onclick="openNotificationModal()">
                🔔 초대 알림 확인
                <!-- 알림이 있을 때 띄울 뱃지 (옵션) -->
                <span id="inviteBadge" class="position-absolute top-0 start-100 translate-middle p-2 bg-danger border border-light rounded-circle" style="display:none;"></span>
            </button>
        </div>

        <div class="row g-4 mb-4">
            <!-- 2. 퀵 메뉴 (좌측) -->
            <div class="col-md-4">
                <div class="card border-0 shadow-sm h-100">
                    <div class="card-header bg-dark text-white fw-bold text-center">
                        🚀 퀵 메뉴
                    </div>
                    <div class="card-body d-flex flex-column gap-2">
                        <a href="${pageContext.request.contextPath}/personal/calendar.do" class="btn btn-outline-primary fw-bold w-100">📔 개인 가계부 이동</a>
                        <a href="${pageContext.request.contextPath}/group/list.do" class="btn btn-outline-success fw-bold w-100">👥 공동 가계부 이동</a>
                        <hr class="my-2">
                        <a href="${pageContext.request.contextPath}/user/myPage.do" class="btn btn-light fw-bold w-100 border">👤 마이페이지</a>
                        <a href="${pageContext.request.contextPath}/user/logout.do" class="btn btn-light fw-bold w-100 border text-danger">🚪 로그아웃</a>
                    </div>
                </div>
            </div>

            <!-- 3. 차트 영역 (우측) -->
            <div class="col-md-8">
                <div class="card border-0 shadow-sm h-100">
                    <div class="card-body p-4 text-center">
                        <h4 class="fw-bold text-dark mb-4">
                            📊 <fmt:formatDate value="${now}" pattern="yyyy년 M월" /> 카테고리별 비율
                        </h4>
                        
                        <!-- 🌟 수입(빨강), 지출(파랑) 색상 규칙 적용 -->
                        <div class="btn-group mb-3 shadow-sm" role="group">
                            <input type="radio" class="btn-check" name="chartType" id="btnChartExp" autocomplete="off" checked onclick="loadChartData('E')">
                            <label class="btn btn-outline-primary fw-bold px-4" for="btnChartExp">지출 차트</label>

                            <input type="radio" class="btn-check" name="chartType" id="btnChartInc" autocomplete="off" onclick="loadChartData('I')">
                            <label class="btn btn-outline-danger fw-bold px-4" for="btnChartInc">수입 차트</label>
                        </div>

                        <div class="chart-container mx-auto" style="position: relative; height: 300px; width: 100%; max-width: 400px;">
                            <canvas id="myPieChart"></canvas>
                        </div>
                        
                        <div class="mt-4">
                            <a href="${pageContext.request.contextPath}/personal/statistics.do" class="btn btn-secondary fw-bold rounded-pill px-4 shadow-sm">
                                📈 통계 더 보기
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- 🌟 초대 알림 모달 -->
    <div class="modal fade" id="inviteModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
            <div class="modal-content border-0 shadow">
                <div class="modal-header bg-light">
                    <h5 class="modal-title fw-bold">📨 새로운 초대장</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body p-0">
                    <ul id="inviteList" class="list-group list-group-flush">
                        <!-- JS에서 동적 렌더링 -->
                        <li class="list-group-item text-center p-4 text-muted">로딩 중...</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>

    <!-- JS 백엔드 환경 설정 -->
    <script>
        window.AppConfig = {
            contextPath: '${pageContext.request.contextPath}'
        };
    </script>

    <!-- 외부 라이브러리 JS -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- 🌟 공통 AJAX 모듈 -->
    <script src="${pageContext.request.contextPath}/assets/js/common/ajaxUtil.js"></script>
    <!-- 분리된 커스텀 JS -->
    <script src="${pageContext.request.contextPath}/assets/js/main.js"></script>

</body>
</html>