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
    
        <div id='calendar'></div>

        <div class="filter-box">
            <label><input type="radio" name="transType" value="ALL" checked onclick="applyFilters()"> 전체</label>
            <label><input type="radio" name="transType" value="I" onclick="applyFilters()"> 수입</label>
            <label><input type="radio" name="transType" value="E" onclick="applyFilters()"> 지출</label>
            
            <input type="text" id="keyword" placeholder="메모 내용 검색" onkeyup="if(event.keyCode==13) applyFilters()">
            <button onclick="applyFilters()">검색</button>
            <button onclick="resetFilters()" style="background:#007BFF;">이번 달 전체보기</button>
            
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
                        
                        let tr = '<tr>' +
                            '<td>' + item.transDate + '</td>' +
                            '<td>' + typeHtml + '</td>' +
                            '<td>' + item.categoryName + '</td>' +
                            '<td style="text-align:right; padding-right:20px;">' + amountHtml + '원</td>' +
                            '<td style="text-align:left; padding-left:20px;">' + item.transMemo + '</td>' +
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
    </script>
</body>
</html>