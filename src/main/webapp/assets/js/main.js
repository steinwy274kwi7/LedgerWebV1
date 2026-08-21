/**
 * main.js - 가계부 메인 대시보드 스크립트
 * 의존성: Chart.js, Bootstrap 5, window.AppConfig, AjaxUtil
 */

let myChartInstance = null;

// ==========================================
// 1. 초대 알림 관리
// ==========================================
function openNotificationModal() {
    const listUl = document.getElementById('inviteList');
    listUl.innerHTML = '<li class="list-group-item text-center p-4 text-muted border-0">로딩 중...</li>';
    
    bootstrap.Modal.getOrCreateInstance(document.getElementById('inviteModal')).show();

    // 🌟 AjaxUtil 통신 적용
    AjaxUtil.request(AppConfig.contextPath + '/group/getInvitations.do', {}, 'GET')
    .then(data => {
        listUl.innerHTML = ''; 

        if (data.length === 0) {
            listUl.innerHTML = '<li class="list-group-item text-center p-5 text-muted border-0 bg-light">도착한 초대장이 없습니다.</li>';
            document.getElementById('inviteBadge').style.display = 'none'; // 알림 뱃지 숨김
            return;
        }

        // 알림 뱃지 표시
        document.getElementById('inviteBadge').style.display = 'block';

        data.forEach(item => {
            let li = document.createElement('li');
            li.className = "list-group-item p-3 border-bottom";
            li.innerHTML = `
                <p class="mb-2 fs-6 text-dark">
                    <strong class="text-primary">${item.inviterName}</strong>님이 <strong>${item.groupName}</strong>에 초대했습니다.
                </p>
                <div class="d-flex gap-2">
                    <button onclick="respondInvite(${item.inviteNum}, 'A')" class="btn btn-sm btn-success fw-bold flex-fill">수락</button>
                    <button onclick="respondInvite(${item.inviteNum}, 'R')" class="btn btn-sm btn-danger fw-bold flex-fill">거절</button>
                </div>
            `;
            listUl.appendChild(li); 
        });
    })
    .catch(error => {
        listUl.innerHTML = '<li class="list-group-item text-center p-4 text-danger border-0">데이터를 불러올 수 없습니다.</li>';
    });
}

function closeNotificationModal() {
    bootstrap.Modal.getInstance(document.getElementById('inviteModal'))?.hide();
}

function respondInvite(inviteNum, status) {
    const actionName = (status === 'A') ? '수락' : '거절';
    if (!confirm(`정말 이 초대를 ${actionName}하시겠습니까?`)) return; 

    // 🌟 AjaxUtil 통신 적용 (POST)
    AjaxUtil.request(AppConfig.contextPath + '/group/respondInvite.do', {
        inviteNum: inviteNum,
        status: status
    })
    .then(data => {
        if (data.success) {
            alert(`초대를 ${actionName}했습니다.`);
            openNotificationModal(); // 모달 내용 새로고침
        } else {
            alert('처리에 실패했습니다.');
        }
    });
}

// ==========================================
// 2. 파이 차트 렌더링
// ==========================================
function loadChartData(type, month = '') {
    AjaxUtil.request(AppConfig.contextPath + '/personal/getChartData.do', { type: type, month: month }, 'GET')
    .then(data => {
        if (data.length === 0) {
            // 차트를 비우고 알림 띄우기
            if (myChartInstance != null) myChartInstance.destroy();
            alert('해당 달의 내역이 없습니다!');
            return;
        }

        const labels = data.map(item => item.categoryName); 
        const amounts = data.map(item => item.totalAmount); 
        
        // 차트 색상 팔레트 (다양한 카테고리를 위한 배열)
        const bgColors = [
            '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF',
            '#FF9F40', '#C9CBCF', '#8AC926', '#E636EB', '#56FFCE'
        ];

        if (myChartInstance != null) {
            myChartInstance.destroy();
        }

        const ctx = document.getElementById('myPieChart').getContext('2d');
        myChartInstance = new Chart(ctx, {
            type: 'pie', 
            data: {
                labels: labels, 
                datasets: [{
                    data: amounts, 
                    backgroundColor: bgColors.slice(0, data.length),
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false, // 컨테이너에 맞게 유연하게 조정
                plugins: {
                    legend: { position: 'bottom' } 
                }
            }
        });
    });
}

// ==========================================
// 3. 화면 로드 초기화
// ==========================================
window.onload = function() {
    loadChartData('E'); // 초기에는 '지출(E)' 데이터 로드
};