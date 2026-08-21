<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!-- jsp:useBean은 최상단에 선언하여 깔끔하게 관리 -->
<jsp:useBean id="now" class="java.util.Date" />

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>그룹 통계 대시보드 - 공동 가계부</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- 커스텀 CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/group_ledger/group_statistics.css">
</head>
<body class="bg-light">

    <div class="container my-5">
        <h2 class="fw-bold mb-4 text-center text-dark">📊 그룹 지출 통계 대시보드</h2>

        <!-- Bootstrap Grid Row (PC에선 양옆으로, 모바일에선 위아래로 자동 배치) -->
        <div class="row g-4 justify-content-center">
            
            <!-- 1. 파이 차트 영역 (왼쪽) -->
            <div class="col-lg-5 col-md-10">
                <div class="card shadow-sm h-100 border-0">
                    <div class="card-body text-center d-flex flex-column align-items-center">
                        <h4 class="card-title fw-bold mb-3 text-primary">
                            <fmt:formatDate value="${now}" pattern="yyyy년 M월" /> 지출 비율
                        </h4>
                        
                        <button onclick="loadAllGroupsPieChart()" class="btn btn-outline-primary btn-sm fw-bold mb-4">
                            🔄 이번 달 통계 새로고침
                        </button>
                        
                        <div class="chart-container w-100" style="position: relative; max-width: 350px;">
                            <canvas id="groupPieChart"></canvas>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 2. 추이 차트 영역 (오른쪽) -->
            <div class="col-lg-7 col-md-10">
                <div class="card shadow-sm h-100 border-0">
                    <div class="card-body text-center d-flex flex-column align-items-center">
                        <h4 class="card-title fw-bold mb-4 text-danger">최근 6개월 전체 그룹 지출 추이</h4>
                        
                        <div class="chart-container w-100" style="position: relative; flex-grow: 1; display: flex; align-items: center;">
                            <canvas id="groupTrendChart"></canvas>
                        </div>
                    </div>
                </div>
            </div>

        </div> <!-- // row 끝 -->
    </div> <!-- // container 끝 -->

    <!-- JS 백엔드 환경 설정 -->
    <script>
        window.AppConfig = {
            contextPath: '${pageContext.request.contextPath}'
        };
    </script>
    
    <!-- Chart.js CDN -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <!-- 공통 ajax 모듈 -->
    <script src="${pageContext.request.contextPath}/assets/js/common/ajaxUtil.js"></script>
    <!-- 분리된 커스텀 JS 연결 -->
    <script src="${pageContext.request.contextPath}/assets/js/group_ledger/group_statistics.js"></script>

</body>
</html>