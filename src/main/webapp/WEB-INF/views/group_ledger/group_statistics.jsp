<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>그룹 통계 대시보드</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        .ratio-container {
            width: 500px; padding: 20px; margin: 20px auto; 
            border: 1px solid #ddd; border-radius: 8px; background: #fff;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        }
    </style>
</head>
<body>

    <jsp:useBean id="now" class="java.util.Date" />

    <div class="ratio-container" style="text-align: center;">
        <h3 style="margin-top: 0;"><fmt:formatDate value="${now}" pattern="yyyy년 M월" /> 그룹 카테고리별 지출 비율</h3>
        <button onclick="loadAllGroupsPieChart()" style="padding: 10px; margin-bottom: 20px; cursor: pointer;">
            이번 달 전체 그룹 통계 새로고침
        </button>
        
        <canvas id="groupPieChart"></canvas>
    </div>

    <div class="ratio-container" style="width: 600px;">
        <h3 style="margin-top: 0; text-align: center;">최근 6개월 전체 그룹 지출 추이</h3>
        <canvas id="groupTrendChart"></canvas>
    </div>

    <script>
        let groupPieChartInstance = null;
        function loadAllGroupsPieChart(month = '') {
            fetch('${pageContext.request.contextPath}/group/getCategoryChartData.do?month=' + month)
                .then(response => {
                    if (!response.ok) throw new Error('서버 통신 에러');
                    return response.json();
                })
                .then(data => {
                    if (data.length === 0) {
                        alert('이번 달 그룹 지출 내역이 없습니다.');
                        return;
                    }

                    const labels = data.map(item => item.categoryName);
                    const amounts = data.map(item => item.totalAmount);

                    const backgroundColors = [
                        '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', 
                        '#9966FF', '#FF9F40', '#E7E9ED', '#8AC926'
                    ];

                    if (groupPieChartInstance != null) {
                        groupPieChartInstance.destroy();
                    }

                    const ctx = document.getElementById('groupPieChart').getContext('2d');
                    groupPieChartInstance = new Chart(ctx, {
                        type: 'pie',
                        data: {
                            labels: labels,
                            datasets: [{
                                data: amounts,
                                backgroundColor: backgroundColors.slice(0, data.length),
                                borderWidth: 1
                            }]
                        },
                        options: {
                            responsive: true,
                            plugins: {
                                legend: {
                                    position: 'bottom',
                                }
                            }
                        }
                    });
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('전체 그룹 카테고리 통계 데이터를 불러오지 못했습니다.');
                });
        }

        let groupTrendChartInstance = null;

        function loadAllGroupsTrendChart(month = '') {
            fetch('${pageContext.request.contextPath}/group/getTrendData.do?month=' + month)
                .then(response => {
                    if (!response.ok) throw new Error('서버 통신 에러');
                    return response.json();
                })
                .then(data => {
                    const labels = data.map(item => item.month);
                    const expenses = data.map(item => item.totalExpense);

                    if (groupTrendChartInstance != null) {
                        groupTrendChartInstance.destroy();
                    }

                    const ctx = document.getElementById('groupTrendChart').getContext('2d');
                    groupTrendChartInstance = new Chart(ctx, {
                        type: 'bar',
                        data: {
                            labels: labels,
                            datasets: [
                                {
                                    type: 'line', 
                                    label: '지출 추세선',
                                    data: expenses,
                                    borderColor: '#FF6384',
                                    borderWidth: 2,
                                    fill: false,
                                    tension: 0.3
                                },
                                {
                                    type: 'bar',
                                    label: '지출 금액',
                                    data: expenses,
                                    backgroundColor: 'rgba(255, 99, 132, 0.6)',
                                    borderRadius: 4
                                }
                            ]
                        },
                        options: {
                            responsive: true,
                            scales: {
                                y: { beginAtZero: true }
                            }
                        }
                    });
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('최근 6개월 그룹 지출 추이 데이터를 불러오지 못했습니다.');
                });
        }

        window.onload = function() {
            loadAllGroupsPieChart(); 
            loadAllGroupsTrendChart();
        };
    </script>
</body>
</html>