<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>나의 공동 가계부</title>
    <style>
        .container { width: 900px; margin: 40px auto; font-family: sans-serif; }
        .header-area { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
        .header-area h2 { margin: 0; color: #333; }
        .btn-create { background: #007BFF; color: white; padding: 10px 20px; border: none; border-radius: 5px; cursor: pointer; font-weight: bold; text-decoration: none; }
        .btn-search { background: #28a745; color: white; padding: 10px 20px; border: none; border-radius: 5px; cursor: pointer; font-weight: bold; text-decoration: none; }
        .btn-search:hover { background: #218838; }
        
        .group-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; }
        .group-card { background: #fff; border: 1px solid #e0e0e0; border-radius: 10px; padding: 20px; cursor: pointer; box-shadow: 0 4px 6px rgba(0,0,0,0.05); transition: transform 0.2s, box-shadow 0.2s; position: relative; }
        .group-card:hover { transform: translateY(-5px); box-shadow: 0 8px 15px rgba(0,0,0,0.1); border-color: #007BFF; }
        
        .group-title { font-size: 1.2em; font-weight: bold; color: #333; margin-bottom: 10px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
        .group-desc { font-size: 0.9em; color: #666; margin-bottom: 15px; height: 40px; overflow: hidden; text-overflow: ellipsis; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; }
        .group-meta { font-size: 0.8em; color: #999; display: flex; justify-content: space-between; border-top: 1px solid #eee; padding-top: 10px; }
        
        .badge-owner { position: absolute; top: -10px; right: -10px; background: #FFC107; color: #000; font-size: 0.7em; font-weight: bold; padding: 5px 10px; border-radius: 20px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        .empty-msg { text-align: center; padding: 50px; color: #999; background: #f8f9fa; border-radius: 10px; grid-column: 1 / -1; }
        
        .btn-stats { 
		    background: #6c757d; 
		    color: white; 
		    padding: 10px 20px; 
		    border: none; 
		    border-radius: 5px; 
		    cursor: pointer; 
		    font-weight: bold; 
		    text-decoration: none; 
		    display: inline-flex; 
		    align-items: center; 
		}
		.btn-stats:hover { background: #5a6268; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header-area">
		    <h2>나의 공동 가계부</h2>
		    <div style="display: flex; gap: 10px;">
                
                <button class="btn-search" onclick="openSearchModal()">
                    공개 가계부 구경하기
                </button>

		        <a href="${pageContext.request.contextPath}/group/statistics.do" class="btn-stats">
		            공동 가계부 통계
		        </a>
		        <button class="btn-create" onclick="location.href='${pageContext.request.contextPath}/group/createForm.do'">
		            + 새 그룹 만들기
		        </button>
		    </div>
		</div>

        <div class="group-grid">
            <c:choose>
                <c:when test="${empty groupList}">
                    <div class="empty-msg">
                        <h3>아직 가입된 공동 가계부가 없습니다.</h3>
                        <p>새로운 그룹을 만들거나, 초대 코드를 통해 그룹에 참여해 보세요!</p>
                    </div>
                </c:when>
                
                <c:otherwise>
                    <c:forEach var="group" items="${groupList}">
                        <div class="group-card" onclick="location.href='${pageContext.request.contextPath}/group/ledger.do?groupNum=${group.groupNum}'">
                            
                            <c:if test="${group.groupOwnerNum == loginUser.userNum}">
                                <div class="badge-owner">방장</div>
                            </c:if>

                            <div class="group-title">${group.groupName}</div>
                            <div class="group-desc">${empty group.groupDesc ? '설명이 없습니다.' : group.groupDesc}</div>
                            
                            <div class="group-meta">
                                <span>멤버 ${group.memberCount}명</span>
                                <span>개설일: ${group.createdAt}</span>
                            </div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <div id="searchModal" style="display:none; position:fixed; top:20%; left:50%; transform:translate(-50%, 0); background:#fff; padding:25px; border-radius:10px; box-shadow:0 10px 25px rgba(0,0,0,0.2); width: 400px; z-index:1000;">
        <div style="display:flex; justify-content:space-between; margin-bottom:15px;">
            <h3 style="margin:0;">공개 가계부 검색</h3>
            <button onclick="closeSearchModal()" style="background:none; border:none; cursor:pointer; font-weight:bold; font-size:1.1em;">X</button>
        </div>
        <div style="display:flex; gap:10px; margin-bottom: 15px;">
            <input type="text" id="searchKeyword" placeholder="방 이름 검색" style="flex:1; padding:8px; border:1px solid #ccc; border-radius:4px;">
            <button onclick="searchGroups()" style="padding:8px 15px; background:#007BFF; color:white; border:none; border-radius:4px; cursor:pointer;">검색</button>
        </div>
        <ul id="searchResultArea" style="list-style:none; padding:0; margin:0; max-height:300px; overflow-y:auto;">
        </ul>
    </div>

    <script>
    function openSearchModal() { document.getElementById('searchModal').style.display = 'block'; }
    function closeSearchModal() { document.getElementById('searchModal').style.display = 'none'; }

    function searchGroups() {
        const keyword = document.getElementById('searchKeyword').value;
        if (!keyword) return;

        fetch('${pageContext.request.contextPath}/group/searchPublic.do?keyword=' + encodeURIComponent(keyword))
        .then(res => res.json())
        .then(data => {
            const resultArea = document.getElementById('searchResultArea');
            resultArea.innerHTML = '';
            if(data.length === 0) {
                resultArea.innerHTML = '<li style="text-align:center; padding:10px; color:#888;">검색 결과가 없습니다.</li>';
                return;
            }
            data.forEach(g => {
                let li = document.createElement('li');
                li.style.cssText = "padding:10px; border-bottom:1px solid #eee; display:flex; justify-content:space-between; align-items:center;";
                
                // 설명이 null일 경우 예외 처리
                let desc = (g.groupDesc === 'null' || !g.groupDesc) ? '설명이 없습니다.' : g.groupDesc;

                li.innerHTML = `
                    <div style="width:70%;">
                        <strong style="display:block; white-space:nowrap; overflow:hidden; text-overflow:ellipsis;">\${g.groupName}</strong>
                        <span style="font-size:0.8em; color:#666; display:block; white-space:nowrap; overflow:hidden; text-overflow:ellipsis;">\${desc}</span>
                    </div>
                    <a href="${pageContext.request.contextPath}/group/ledger.do?groupNum=\${g.groupNum}" style="padding:5px 10px; background:#17a2b8; color:white; text-decoration:none; border-radius:3px; font-size:0.9em; white-space:nowrap;">구경하기</a>
                `;
                resultArea.appendChild(li);
            });
        })
        .catch(err => console.error('검색 실패:', err));
    }
    </script>
</body>
</html>