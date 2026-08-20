<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${group.groupName} - 공동 가계부</title>
</head>
<body>
    <div style="width: 1000px; margin: 20px auto; font-family: sans-serif;">
        <!-- 1. 그룹 헤더 영역 (방장일 때만 톱니바퀴 노출) -->
       <div style="display: flex; align-items: center; gap: 10px;">
		    <h2 id="displayGroupName" style="margin: 0;">${group.groupName}</h2>
		    <button onclick="openMemberModal()" style="background: none; border: none; font-size: 1.5em; cursor: pointer;" title="멤버 목록">👥</button>
		    
		    <c:if test="${group.groupOwnerNum == loginUser.userNum}">
  				<button id="inviteBtn" onclick="openInviteModal()" style="background: none; border: none; font-size: 1.5em; cursor: pointer;" title="멤버 초대">➕</button>
    			<button id="settingBtn" onclick="openSettingsModal()" style="background: none; border: none; font-size: 1.5em; cursor: pointer;" title="방 설정">⚙️</button>
			</c:if>
		</div>
        <p id="displayGroupDesc" style="color: #666;">${group.groupDesc}</p>

        <hr style="border: 0; border-top: 1px solid #ddd; margin: 20px 0;">

        <!-- 상단 토글 버튼 -->
        <div style="text-align:center; margin-bottom: 20px;">
            <button onclick="switchView('calendar')" style="padding:10px 20px; background:#007BFF; color:white; border:none; border-radius:5px; cursor:pointer;">달력 뷰</button>
            <button onclick="switchView('list')" style="padding:10px 20px; background:#6c757d; color:white; border:none; border-radius:5px; cursor:pointer;">리스트 뷰</button>
        </div>

        <!-- 뷰 영역 (한 번에 하나만 보임) -->
        <div id="calendarView" style="display:block;">
            <h3 id="currentMonthLabel" style="text-align:center;"></h3>
            <!-- 여기에 자바스크립트로 달력 그리드를 그릴 예정입니다 -->
        </div>
        <div id="listView" style="display:none;">
            <!-- 여기에 자바스크립트로 리스트를 그릴 예정입니다 -->
        </div>

    </div> <!-- // container 끝 -->

    <!-- 2. 설정 모달창 (평소엔 숨김) -->
    <div id="settingsModal" style="display:none; position:fixed; top:20%; left:50%; transform:translate(-50%, 0); background:#fff; padding:25px; border-radius:10px; box-shadow:0 10px 25px rgba(0,0,0,0.2); width: 400px; z-index:1000;">
        <h3 style="margin-top:0; border-bottom: 1px solid #eee; padding-bottom: 10px;">방 설정 관리</h3>
        
        <input type="hidden" id="settingGroupNum" value="${group.groupNum}">
        
        <div style="margin-bottom: 15px;">
            <label style="display:block; font-weight:bold; margin-bottom:5px;">방 이름 (최대 20자)</label>
            <input type="text" id="settingGroupName" value="${group.groupName}" maxlength="20" style="width:100%; padding:8px; border:1px solid #ccc; border-radius:4px; box-sizing: border-box;">
        </div>
        
        <div style="margin-bottom: 15px;">
            <label style="display:block; font-weight:bold; margin-bottom:5px;">설명</label>
            <textarea id="settingGroupDesc" rows="3" style="width:100%; padding:8px; border:1px solid #ccc; border-radius:4px; box-sizing: border-box;">${group.groupDesc}</textarea>
        </div>
        
        <div style="margin-bottom: 20px;">
            <label style="display:block; font-weight:bold; margin-bottom:5px;">공개 여부</label>
            <select id="settingGroupOpenYn" style="width:100%; padding:8px; border:1px solid #ccc; border-radius:4px;">
                <option value="N" ${group.groupOpenYn == 'N' ? 'selected' : ''}>비공개 (초대 전용)</option>
                <option value="Y" ${group.groupOpenYn == 'Y' ? 'selected' : ''}>공개 (검색 허용)</option>
            </select>
        </div>
        
        <div style="display:flex; gap:10px;">
            <button onclick="saveGroupSettings()" style="flex:1; background:#007BFF; color:white; border:none; padding:10px; border-radius:5px; font-weight:bold; cursor:pointer;">저장</button>
            <button onclick="closeSettingsModal()" style="flex:1; background:#6c757d; color:white; border:none; padding:10px; border-radius:5px; cursor:pointer;">취소</button>
        </div>
        
        <div style="margin-top: 15px; text-align: right; border-top: 1px dashed #ccc; padding-top: 15px;">
            <button onclick="deleteGroup()" style="background:none; color:#dc3545; border:none; font-weight:bold; cursor:pointer; text-decoration:underline;">🗑️ 이 방 삭제하기</button>
        </div>
    </div>

	<!-- 멤버 초대 모달창 -->
	<div id="inviteModal" style="display:none; position:fixed; top:20%; left:50%; transform:translate(-50%, 0); background:#fff; padding:25px; border-radius:10px; box-shadow:0 10px 25px rgba(0,0,0,0.2); width: 400px; z-index:1000;">
	    <h3 style="margin-top:0; border-bottom: 1px solid #eee; padding-bottom: 10px;">멤버 초대하기</h3>
	    
	    <div style="margin-bottom: 15px;">
	        <label style="display:block; font-weight:bold; margin-bottom:5px;">초대할 유저 아이디 검색</label>
	        <div style="display:flex; gap:10px;">
	            <input type="text" id="searchUserId" placeholder="아이디를 정확히 입력하세요" style="flex:1; padding:8px; border:1px solid #ccc; border-radius:4px;">
	        </div>
	    </div>
	    
	    <div style="display:flex; gap:10px; margin-top:20px;">
	        <button onclick="sendGroupInvite()" style="flex:1; background:#28a745; color:white; border:none; padding:10px; border-radius:5px; font-weight:bold; cursor:pointer;">초대장 발송</button>
	        <button onclick="closeInviteModal()" style="flex:1; background:#6c757d; color:white; border:none; padding:10px; border-radius:5px; cursor:pointer;">닫기</button>
	    </div>
	</div>

	<!-- 멤버 관리 모달창 -->
    <div id="memberModal" style="display:none; position:fixed; top:20%; left:50%; transform:translate(-50%, 0); background:#fff; padding:25px; border-radius:10px; box-shadow:0 10px 25px rgba(0,0,0,0.2); width: 450px; z-index:1000;">
        <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #eee; padding-bottom: 10px; margin-bottom: 15px;">
            <h3 style="margin: 0;">그룹 멤버 관리</h3>
            <button onclick="closeMemberModal()" style="background: none; border: none; font-size: 1.2em; cursor: pointer;">❌</button>
        </div>
        
        <!-- 실시간으로 멤버 리스트가 꽂힐 영역 -->
        <ul id="memberListArea" style="list-style: none; padding: 0; margin: 0; max-height: 250px; overflow-y: auto;">
            <!-- JS로 동적 생성됨 -->
        </ul>
        
        <!-- 자진 탈퇴 버튼 (관전자에게는 숨김 처리) -->
        <c:if test="${isMember}">
            <div style="margin-top: 20px; text-align: center;">
                <button onclick="leaveGroup()" style="width: 100%; padding: 12px; background: #dc3545; color: white; border: none; border-radius: 5px; font-weight: bold; cursor: pointer;">🚪 이 방 나가기</button>
            </div>
        </c:if>
    </div>
    
    <script>
        function openSettingsModal() {
            document.getElementById('settingsModal').style.display = 'block';
        }

        function closeSettingsModal() {
            document.getElementById('settingsModal').style.display = 'none';
        }

        function saveGroupSettings() {
            const num = document.getElementById('settingGroupNum').value;
            const name = document.getElementById('settingGroupName').value.trim();
            const desc = document.getElementById('settingGroupDesc').value.trim();
            const openYn = document.getElementById('settingGroupOpenYn').value;

            if (!name) { alert("방 이름을 입력해 주세요."); return; }

            const params = new URLSearchParams({
                groupNum: num,
                groupName: name,
                groupDesc: desc,
                groupOpenYn: openYn
            });

            fetch('${pageContext.request.contextPath}/group/updateSettings.do', {
                method: 'POST',
                body: params
            })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    alert(data.message);
                    document.getElementById('displayGroupName').innerText = name;
                    document.getElementById('displayGroupDesc').innerText = desc;
                    closeSettingsModal();
                } else {
                    alert("오류: " + data.message);
                }
            })
            .catch(err => console.error('설정 변경 실패:', err));
        }

        function deleteGroup() {
            if (!confirm("정말 이 공동 가계부를 삭제하시겠습니까?\n(이 작업은 되돌릴 수 없으며, 모든 멤버가 더 이상 접근할 수 없습니다.)")) {
                return;
            }

            const num = document.getElementById('settingGroupNum').value;
            const params = new URLSearchParams({ groupNum: num });

            fetch('${pageContext.request.contextPath}/group/delete.do', {
                method: 'POST',
                body: params
            })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    alert(data.message);
                    window.location.href = '${pageContext.request.contextPath}/group/list.do';
                } else {
                    alert("오류: " + data.message);
                }
            })
            .catch(err => console.error('삭제 실패:', err));
        }
        
        function openInviteModal() {
            document.getElementById('inviteModal').style.display = 'block';
            document.getElementById('searchUserId').value = '';
        }

        function closeInviteModal() {
            document.getElementById('inviteModal').style.display = 'none';
        }

        function sendGroupInvite() {
            const userId = document.getElementById('searchUserId').value.trim();
            const groupNum = document.getElementById('settingGroupNum').value;

            if (!userId) {
                alert("초대할 유저의 아이디를 입력해 주세요.");
                return;
            }

            const params = new URLSearchParams({
                groupNum: groupNum,
                inviteeId: userId
            });

            fetch('${pageContext.request.contextPath}/group/sendInvite.do', {
                method: 'POST',
                body: params
            })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    alert(data.message);
                    closeInviteModal();
                } else {
                    alert("초대 실패: " + data.message);
                }
            })
            .catch(err => console.error('초대 에러:', err));
        }
        
     	// JSTL 값을 JS 변수로 가져오기 (권한 분기용)
        const currentUserNum = parseInt('${loginUser.userNum}');
		let groupOwnerNum = parseInt('${group.groupOwnerNum}');
		const isMember = ${isMember};
		
        function openMemberModal() {
            document.getElementById('memberModal').style.display = 'block';
            loadMemberList();
        }

        function closeMemberModal() {
            document.getElementById('memberModal').style.display = 'none';
        }

        // 멤버 목록 로드
        function loadMemberList() {
            const groupNum = document.getElementById('settingGroupNum').value;
            
            fetch('${pageContext.request.contextPath}/group/getMemberList.do?groupNum=' + groupNum)
            .then(res => res.json())
            .then(data => {
                const listArea = document.getElementById('memberListArea');
                listArea.innerHTML = '';
                
                data.forEach(m => {
                    let li = document.createElement('li');
                    li.style.cssText = "display: flex; justify-content: space-between; align-items: center; padding: 10px; border-bottom: 1px solid #f0f0f0;";
                    
                    let nameHtml = `<div style="display: flex; flex-direction: column;">
                                        <span><strong>\${m.userNickname}</strong> (\${m.userId}) \${m.userNum === groupOwnerNum ? '👑' : ''}</span>
                                        <span style="font-size: 0.8em; color: #888;">가입일: \${m.joinDate}</span>
                                    </div>`;
                    
                    let actionHtml = '';
                    if (currentUserNum === groupOwnerNum && m.userNum !== groupOwnerNum) {
                        actionHtml = `<button onclick="transferOwner(\${m.userNum}, '\${m.userNickname}')" style="background: none; border: 1px solid #007BFF; color: #007BFF; padding: 5px 10px; border-radius: 3px; cursor: pointer; margin-right: 5px;">👑 위임</button>`
                                   + `<button onclick="kickMember(\${m.userNum})" style="background: none; border: 1px solid #dc3545; color: #dc3545; padding: 5px 10px; border-radius: 3px; cursor: pointer;">강퇴</button>`;
                    }
                    
                    li.innerHTML = nameHtml + actionHtml;
                    listArea.appendChild(li);
                });
            })
            .catch(err => console.error('멤버 목록 로드 실패:', err));
        }

        // 강퇴 처리
        function kickMember(targetUserNum) {
            if (!confirm("해당 멤버를 강퇴하시겠습니까?\n(미정산 잔액과 무관하게 즉시 이탈 처리됩니다.)")) {
                return;
            }
            
            const groupNum = document.getElementById('settingGroupNum').value;
            const params = new URLSearchParams({ groupNum: groupNum, targetUserNum: targetUserNum });

            fetch('${pageContext.request.contextPath}/group/kickMember.do', {
                method: 'POST',
                body: params
            })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    alert(data.message);
                    loadMemberList();
                } else {
                    alert("오류: " + data.message);
                }
            })
            .catch(err => console.error('강퇴 실패:', err));
        }

        // 자진 탈퇴 처리 (방장/멤버 동적 처리)
        function leaveGroup() {
            let confirmMsg = "";
            
            if (currentUserNum === groupOwnerNum) {
                confirmMsg = "정말 이 공동 가계부에서 나가시겠습니까?\n(방장 권한은 가입일이 가장 빠른 멤버에게 자동 위임되며, 남은 멤버가 없을 경우 방이 즉시 삭제됩니다.)";
            } else {
                confirmMsg = "정말 이 공동 가계부에서 나가시겠습니까?\n(미정산 잔액과 무관하게 즉시 탈퇴 처리됩니다.)";
            }

            if (!confirm(confirmMsg)) {
                return;
            }
            
            const groupNum = document.getElementById('settingGroupNum').value;
            const params = new URLSearchParams({ groupNum: groupNum });

            fetch('${pageContext.request.contextPath}/group/leaveGroup.do', {
                method: 'POST',
                body: params
            })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    alert(data.message);
                    window.location.href = '${pageContext.request.contextPath}/group/list.do'; // 공통: 탈퇴 후 목록으로 이동
                } else {
                    alert("오류: " + data.message);
                }
            })
            .catch(err => console.error('탈퇴 실패:', err));
        }
        
     	// 방장 수동 위임 처리 (새로고침 없는 실시간 버전)
        function transferOwner(targetUserNum, targetNickname) {
            if (!confirm(targetNickname + " 님에게 방장 권한을 넘겨주시겠습니까?\n(위임 즉시 본인은 일반 멤버로 전환되며, 더 이상 방 설정 및 강퇴가 불가능합니다.)")) {
                return;
            }
            
            const groupNum = document.getElementById('settingGroupNum').value;
            const params = new URLSearchParams({ groupNum: groupNum, targetUserNum: targetUserNum });

            fetch('${pageContext.request.contextPath}/group/transferOwner.do', {
                method: 'POST',
                body: params
            })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    alert(data.message);
                    
                    // 1. 자바스크립트가 알고 있는 방장 번호를 새로운 방장으로 업데이트
                    groupOwnerNum = targetUserNum; 
                    
                    // 2. 헤더에 있던 방장 전용 버튼(초대, 설정)을 DOM에서 숨김 처리
                    document.getElementById('inviteBtn').style.display = 'none';
                    document.getElementById('settingBtn').style.display = 'none';
                    
                    // 3. 모달창 리스트 다시 그리기 (새로고침 없이 갱신됨)
                    loadMemberList(); 
                } else {
                    alert("오류: " + data.message);
                }
            })
            .catch(err => console.error('위임 실패:', err));
        }
     
        // 백엔드에서 가져온 데이터를 저장해둘 전역 변수 (이 데이터 하나로 달력도 그리고 리스트도 그립니다!)
        let currentData = []; 

        function switchView(type) {
            // 화면 숨김/표시 처리
            document.getElementById('calendarView').style.display = (type === 'calendar') ? 'block' : 'none';
            document.getElementById('listView').style.display = (type === 'list') ? 'block' : 'none';
            
            // 추후 구현할 실제 화면 그리기 함수 호출
            if(type === 'calendar') renderCalendar();
            if(type === 'list') renderList();
        }

        function loadMonthData(yearMonth) {
            const groupNum = '${group.groupNum}'; // 현재 방 번호
            
            // 백엔드에 1번만 요청!
            fetch(`${pageContext.request.contextPath}/groupLedger/getTransactions.do?groupNum=\${groupNum}&yearMonth=\${yearMonth}`)
            .then(res => res.json())
            .then(data => {
                currentData = data; // 가져온 데이터를 저장
                document.getElementById('currentMonthLabel').innerText = yearMonth;
                switchView('calendar'); // 뷰 전환
            })
            .catch(err => console.error('데이터 로드 실패:', err));
        }

        // 화면이 처음 켜질 때 실행
        window.onload = () => { 
            // 임시 하드코딩 (나중에는 실제 현재 연/월을 구하는 로직으로 교체해야 합니다)
            loadMonthData("2023-10"); 
        };
        
        // 달력 뷰 그리기 (renderCalendar)
        function renderCalendar() {
            const calendarView = document.getElementById('calendarView');
            const yearMonth = document.getElementById('currentMonthLabel').innerText; // 예: "2023-10"
            if (!yearMonth) return;

            const [year, month] = yearMonth.split('-');
            
            // 이번 달의 첫째 날 요일과 마지막 날짜 계산
            const firstDay = new Date(year, month - 1, 1).getDay(); // 0(일) ~ 6(토)
            const lastDate = new Date(year, month, 0).getDate(); // 28, 29, 30, 31

            // 달력 HTML 뼈대 생성 (CSS Grid 활용)
            let html = `<div style="display: grid; grid-template-columns: repeat(7, 1fr); gap: 5px; text-align: center; margin-top: 20px;">`;
            
            // 요일 헤더
            const days = ['일', '월', '화', '수', '목', '금', '토'];
            days.forEach(day => {
                html += `<div style="font-weight: bold; padding: 10px; background: #f8f9fa;">\${day}</div>`;
            });

            // 1일 시작 전 빈 칸 채우기
            for (let i = 0; i < firstDay; i++) {
                html += `<div style="padding: 10px; background: #fff; border: 1px solid #eee;"></div>`;
            }

            // 실제 날짜 그리기
            for (let d = 1; d <= lastDate; d++) {
                // 날짜 포맷 맞추기 (예: "2023-10-05")
                const dateStr = `\${year}-\${month}-\${String(d).padStart(2, '0')}`;
                
                // currentData에서 이 날짜에 해당하는 지출만 쏙쏙 뽑아냄!
                const dayTransactions = currentData.filter(t => t.transDate === dateStr);
                
                let dayHtml = `<div style="padding: 10px; min-height: 80px; background: #fff; border: 1px solid #eee; text-align: left; position: relative;">`;
                dayHtml += `<strong style="display: block; margin-bottom: 5px;">\${d}</strong>`; // 날짜 번호
                
                // 해당 날짜에 지출 내역이 있다면 그리기
                if (dayTransactions.length > 0) {
                    let dailyTotal = 0;
                    dayTransactions.forEach(t => {
                        dailyTotal += t.transAmount;
                        // 개별 결제자 + 금액 표시 (너무 길면 잘리도록)
                        dayHtml += `<div style="font-size: 0.75em; color: #555; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
                                        [\${t.userNickname}] \${t.transAmount.toLocaleString()}원
                                    </div>`;
                    });
                    // 그날의 총 지출액 굵게 표시
                    dayHtml += `<div style="font-size: 0.85em; font-weight: bold; color: #dc3545; margin-top: 5px; border-top: 1px dashed #ccc; padding-top: 3px;">
                                    총 \${dailyTotal.toLocaleString()}원
                                </div>`;
                }
                
                dayHtml += `</div>`;
                html += dayHtml;
            }
            html += `</div>`; // grid 컨테이너 닫기
            
            // 기존 제목(h3) 아래에 달력 덮어쓰기
            calendarView.innerHTML = `<h3 id="currentMonthLabel" style="text-align:center;">\${yearMonth}</h3>` + html;
        }
        
        // 리스트 뷰 그리기 (renderList)
        function renderList() {
            const listView = document.getElementById('listView');
            
            if (currentData.length === 0) {
                listView.innerHTML = `<div style="text-align: center; padding: 50px; color: #888;">이번 달 지출 내역이 없습니다.</div>`;
                return;
            }

            let html = `<ul style="list-style: none; padding: 0; margin: 0;">`;
            
            // 최신 날짜순으로 출력 (이미 DB에서 정렬해 왔으므로 바로 출력)
            currentData.forEach(t => {
                html += `
                    <li style="display: flex; justify-content: space-between; align-items: center; padding: 15px; border-bottom: 1px solid #eee;">
                        <div>
                            <strong style="font-size: 1.1em; display: block;">\${t.categoryName} - \${t.transMemo || '메모 없음'}</strong>
                            <span style="font-size: 0.85em; color: #888;">\${t.transDate} | 결제자: \${t.userNickname}</span>
                        </div>
                        <div style="font-size: 1.2em; font-weight: bold; color: #dc3545;">
                            \${t.transAmount.toLocaleString()} 원
                        </div>
                    </li>
                `;
            });
            
            html += `</ul>`;
            listView.innerHTML = html;
        }

        // 오늘 날짜 기준으로 이번 달 데이터 불러오기 함수 수정
        window.onload = () => { 
            // 2026년 등 현재 시점의 진짜 연/월을 자동으로 계산해서 넘겨줍니다!
            const today = new Date();
            const yyyy = today.getFullYear();
            const mm = String(today.getMonth() + 1).padStart(2, '0');
            const currentYearMonth = `\${yyyy}-\${mm}`;
            
            loadMonthData(currentYearMonth); 
        };
    </script>
</body>
</html>