<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="kr.co.ledger.dto.UserDTO" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
    
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    
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
        
        .chart-container {
            width: 400px;
            height: 400px;
            margin: 20px auto;
        }
    </style>
</head>
<body>
    <jsp:useBean id="now" class="java.util.Date" />

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
    
    <br><hr><br>
    
    <h2 style="text-align: center;"><fmt:formatDate value="${now}" pattern="yyyy년 M월" /> 내역 비율</h2>
    
    <div style="text-align: center;">
        <button onclick="loadChartData('E')">지출 차트 보기</button>
        <button onclick="loadChartData('I')">수입 차트 보기</button>
    </div>

    <div class="chart-container">
        <canvas id="myPieChart"></canvas>
    </div>
    
    <div style="text-align: center; margin-top: 20px;">
        <button onclick="location.href='${pageContext.request.contextPath}/personal/statistics.do'" style="padding: 10px 20px; font-weight: bold; cursor: pointer;">
            통계 더 보기
        </button>
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
        
        let myChartInstance = null; 

        function loadChartData(type, month = '') {
            
            fetch('${pageContext.request.contextPath}/personal/getChartData.do?type=' + type + '&month=' + month)
                .then(response => {
                    if (!response.ok) throw new Error('서버 에러');
                    return response.json();
                })
                .then(data => {
                    if (data.length === 0) {
                        alert('해당 달의 내역이 없습니다!');
                        if (myChartInstance != null) {
                            myChartInstance.destroy();
                        }
                        return;
                    }

                    const labels = data.map(item => item.categoryName); 
                    const amounts = data.map(item => item.totalAmount); 

                    if (myChartInstance != null) {
                        myChartInstance.destroy();
                    }

                    const ctx = document.getElementById('myPieChart').getContext('2d');
                    myChartInstance = new Chart(ctx, {
                        type: 'pie', 
                        data: {
                            labels: labels, 
                            datasets: [{
                                data: amounts, 
                                backgroundColor: [
                                    '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF',
                                    '#FF9F40', '#C9CBCF', '#84FF63', '#E636EB', '#56FFCE'
                                ]
                            }]
                        },
                        options: {
                            responsive: true,
                            plugins: {
                                legend: { position: 'bottom' } 
                            }
                        }
                    });
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('차트 데이터를 불러오는 데 실패했습니다.');
                });
        }

        window.onload = function() {
            loadChartData('E'); 
        };
    </script>

</body>
</html>