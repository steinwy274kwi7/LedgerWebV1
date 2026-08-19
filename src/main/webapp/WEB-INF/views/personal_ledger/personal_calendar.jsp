<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>개인 가계부 - 달력 및 내역</title>
    <script src='https://cdn.jsdelivr.net/npm/fullcalendar@6.1.15/index.global.min.js'></script>
    <style>
        .ledger-container { width: 900px; margin: 30px auto; font-family: sans-serif; }
        #calendar { margin-bottom: 30px; background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }
        .income-text { color: #36A2EB; font-weight: bold; font-size: 0.85em; text-align: center; margin-top: 2px; }
        .expense-text { color: #FF6384; font-weight: bold; font-size: 0.85em; text-align: center; margin-top: 2px; }
        .filter-box { padding: 15px; background: #f8f9fa; border: 1px solid #ddd; border-radius: 8px; margin-bottom: 20px; display: flex; align-items: center; gap: 15px; }
        .filter-box input[type="text"] { padding: 5px; border: 1px solid #ccc; border-radius: 4px; }
        .filter-box button { padding: 6px 12px; cursor: pointer; border: none; background: #555; color: white; border-radius: 4px; }
        .fc-daygrid-day { cursor: pointer; }
        .fc-daygrid-day:hover { background-color: #f0f8ff !important; }
        table { width: 100%; border-collapse: collapse; text-align: center; background: #fff; }
        th, td { padding: 12px; border: 1px solid #ddd; }
        th { background: #f4f4f4; }
    </style>
</head>
<body>
    <div class="ledger-container">
    
    	<!-- 타이틀 및 설정 뱃지 영역 -->
		<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
		    <h2 style="margin: 0; color: #333;">개인 가계부</h2>
		    
		    <button id="btnPublicToggle" onclick="togglePublicYn()" 
		            style="padding: 6px 15px; border-radius: 20px; font-weight: bold; cursor: pointer; border: none; transition: background-color 0.3s;
		                   ${loginUser.bookOpenYn == 'Y' ? 'background-color: #17a2b8; color: white;' : 'background-color: #e2e3e5; color: #555;'}">
		        ${loginUser.bookOpenYn == 'Y' ? '🔓 공개 모드' : '🔒 비공개 모드'}
		    </button>
		</div>
		
        <div id='calendar'></div>

        <div class="filter-box">
            <label><input type="radio" name="transType" value="ALL" checked onclick="applyFilters()"> 전체</label>
            <label><input type="radio" name="transType" value="I" onclick="applyFilters()"> 수입</label>
            <label><input type="radio" name="transType" value="E" onclick="applyFilters()"> 지출</label>
            
            <input type="text" id="keyword" placeholder="메모 내용 검색" onkeyup="if(event.keyCode==13) applyFilters()">
            <button onclick="applyFilters()">검색</button>
            <button onclick="resetFilters()" style="background:#007BFF;">이번 달 전체보기</button>
            <button onclick="openModal()" style="background:#28a745; color:white; margin-left:10px;">+ 내역 추가</button>
            <button onclick="openCategoryManage()" style="background:#6c757d; color:white; margin-left:5px;">카테고리 관리</button>
            
            <span id="dateLabel" style="margin-left:auto; font-weight:bold; color:#333;">이번 달 전체 내역</span>
        </div>

        <table>
            <thead>
                <tr>
                    <th>날짜</th>
                    <th>분류</th>
                    <th>카테고리</th>
                    <th>금액</th>
                    <th>메모</th>
                </tr>
            </thead>
            <tbody id="listBody">
                
            </tbody>
        </table>
    </div>

    <div id="txModal" style="display:none; position:fixed; top:20%; left:50%; transform:translate(-50%, 0); background:#fff; padding:20px; border:1px solid #ccc; box-shadow:0 5px 15px rgba(0,0,0,0.3); z-index:1000;">
        <h3 id="modalTitle">내역 등록</h3>
        <input type="hidden" id="modalTransNum">
        
        <p>날짜: <input type="date" id="modalDate"></p>
        <p>분류: 
            <label><input type="radio" name="modalType" value="I"> 수입</label>
            <label><input type="radio" name="modalType" value="E" checked> 지출</label>
        </p>
        <p>카테고리: 
            <select id="modalCategory">
                <option value="">카테고리 선택</option>
            </select>
        </p>
        <p>금액: <input type="number" id="modalAmount" placeholder="0"></p>
        <p>메모: <input type="text" id="modalMemo" maxlength="100" placeholder="최대 100자"></p>
        
        <button onclick="saveTransaction()">저장</button>
        <button id="btnDelete" onclick="deleteTransaction()" style="background:#dc3545; color:white; display:none;">삭제</button>
        <button onclick="closeModal()">취소</button>
    </div>

	<div id="catManageModal" style="display:none; position:fixed; top:15%; left:50%; transform:translate(-50%, 0); background:#fff; padding:20px; border:1px solid #ccc; width:350px; box-shadow:0 5px 15px rgba(0,0,0,0.3); z-index:1001;">
	    <h3>카테고리 관리</h3>
	    <div style="margin-bottom:15px;">
	        <label><input type="radio" name="mngCatType" value="I" onclick="loadManageCategories()"> 수입</label>
	        <label><input type="radio" name="mngCatType" value="E" checked onclick="loadManageCategories()"> 지출</label>
	    </div>
	    
	    <ul id="catManageList" style="list-style:none; padding:0; margin-bottom:15px; max-height:200px; overflow-y:auto;"></ul>
	    
	    <div style="display:flex; gap:5px;">
	        <input type="text" id="newCatName" placeholder="새 카테고리명 (20자)" maxlength="20" style="flex:1;">
	        <button onclick="saveCategoryManage('')" style="background:#28a745; color:white; border:none; padding:5px 10px; cursor:pointer;">추가</button>
	    </div>
	    
	    <div style="text-align:right; margin-top:15px;">
	        <button onclick="document.getElementById('catManageModal').style.display='none'" style="cursor:pointer; padding:5px 10px;">닫기</button>
	    </div>
	</div>

    <script>
        let currentMonth = '';
        let selectedDate = '';
        let calendar;
        
        document.addEventListener('DOMContentLoaded', function() {
            var calendarEl = document.getElementById('calendar');
            
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
                    return {
                        html: '<div class="' + cssClass + '">' + arg.event.title + '</div>'
                    };
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
            const type = document.querySelector('input[name="transType"]:checked').value;
            const keyword = document.getElementById('keyword').value;
            
            let url = '${pageContext.request.contextPath}/personal/getCalendarData.do?month=' + month + '&type=' + type + '&keyword=' + keyword;
            
            fetch(url)
                .then(res => res.json())
                .then(data => {
                    calendar.removeAllEvents();
                    data.forEach(item => {
                        if (item.dailyIncome > 0) {
                            calendar.addEvent({
                                title: '+' + item.dailyIncome.toLocaleString(),
                                start: item.date,
                                className: 'income-text',
                                backgroundColor: 'transparent',
                                borderColor: 'transparent', 
                                textColor: '#36A2EB'
                            });
                        }
                        if (item.dailyExpense > 0) {
                            calendar.addEvent({
                                title: '-' + item.dailyExpense.toLocaleString(),
                                start: item.date,
                                className: 'expense-text',
                                backgroundColor: 'transparent',
                                borderColor: 'transparent', 
                                textColor: '#FF6384'
                            });
                        }
                    });
                })
                .catch(err => console.error('달력 데이터 로드 실패:', err));
        }

        function fetchList() {
            const type = document.querySelector('input[name="transType"]:checked').value;
            const keyword = document.getElementById('keyword').value;
            
            let url = '${pageContext.request.contextPath}/personal/getTransactionList.do?month=' + currentMonth + '&type=' + type + '&keyword=' + keyword;
            if (selectedDate !== '') {
                url += '&date=' + selectedDate;
            }

            fetch(url)
                .then(res => res.json())
                .then(data => {
                    const tbody = document.getElementById('listBody');
                    tbody.innerHTML = '';
                    if (data.length === 0) {
                        tbody.innerHTML = '<tr><td colspan="5" style="padding:30px; color:#999;">조건에 맞는 내역이 없습니다.</td></tr>';
                        return;
                    }
                    data.forEach(item => {
                        let isIncome = item.transType === 'I';
                        let typeHtml = isIncome ? '<span style="color:#36A2EB;font-weight:bold;">수입</span>' : '<span style="color:#FF6384;font-weight:bold;">지출</span>';
                        let amountHtml = isIncome ? '+' + item.transAmount.toLocaleString() : '-' + item.transAmount.toLocaleString();
                        
                        let tr = '<tr onclick="openModal(\'' + item.transNum + '\', \'' + item.transDate + '\', \'' + item.transType + '\', \'' + item.categoryNum + '\', \'' + item.transAmount + '\', \'' + (item.transMemo || '') + '\')" style="cursor:pointer;">' +
                            '<td>' + item.transDate + '</td>' +
                            '<td>' + typeHtml + '</td>' +
                            '<td>' + item.categoryName + '</td>' +
                            '<td style="text-align:right; padding-right:20px;">' + amountHtml + '원</td>' +
                            '<td style="text-align:left; padding-left:20px;">' + (item.transMemo || '') + '</td>' +
                            '</tr>';
                                                
                        tbody.innerHTML += tr;
                    });
                })
                .catch(err => console.error('리스트 데이터 로드 실패:', err));
        }

        function resetFilters() {
            selectedDate = '';
            document.getElementById('keyword').value = '';
            document.querySelector('input[name="transType"][value="ALL"]').checked = true;
            document.getElementById('dateLabel').innerText = '이번 달 전체 내역';
            
            applyFilters();
        }
        
        function openModal(num = '', date = '', type = '', cat = '', amt = '', memo = '') {
            if (!type) {
                const mainType = document.querySelector('input[name="transType"]:checked').value;
                type = (mainType === 'ALL') ? 'E' : mainType;
            }

            document.getElementById('modalTransNum').value = num;
            document.getElementById('modalDate').value = date || new Date().toISOString().split('T')[0];
            
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
            
            document.getElementById('txModal').style.display = 'block';
        }

        function loadCategoryOptions(type, selectedCatNum) {
            fetch('${pageContext.request.contextPath}/personal/getCategoryList.do?type=' + type)
                .then(res => res.json())
                .then(data => {
                    const select = document.getElementById('modalCategory');
                    select.innerHTML = '<option value="">카테고리 선택</option>';
                    data.forEach(item => {
                        select.innerHTML += '<option value="' + item.categoryNum + '">' + item.categoryName + '</option>';
                    });
                    if(selectedCatNum) select.value = selectedCatNum;
                });
        }
        
        function closeModal() { document.getElementById('txModal').style.display = 'none'; }

        function saveTransaction() {
            const num = document.getElementById('modalTransNum').value;
            const date = document.getElementById('modalDate').value;
            const type = document.querySelector('input[name="modalType"]:checked').value;
            const cat = document.getElementById('modalCategory').value;
            const amt = document.getElementById('modalAmount').value;
            const memo = document.getElementById('modalMemo').value;

            if (!date) { alert("날짜를 선택하세요."); return; }
            if (new Date(date) > new Date()) { alert("미래 날짜는 등록할 수 없습니다."); return; }
            if (amt <= 0) { alert("금액은 1원 이상이어야 합니다."); return; }
            if (memo.length > 100) { alert("메모는 100자를 넘을 수 없습니다."); return; }
            if (!cat) { alert("카테고리를 선택해주세요."); return; }
            
            const params = new URLSearchParams({
                transNum: num, transDate: date, transType: type, 
                categoryNum: cat, transAmount: amt, transMemo: memo
            });

            fetch('${pageContext.request.contextPath}/personal/saveTransaction.do', {
                method: 'POST',
                body: params
            }).then(res => res.json()).then(data => {
                if (data.success) {
                    alert("저장되었습니다!");
                    closeModal();
                    applyFilters(); 
                } else {
                    alert(data.message); 
                }
            });
        }
        
        function deleteTransaction() {
            const num = document.getElementById('modalTransNum').value;
            
            if (!confirm('정말 이 내역을 삭제하시겠습니까?')) return;

            fetch('${pageContext.request.contextPath}/personal/deleteTransaction.do?transNum=' + num)
                .then(res => res.json())
                .then(data => {
                    if (data.success) {
                        alert("삭제되었습니다.");
                        closeModal();
                        applyFilters(); 
                    } else {
                        alert("삭제 실패: " + data.message);
                    }
                })
                .catch(err => console.error('삭제 요청 실패:', err));
        }

        function openCategoryManage() {
            document.getElementById('catManageModal').style.display = 'block';
            loadManageCategories();
        }

        function loadManageCategories() {
            const type = document.querySelector('input[name="mngCatType"]:checked').value;
            fetch('${pageContext.request.contextPath}/personal/getCategoryList.do?type=' + type)
                .then(res => res.json())
                .then(data => {
                    const listUl = document.getElementById('catManageList');
                    listUl.innerHTML = '';
                    data.forEach(item => {
                        let btnHtml = '';
                        if(item.categoryName !== '미분류') {
                            btnHtml = '<button onclick="saveCategoryManage(\'' + item.categoryNum + '\')" style="font-size:12px; cursor:pointer;">수정</button> ' +
                                      '<button onclick="deleteCategoryManage(\'' + item.categoryNum + '\')" style="font-size:12px; cursor:pointer; color:red;">삭제</button>';
                        } else {
                            btnHtml = '<span style="font-size:12px; color:#888;">기본</span>';
                        }
                        listUl.innerHTML += '<li style="display:flex; justify-content:space-between; margin-bottom:5px; border-bottom:1px solid #eee; padding-bottom:5px;">' + 
                                            '<span>' + item.categoryName + '</span>' +
                                            '<div>' + btnHtml + '</div></li>';
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
            
            const params = new URLSearchParams({ categoryNum: catNum, categoryName: name, categoryType: type });
            
            fetch('${pageContext.request.contextPath}/personal/saveCategory.do', { method:'POST', body:params })
                .then(res=>res.json()).then(data => {
                    if(data.success) {
                        document.getElementById('newCatName').value = '';
                        loadManageCategories(); 
                    } else alert(data.message);
                });
        }

        function deleteCategoryManage(catNum) {
            if(!confirm('삭제 시 해당 내역들은 [미분류]로 이동됩니다. 삭제하시겠습니까?')) return;
            
            const type = document.querySelector('input[name="mngCatType"]:checked').value;
            
            fetch('${pageContext.request.contextPath}/personal/deleteCategory.do?categoryNum=' + catNum + '&categoryType=' + type)
                .then(res => res.json())
                .then(data => {
                    if(data.success) {
                        alert("삭제 및 내역 이관이 완료되었습니다.");
                        loadManageCategories();
                        applyFilters(); 
                    } else {
                        alert("삭제 실패: " + data.message);
                    }
                });
        }
        
        function togglePublicYn() {
            const btn = document.getElementById('btnPublicToggle');
            const isCurrentlyPublic = btn.innerText.includes('공개 모드') && !btn.innerText.includes('비');
            const targetYn = isCurrentlyPublic ? 'N' : 'Y'; 
            
            const confirmMsg = targetYn === 'Y' 
                ? "내 가계부를 그룹 멤버 등 외부 사용자에게 공개하시겠습니까?" 
                : "내 가계부를 비공개로 전환하시겠습니까?";

            if (!confirm(confirmMsg)) return;

            const params = new URLSearchParams({ bookOpenYn: targetYn });

            fetch('${pageContext.request.contextPath}/personal/togglePublic.do', {
                method: 'POST',
                body: params
            })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                	
                    if (data.currentYn === 'Y') {
                        btn.innerText = '공개 모드';
                        btn.style.backgroundColor = '#17a2b8';
                        btn.style.color = 'white';
                    } else {
                        btn.innerText = '비공개 모드';
                        btn.style.backgroundColor = '#e2e3e5';
                        btn.style.color = '#555';
                    }
                } else {
                    alert("상태 변경에 실패했습니다.");
                }
            })
            .catch(err => console.error('토글 실패:', err));
        }
    </script>
</body>
</html>