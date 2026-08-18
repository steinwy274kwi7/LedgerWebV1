<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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

    <div class="ratio-container" style="text-align: center;">
        <h3 style="margin-top: 0;">이번 달 그룹 카테고리별 지출 비율</h3>
        <button onclick="loadAllGroupsPieChart()" style="padding: 10px; margin-bottom: 20px; cursor: pointer;">
            이번 달 전체 그룹 통계 새로고침
        </button>
        
        <canvas id="groupPieChart"></canvas>
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

        window.onload = function() {
            loadAllGroupsPieChart(); 
        };
    </script>
</body>
</html>