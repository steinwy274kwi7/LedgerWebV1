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
            
            <c:if test="${group.groupOwnerNum == loginUser.userNum}">
                <button onclick="openSettingsModal()" style="background: none; border: none; font-size: 1.5em; cursor: pointer;">⚙️</button>
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
    </script>
</body>
</html>