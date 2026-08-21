/**
 * group_statistics.js - 그룹 통계 대시보드 스크립트 (AjaxUtil 적용 완결판)
 * 의존성: Chart.js, window.AppConfig, AjaxUtil
 */

let groupPieChartInstance = null;
let groupTrendChartInstance = null;

// ==========================================
// 1. 파이 차트 렌더링 (이번 달 지출 비율)
// ==========================================
function loadAllGroupsPieChart(month = '') {
    // 🌟 URL 쿼리스트링 조립과 예외 처리를 AjaxUtil이 모두 대신해 줍니다.
    AjaxUtil.request(AppConfig.contextPath + '/group/getCategoryChartData.do', { month: month }, 'GET')
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
    });
}

// ==========================================
// 2. 추이 차트 렌더링 (최근 6개월 지출 추이)
// ==========================================
function loadAllGroupsTrendChart(month = '') {
    // 🌟 마찬가지로 GET 방식으로 간결하게 호출합니다.
    AjaxUtil.request(AppConfig.contextPath + '/group/getTrendData.do', { month: month }, 'GET')
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
		                        borderColor: '#36A2EB', /* 🌟 파랑으로 변경 */
		                        borderWidth: 2, fill: false, tension: 0.3
		                    },
		                    {
		                        type: 'bar',
		                        label: '지출 금액',
		                        data: expenses,
		                        backgroundColor: 'rgba(54, 162, 235, 0.6)', /* 🌟 파랑으로 변경 */
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
    });
}

// ==========================================
// 3. 화면 로드 시 초기화
// ==========================================
window.onload = function() {
    loadAllGroupsPieChart(); 
    loadAllGroupsTrendChart();
};