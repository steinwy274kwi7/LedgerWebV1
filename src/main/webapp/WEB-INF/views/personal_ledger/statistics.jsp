<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>통계 대시보드</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        .ratio-container {
            width: 400px; padding: 20px; margin: 20px auto; 
            border: 1px solid #ddd; border-radius: 8px; background: #fff;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        }
        .progress-bar-bg {
            width: 100%; height: 24px; background: #eee; 
            border-radius: 12px; overflow: hidden; margin: 15px 0;
            position: relative;
        }
        .progress-bar-fill {
            height: 100%; background: #4caf50; width: 0%; 
            transition: width 0.8s ease-out;
        }
        .over-budget { background: #f44336; }
    </style>
</head>
<body>

    <jsp:useBean id="now" class="java.util.Date" />

    <h2 style="text-align: center;"><fmt:formatDate value="${now}" pattern="yyyy년 M월" /> 흑자/적자 분석</h2>
    
    <div style="text-align: center;">
        <button onclick="loadRatioData(''); loadTrendData('');" style="padding: 10px; cursor: pointer;">
            통계 데이터 모두 불러오기
        </button>
    </div>

    <div class="ratio-container">
        <h3 style="margin-top: 0;">이번 달 예산 현황</h3>
        <p>수입: <b id="uiIncome">0</b>원</p>
        <p>지출: <b id="uiExpense">0</b>원</p>
        
        <div class="progress-bar-bg">
            <div id="uiProgressBar" class="progress-bar-fill"></div>
        </div>
        
        <h2 id="uiPercent" style="text-align: center; margin: 10px 0;">0%</h2>
        <p id="uiMessage" style="text-align: center; color: #555; font-size: 14px; font-weight: bold;"></p>
    </div>

	<div class="ratio-container" style="width: 600px;">
	    <h3 style="margin-top: 0; text-align: center;">최근 6개월 수입/지출 추이</h3>
	    <canvas id="trendChart"></canvas>
	</div>
	
    <script>
        function loadRatioData(month = '') {
            fetch('${pageContext.request.contextPath}/personal/getRatioData.do?month=' + month)
                .then(response => {
                    if (!response.ok) throw new Error('서버 통신 에러');
                    return response.json();
                })
                .then(data => {
                    document.getElementById('uiIncome').innerText = data.totalIncome.toLocaleString();
                    document.getElementById('uiExpense').innerText = data.totalExpense.toLocaleString();
                    
                    const bar = document.getElementById('uiProgressBar');
                    const percentText = document.getElementById('uiPercent');
                    
                    document.getElementById('uiMessage').innerText = data.statusMessage;

                    if (data.expenseRatio === -1) {
                        bar.style.width = '100%';
                        bar.classList.add('over-budget'); 
                        percentText.innerText = "계산 불가 (적자)";
                    } else {
                        let widthPercent = data.expenseRatio > 100 ? 100 : data.expenseRatio;
                        bar.style.width = widthPercent + '%';
                        percentText.innerText = data.expenseRatio + '%';

                        if (data.expenseRatio > 100) {
                            bar.classList.add('over-budget');
                        } else {
                            bar.classList.remove('over-budget');
                        }
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('데이터를 불러오지 못했습니다.');
                });
        }
        
        let trendChartInstance = null;

        function loadTrendData(month = '') {
            fetch('${pageContext.request.contextPath}/personal/getTrendData.do?month=' + month)
                .then(response => {
                    if (!response.ok) throw new Error('서버 통신 에러');
                    return response.json();
                })
                .then(data => {
                    const labels = data.map(item => item.month);
                    const incomes = data.map(item => item.totalIncome);
                    const expenses = data.map(item => item.totalExpense);

                    if (trendChartInstance != null) {
                        trendChartInstance.destroy();
                    }

                    const ctx = document.getElementById('trendChart').getContext('2d');
                    trendChartInstance = new Chart(ctx, {
                        type: 'bar',
                        data: {
                            labels: labels,
                            datasets: [
                                {
                                    type: 'line',
                                    label: '수입 추세',
                                    data: incomes,
                                    borderColor: '#36A2EB',
                                    borderWidth: 2,
                                    fill: false,
                                    tension: 0.3
                                },
                                {
                                    type: 'line', 
                                    label: '지출 추세',
                                    data: expenses,
                                    borderColor: '#FF6384',
                                    borderWidth: 2,
                                    fill: false,
                                    tension: 0.3
                                },
                                {
                                    type: 'bar',
                                    label: '수입',
                                    data: incomes,
                                    backgroundColor: 'rgba(54, 162, 235, 0.6)',
                                    borderRadius: 4
                                },
                                {
                                    type: 'bar',
                                    label: '지출',
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
                    alert('추이 데이터를 불러오지 못했습니다.');
                });
        }
        
        window.onload = function() {
            loadRatioData();
            loadTrendData();
        };
        
    </script>
</body>
</html>