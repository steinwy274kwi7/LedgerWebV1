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
		        <button onclick="openInviteModal()" style="background: none; border: none; font-size: 1.5em; cursor: pointer;" title="멤버 초대">➕</button>
		        <button onclick="openSettingsModal()" style="background: none; border: none; font-size: 1.5em; cursor: pointer;" title="방 설정">⚙️</button>
		    </c:if>
		</div>
        <p id="displayGroupDesc" style="color: #666;">${group.groupDesc}</p>

        <hr style="border: 0; border-top: 1px solid #ddd; margin: 20px 0;">

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
        
        <!-- 자진 탈퇴 버튼 -->
        <div style="margin-top: 20px; text-align: center;">
        	<button onclick="leaveGroup()" style="width: 100%; padding: 12px; background: #dc3545; color: white; border: none; border-radius: 5px; font-weight: bold; cursor: pointer;">🚪 이 방 나가기</button>
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
        
     	// JSTL 값을 JS 변수로 가져오기 (권한 분기용)
        const currentUserNum = parseInt('${loginUser.userNum}');
        const groupOwnerNum = parseInt('${group.groupOwnerNum}');
        
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
                        actionHtml = `<button onclick="kickMember(\${m.userNum})" style="background: none; border: 1px solid #dc3545; color: #dc3545; padding: 5px 10px; border-radius: 3px; cursor: pointer;">강퇴</button>`;
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
        
    </script>
</body>
</html>