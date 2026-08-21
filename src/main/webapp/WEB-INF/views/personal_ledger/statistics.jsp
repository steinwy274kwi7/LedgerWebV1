<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>개인 가계부 - 통계 대시보드</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- 커스텀 CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/personal_ledger/statistics.css">
</head>
<body class="bg-light">

    <jsp:useBean id="now" class="java.util.Date" />

    <div class="container my-5" style="max-width: 1100px;">
        
        <!-- 상단 헤더 영역 -->
        <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom border-2">
            <h2 class="fw-bold text-dark m-0">📊 <fmt:formatDate value="${now}" pattern="yyyy년 M월" /> 흑자/적자 분석</h2>
            <button class="btn btn-primary fw-bold shadow-sm" onclick="loadRatioData(''); loadTrendData('');">
                🔄 통계 새로고침
            </button>
        </div>

        <div class="row g-4">
            <!-- 1. 예산 현황 카드 (좌측) -->
            <div class="col-lg-4 col-md-12">
                <div class="card border-0 shadow-sm h-100">
                    <div class="card-body p-4 d-flex flex-column justify-content-center">
                        <h4 class="card-title fw-bold text-dark mb-4 text-center">이번 달 예산 현황</h4>
                        
                        <div class="d-flex justify-content-between align-items-center mb-2 fs-5">
                            <span class="text-secondary fw-bold">수입</span>
                            <span class="text-danger fw-bold"><span id="uiIncome">0</span>원</span>
                        </div>
                        <div class="d-flex justify-content-between align-items-center mb-4 fs-5">
                            <span class="text-secondary fw-bold">지출</span>
                            <span class="text-primary fw-bold"><span id="uiExpense">0</span>원</span>
                        </div>
                        
                        <!-- Bootstrap Progress Bar -->
                        <div class="progress mb-3 shadow-sm" style="height: 28px; border-radius: 14px;">
                            <div id="uiProgressBar" class="progress-bar bg-success progress-bar-striped progress-bar-animated" role="progressbar" style="width: 0%;"></div>
                        </div>
                        
                        <h1 id="uiPercent" class="text-center fw-bold text-dark my-3 display-5">0%</h1>
                        <p id="uiMessage" class="text-center text-muted fw-bold m-0 fs-5"></p>
                    </div>
                </div>
            </div>

            <!-- 2. 추이 차트 카드 (우측) -->
            <div class="col-lg-8 col-md-12">
                <div class="card border-0 shadow-sm h-100">
                    <div class="card-body p-4">
                        <h4 class="card-title fw-bold text-dark mb-4 text-center">최근 6개월 수입/지출 추이</h4>
                        <div class="chart-container" style="position: relative; height: 350px; width: 100%;">
                            <canvas id="trendChart"></canvas>
                        </div>
                    </div>
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
    <script src="${pageContext.request.contextPath}/assets/js/personal_ledger/statistics.js"></script>

</body>
</html>