<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>나의 공동 가계부 - 리스트</title>
    <!-- Bootstrap 5 CSS CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- 커스텀 CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/group_manage/groupList.css">
</head>
<body class="bg-light">

    <div class="container my-5" style="max-width: 1000px;">
        <!-- 상단 헤더 영역 -->
        <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-4 gap-3">
            <h2 class="fw-bold text-dark m-0">나의 공동 가계부</h2>
            
            <div class="d-flex flex-wrap gap-2">
                <button class="btn btn-success fw-bold shadow-sm" onclick="openSearchModal()">
                    🔍 공개 가계부 구경하기
                </button>
                <a href="${pageContext.request.contextPath}/group/statistics.do" class="btn btn-secondary fw-bold shadow-sm">
                    📊 통계
                </a>
                <a href="${pageContext.request.contextPath}/group/createForm.do" class="btn btn-primary fw-bold shadow-sm">
                    + 새 방 만들기
                </a>
            </div>
        </div>

        <!-- 가계부 그룹 카드 그리드 영역 -->
        <div class="row g-4">
            <c:choose>
                <%-- 가입된 가계부가 없을 경우 --%>
                <c:when test="${empty groupList}">
                    <div class="col-12">
                        <div class="text-center p-5 bg-white border rounded-3 shadow-sm text-muted">
                            <h3 class="fw-bold mb-3">아직 가입된 공동 가계부가 없습니다.</h3>
                            <p class="mb-0">새로운 그룹을 만들거나, 검색을 통해 다른 공개 가계부를 구경해 보세요!</p>
                        </div>
                    </div>
                </c:when>
                
                <%-- 가입된 가계부가 있을 경우 --%>
                <c:otherwise>
                    <c:forEach var="group" items="${groupList}">
                        <!-- 카드 1개 (PC: 3열, 태블릿: 2열, 모바일: 1열 반응형) -->
                        <div class="col-lg-4 col-md-6 col-sm-12">
                            <div class="card h-100 shadow-sm custom-hover-card border-0" onclick="location.href='${pageContext.request.contextPath}/group/ledger.do?groupNum=${group.groupNum}'">
                                
                                <!-- 방장 뱃지 -->
                                <c:if test="${group.groupOwnerNum == loginUser.userNum}">
                                    <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-warning text-dark border border-light shadow" style="font-size: 0.8rem; z-index: 10;">
                                        👑 방장
                                    </span>
                                </c:if>

                                <div class="card-body">
                                    <h5 class="card-title fw-bold text-dark text-truncate mb-2">${group.groupName}</h5>
                                    <p class="card-text text-secondary desc-truncate">
                                        ${empty group.groupDesc ? '설명이 없습니다.' : group.groupDesc}
                                    </p>
                                </div>
                                
                                <div class="card-footer bg-white border-top border-light d-flex justify-content-between align-items-center">
                                    <small class="text-muted fw-bold">👥 ${group.memberCount}명</small>
                                    <small class="text-muted">📅 ${group.createdAt}</small>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <!-- 🌟 Bootstrap 5 공개 가계부 검색 모달 -->
    <div class="modal fade" id="searchModal" tabindex="-1" aria-labelledby="searchModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
            <div class="modal-content border-0 shadow-lg">
                <div class="modal-header bg-light">
                    <h5 class="modal-title fw-bold" id="searchModalLabel">🔍 공개 가계부 검색</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <!-- 검색 입력창 -->
                    <div class="input-group mb-3">
                        <input type="text" id="searchKeyword" class="form-control" placeholder="방 이름을 입력하세요" onkeypress="if(event.keyCode===13) searchGroups()">
                        <button class="btn btn-primary fw-bold" type="button" onclick="searchGroups()">검색</button>
                    </div>
                    
                    <!-- 검색 결과 리스트 -->
                    <ul id="searchResultArea" class="list-group list-group-flush">
                        <!-- JS에서 동적으로 채워짐 -->
                        <li class="list-group-item text-center text-muted py-4 border-0">검색어를 입력해 주세요.</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>

    <!-- JS 환경 변수 -->
    <script>
        window.AppConfig = { contextPath: '${pageContext.request.contextPath}' };
    </script>

    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <!-- 공통 AJAX 모듈 -->
    <script src="${pageContext.request.contextPath}/assets/js/common/ajaxUtil.js"></script>
    <!-- 분리된 커스텀 JS -->
    <script src="${pageContext.request.contextPath}/assets/js/group_manage/groupList.js"></script>

</body>
</html>