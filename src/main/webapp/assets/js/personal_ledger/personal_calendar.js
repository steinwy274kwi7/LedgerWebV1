/**
 * personal_calendar.js - 개인 가계부 스크립트 (AjaxUtil 적용 완결판)
 * 의존성: FullCalendar, Bootstrap 5, window.AppConfig, AjaxUtil
 */

let currentMonth = '';
let selectedDate = '';
let calendar;

// 공통 파라미터(읽기 전용 모드일 경우 targetUserNum 추가)
function getBaseParams() {
    const params = {};
    if (AppConfig.targetUserNum) {
        params.targetUserNum = AppConfig.targetUserNum;
    }
    return params;
}

// ==========================================
// 1. FullCalendar 렌더링 및 데이터 로드
// ==========================================
document.addEventListener('DOMContentLoaded', function() {
    const calendarEl = document.getElementById('calendar');
    
    calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        locale: 'ko',
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: ''
        },
        eventContent: function(arg) {
            let cssClass = arg.event.classNames.length > 0 ? arg.event.classNames[0] : '';
            return { html: `<div class="${cssClass}">${arg.event.title}</div>` };
        },
        datesSet: function(info) {
            let d = info.view.calendar.getDate(); 
            let year = d.getFullYear();
            let month = String(d.getMonth() + 1).padStart(2, '0');
            currentMonth = year + '-' + month;
            applyFilters();
        },
        dateClick: function(info) {
            selectedDate = info.dateStr;
            document.getElementById('dateLabel').innerText = selectedDate + ' 내역';
            fetchList();
        }
    });
    calendar.render();
});

function applyFilters() {
    fetchCalendarData(currentMonth);
    fetchList();
}

function fetchCalendarData(month) {
    const params = { 
        ...getBaseParams(),
        month: month,
        type: document.querySelector('input[name="transType"]:checked').value,
        keyword: document.getElementById('keyword').value
    };
    
    AjaxUtil.request(AppConfig.contextPath + '/personal/getCalendarData.do', params, 'GET')
    .then(data => {
        calendar.removeAllEvents();
        data.forEach(item => {
            if (item.dailyIncome > 0) {
                calendar.addEvent({
                    title: '+' + item.dailyIncome.toLocaleString(),
                    start: item.date,
                    className: 'income-text',
                    backgroundColor: 'transparent',
                    borderColor: 'transparent'
                });
            }
            if (item.dailyExpense > 0) {
                calendar.addEvent({
                    title: '-' + item.dailyExpense.toLocaleString(),
                    start: item.date,
                    className: 'expense-text',
                    backgroundColor: 'transparent',
                    borderColor: 'transparent'
                });
            }
        });
    });
}

function fetchList() {
    const params = { 
        ...getBaseParams(),
        month: currentMonth,
        type: document.querySelector('input[name="transType"]:checked').value,
        keyword: document.getElementById('keyword').value
    };
    
    if (selectedDate !== '') {
        params.date = selectedDate;
    }

    AjaxUtil.request(AppConfig.contextPath + '/personal/getTransactionList.do', params, 'GET')
    .then(data => {
        const tbody = document.getElementById('listBody');
        tbody.innerHTML = '';
        
        if (data.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="py-5 text-muted">조건에 맞는 내역이 없습니다.</td></tr>';
            return;
        }
        
        data.forEach(item => {
            let isIncome = item.transType === 'I';
			let typeHtml = isIncome ? '<span class="badge bg-danger-subtle text-danger border border-danger-subtle">수입</span>' : '<span class="badge bg-primary-subtle text-primary border border-primary-subtle">지출</span>';
            let amountHtml = isIncome ? `+${item.transAmount.toLocaleString()}` : `-${item.transAmount.toLocaleString()}`;
            let amountColor = isIncome ? 'text-danger' : 'text-primary';
            
            // 읽기 전용 모드 처리
            let clickEvent = '';
            let cursorStyle = '';
            if (!AppConfig.isReadOnly) {
                const safeMemo = item.transMemo ? item.transMemo.replace(/'/g, "\\'") : '';
                clickEvent = `onclick="openTxModal('${item.transNum}', '${item.transDate}', '${item.transType}', '${item.categoryNum}', '${item.transAmount}', '${safeMemo}')"`;
                cursorStyle = "cursor: pointer; class='custom-row-hover'";
            }
            
            let tr = `<tr ${clickEvent} ${cursorStyle}>
                        <td>${item.transDate}</td>
                        <td>${typeHtml}</td>
                        <td class="fw-bold text-secondary">${item.categoryName}</td>
                        <td class="text-end pe-4 fw-bold ${amountColor}">${amountHtml}원</td>
                        <td class="text-start ps-4 text-muted">${item.transMemo || ''}</td>
                      </tr>`;
            tbody.innerHTML += tr;
        });
    });
}

function resetFilters() {
    selectedDate = '';
    document.getElementById('keyword').value = '';
    document.getElementById('typeAll').checked = true;
    document.getElementById('dateLabel').innerText = '이번 달 전체 내역';
    applyFilters();
}

// ==========================================
// 2. 내역 등록/수정 모달 관리
// ==========================================
function openTxModal(num = '', date = '', type = '', cat = '', amt = '', memo = '') {
    if (!type) {
        const mainType = document.querySelector('input[name="transType"]:checked').value;
        type = (mainType === 'ALL') ? 'E' : mainType;
    }

    document.getElementById('modalTransNum').value = num;
    document.getElementById('modalDate').value = date || new Date().toISOString().split('T')[0];
    
    // 라디오 버튼 세팅 및 이벤트 바인딩
    const typeRadios = document.querySelectorAll('input[name="modalType"]');
    typeRadios.forEach(radio => {
        if(radio.value === type) radio.checked = true;
        radio.onclick = function() { loadCategoryOptions(this.value, ''); };
    });

    document.getElementById('modalAmount').value = amt;
    document.getElementById('modalMemo').value = memo;
    document.getElementById('modalTitle').innerText = num === '' ? '내역 등록' : '내역 수정';
    document.getElementById('btnDelete').style.display = num === '' ? 'none' : 'inline-block';
    
    loadCategoryOptions(type, cat);
    
    bootstrap.Modal.getOrCreateInstance(document.getElementById('txModal')).show();
}

function closeTxModal() {
    bootstrap.Modal.getInstance(document.getElementById('txModal'))?.hide();
}

function loadCategoryOptions(type, selectedCatNum) {
    const params = { ...getBaseParams(), type: type };
    
    AjaxUtil.request(AppConfig.contextPath + '/personal/getCategoryList.do', params, 'GET')
    .then(data => {
        const select = document.getElementById('modalCategory');
        select.innerHTML = '<option value="">카테고리 선택</option>';
        data.forEach(item => {
            select.innerHTML += `<option value="${item.categoryNum}">${item.categoryName}</option>`;
        });
        if(selectedCatNum) select.value = selectedCatNum;
    });
}

function saveTransaction() {
    const num = document.getElementById('modalTransNum').value;
    const date = document.getElementById('modalDate').value;
    const type = document.querySelector('input[name="modalType"]:checked').value;
    const cat = document.getElementById('modalCategory').value;
    const amt = document.getElementById('modalAmount').value;
    const memo = document.getElementById('modalMemo').value.trim();

    if (!date) { alert("날짜를 선택하세요."); return; }
    if (new Date(date) > new Date()) { alert("미래 날짜는 등록할 수 없습니다."); return; }
    if (amt <= 0) { alert("금액은 1원 이상이어야 합니다."); return; }
    if (memo.length > 100) { alert("메모는 100자를 넘을 수 없습니다."); return; }
    if (!cat) { alert("카테고리를 선택해주세요."); return; }
    
    AjaxUtil.request(AppConfig.contextPath + '/personal/saveTransaction.do', {
        transNum: num, transDate: date, transType: type, 
        categoryNum: cat, transAmount: amt, transMemo: memo
    }).then(data => {
        if (data.success) {
            alert("저장되었습니다!");
            closeTxModal();
            applyFilters(); 
        } else {
            alert("오류: " + data.message); 
        }
    });
}

function deleteTransaction() {
    const num = document.getElementById('modalTransNum').value;
    if (!confirm('정말 이 내역을 삭제하시겠습니까?')) return;

    AjaxUtil.request(AppConfig.contextPath + '/personal/deleteTransaction.do', { transNum: num }, 'GET')
    .then(data => {
        if (data.success) {
            alert("삭제되었습니다.");
            closeTxModal();
            applyFilters(); 
        } else {
            alert("삭제 실패: " + data.message);
        }
    });
}

// ==========================================
// 3. 카테고리 관리 모달
// ==========================================
function openCategoryManageModal() {
    bootstrap.Modal.getOrCreateInstance(document.getElementById('catManageModal')).show();
    loadManageCategories();
}

function loadManageCategories() {
    const type = document.querySelector('input[name="mngCatType"]:checked').value;
    
    AjaxUtil.request(AppConfig.contextPath + '/personal/getCategoryList.do', { type: type }, 'GET')
    .then(data => {
        const listUl = document.getElementById('catManageList');
        listUl.innerHTML = '';
        data.forEach(item => {
            let btnHtml = '';
            if(item.categoryName !== '미분류') {
                btnHtml = `<button onclick="saveCategoryManage('${item.categoryNum}')" class="btn btn-sm btn-outline-secondary py-0 me-1">수정</button> 
                           <button onclick="deleteCategoryManage('${item.categoryNum}')" class="btn btn-sm btn-outline-danger py-0">삭제</button>`;
            } else {
                btnHtml = `<span class="badge bg-secondary">기본</span>`;
            }
            listUl.innerHTML += `<li class="list-group-item d-flex justify-content-between align-items-center py-2">
                                    <span class="fw-bold">${item.categoryName}</span>
                                    <div>${btnHtml}</div>
                                 </li>`;
        });
    });
}

function saveCategoryManage(catNum) {
    let name = '';
    if(catNum === '') {
        name = document.getElementById('newCatName').value.trim();
    } else {
        name = prompt("변경할 카테고리명을 입력하세요 (최대 20자)");
        if(name === null) return;
        name = name.trim();
    }
    
    const type = document.querySelector('input[name="mngCatType"]:checked').value;
    
    if(!name) { alert("카테고리명을 입력하세요."); return; }
    if(name === "미분류") { alert("'미분류'는 시스템 예약어입니다."); return; }
    if(name.length > 20) { alert("카테고리명은 20자를 넘을 수 없습니다."); return; }
    
    AjaxUtil.request(AppConfig.contextPath + '/personal/saveCategory.do', { 
        categoryNum: catNum, categoryName: name, categoryType: type 
    }).then(data => {
        if(data.success) {
            document.getElementById('newCatName').value = '';
            loadManageCategories(); 
        } else alert("오류: " + data.message);
    });
}

function deleteCategoryManage(catNum) {
    if(!confirm('삭제 시 해당 내역들은 [미분류]로 이동됩니다. 삭제하시겠습니까?')) return;
    
    const type = document.querySelector('input[name="mngCatType"]:checked').value;
    
    AjaxUtil.request(AppConfig.contextPath + '/personal/deleteCategory.do', { 
        categoryNum: catNum, categoryType: type 
    }, 'GET').then(data => {
        if(data.success) {
            alert("삭제 및 내역 이관이 완료되었습니다.");
            loadManageCategories();
            applyFilters(); 
        } else alert("삭제 실패: " + data.message);
    });
}

// ==========================================
// 4. 타 유저 검색 및 설정
// ==========================================
let searchTimeout;
function searchPublicUser() {
    clearTimeout(searchTimeout);
    const keyword = document.getElementById('searchUserInput').value.trim();
    const resultUl = document.getElementById('searchResultList');
    
    if(!keyword) { resultUl.style.display = 'none'; return; }

    searchTimeout = setTimeout(() => {
        AjaxUtil.request(AppConfig.contextPath + '/user/searchPublicUser.do', { keyword: keyword }, 'GET')
        .then(data => {
            resultUl.innerHTML = '';
            if(data.length === 0) {
                resultUl.innerHTML = '<li class="list-group-item text-center text-muted py-3">검색 결과가 없습니다.</li>';
            } else {
                data.forEach(user => {
                    const url = `${AppConfig.contextPath}/personal/calendar.do?targetUserNum=${user.userNum}&targetNickname=${encodeURIComponent(user.userNickname)}`;
                    resultUl.innerHTML += `<li class="list-group-item list-group-item-action cursor-pointer" onclick="location.href='${url}'">
                                              <strong class="text-primary">${user.userId}</strong> <small class="text-muted">(${user.userNickname})</small>
                                           </li>`;
                });
            }
            resultUl.style.display = 'block';
        });
    }, 300); // 디바운싱
}

// 화면 빈 곳 클릭 시 검색 드롭다운 닫기
document.addEventListener('click', function(e) {
    if(e.target.id !== 'searchUserInput') {
        const list = document.getElementById('searchResultList');
        if(list) list.style.display = 'none';
    }
});

function togglePublicYn() {
    const btn = document.getElementById('btnPublicToggle');
    if(!btn) return;

    const isCurrentlyPublic = btn.innerText.includes('공개 모드') && !btn.innerText.includes('비');
    const targetYn = isCurrentlyPublic ? 'N' : 'Y'; 
    const confirmMsg = targetYn === 'Y' 
        ? "내 가계부를 그룹 멤버 등 외부 사용자에게 공개하시겠습니까?" 
        : "내 가계부를 비공개로 전환하시겠습니까?";

    if (!confirm(confirmMsg)) return;

    AjaxUtil.request(AppConfig.contextPath + '/personal/togglePublic.do', { bookOpenYn: targetYn })
    .then(data => {
        if (data.success) {
            if (data.currentYn === 'Y') {
                btn.innerText = '공개 모드';
                btn.className = 'btn rounded-pill shadow-sm fw-bold px-3 btn-info text-white';
            } else {
                btn.innerText = '비공개 모드';
                btn.className = 'btn rounded-pill shadow-sm fw-bold px-3 btn-light text-secondary border';
            }
        } else {
            alert("상태 변경에 실패했습니다.");
        }
    });
}