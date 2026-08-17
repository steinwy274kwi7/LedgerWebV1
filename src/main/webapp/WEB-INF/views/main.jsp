<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="kr.co.ledger.dto.UserDTO" %>
<%
    UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");

    if (loginUser == null) {
        response.sendRedirect(request.getContextPath() + "/user/loginForm.do");
        return;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>가계부 메인 화면</title>
    
    <style>
        #inviteModal {
            display: none; 
            position: absolute; 
            top: 60px; 
            right: 20px; 
            width: 320px; 
            background: white; 
            border: 1px solid #ccc; 
            box-shadow: 0 4px 8px rgba(0,0,0,0.2); 
            z-index: 1000; 
            padding: 15px; 
            border-radius: 8px;
            max-height: 400px;
    		overflow-y: auto;
        }
    </style>
</head>
<body>
    <h1>환영합니다, ${sessionScope.loginUser.userId}님!</h1>
    
    <a href="${pageContext.request.contextPath}/user/myPage.do">마이페이지</a><br>
    <a href="${pageContext.request.contextPath}/user/logout.do">로그아웃</a>
    <br><br>

    <button onclick="openNotificationModal()" style="padding: 10px; cursor: pointer;">
        초대 알림 확인
    </button>

    <div id="inviteModal">
        <div style="display: flex; justify-content: space-between; border-bottom: 1px solid #eee; padding-bottom: 10px; margin-bottom: 10px;">
            <h3 style="margin: 0; font-size: 16px;">새로운 초대장</h3>
            <button onclick="closeNotificationModal()" style="border: none; background: none; font-size: 16px; cursor: pointer;">X</button>
        </div>
        
        <div id="inviteList"></div>
    </div>
    
    <script>
        function openNotificationModal() {
            document.getElementById('inviteModal').style.display = 'block';
            const listDiv = document.getElementById('inviteList');
            listDiv.innerHTML = '<p style="text-align:center;">로딩 중...</p>';

            fetch('${pageContext.request.contextPath}/group/getInvitations.do')
                .then(response => {
                    if (!response.ok) throw new Error('서버 에러 발생');
                    return response.json(); 
                }) 
                .then(data => {
                    listDiv.innerHTML = ''; 

                    if(data.length === 0) {
                        listDiv.innerHTML = '<p style="text-align:center; color:#888;">도착한 초대장이 없습니다.</p>';
                        return;
                    }

                    data.forEach(item => {
                        let html = `
                            <div style="margin-bottom: 10px; padding: 10px; background: #f9f9f9; border-radius: 5px;">
                                <p style="margin: 0 0 10px 0; font-size: 14px;">
                                    <b>\${item.inviterName}</b>님이 <b>\${item.groupName}</b>에 초대했습니다.
                                </p>
                                <div style="display: flex; gap: 5px;">
                                    <button onclick="respondInvite(\${item.inviteNum}, 'A')" style="flex: 1; padding: 5px; background: #4caf50; color: white; border: none; cursor: pointer;">수락</button>
                                    <button onclick="respondInvite(\${item.inviteNum}, 'R')" style="flex: 1; padding: 5px; background: #f44336; color: white; border: none; cursor: pointer;">거절</button>
                                </div>
                            </div>
                        `;
                        listDiv.innerHTML += html; 
                    });
                })
                .catch(error => {
                    console.error('Error:', error);
                    listDiv.innerHTML = '<p style="text-align:center; color:red;">데이터를 불러올 수 없습니다.</p>';
                });
        }

        function closeNotificationModal() {
            document.getElementById('inviteModal').style.display = 'none';
        }

        function respondInvite(inviteNum, status) {
            const actionName = (status === 'A') ? '수락' : '거절';
            
            if (!confirm('정말 이 초대를 ' + actionName + '하시겠습니까?')) {
                return; 
            }

            fetch('${pageContext.request.contextPath}/group/respondInvite.do', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'inviteNum=' + inviteNum + '&status=' + status
            })
                .then(response => {
                    if (!response.ok) throw new Error('서버 에러');
                    return response.json();
                })
                .then(data => {
                    if (data.success) {
                    	alert('초대를 ' + actionName + '했습니다.');
                        openNotificationModal(); 
                    } else {
                        alert('처리에 실패했습니다.');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('오류가 발생했습니다.');
                });
        }
    </script>

</body>
</html>