/**
 * groupList.js - 나의 공동 가계부 목록 스크립트
 * 의존성: Bootstrap 5, window.AppConfig, AjaxUtil
 */

// 모달 열기
function openSearchModal() {
    const modalEl = document.getElementById('searchModal');
    // 입력창 초기화
    document.getElementById('searchKeyword').value = '';
    document.getElementById('searchResultArea').innerHTML = '<li class="list-group-item text-center text-muted py-4 border-0">검색어를 입력해 주세요.</li>';
    
    // 모달 띄우기
    bootstrap.Modal.getOrCreateInstance(modalEl).show();
}

// 공개 가계부 검색 (AjaxUtil 적용)
function searchGroups() {
    const keyword = document.getElementById('searchKeyword').value.trim();
    if (!keyword) {
        alert("검색어를 입력해 주세요.");
        return;
    }

    // 🌟 AjaxUtil을 이용한 GET 요청 (파라미터 조립을 알아서 해줌)
    AjaxUtil.request(AppConfig.contextPath + '/group/searchPublic.do', { keyword: keyword }, 'GET')
    .then(data => {
        const resultArea = document.getElementById('searchResultArea');
        resultArea.innerHTML = '';
        
        if(data.length === 0) {
            resultArea.innerHTML = '<li class="list-group-item text-center text-muted py-4 border-0 bg-light rounded">검색 결과가 없습니다.</li>';
            return;
        }
        
        data.forEach(g => {
            // 설명이 null 이거나 문자열 'null'일 경우 예외 처리
            let desc = (g.groupDesc === 'null' || !g.groupDesc) ? '설명이 없습니다.' : g.groupDesc;
            
            let li = document.createElement('li');
            li.className = "list-group-item d-flex justify-content-between align-items-center py-3";
            
            li.innerHTML = `
                <div class="w-75">
                    <strong class="d-block text-truncate text-dark mb-1">\${g.groupName}</strong>
                    <small class="text-muted d-block text-truncate">\${desc}</small>
                </div>
                <a href="\${AppConfig.contextPath}/group/ledger.do?groupNum=\${g.groupNum}" class="btn btn-sm btn-outline-info fw-bold text-nowrap rounded-pill px-3">
                    구경하기
                </a>
            `;
            resultArea.appendChild(li);
        });
    });
}