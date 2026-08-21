/**
 * statistics.js - 개인 가계부 통계 대시보드 스크립트
 * 의존성: Chart.js, Bootstrap 5, window.AppConfig, AjaxUtil
 */

let trendChartInstance = null;

// ==========================================
// 1. 예산 현황 (비율) 데이터 로드
// ==========================================
function loadRatioData(month = '') {
    // 🌟 AjaxUtil을 이용한 GET 통신
    AjaxUtil.request(AppConfig.contextPath + '/personal/getRatioData.do', { month: month }, 'GET')
    .then(data => {
        // 1. 금액 및 메시지 세팅
        document.getElementById('uiIncome').innerText = data.totalIncome.toLocaleString();
        document.getElementById('uiExpense').innerText = data.totalExpense.toLocaleString();
        document.getElementById('uiMessage').innerText = data.statusMessage;
        
        const bar = document.getElementById('uiProgressBar');
        const percentText = document.getElementById('uiPercent');

        // 프로그레스 바 기본 상태(안전: 초록색)로 초기화
        bar.classList.remove('bg-danger');
        bar.classList.add('bg-success');

        // 2. 적자 또는 정상 비율 계산
        if (data.expenseRatio === -1) {
            bar.style.width = '100%';
            bar.classList.replace('bg-success', 'bg-danger'); // 부트스트랩 빨간색으로 변경
            percentText.innerText = "계산 불가 (적자)";
            percentText.classList.add('text-danger');
        } else {
            let widthPercent = data.expenseRatio > 100 ? 100 : data.expenseRatio;
            bar.style.width = widthPercent + '%';
            percentText.innerText = data.expenseRatio + '%';
            percentText.classList.remove('text-danger');

            // 100% 초과 시 경고(빨간색)로 변경
            if (data.expenseRatio > 100) {
                bar.classList.replace('bg-success', 'bg-danger');
                percentText.classList.add('text-danger');
            }
        }
    });
}

// ==========================================
// 2. 최근 6개월 추이 차트 데이터 로드
// ==========================================
function loadTrendData(month = '') {
    AjaxUtil.request(AppConfig.contextPath + '/personal/getTrendData.do', { month: month }, 'GET')
    .then(data => {
        const labels = data.map(item => item.month);
        const incomes = data.map(item => item.totalIncome);
        const expenses = data.map(item => item.totalExpense);

        // 기존 차트가 있다면 파괴 후 다시 그림
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
						        borderColor: '#FF6384', /* 🌟 빨강으로 변경 */
						        borderWidth: 2, fill: false, tension: 0.3
						    },
						    {
						        type: 'line', 
						        label: '지출 추세',
						        data: expenses,
						        borderColor: '#36A2EB', /* 🌟 파랑으로 변경 */
						        borderWidth: 2, fill: false, tension: 0.3
						    },
						    {
						        type: 'bar',
						        label: '수입',
						        data: incomes,
						        backgroundColor: 'rgba(255, 99, 132, 0.6)', /* 🌟 빨강으로 변경 */
						        borderRadius: 4
						    },
						    {
						        type: 'bar',
						        label: '지출',
						        data: expenses,
						        backgroundColor: 'rgba(54, 162, 235, 0.6)', /* 🌟 파랑으로 변경 */
						        borderRadius: 4
						    }
						]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false, // 컨테이너 크기에 맞춰 높이가 유연하게 늘어나도록 설정
                scales: {
                    y: { beginAtZero: true }
                },
                plugins: {
                    legend: { position: 'bottom' }
                }
            }
        });
    });
}

// ==========================================
// 3. 화면 초기화
// ==========================================
window.onload = function() {
    loadRatioData();
    loadTrendData();
};