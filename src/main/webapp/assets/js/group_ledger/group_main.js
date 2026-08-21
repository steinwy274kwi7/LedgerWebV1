/**
 * group_main.js - 공동 가계부 메인 스크립트 (AjaxUtil 적용 완결판)
 * 의존성: Bootstrap 5, window.AppConfig, AjaxUtil
 */

let currentData = []; 
let isClosing = false; 

// ==========================================
// 공통 모달 제어 헬퍼 함수
// ==========================================
function showModal(modalId) {
    const modalEl = document.getElementById(modalId);
    if(modalEl) bootstrap.Modal.getOrCreateInstance(modalEl).show();
}

function hideModal(modalId) {
    const modalEl = document.getElementById(modalId);
    if(modalEl) bootstrap.Modal.getInstance(modalEl)?.hide(); 
}

// ==========================================
// 1. 방 설정 관리
// ==========================================
function openSettingsModal() { showModal('settingsModal'); }

function saveGroupSettings() {
    const num = document.getElementById('settingGroupNum').value;
    const name = document.getElementById('settingGroupName').value.trim();
    const desc = document.getElementById('settingGroupDesc').value.trim();
    const openYn = document.getElementById('settingGroupOpenYn').value;
    const settleYn = document.getElementById('settingSettleUseYn').value; 

    if (!name) { alert("방 이름을 입력해 주세요."); return; }

    AjaxUtil.request(AppConfig.contextPath + '/group/updateSettings.do', {
        groupNum: num,
        groupName: name,
        groupDesc: desc,
        groupOpenYn: openYn,
        settleUseYn: settleYn
    }).then(data => {
        if (data.success) {
            alert(data.message);
            document.getElementById('displayGroupName').innerText = name;
            document.getElementById('displayGroupDesc').innerText = desc;
            document.getElementById('groupSettleUseYn').value = settleYn; 
            const previewBtn = document.getElementById('previewBtn');
            if (previewBtn) previewBtn.style.display = (settleYn === 'Y') ? 'inline-block' : 'none';
            hideModal('settingsModal');
        } else {
            alert("오류: " + data.message);
        }
    });
}

function deleteGroup() {
    if (!confirm("정말 이 공동 가계부를 삭제하시겠습니까?\n(이 작업은 되돌릴 수 없으며, 모든 멤버가 더 이상 접근할 수 없습니다.)")) return;
    
    AjaxUtil.request(AppConfig.contextPath + '/group/delete.do', { groupNum: AppConfig.groupNum })
    .then(data => {
        if (data.success) {
            alert(data.message);
            window.location.href = AppConfig.contextPath + '/group/list.do';
        } else alert("오류: " + data.message);
    });
}

// ==========================================
// 2. 멤버 및 초대 관리
// ==========================================
function openInviteModal() {
    showModal('inviteModal');
    document.getElementById('searchUserId').value = '';
}

function sendGroupInvite() {
    const userId = document.getElementById('searchUserId').value.trim();
    if (!userId) { alert("초대할 유저의 아이디를 입력해 주세요."); return; }
    
    AjaxUtil.request(AppConfig.contextPath + '/group/sendInvite.do', { 
        groupNum: AppConfig.groupNum, 
        inviteeId: userId 
    }).then(data => {
        if (data.success) { alert(data.message); hideModal('inviteModal'); }
        else alert("초대 실패: " + data.message);
    });
}

function openMemberModal() {
    showModal('memberModal');
    loadMemberList();
}

function loadMemberList() {
    // 🌟 GET 방식 호출 시 3번째 파라미터로 'GET' 명시
    AjaxUtil.request(AppConfig.contextPath + '/group/getMemberList.do', { groupNum: AppConfig.groupNum }, 'GET')
    .then(data => {
        const listArea = document.getElementById('memberListArea');
        listArea.innerHTML = '';
        data.forEach(m => {
            let li = document.createElement('li');
            li.className = "list-group-item d-flex justify-content-between align-items-center";
            let roleBadge = m.userNum === AppConfig.groupOwnerNum ? '<span class="badge bg-primary ms-1">방장</span>' : '';
            let nameHtml = `<div><strong class="mb-1 d-block">${m.userNickname} <small class="text-muted">(${m.userId})</small> ${roleBadge}</strong><small class="text-muted">가입일: ${m.joinDate}</small></div>`;
            
            let actionHtml = '';
            if (AppConfig.currentUserNum === AppConfig.groupOwnerNum && m.userNum !== AppConfig.groupOwnerNum) {
                actionHtml = `<div>
                                <button onclick="transferOwner(${m.userNum}, '${m.userNickname}')" class="btn btn-sm btn-outline-primary me-1">위임</button>
                                <button onclick="kickMember(${m.userNum})" class="btn btn-sm btn-outline-danger">강퇴</button>
                              </div>`;
            }
            li.innerHTML = nameHtml + actionHtml;
            listArea.appendChild(li);
        });
    });
}

function kickMember(targetUserNum) {
    if (!confirm("해당 멤버를 강퇴하시겠습니까?\n(미정산 잔액과 무관하게 즉시 이탈 처리됩니다.)")) return;
    
    AjaxUtil.request(AppConfig.contextPath + '/group/kickMember.do', { 
        groupNum: AppConfig.groupNum, 
        targetUserNum: targetUserNum 
    }).then(data => {
        if (data.success) { alert(data.message); loadMemberList(); }
        else alert("오류: " + data.message);
    });
}

function leaveGroup() {
    let confirmMsg = AppConfig.currentUserNum === AppConfig.groupOwnerNum ? 
        "정말 이 방에서 나가시겠습니까?\n(방장 권한은 가입일이 가장 빠른 멤버에게 위임되며, 남은 멤버가 없으면 방이 삭제됩니다.)" : 
        "정말 이 방에서 나가시겠습니까?\n(미정산 잔액과 무관하게 즉시 탈퇴 처리됩니다.)";
    if (!confirm(confirmMsg)) return;
    
    AjaxUtil.request(AppConfig.contextPath + '/group/leaveGroup.do', { groupNum: AppConfig.groupNum })
    .then(data => {
        if (data.success) { alert(data.message); window.location.href = AppConfig.contextPath + '/group/list.do'; }
        else alert("오류: " + data.message);
    });
}

function transferOwner(targetUserNum, targetNickname) {
    if (!confirm(`${targetNickname} 님에게 방장 권한을 넘겨주시겠습니까?\n(위임 즉시 일반 멤버로 전환됩니다.)`)) return;
    
    AjaxUtil.request(AppConfig.contextPath + '/group/transferOwner.do', { 
        groupNum: AppConfig.groupNum, 
        targetUserNum: targetUserNum 
    }).then(data => {
        if (data.success) {
            alert(data.message);
            AppConfig.groupOwnerNum = targetUserNum; 
            document.getElementById('inviteBtn').style.display = 'none';
            document.getElementById('settingBtn').style.display = 'none';
            document.getElementById('closePeriodBtn').style.display = 'none';
            loadMemberList(); 
        } else alert("오류: " + data.message);
    });
}

// ==========================================
// 3. 화면 렌더링 (달력 & 리스트)
// ==========================================
window.onload = () => { 
    const today = new Date();
    const yyyy = today.getFullYear();
    const mm = String(today.getMonth() + 1).padStart(2, '0');
    loadMonthData(`${yyyy}-${mm}`); 
    fetchCategoryList(); 
};

function switchView(type) {
    document.getElementById('calendarView').style.display = (type === 'calendar') ? 'block' : 'none';
    document.getElementById('listView').style.display = (type === 'list') ? 'block' : 'none';
    
    document.getElementById('btnCalendarView').classList.toggle('active', type === 'calendar');
    document.getElementById('btnListView').classList.toggle('active', type === 'list');
    
    if(type === 'calendar') renderCalendar();
    if(type === 'list') renderList();
}

function refreshCurrentView() {
    document.getElementById('calendarView').style.display === 'block' ? renderCalendar() : renderList();
}

function loadMonthData(yearMonth) {
    AjaxUtil.request(AppConfig.contextPath + '/groupLedger/getTransactions.do', { 
        groupNum: AppConfig.groupNum, 
        yearMonth: yearMonth 
    }, 'GET').then(data => {
        currentData = data; 
        document.getElementById('currentMonthLabel').innerText = yearMonth;
        refreshCurrentView();
    });
}

function renderCalendar() {
    const calendarView = document.getElementById('calendarView');
    const yearMonth = document.getElementById('currentMonthLabel').innerText; 
    if (!yearMonth) return;

    const parts = yearMonth.split('-');
    const year = parts[0];
    const month = parts[1];
    const firstDay = new Date(year, month - 1, 1).getDay(); 
    const lastDate = new Date(year, month, 0).getDate(); 
    const showClosed = document.getElementById('toggleClosedData').checked; 
    
    let html = `<div class="calendar-grid">`;
    const days = ['일', '월', '화', '수', '목', '금', '토'];
    days.forEach(day => html += `<div class="calendar-header">${day}</div>`);
    for (let i = 0; i < firstDay; i++) html += `<div class="calendar-empty"></div>`;

    for (let d = 1; d <= lastDate; d++) {
        const dateStr = `${year}-${month}-${String(d).padStart(2, '0')}`;
        const dayTransactions = currentData.filter(t => t.transDate === dateStr);
        
        let dayHtml = `<div class="calendar-day"><span class="day-number">${d}</span>`; 
        
        if (dayTransactions.length > 0) {
            let activeTotal = 0; 
            dayTransactions.forEach(t => {
                const isClosed = (t.periodStatus === 'C');
                if (!showClosed && isClosed) return; 
                if (!isClosed) activeTotal += t.transAmount;
                
                const colorStyle = isClosed ? "opacity: 0.4; color: #555;" : "color: #555;";
                const icon = isClosed ? "[마감] " : "";
                dayHtml += `<div style="font-size: 0.75rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; ${colorStyle}">
                                ${icon}[${t.userNickname}] ${t.transAmount.toLocaleString()}원
                            </div>`;
            });
            if (activeTotal > 0) {
                dayHtml += `<div class="mt-1 pt-1 border-top text-primary fw-bold" style="font-size: 0.8rem;">총 ${activeTotal.toLocaleString()}원</div>`;
            }
        }
        dayHtml += `</div>`;
        html += dayHtml;
    }
    html += `</div>`; 
    calendarView.innerHTML = `<h4 id="currentMonthLabel" class="text-center mb-3 fw-bold text-primary">${yearMonth}</h4>` + html;
}

function renderList() {
    const listView = document.getElementById('listView');
    const showClosed = document.getElementById('toggleClosedData').checked;
    let html = `<ul class="list-group shadow-sm">`;
    let visibleCount = 0; 

    currentData.forEach(t => {
        const isClosed = (t.periodStatus === 'C');
        if (!showClosed && isClosed) return;
        visibleCount++;
        
        const itemClass = isClosed ? "list-group-item bg-light text-muted" : "list-group-item";
        const badgeHtml = isClosed ? `<span class="badge bg-secondary ms-2">정산완료</span>` : "";
        
        let actionButtons = '';
        if (!isClosed && (t.userNum === AppConfig.currentUserNum || AppConfig.currentUserNum === AppConfig.groupOwnerNum)) {
            const safeMemo = t.transMemo ? t.transMemo.replace(/'/g, "\\'") : '';
            actionButtons = `
                <div class="mt-2">
                    <button onclick="openEditExpenseModal(${t.gtransNum}, '${t.transDate}', '${t.categoryName}', ${t.transAmount}, '${safeMemo}')" class="btn btn-sm btn-warning fw-bold me-1">수정</button>
                    <button onclick="deleteTransaction(${t.gtransNum})" class="btn btn-sm btn-danger fw-bold">삭제</button>
                </div>`;
        }

        html += `
            <li class="${itemClass} d-flex justify-content-between align-items-center p-3">
                <div>
                    <h6 class="mb-1 fw-bold">${t.categoryName} - ${t.transMemo || '메모 없음'} ${badgeHtml}</h6>
                    <small class="text-secondary">${t.transDate} | 결제자: ${t.userNickname}</small>
                    ${actionButtons}
                </div>
        	    <div class="fs-5 fw-bold ${isClosed ? 'text-secondary' : 'text-primary'}">
                    ${t.transAmount.toLocaleString()} 원
                </div>
            </li>`;
    });
    html += `</ul>`;
    
    listView.innerHTML = visibleCount === 0 ? `<div class="text-center p-5 text-muted bg-white border rounded shadow-sm">이번 달 지출 내역이 없습니다.</div>` : html;
}

// ==========================================
// 4. 지출 등록, 수정, 삭제
// ==========================================
function openExpenseModal() {
    showModal('expenseModal');
    const todayStr = new Date(new Date().getTime() - new Date().getTimezoneOffset() * 60000).toISOString().split("T")[0];
    const dateInput = document.getElementById('expDate');
    dateInput.max = todayStr; 
    dateInput.value = todayStr; 
    document.getElementById('expAmount').value = '';
    document.getElementById('expMemo').value = '';
}

function closeExpenseModal() { hideModal('expenseModal'); }

function saveExpense() {
    const date = document.getElementById('expDate').value;
    const category = document.getElementById('expCategory').value;
    const amount = parseInt(document.getElementById('expAmount').value);
    const memo = document.getElementById('expMemo').value.trim();

    if (!date) { alert("결제 날짜를 선택해주세요."); return; }
    if (!amount || amount <= 0) { alert("결제 금액은 1원 이상이어야 합니다."); return; }
    if (memo.length > 100) { alert("메모는 100자를 초과할 수 없습니다."); return; }

    AjaxUtil.request(AppConfig.contextPath + '/groupLedger/insert.do', { 
        groupNum: AppConfig.groupNum, 
        transDate: date, 
        gcategoryNum: category, 
        transAmount: amount, 
        transMemo: memo 
    }).then(data => {
        if (data.success) { 
            alert(data.message); 
            hideModal('expenseModal'); 
            loadMonthData(document.getElementById('currentMonthLabel').innerText); 
        } else {
            alert("오류: " + data.message);
        }
    });
}

function openEditExpenseModal(transNum, date, catName, amount, memo) {
    showModal('editExpenseModal');
    const todayStr = new Date(new Date().getTime() - new Date().getTimezoneOffset() * 60000).toISOString().split("T")[0];
    document.getElementById('editExpDate').max = todayStr; 
    document.getElementById('editTransNum').value = transNum;
    document.getElementById('editExpDate').value = date;
    document.getElementById('editExpAmount').value = amount;
    document.getElementById('editExpMemo').value = memo;
    
    const select = document.getElementById('editExpCategory');
    for(let i=0; i<select.options.length; i++) {
        if(select.options[i].text === catName) { select.selectedIndex = i; break; }
    }
}

function saveEditExpense() {
    const transNum = document.getElementById('editTransNum').value;
    const date = document.getElementById('editExpDate').value;
    const selectBox = document.getElementById('editExpCategory');
    const categoryNum = selectBox.value;
    const categoryName = selectBox.options[selectBox.selectedIndex].text; 
    const amount = parseInt(document.getElementById('editExpAmount').value);
    const memo = document.getElementById('editExpMemo').value.trim();

    if (!date) { alert("날짜를 선택해주세요."); return; }
    if (!amount || amount <= 0) { alert("결제 금액은 1원 이상이어야 합니다."); return; }

    AjaxUtil.request(AppConfig.contextPath + '/groupLedger/editTransaction.do', { 
        gtransNum: transNum, 
        groupOwnerNum: AppConfig.groupOwnerNum, 
        transDate: date, 
        gcategoryNum: categoryNum, 
        categoryName: categoryName, 
        transAmount: amount, 
        transMemo: memo 
    }).then(data => {
        if (data.success) { 
            alert(data.message); 
            hideModal('editExpenseModal'); 
            loadMonthData(document.getElementById('currentMonthLabel').innerText); 
        } else {
            alert("오류: " + data.message);
        }
    });
}

function deleteTransaction(transNum) {
    if(!confirm("이 지출 내역을 정말 삭제하시겠습니까? (삭제 로그가 기록됩니다)")) return;
    
    AjaxUtil.request(AppConfig.contextPath + '/groupLedger/removeTransaction.do', { 
        gtransNum: transNum, 
        groupOwnerNum: AppConfig.groupOwnerNum 
    }).then(data => {
        if (data.success) { 
            alert(data.message); 
            loadMonthData(document.getElementById('currentMonthLabel').innerText); 
        } else {
            alert("오류: " + data.message);
        }
    });
}

// ==========================================
// 5. 카테고리 관리
// ==========================================
function openCategoryModal() {
    showModal('categoryManageModal');
    document.getElementById('newCategoryName').value = '';
}

function fetchCategoryList() {
    AjaxUtil.request(AppConfig.contextPath + '/groupLedger/getCategoryList.do', { groupNum: AppConfig.groupNum }, 'GET')
    .then(data => {
        const select = document.getElementById('expCategory');
        const editSelect = document.getElementById('editExpCategory');
        const listArea = document.getElementById('categoryListArea');
        select.innerHTML = ''; editSelect.innerHTML = ''; listArea.innerHTML = '';

        data.forEach(c => {
            select.add(new Option(c.categoryName, c.gcategoryNum));
            editSelect.add(new Option(c.categoryName, c.gcategoryNum));

            let li = document.createElement('li');
            li.className = "list-group-item d-flex justify-content-between align-items-center";
            if(c.categoryName === '미분류') {
                li.innerHTML = `<span><strong class="text-dark">${c.categoryName}</strong> <small class="text-muted">(기본)</small></span>`;
            } else {
                li.innerHTML = `<span>${c.categoryName}</span>
                                <div>
                                    <button onclick="editCategory(${c.gcategoryNum}, '${c.categoryName}')" class="btn btn-sm btn-outline-warning text-dark fw-bold me-1">수정</button>
                                    <button onclick="deleteCategory(${c.gcategoryNum}, '${c.categoryName}')" class="btn btn-sm btn-outline-danger fw-bold">삭제</button>
                                </div>`;
            }
            listArea.appendChild(li);
        });
    });
}

function addCategory() {
    const name = document.getElementById('newCategoryName').value.trim();
    if(!name) { alert("카테고리명을 입력해주세요."); return; }
    if(name === '미분류') { alert("시스템 예약어인 '미분류'는 사용할 수 없습니다."); return; }

    AjaxUtil.request(AppConfig.contextPath + '/groupLedger/addCategory.do', { 
        groupNum: AppConfig.groupNum, 
        categoryName: name 
    }).then(data => {
        if (data.success) { document.getElementById('newCategoryName').value = ''; fetchCategoryList(); } 
        else alert("오류: " + data.message);
    });
}

function editCategory(catNum, oldName) {
    const newName = prompt("새로운 카테고리명을 입력하세요 (최대 20자)", oldName);
    if(!newName || newName.trim() === "" || newName.trim() === oldName) return;
    if(newName.trim() === '미분류') { alert("시스템 예약어인 '미분류'는 사용할 수 없습니다."); return; }

    AjaxUtil.request(AppConfig.contextPath + '/groupLedger/editCategory.do', { 
        groupNum: AppConfig.groupNum, 
        categoryNum: catNum, 
        categoryName: newName.trim() 
    }).then(data => {
        if (data.success) { fetchCategoryList(); loadMonthData(document.getElementById('currentMonthLabel').innerText); } 
        else alert("오류: " + data.message);
    });
}

function deleteCategory(catNum, catName) {
    if(!confirm(`정말 '${catName}' 카테고리를 삭제하시겠습니까?\n이 카테고리로 등록된 지출 내역은 '미분류'로 자동 이관됩니다.`)) return;
    
    AjaxUtil.request(AppConfig.contextPath + '/groupLedger/removeCategory.do', { 
        groupNum: AppConfig.groupNum, 
        categoryNum: catNum, 
        categoryName: catName 
    }).then(data => {
        if (data.success) { alert(data.message); fetchCategoryList(); loadMonthData(document.getElementById('currentMonthLabel').innerText); } 
        else alert("오류: " + data.message);
    });
}

// ==========================================
// 6. 무결성 변경 이력 & 과거 보관함 & 실시간 정산
// ==========================================
function openLogModal() {
    showModal('logModal');
    AjaxUtil.request(AppConfig.contextPath + '/groupLedger/getLogs.do', { groupNum: AppConfig.groupNum }, 'GET')
    .then(data => {
        const area = document.getElementById('logListArea');
        if (data.length === 0) {
            area.innerHTML = '<li class="list-group-item text-center p-4 text-muted border-0">아직 기록된 변경 이력이 없습니다.</li>'; return;
        }
        let html = '';
        data.forEach(log => {
            let logMsg = log.actionType === 'U' 
                ? `[수정] <b class="text-warning">${log.beforeCategory} (${log.beforeAmount.toLocaleString()}원)</b> ➔ <b class="text-success">${log.afterCategory} (${log.afterAmount.toLocaleString()}원)</b>` 
                : `[삭제] <b class="text-danger">${log.beforeCategory} (${log.beforeAmount.toLocaleString()}원)</b> 내역 영구 삭제`;
            html += `<li class="list-group-item py-3"><div class="small text-muted mb-1">${log.createdAtStr} | 행위자: <b>${log.userNickname}</b> (메모: ${log.transMemo || '메모 없음'})</div><div class="fs-6">${logMsg}</div></li>`;
        });
        area.innerHTML = html;
    });
}

function openArchiveModal() {
    showModal('archiveModal');
    document.getElementById('archiveDetailArea').style.display = 'none';
    document.getElementById('archivePeriodList').style.display = 'block';
    
    AjaxUtil.request(AppConfig.contextPath + '/groupLedger/getClosedPeriods.do', { groupNum: AppConfig.groupNum }, 'GET')
    .then(data => {
        let html = '';
        if(data.length === 0) html = '<div class="text-center p-5 text-muted border rounded bg-light">보관된 과거 정산 내역이 없습니다.</div>';
        else {
            data.forEach(p => {
                html += `<button onclick="loadArchiveDetails(${p.periodNum}, ${p.periodSeq})" class="btn btn-outline-secondary text-start mb-2 p-3 shadow-sm border-0 w-100">
                            <strong class="d-block fs-5 text-dark">제 ${p.periodSeq}회차 정산 기록</strong> 
                            <small class="text-muted">${p.startDate} ~ ${p.endDate}</small>
                         </button>`;
            });
        }
        document.getElementById('archivePeriodList').innerHTML = html;
    });
}

function backToPeriodList() {
    document.getElementById('archiveDetailArea').style.display = 'none';
    document.getElementById('archivePeriodList').style.display = 'block';
}

function loadArchiveDetails(periodNum, periodSeq) {
    document.getElementById('archivePeriodList').style.display = 'none';
    document.getElementById('archiveDetailArea').style.display = 'block';
    document.getElementById('detailPeriodTitle').innerText = `제 ${periodSeq}회차 상세 내역`;
    
    AjaxUtil.request(AppConfig.contextPath + '/groupLedger/getArchiveDetails.do', { periodNum: periodNum }, 'GET')
    .then(data => {
        const isSettleVisible = document.getElementById('groupSettleUseYn').value;
        let snapHtml = '';
        if (isSettleVisible === 'N') {
            snapHtml = '<li class="text-muted fst-italic py-2">🔒 방 설정에 의해 정산 결과가 비공개 처리되었습니다.</li>';
        } else {
            if(!data.snapshots || data.snapshots.length === 0) snapHtml = '<li class="text-muted py-2">정산할 금액이 없습니다 (모두 0원)</li>';
            else {
                data.snapshots.forEach(s => {
                    snapHtml += `<li class="py-2 border-bottom border-secondary-subtle fs-5">
                                    <b class="text-danger">${s.payerNickname}</b> 님이 <b class="text-success">${s.receiverNickname}</b> 님에게 
                                    <strong class="text-dark">${s.settleAmount.toLocaleString()}원</strong> 송금
                                 </li>`;
                });
            }
        }
        document.getElementById('snapshotList').innerHTML = snapHtml;
        
        let transHtml = '';
        if(!data.transactions || data.transactions.length === 0) transHtml = '<li class="list-group-item text-center p-4 text-muted">지출 내역이 없습니다.</li>';
        else {
            data.transactions.forEach(t => {
                transHtml += `<li class="list-group-item d-flex justify-content-between align-items-center bg-light opacity-75">
                                <div><strong class="d-block text-secondary">${t.categoryName} - ${t.transMemo || '메모 없음'}</strong><small class="text-muted">${t.transDate} | 결제자: ${t.userNickname}</small></div>
                                <div class="fw-bold text-secondary">${t.transAmount.toLocaleString()} 원</div>
                              </li>`;
            });
        }
        document.getElementById('archiveTransactionList').innerHTML = transHtml;
    });
}

function openPreviewModal() {
    showModal('previewModal');
    AjaxUtil.request(AppConfig.contextPath + '/settlement/preview.do', { groupNum: AppConfig.groupNum }, 'GET')
    .then(data => {
        const listArea = document.getElementById('previewListArea');
        if (data.length === 0) { listArea.innerHTML = '<li class="list-group-item text-center p-4 text-muted border-0">정산할 내역이 없습니다 (모두 0원).</li>'; return; }
        
        let html = '';
        data.forEach(s => {
            html += `<li class="list-group-item py-3 d-flex justify-content-between align-items-center fs-5">
                        <div><b class="text-danger">${s.payerNickname}</b> ➔ <b class="text-success">${s.receiverNickname}</b></div>
                        <strong class="text-dark">${s.settleAmount.toLocaleString()} 원</strong>
                     </li>`;
        });
        listArea.innerHTML = html;
    });
}

function closeLedgerPeriod() {
    if (isClosing) return; 
    if (!confirm("정말 현재 장부를 마감하시겠습니까?\n\n- 모든 지출은 과거 기록으로 보관됩니다.\n- 정산 결과가 영구 저장되며 장부 잔액이 0원으로 초기화됩니다.")) return;
    
    isClosing = true; 
    AjaxUtil.request(AppConfig.contextPath + '/settlement/closePeriod.do', { 
        groupNum: AppConfig.groupNum, 
        groupOwnerNum: AppConfig.groupOwnerNum 
    }).then(data => {
        if (data.success) { alert(data.message); window.location.reload(); } 
        else { alert("마감 실패: " + data.message); isClosing = false; }
    }).catch(err => { 
        // 🌟 마감 등 크리티컬한 에러 시 플래그 복구를 위해 catch 추가
        isClosing = false; 
    });
}