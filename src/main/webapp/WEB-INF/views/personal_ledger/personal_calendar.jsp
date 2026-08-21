<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>개인 가계부 - 달력 및 내역</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- FullCalendar JS -->
    <script src='https://cdn.jsdelivr.net/npm/fullcalendar@6.1.15/index.global.min.js'></script>
    <!-- 커스텀 CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/personal_ledger/personal_calendar.css">
</head>
<body class="bg-light">

    <div class="container my-5" style="max-width: 1000px;">
        
        <!-- 상단 헤더 영역 -->
        <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-4 gap-3">
            <div class="d-flex align-items-center gap-3">
                <c:choose>
                    <%-- 남의 가계부 (읽기 전용 모드) --%>
                    <c:when test="${not empty param.targetUserNum}">
                        <h2 class="fw-bold text-dark m-0">👀 ${param.targetNickname}님의 가계부</h2>
                        <button onclick="location.href='${pageContext.request.contextPath}/personal/calendar.do'" class="btn btn-secondary rounded-pill shadow-sm fw-bold px-3">
                            내 가계부로 돌아가기
                        </button>
                    </c:when>
                    
                    <%-- 내 가계부 --%>
                    <c:otherwise>
                        <h2 class="fw-bold text-dark m-0">📔 개인 가계부</h2>
                        <button id="btnPublicToggle" onclick="togglePublicYn()" class="btn rounded-pill shadow-sm fw-bold px-3 ${loginUser.bookOpenYn == 'Y' ? 'btn-info text-white' : 'btn-light text-secondary border'}">
                            ${loginUser.bookOpenYn == 'Y' ? '공개 모드' : '비공개 모드'}
                        </button>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- 타 유저 검색 영역 -->
            <div class="position-relative">
                <div class="input-group shadow-sm">
                    <span class="input-group-text bg-white border-end-0">🔍</span>
                    <input type="text" id="searchUserInput" class="form-control border-start-0 ps-0 rounded-end" placeholder="타 유저 ID 검색" onkeyup="searchPublicUser()">
                </div>
                <!-- 검색 결과 드롭다운 -->
                <ul id="searchResultList" class="list-group position-absolute w-100 shadow mt-1" style="display: none; z-index: 1050; max-height: 250px; overflow-y: auto;"></ul>
            </div>
        </div>
    
        <!-- 달력 영역 -->
        <div class="card border-0 shadow-sm mb-4">
            <div class="card-body p-4">
                <div id='calendar'></div>
            </div>
        </div>

        <!-- 필터 및 컨트롤 영역 -->
        <div class="card border-0 shadow-sm mb-4 bg-white">
            <div class="card-body d-flex flex-wrap align-items-center gap-3 p-3">
                
                <div class="btn-group shadow-sm" role="group">
                    <input type="radio" class="btn-check" name="transType" id="typeAll" value="ALL" checked onclick="applyFilters()">
                    <label class="btn btn-outline-secondary fw-bold" for="typeAll">전체</label>
                    <input type="radio" class="btn-check" name="transType" id="typeInc" value="I" onclick="applyFilters()">
                    <label class="btn btn-outline-danger fw-bold" for="typeInc">수입</label>
                    <input type="radio" class="btn-check" name="transType" id="typeExp" value="E" onclick="applyFilters()">
                    <label class="btn btn-outline-primary fw-bold" for="typeExp">지출</label>
                </div>
                
                <div class="input-group w-auto shadow-sm">
                    <input type="text" id="keyword" class="form-control" placeholder="메모 검색" onkeyup="if(event.keyCode==13) applyFilters()">
                    <button class="btn btn-dark fw-bold" onclick="applyFilters()">검색</button>
                </div>
                
                <button class="btn btn-primary fw-bold shadow-sm" onclick="resetFilters()">전체보기</button>
                
                <!-- 읽기 전용이 아닐 때만 버튼 표시 -->
                <c:if test="${empty param.targetUserNum}">
                    <button class="btn btn-success fw-bold shadow-sm ms-auto" onclick="openTxModal()">+ 내역 추가</button>
                    <button class="btn btn-secondary fw-bold shadow-sm" onclick="openCategoryManageModal()">카테고리 관리</button>
                </c:if>
                
                <span id="dateLabel" class="fw-bold text-dark fs-5 ${not empty param.targetUserNum ? 'ms-auto' : 'ms-3'}">이번 달 전체 내역</span>
            </div>
        </div>

        <!-- 리스트 영역 -->
        <div class="card border-0 shadow-sm">
            <div class="table-responsive">
                <table class="table table-hover align-middle text-center m-0">
                    <thead class="table-light">
                        <tr>
                            <th>날짜</th>
                            <th>분류</th>
                            <th>카테고리</th>
                            <th>금액</th>
                            <th class="text-start">메모</th>
                        </tr>
                    </thead>
                    <tbody id="listBody">
                        <!-- JS에서 렌더링 -->
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- 🌟 내역 추가/수정 모달 -->
    <div class="modal fade" id="txModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content border-0 shadow">
                <div class="modal-header bg-light">
                    <h5 class="modal-title fw-bold" id="modalTitle">내역 등록</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body p-4">
                    <input type="hidden" id="modalTransNum">
                    
                    <div class="mb-3">
                        <label class="form-label fw-bold text-secondary">날짜</label>
                        <input type="date" id="modalDate" class="form-control">
                    </div>
                    
                    <div class="mb-3">
                        <label class="form-label fw-bold text-secondary d-block">분류</label>
                        <div class="btn-group w-100" role="group">
                            <input type="radio" class="btn-check" name="modalType" id="modalTypeInc" value="I">
                            <label class="btn btn-outline-danger" for="modalTypeInc">수입</label>
                            <input type="radio" class="btn-check" name="modalType" id="modalTypeExp" value="E" checked>
                            <label class="btn btn-outline-primary" for="modalTypeExp">지출</label>
                        </div>
                    </div>
                    
                    <div class="mb-3">
                        <label class="form-label fw-bold text-secondary">카테고리</label>
                        <select id="modalCategory" class="form-select">
                            <option value="">카테고리 선택</option>
                        </select>
                    </div>
                    
                    <div class="mb-3">
                        <label class="form-label fw-bold text-secondary">금액 (원)</label>
                        <input type="number" id="modalAmount" class="form-control" placeholder="0">
                    </div>
                    
                    <div class="mb-3">
                        <label class="form-label fw-bold text-secondary">메모</label>
                        <input type="text" id="modalMemo" class="form-control" maxlength="100" placeholder="최대 100자">
                    </div>
                </div>
                <div class="modal-footer bg-light">
                    <button id="btnDelete" class="btn btn-danger me-auto fw-bold" onclick="deleteTransaction()" style="display:none;">삭제</button>
                    <button type="button" class="btn btn-secondary fw-bold" data-bs-dismiss="modal">취소</button>
                    <button type="button" class="btn btn-success fw-bold" onclick="saveTransaction()">저장</button>
                </div>
            </div>
        </div>
    </div>

    <!-- 🌟 카테고리 관리 모달 -->
    <div class="modal fade" id="catManageModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-sm modal-dialog-centered">
            <div class="modal-content border-0 shadow">
                <div class="modal-header bg-light">
                    <h5 class="modal-title fw-bold">카테고리 관리</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="btn-group w-100 mb-3" role="group">
                        <input type="radio" class="btn-check" name="mngCatType" id="mngCatInc" value="I" onclick="loadManageCategories()">
                        <label class="btn btn-outline-danger btn-sm" for="mngCatInc">수입</label>
                        <input type="radio" class="btn-check" name="mngCatType" id="mngCatExp" value="E" checked onclick="loadManageCategories()">
                    	<label class="btn btn-outline-primary btn-sm" for="mngCatExp">지출</label>
                    </div>
                    
                    <ul id="catManageList" class="list-group mb-3" style="max-height: 200px; overflow-y: auto;"></ul>
                    
                    <div class="input-group">
                        <input type="text" id="newCatName" class="form-control form-control-sm" placeholder="새 카테고리 (20자)" maxlength="20">
                        <button class="btn btn-success btn-sm fw-bold" onclick="saveCategoryManage('')">추가</button>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- JS 백엔드 환경 설정 -->
    <script>
        window.AppConfig = {
            contextPath: '${pageContext.request.contextPath}',
            targetUserNum: '${param.targetUserNum}', // 비어있으면 내 가계부
            isReadOnly: '${not empty param.targetUserNum}' === 'true'
        };
    </script>
    
    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <!-- 🌟 공통 AJAX 모듈 -->
    <script src="${pageContext.request.contextPath}/assets/js/common/ajaxUtil.js"></script>
    <!-- 분리된 커스텀 JS -->
    <script src="${pageContext.request.contextPath}/assets/js/personal_ledger/personal_calendar.js"></script>

</body>
</html>