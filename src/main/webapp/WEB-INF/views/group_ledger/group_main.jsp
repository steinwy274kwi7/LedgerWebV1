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
    			<button id="closePeriodBtn" onclick="closeLedgerPeriod()" style="background: #dc3545; color: white; border: none; border-radius: 5px; padding: 5px 12px; font-weight: bold; font-size: 0.9em; cursor: pointer; margin-left: 10px;" title="현재 장부를 마감하고 정산합니다">정산 및 마감 💰</button>
			</c:if>
		</div>
        <p id="displayGroupDesc" style="color: #666;">${group.groupDesc}</p>

        <hr style="border: 0; border-top: 1px solid #ddd; margin: 20px 0;">

        <!-- 지출 등록 & 카테고리 관리 버튼 (멤버에게만 노출) -->
        <c:if test="${isMember}">
            <div style="display: flex; justify-content: flex-end; gap: 10px; margin-bottom: 10px;">
                <button onclick="openCategoryModal()" style="padding: 10px 20px; background: #6f42c1; color: white; border: none; border-radius: 5px; font-weight: bold; cursor: pointer;">🏷️ 카테고리 관리</button>
                <button onclick="openExpenseModal()" style="padding: 10px 20px; background: #28a745; color: white; border: none; border-radius: 5px; font-weight: bold; cursor: pointer;">+ 지출 등록</button>
                <button onclick="openLogModal()" style="padding: 10px 20px; background: #17a2b8; color: white; border: none; border-radius: 5px; font-weight: bold; cursor: pointer;">📜 무결성 변경 이력</button>
                <button onclick="openArchiveModal()" style="padding: 10px 20px; background: #6c757d; color: white; border: none; border-radius: 5px; font-weight: bold; cursor: pointer;">🗂️ 과거 내역</button>
            </div>
        </c:if>

        <!-- 상단 토글 버튼 -->
        <div style="text-align:center; margin-bottom: 20px;">
            <button onclick="switchView('calendar')" style="padding:10px 20px; background:#007BFF; color:white; border:none; border-radius:5px; cursor:pointer;">달력 뷰</button>
            <button onclick="switchView('list')" style="padding:10px 20px; background:#6c757d; color:white; border:none; border-radius:5px; cursor:pointer;">리스트 뷰</button>
            
        </div>
        
		<div style="text-align: right; margin-bottom: 10px; padding-right: 10px;">
            <label style="cursor: pointer; font-size: 0.9em; color: #666; font-weight: bold;">
                <input type="checkbox" id="toggleClosedData" onchange="refreshCurrentView()" style="accent-color: #6c757d;">
                🔒 이전 정산 내역 같이 보기
            </label>
        </div>
        
        <!-- 뷰 영역 (한 번에 하나만 보임) -->
        <div id="calendarView" style="display:block;">
            <h3 id="currentMonthLabel" style="text-align:center;"></h3>
            <!-- 자바스크립트로 달력 그리드 렌더링 -->
        </div>
        <div id="listView" style="display:none;">
            <!-- 자바스크립트로 리스트 렌더링 -->
        </div>

    </div> <!-- // container 끝 -->

    <!-- 2. 설정 모달창 -->
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
            <button onclick="deleteGroup()" style="background:none; color:#dc3545; border:none; font-weight:bold; cursor:pointer; text-decoration:underline;">이 방 삭제하기</button>
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
            <button onclick="closeMemberModal()" style="background: none; border: none; font-size: 1.2em; cursor: pointer;">X</button>
        </div>
        
        <!-- 동적 리스트 영역 -->
        <ul id="memberListArea" style="list-style: none; padding: 0; margin: 0; max-height: 250px; overflow-y: auto;">
        </ul>
        
        <!-- 자진 탈퇴 버튼 (관전자에게는 숨김 처리) -->
        <c:if test="${isMember}">
            <div style="margin-top: 20px; text-align: center;">
                <button onclick="leaveGroup()" style="width: 100%; padding: 12px; background: #dc3545; color: white; border: none; border-radius: 5px; font-weight: bold; cursor: pointer;">이 방 나가기</button>
            </div>
        </c:if>
    </div>

    <!-- 카테고리 관리 모달창 -->
    <div id="categoryManageModal" style="display:none; position:fixed; top:20%; left:50%; transform:translate(-50%, 0); background:#fff; padding:25px; border-radius:10px; box-shadow:0 10px 25px rgba(0,0,0,0.2); width: 450px; z-index:1000;">
        <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #eee; padding-bottom: 10px; margin-bottom: 15px;">
            <h3 style="margin: 0;">공동 카테고리 관리</h3>
            <button onclick="closeCategoryModal()" style="background: none; border: none; font-size: 1.2em; cursor: pointer;">X</button>
        </div>
        
        <div style="display:flex; gap:10px; margin-bottom:20px;">
            <input type="text" id="newCategoryName" maxlength="20" placeholder="새 카테고리명 (20자 이내)" style="flex:1; padding:8px; border:1px solid #ccc; border-radius:4px;">
            <button onclick="addCategory()" style="padding:8px 15px; background:#28a745; color:white; border:none; border-radius:4px; font-weight:bold; cursor:pointer;">추가</button>
        </div>
        
        <ul id="categoryListArea" style="list-style: none; padding: 0; margin: 0; max-height: 250px; overflow-y: auto; border-top: 1px solid #eee;">
        </ul>
    </div>
    
    <!-- 지출 등록 모달창 -->
    <div id="expenseModal" style="display:none; position:fixed; top:20%; left:50%; transform:translate(-50%, 0); background:#fff; padding:25px; border-radius:10px; box-shadow:0 10px 25px rgba(0,0,0,0.2); width: 400px; z-index:1000;">
        <h3 style="margin-top:0; border-bottom: 1px solid #eee; padding-bottom: 10px;">새 지출 등록</h3>
        
        <div style="margin-bottom: 15px;">
            <label style="display:block; font-weight:bold; margin-bottom:5px;">결제 날짜</label>
            <input type="date" id="expDate" style="width:100%; padding:8px; border:1px solid #ccc; border-radius:4px; box-sizing: border-box;" required>
        </div>
        
        <div style="margin-bottom: 15px;">
            <label style="display:block; font-weight:bold; margin-bottom:5px;">카테고리</label>
            <select id="expCategory" style="width:100%; padding:8px; border:1px solid #ccc; border-radius:4px;">
            </select>
        </div>
        
        <div style="margin-bottom: 15px;">
            <label style="display:block; font-weight:bold; margin-bottom:5px;">결제 금액 (원)</label>
            <input type="number" id="expAmount" min="1" placeholder="금액을 입력하세요" style="width:100%; padding:8px; border:1px solid #ccc; border-radius:4px; box-sizing: border-box;" required>
        </div>
        
        <div style="margin-bottom: 20px;">
            <label style="display:block; font-weight:bold; margin-bottom:5px;">메모 (선택)</label>
            <input type="text" id="expMemo" maxlength="100" placeholder="100자 이내로 적어주세요" style="width:100%; padding:8px; border:1px solid #ccc; border-radius:4px; box-sizing: border-box;">
        </div>
        
        <div style="display:flex; gap:10px;">
            <button onclick="saveExpense()" style="flex:1; background:#007BFF; color:white; border:none; padding:10px; border-radius:5px; font-weight:bold; cursor:pointer;">등록하기</button>
            <button onclick="closeExpenseModal()" style="flex:1; background:#6c757d; color:white; border:none; padding:10px; border-radius:5px; cursor:pointer;">취소</button>
        </div>
    </div>
    
    <!-- 지출 수정 모달창 -->
    <div id="editExpenseModal" style="display:none; position:fixed; top:20%; left:50%; transform:translate(-50%, 0); background:#fff; padding:25px; border-radius:10px; box-shadow:0 10px 25px rgba(0,0,0,0.2); width: 400px; z-index:1000;">
        <h3 style="margin-top:0; border-bottom: 1px solid #eee; padding-bottom: 10px;">지출 내역 수정</h3>
        <input type="hidden" id="editTransNum">
        
        <div style="margin-bottom: 15px;">
            <label style="display:block; font-weight:bold; margin-bottom:5px;">결제 날짜</label>
            <input type="date" id="editExpDate" style="width:100%; padding:8px; border:1px solid #ccc; border-radius:4px; box-sizing: border-box;" required>
        </div>
        <div style="margin-bottom: 15px;">
            <label style="display:block; font-weight:bold; margin-bottom:5px;">카테고리</label>
            <select id="editExpCategory" style="width:100%; padding:8px; border:1px solid #ccc; border-radius:4px;"></select>
        </div>
        <div style="margin-bottom: 15px;">
            <label style="display:block; font-weight:bold; margin-bottom:5px;">결제 금액 (원)</label>
            <input type="number" id="editExpAmount" min="1" style="width:100%; padding:8px; border:1px solid #ccc; border-radius:4px; box-sizing: border-box;" required>
        </div>
        <div style="margin-bottom: 20px;">
            <label style="display:block; font-weight:bold; margin-bottom:5px;">메모 (선택)</label>
            <input type="text" id="editExpMemo" maxlength="100" style="width:100%; padding:8px; border:1px solid #ccc; border-radius:4px; box-sizing: border-box;">
        </div>
        <div style="display:flex; gap:10px;">
            <button onclick="saveEditExpense()" style="flex:1; background:#ffc107; color:#333; border:none; padding:10px; border-radius:5px; font-weight:bold; cursor:pointer;">수정완료</button>
            <button onclick="closeEditExpenseModal()" style="flex:1; background:#6c757d; color:white; border:none; padding:10px; border-radius:5px; cursor:pointer;">취소</button>
        </div>
    </div>
    
    <!-- 무결성 변경 이력 모달창 -->
    <div id="logModal" style="display:none; position:fixed; top:10%; left:50%; transform:translate(-50%, 0); background:#fff; padding:25px; border-radius:10px; box-shadow:0 10px 25px rgba(0,0,0,0.2); width: 500px; z-index:1000; max-height: 80vh; overflow-y: auto;">
        <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #eee; padding-bottom: 10px;">
            <h3 style="margin: 0;">무결성 변경 이력 <span style="font-size: 0.6em; color: #dc3545; font-weight: normal;">(Read-Only)</span></h3>
            <button onclick="document.getElementById('logModal').style.display='none'" style="background: none; border: none; font-size: 1.2em; cursor: pointer;">X</button>
        </div>
        <ul id="logListArea" style="list-style: none; padding: 0; margin-top: 15px;">
        </ul>
    </div>
	    
	<!-- 과거 정산 보관함 모달창 -->
	<div id="archiveModal" style="display:none; position:fixed; top:10%; left:50%; transform:translate(-50%, 0); background:#fff; padding:25px; border-radius:10px; box-shadow:0 10px 25px rgba(0,0,0,0.2); width: 600px; z-index:1000; max-height: 80vh; overflow-y: auto;">
	    <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #eee; padding-bottom: 10px; margin-bottom: 15px;">
	        <h3 style="margin: 0;">🗂️ 과거 정산 보관함</h3>
	        <button onclick="closeArchiveModal()" style="background: none; border: none; font-size: 1.2em; cursor: pointer;">X</button>
	    </div>
	    
	    <!-- 회차 목록 영역 -->
	    <div id="archivePeriodList"></div>
	    
	    <!-- 상세 내역 영역 -->
	    <div id="archiveDetailArea" style="display:none; margin-top:10px;">
	        <button onclick="backToPeriodList()" style="margin-bottom:15px; padding:5px 10px; cursor:pointer; background:#f8f9fa; border:1px solid #ddd; border-radius:3px;">⬅️ 목록으로 돌아가기</button>
	        <h4 id="detailPeriodTitle" style="color:#007BFF; margin-top:0; margin-bottom:15px;"></h4>
	        
	        <div style="background:#f8f9fa; padding:15px; border-radius:8px; margin-bottom:20px; border:1px solid #e9ecef;">
	            <strong style="display:block; margin-bottom:10px;">💰 최종 정산 결과</strong>
	            <ul id="snapshotList" style="list-style:none; padding-left:0; margin:0;"></ul>
	        </div>
	        
	        <strong style="display:block; margin-bottom:10px;">📝 상세 지출 내역</strong>
	        <ul id="archiveTransactionList" style="list-style:none; padding-left:0; margin:0;"></ul>
	    </div>
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
                                        <span><strong>\${m.userNickname}</strong> (\${m.userId}) \${m.userNum === groupOwnerNum ? '방장' : ''}</span>
                                        <span style="font-size: 0.8em; color: #888;">가입일: \${m.joinDate}</span>
                                    </div>`;
                    
                    let actionHtml = '';
                    if (currentUserNum === groupOwnerNum && m.userNum !== groupOwnerNum) {
                        actionHtml = `<button onclick="transferOwner(\${m.userNum}, '\${m.userNickname}')" style="background: none; border: 1px solid #007BFF; color: #007BFF; padding: 5px 10px; border-radius: 3px; cursor: pointer; margin-right: 5px;">위임</button>`
                                   + `<button onclick="kickMember(\${m.userNum})" style="background: none; border: 1px solid #dc3545; color: #dc3545; padding: 5px 10px; border-radius: 3px; cursor: pointer;">강퇴</button>`;
                    }
                    
                    li.innerHTML = nameHtml + actionHtml;
                    listArea.appendChild(li);
                });
            })
            .catch(err => console.error('멤버 목록 로드 실패:', err));
        }

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
                    window.location.href = '${pageContext.request.contextPath}/group/list.do';
                } else {
                    alert("오류: " + data.message);
                }
            })
            .catch(err => console.error('탈퇴 실패:', err));
        }
        
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
                    groupOwnerNum = targetUserNum; 
                    document.getElementById('inviteBtn').style.display = 'none';
                    document.getElementById('settingBtn').style.display = 'none';
                    loadMemberList(); 
                } else {
                    alert("오류: " + data.message);
                }
            })
            .catch(err => console.error('위임 실패:', err));
        }
     
        let currentData = []; 

        function switchView(type) {
            document.getElementById('calendarView').style.display = (type === 'calendar') ? 'block' : 'none';
            document.getElementById('listView').style.display = (type === 'list') ? 'block' : 'none';
            
            if(type === 'calendar') renderCalendar();
            if(type === 'list') renderList();
        }

        function loadMonthData(yearMonth) {
            const groupNum = '${group.groupNum}'; 
            
            fetch(`${pageContext.request.contextPath}/groupLedger/getTransactions.do?groupNum=\${groupNum}&yearMonth=\${yearMonth}`)
            .then(res => res.json())
            .then(data => {
                currentData = data; 
                document.getElementById('currentMonthLabel').innerText = yearMonth;
                switchView('calendar'); 
            })
            .catch(err => console.error('데이터 로드 실패:', err));
        }

        function fetchCategoryList() {
            const groupNum = '${group.groupNum}';
            
            fetch(`${pageContext.request.contextPath}/groupLedger/getCategoryList.do?groupNum=\${groupNum}`)
            .then(res => res.json())
            .then(data => {
                const select = document.getElementById('expCategory');
                select.innerHTML = '';
                
                const editSelect = document.getElementById('editExpCategory');
                editSelect.innerHTML = ''; 
                
                const listArea = document.getElementById('categoryListArea');
                listArea.innerHTML = '';

                data.forEach(c => {
                    let option1 = document.createElement('option');
                    option1.value = c.gcategoryNum;
                    option1.innerText = c.categoryName;
                    select.appendChild(option1);

                    let option2 = document.createElement('option');
                    option2.value = c.gcategoryNum;
                    option2.innerText = c.categoryName;
                    editSelect.appendChild(option2);

                    let li = document.createElement('li');
                    li.style.cssText = "display:flex; justify-content:space-between; padding:12px; border-bottom:1px solid #eee; align-items:center;";
                    
                    if(c.categoryName === '미분류') {
                        li.innerHTML = `<span><strong>\${c.categoryName}</strong> <span style="font-size:0.8em; color:#888;">(기본)</span></span>`;
                    } else {
                        li.innerHTML = `
                            <span>\${c.categoryName}</span>
                            <div>
                                <button onclick="editCategory(\${c.gcategoryNum}, '\${c.categoryName}')" style="background:#ffc107; border:none; padding:5px 10px; border-radius:3px; cursor:pointer; margin-right:5px; font-weight:bold;">수정</button>
                                <button onclick="deleteCategory(\${c.gcategoryNum}, '\${c.categoryName}')" style="background:#dc3545; color:white; border:none; padding:5px 10px; border-radius:3px; cursor:pointer; font-weight:bold;">삭제</button>
                            </div>
                        `;
                    }
                    listArea.appendChild(li);
                });
            })
            .catch(err => console.error('카테고리 로드 실패:', err));
        }

        window.onload = () => { 
            const today = new Date();
            const yyyy = today.getFullYear();
            const mm = String(today.getMonth() + 1).padStart(2, '0');
            const currentYearMonth = `\${yyyy}-\${mm}`;
            
            loadMonthData(currentYearMonth); 
            fetchCategoryList(); 
        };
        
        function renderCalendar() {
            const calendarView = document.getElementById('calendarView');
            const yearMonth = document.getElementById('currentMonthLabel').innerText; 
            if (!yearMonth) return;

            const [year, month] = yearMonth.split('-');
            const firstDay = new Date(year, month - 1, 1).getDay(); 
            const lastDate = new Date(year, month, 0).getDate(); 

            const showClosed = document.getElementById('toggleClosedData').checked; 
            
            let html = `<div style="display: grid; grid-template-columns: repeat(7, 1fr); gap: 5px; text-align: center; margin-top: 20px;">`;
            
            const days = ['일', '월', '화', '수', '목', '금', '토'];
            days.forEach(day => {
                html += `<div style="font-weight: bold; padding: 10px; background: #f8f9fa;">\${day}</div>`;
            });

            for (let i = 0; i < firstDay; i++) {
                html += `<div style="padding: 10px; background: #fff; border: 1px solid #eee;"></div>`;
            }

            for (let d = 1; d <= lastDate; d++) {
                const dateStr = `\${year}-\${month}-\${String(d).padStart(2, '0')}`;
                const dayTransactions = currentData.filter(t => t.transDate === dateStr);
                
                let dayHtml = `<div style="padding: 10px; min-height: 80px; background: #fff; border: 1px solid #eee; text-align: left; position: relative;">`;
                dayHtml += `<strong style="display: block; margin-bottom: 5px;">\${d}</strong>`; 
                
                if (dayTransactions.length > 0) {
                    let activeTotal = 0; 
                    
                    dayTransactions.forEach(t => {
                        const isClosed = (t.periodStatus === 'C');
                        
                        if (!showClosed && isClosed) return; 
                        
                        if (!isClosed) activeTotal += t.transAmount;
                        
                        const colorStyle = isClosed ? "opacity: 0.4; color: #555;" : "color: #555;";
                        const icon = isClosed ? "[마감] " : "";
                        
                        dayHtml += `<div style="font-size: 0.75em; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; \${colorStyle}">
                                        \${icon}[\${t.userNickname}] \${t.transAmount.toLocaleString()}원
                                    </div>`;
                    });
                    
                    if (activeTotal > 0) {
                        dayHtml += `<div style="font-size: 0.85em; font-weight: bold; color: #dc3545; margin-top: 5px; border-top: 1px dashed #ccc; padding-top: 3px;">
                                        총 \${activeTotal.toLocaleString()}원
                                    </div>`;
                    }
                }
                dayHtml += `</div>`;
                html += dayHtml;
            }
            html += `</div>`; 
            
            calendarView.innerHTML = `<h3 id="currentMonthLabel" style="text-align:center;">\${yearMonth}</h3>` + html;
        }
        
        function renderList() {
            const listView = document.getElementById('listView');
            const showClosed = document.getElementById('toggleClosedData').checked;
            
            if (currentData.length === 0) {
                listView.innerHTML = `<div style="text-align: center; padding: 50px; color: #888;">이번 달 지출 내역이 없습니다.</div>`;
                return;
            }

            let html = `<ul style="list-style: none; padding: 0; margin: 0;">`;
            let visibleCount = 0; 

            currentData.forEach(t => {
                const isClosed = (t.periodStatus === 'C');
                
                if (!showClosed && isClosed) return;
                
                visibleCount++;
                
                const itemStyle = isClosed ? "opacity: 0.4; color: #555;" : "color: #000;";
                const badgeHtml = isClosed ? `<span style="font-size:0.7em; background:#6c757d; color:white; padding:2px 6px; border-radius:3px; margin-left:5px; vertical-align:middle;">정산완료</span>` : "";
                
                let actionButtons = '';
                if (!isClosed && (t.userNum === currentUserNum || currentUserNum === groupOwnerNum)) {
                    const safeMemo = t.transMemo ? t.transMemo.replace(/'/g, "\\'") : '';
                    actionButtons = `
                        <div style="margin-top: 8px;">
                            <button onclick="openEditExpenseModal(\${t.gtransNum}, '\${t.transDate}', '\${t.categoryName}', \${t.transAmount}, '\${safeMemo}')" style="background:#ffc107; border:none; padding:4px 10px; border-radius:3px; font-size:0.85em; cursor:pointer; font-weight:bold; margin-right:5px;">수정</button>
                            <button onclick="deleteTransaction(\${t.gtransNum})" style="background:#dc3545; color:white; border:none; padding:4px 10px; border-radius:3px; font-size:0.85em; cursor:pointer; font-weight:bold;">삭제</button>
                        </div>
                    `;
                }

                html += `
                    <li style="display: flex; justify-content: space-between; align-items: center; padding: 15px; border-bottom: 1px solid #eee; \${itemStyle}">
                        <div>
                            <strong style="font-size: 1.1em; display: inline-block; margin-bottom: 5px;">
                                \${t.categoryName} - \${t.transMemo || '메모 없음'} \${badgeHtml}
                            </strong>
                            <span style="font-size: 0.85em; display: block; \${isClosed ? 'color: #888;' : 'color: #888;'}">
                                \${t.transDate} | 결제자: \${t.userNickname}
                            </span>
                            \${actionButtons}
                        </div>
                        <div style="font-size: 1.2em; font-weight: bold; \${isClosed ? 'color: #555;' : 'color: #dc3545;'}">
                            \${t.transAmount.toLocaleString()} 원
                        </div>
                    </li>
                `;
            });
            html += `</ul>`;
            
            if (visibleCount === 0) {
                listView.innerHTML = `<div style="text-align: center; padding: 50px; color: #888;">이번 달 지출 내역이 없습니다.</div>`;
            } else {
                listView.innerHTML = html;
            }
        }

        function openExpenseModal() {
            document.getElementById('expenseModal').style.display = 'block';
            const today = new Date();
            const offset = today.getTimezoneOffset() * 60000; 
            const todayStr = new Date(today.getTime() - offset).toISOString().split("T")[0];
            
            const dateInput = document.getElementById('expDate');
            dateInput.max = todayStr; 
            dateInput.value = todayStr; 
            
            document.getElementById('expAmount').value = '';
            document.getElementById('expMemo').value = '';
        }

        function closeExpenseModal() {
            document.getElementById('expenseModal').style.display = 'none';
        }

        function saveExpense() {
            const groupNum = document.getElementById('settingGroupNum').value;
            const date = document.getElementById('expDate').value;
            const category = document.getElementById('expCategory').value;
            const amount = parseInt(document.getElementById('expAmount').value);
            const memo = document.getElementById('expMemo').value.trim();

            if (!date) { alert("결제 날짜를 선택해주세요."); return; }
            if (!amount || amount <= 0) { alert("결제 금액은 1원 이상이어야 합니다."); return; }
            if (memo.length > 100) { alert("메모는 100자를 초과할 수 없습니다."); return; }

            const params = new URLSearchParams({
                groupNum: groupNum,
                transDate: date,
                gcategoryNum: category,
                transAmount: amount,
                transMemo: memo
            });

            fetch('${pageContext.request.contextPath}/groupLedger/insert.do', {
                method: 'POST',
                body: params
            })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    alert(data.message);
                    closeExpenseModal();
                    const currentYearMonth = document.getElementById('currentMonthLabel').innerText;
                    loadMonthData(currentYearMonth); 
                } else {
                    alert("오류: " + data.message);
                }
            })
            .catch(err => console.error('지출 등록 실패:', err));
        }

        function openEditExpenseModal(transNum, date, catName, amount, memo) {
            document.getElementById('editExpenseModal').style.display = 'block';
            
            const todayStr = new Date(new Date().getTime() - new Date().getTimezoneOffset() * 60000).toISOString().split("T")[0];
            document.getElementById('editExpDate').max = todayStr; 
            
            document.getElementById('editTransNum').value = transNum;
            document.getElementById('editExpDate').value = date;
            document.getElementById('editExpAmount').value = amount;
            document.getElementById('editExpMemo').value = memo;
            
            const select = document.getElementById('editExpCategory');
            for(let i=0; i<select.options.length; i++) {
                if(select.options[i].text === catName) {
                    select.selectedIndex = i;
                    break;
                }
            }
        }
        function closeEditExpenseModal() { document.getElementById('editExpenseModal').style.display = 'none'; }

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

            const params = new URLSearchParams({ 
                gtransNum: transNum, 
                groupOwnerNum: groupOwnerNum, 
                transDate: date, 
                gcategoryNum: categoryNum, 
                categoryName: categoryName,
                transAmount: amount, 
                transMemo: memo 
            });

            fetch('${pageContext.request.contextPath}/groupLedger/editTransaction.do', { method: 'POST', body: params })
            .then(res => res.json()).then(data => {
                if (data.success) { 
                    alert(data.message); 
                    closeEditExpenseModal(); 
                    loadMonthData(document.getElementById('currentMonthLabel').innerText); 
                } 
                else { alert("오류: " + data.message); }
            }).catch(err => console.error(err));
        }

        function deleteTransaction(transNum) {
            if(!confirm("이 지출 내역을 정말 삭제하시겠습니까? (삭제 로그가 기록됩니다)")) return;
            
            const params = new URLSearchParams({ gtransNum: transNum, groupOwnerNum: groupOwnerNum });
            fetch('${pageContext.request.contextPath}/groupLedger/removeTransaction.do', { method: 'POST', body: params })
            .then(res => res.json()).then(data => {
                if (data.success) { 
                    alert(data.message); 
                    loadMonthData(document.getElementById('currentMonthLabel').innerText); 
                } 
                else { alert("오류: " + data.message); }
            }).catch(err => console.error(err));
        }
        
        function openCategoryModal() {
            document.getElementById('categoryManageModal').style.display = 'block';
            document.getElementById('newCategoryName').value = '';
        }

        function closeCategoryModal() {
            document.getElementById('categoryManageModal').style.display = 'none';
        }

        function addCategory() {
            const name = document.getElementById('newCategoryName').value.trim();
            if(!name) { alert("카테고리명을 입력해주세요."); return; }
            if(name === '미분류') { alert("시스템 예약어인 '미분류'는 사용할 수 없습니다."); return; }

            const groupNum = '${group.groupNum}';
            const params = new URLSearchParams({ groupNum: groupNum, categoryName: name });

            fetch('${pageContext.request.contextPath}/groupLedger/addCategory.do', {
                method: 'POST',
                body: params
            }).then(res => res.json()).then(data => {
                if (data.success) {
                    document.getElementById('newCategoryName').value = '';
                    fetchCategoryList(); 
                } else {
                    alert("오류: " + data.message);
                }
            }).catch(err => console.error('카테고리 등록 실패:', err));
        }

        function editCategory(catNum, oldName) {
            const newName = prompt("새로운 카테고리명을 입력하세요 (최대 20자)", oldName);
            if(newName === null || newName.trim() === "") return;
            if(newName.trim() === '미분류') { alert("시스템 예약어인 '미분류'는 사용할 수 없습니다."); return; }
            if(newName.trim() === oldName) return;

            const groupNum = '${group.groupNum}';
            const params = new URLSearchParams({ groupNum: groupNum, categoryNum: catNum, categoryName: newName.trim() });

            fetch('${pageContext.request.contextPath}/groupLedger/editCategory.do', {
                method: 'POST',
                body: params
            }).then(res => res.json()).then(data => {
                if (data.success) {
                    fetchCategoryList();
                    const currentYearMonth = document.getElementById('currentMonthLabel').innerText;
                    loadMonthData(currentYearMonth);
                } else {
                    alert("오류: " + data.message);
                }
            }).catch(err => console.error('카테고리 수정 실패:', err));
        }

        function deleteCategory(catNum, catName) {
            const msg = `정말 '` + catName + `' 카테고리를 삭제하시겠습니까?\n이 카테고리로 등록된 모든 지출 내역은 '미분류'로 자동 이관됩니다.`;
            if(!confirm(msg)) return;

            const groupNum = '${group.groupNum}';
            const params = new URLSearchParams({ groupNum: groupNum, categoryNum: catNum, categoryName: catName });

            fetch('${pageContext.request.contextPath}/groupLedger/removeCategory.do', {
                method: 'POST',
                body: params
            }).then(res => res.json()).then(data => {
                if (data.success) {
                    alert(data.message);
                    fetchCategoryList();
                    const currentYearMonth = document.getElementById('currentMonthLabel').innerText;
                    loadMonthData(currentYearMonth);
                } else {
                    alert("오류: " + data.message);
                }
            }).catch(err => console.error('카테고리 삭제 실패:', err));
        }
        
        function openLogModal() {
            document.getElementById('logModal').style.display = 'block';
            
            const groupNum = document.getElementById('settingGroupNum').value;
            
            fetch(`${pageContext.request.contextPath}/groupLedger/getLogs.do?groupNum=\${groupNum}`)
            .then(res => res.json())
            .then(data => {
                const area = document.getElementById('logListArea');
                
                if (data.length === 0) {
                    area.innerHTML = '<li style="text-align:center; padding:30px; color:#888;">아직 기록된 변경 이력이 없습니다.</li>';
                    return;
                }
                
                let html = '';
                data.forEach(log => {
                    let logMessage = '';
                    
                    if (log.actionType === 'U') {
                        logMessage = `[수정] <b style="color:#ffc107;">\${log.beforeCategory} (\${log.beforeAmount.toLocaleString()}원)</b> ➔ 
                                      <b style="color:#28a745;">\${log.afterCategory} (\${log.afterAmount.toLocaleString()}원)</b>`;
                    } else if (log.actionType === 'D') {
                        logMessage = `[삭제] <b style="color:#dc3545;">\${log.beforeCategory} (\${log.beforeAmount.toLocaleString()}원)</b> 내역 영구 삭제`;
                    }

                    const memoDisplay = log.transMemo ? log.transMemo : '메모 없음';

                    html += `
                        <li style="padding: 12px 10px; border-bottom: 1px solid #f0f0f0;">
                            <div style="font-size: 0.85em; color: #666; margin-bottom: 5px;">
                                \${log.createdAtStr} | 행위자: <b>\${log.userNickname}</b> 
                                (메모: \${memoDisplay})
                            </div>
                            <div style="font-size: 0.95em; line-height: 1.4;">
                                \${logMessage}
                            </div>
                        </li>
                    `;
                });
                
                area.innerHTML = html;
            })
            .catch(err => console.error('이력 로드 실패:', err));
        }
     
        let isClosing = false; 

        function closeLedgerPeriod() {
            if (isClosing) return; 
            
            const msg = "정말 현재 장부를 마감하시겠습니까?\n\n- 현재까지의 모든 지출은 과거 기록으로 보관됩니다.\n- 멤버 간 1/N 정산 결과가 스냅샷으로 영구 저장됩니다.\n- 장부 잔액이 0원으로 초기화되며 새 회차가 시작됩니다.";
            
            if (!confirm(msg)) return;
            
            isClosing = true; 
            
            const params = new URLSearchParams({ 
                groupNum: document.getElementById('settingGroupNum').value,
                groupOwnerNum: groupOwnerNum 
            });

            fetch('${pageContext.request.contextPath}/settlement/closePeriod.do', {
                method: 'POST',
                body: params
            })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    alert(data.message);
                    window.location.reload(); 
                } else {
                    alert("마감 실패: " + data.message);
                    isClosing = false; 
                }
            })
            .catch(err => {
                console.error('장부 마감 중 에러 발생:', err);
                isClosing = false;
            });
        }
        
        function refreshCurrentView() {
            if (document.getElementById('calendarView').style.display === 'block') {
                renderCalendar();
            } else {
                renderList();
            }
        }

        // 과거 정산 보관함 JS 동작 구현부 추가
        function openArchiveModal() {
            document.getElementById('archiveModal').style.display = 'block';
            document.getElementById('archiveDetailArea').style.display = 'none';
            document.getElementById('archivePeriodList').style.display = 'block';
            
            const groupNum = '${group.groupNum}'; 
            
            fetch(`${pageContext.request.contextPath}/groupLedger/getClosedPeriods.do?groupNum=\${groupNum}`)
            .then(res => res.json())
            .then(data => {
                let html = '';
                if(data.length === 0) {
                    html = '<div style="text-align:center; padding:30px; color:#888;">보관된 과거 정산 내역이 없습니다.</div>';
                } else {
                    data.forEach(p => {
                        html += `
                            <div onclick="loadArchiveDetails(\${p.periodNum}, \${p.periodSeq})" 
                                 style="padding:15px; border:1px solid #ddd; margin-bottom:10px; border-radius:5px; cursor:pointer; background:#f8f9fa; transition:background 0.2s;"
                                 onmouseover="this.style.background='#e2e6ea'" onmouseout="this.style.background='#f8f9fa'">
                                <strong style="font-size:1.1em; color:#333;">제 \${p.periodSeq}회차 정산 기록</strong> 
                                <span style="font-size:0.85em; color:#666; display:block; margin-top:5px;">(\${p.startDate} ~ \${p.endDate})</span>
                            </div>`;
                    });
                }
                document.getElementById('archivePeriodList').innerHTML = html;
            })
            .catch(err => console.error('회차 목록 로드 실패:', err));
        }

        function closeArchiveModal() {
            document.getElementById('archiveModal').style.display = 'none';
        }

        function backToPeriodList() {
            document.getElementById('archiveDetailArea').style.display = 'none';
            document.getElementById('archivePeriodList').style.display = 'block';
        }

        function loadArchiveDetails(periodNum, periodSeq) {
            document.getElementById('archivePeriodList').style.display = 'none';
            document.getElementById('archiveDetailArea').style.display = 'block';
            document.getElementById('detailPeriodTitle').innerText = `제 \${periodSeq}회차 상세 내역`;
            
            fetch(`${pageContext.request.contextPath}/groupLedger/getArchiveDetails.do?periodNum=\${periodNum}`)
            .then(res => res.json())
            .then(data => {
                
                let snapHtml = '';
                if(!data.snapshots || data.snapshots.length === 0) {
                    snapHtml = '<li style="color:#666;">정산할 금액이 없습니다 (모두 0원)</li>';
                } else {
                    data.snapshots.forEach(s => {
                        snapHtml += `
                            <li style="padding:8px 0; border-bottom:1px dashed #ccc; font-size:1.05em;">
                                <b style="color:#dc3545;">\${s.payerNickname}</b> 님이 
                                <b style="color:#28a745;">\${s.receiverNickname}</b> 님에게 
                                <strong style="color:#333;">\${s.settleAmount.toLocaleString()}원</strong> 송금
                            </li>`;
                    });
                }
                document.getElementById('snapshotList').innerHTML = snapHtml;
                
                let transHtml = '';
                if(!data.transactions || data.transactions.length === 0) {
                    transHtml = '<li style="padding:20px; text-align:center; color:#888;">지출 내역이 없습니다.</li>';
                } else {
                    data.transactions.forEach(t => {
                        const safeMemo = t.transMemo ? t.transMemo : '메모 없음';
                        transHtml += `
                            <li style="display: flex; justify-content: space-between; align-items: center; padding: 12px; border-bottom: 1px solid #eee; opacity: 0.6; background:#fafafa;">
                                <div>
                                    <strong style="font-size: 1.05em; margin-bottom: 5px; display:inline-block; color:#555;">\${t.categoryName} - \${safeMemo}</strong>
                                    <span style="font-size: 0.85em; display: block; color: #888;">\${t.transDate} | 결제자: \${t.userNickname}</span>
                                </div>
                                <div style="font-size: 1.1em; font-weight: bold; color:#6c757d;">
                                    \${t.transAmount.toLocaleString()} 원
                                </div>
                            </li>`;
                    });
                }
                document.getElementById('archiveTransactionList').innerHTML = transHtml;
            })
            .catch(err => console.error('상세 내역 로드 실패:', err));
        }
    </script>
</body>
</html>