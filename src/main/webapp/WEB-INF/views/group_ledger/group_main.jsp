<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${group.groupName} - 공동 가계부</title>
    <!-- Bootstrap 5 CSS CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- 커스텀 CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/group_ledger/group_main.css">
</head>
<body class="bg-light">

    <div class="container my-5 p-4 bg-white shadow-sm rounded" style="max-width: 1000px;">
        
        <!-- 1. 그룹 헤더 영역 -->
        <div class="d-flex align-items-center gap-2">
            <h2 id="displayGroupName" class="m-0 fw-bold text-dark">${group.groupName}</h2>
            <button onclick="openMemberModal()" class="btn btn-link text-decoration-none fs-4 p-0 ms-2" title="멤버 목록">👥</button>
            
            <c:if test="${group.groupOwnerNum == loginUser.userNum}">
                <button id="inviteBtn" onclick="openInviteModal()" class="btn btn-link text-decoration-none fs-4 p-0" title="멤버 초대">➕</button>
                <button id="settingBtn" onclick="openSettingsModal()" class="btn btn-link text-decoration-none fs-4 p-0" title="방 설정">⚙️</button>
                <button id="closePeriodBtn" onclick="closeLedgerPeriod()" class="btn btn-danger btn-sm ms-3 fw-bold shadow-sm" title="현재 장부를 마감하고 정산합니다">정산 및 마감 💰</button>
            </c:if>
        </div>
        <p id="displayGroupDesc" class="text-secondary mt-2 mb-0">${group.groupDesc}</p>

        <hr class="my-4">

        <!-- 지출 등록 & 관리 버튼 그룹 (멤버에게만 노출) -->
        <c:if test="${isMember}">
            <div class="d-flex justify-content-end gap-2 mb-3 flex-wrap">
                <button onclick="openCategoryModal()" class="btn btn-custom-purple fw-bold shadow-sm">🏷️ 카테고리 관리</button>
                <button onclick="openExpenseModal()" class="btn btn-success fw-bold shadow-sm">+ 지출 등록</button>
                <button onclick="openLogModal()" class="btn btn-info text-white fw-bold shadow-sm">📜 변경 이력</button>
                <button id="previewBtn" onclick="openPreviewModal()" class="btn btn-warning text-white fw-bold shadow-sm" style="display: ${group.settleUseYn == 'Y' ? 'inline-block' : 'none'};">📊 실시간 정산</button>
                <button onclick="openArchiveModal()" class="btn btn-secondary fw-bold shadow-sm">🗂️ 과거 내역</button>
            </div>
        </c:if>
    
        <!-- 상단 뷰 토글 버튼 & 체크박스 -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div class="btn-group shadow-sm">
                <button onclick="switchView('calendar')" class="btn btn-outline-primary active" id="btnCalendarView">달력 뷰</button>
                <button onclick="switchView('list')" class="btn btn-outline-primary" id="btnListView">리스트 뷰</button>
            </div>
            <div class="form-check form-switch fs-6">
                <input class="form-check-input" type="checkbox" id="toggleClosedData" onchange="refreshCurrentView()">
                <label class="form-check-label text-secondary fw-bold" for="toggleClosedData" style="cursor: pointer;">🔒 이전 정산 내역 보기</label>
            </div>
        </div>
        
        <!-- 뷰 영역 -->
        <div id="calendarView" style="display:block;">
            <h4 id="currentMonthLabel" class="text-center mb-3 fw-bold text-primary"></h4>
            <!-- JS 렌더링 -->
        </div>
        <div id="listView" style="display:none;">
            <!-- JS 렌더링 -->
        </div>

    </div>

    <!-- ============================================== -->
    <!-- 모든 모달(Modal) 영역 (생략 없음) -->
    <!-- ============================================== -->

    <!-- 방 설정 모달 -->
    <div class="modal fade" id="settingsModal" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold">방 설정 관리</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <input type="hidden" id="settingGroupNum" value="${group.groupNum}">
                    <input type="hidden" id="groupSettleUseYn" value="${group.settleUseYn}">
                    
                    <div class="mb-3">
                        <label class="form-label fw-bold">방 이름 (최대 20자)</label>
                        <input type="text" id="settingGroupName" value="${group.groupName}" class="form-control" maxlength="20">
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">설명</label>
                        <textarea id="settingGroupDesc" rows="3" class="form-control">${group.groupDesc}</textarea>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">공개 여부</label>
                        <select id="settingGroupOpenYn" class="form-select">
                            <option value="N" ${group.groupOpenYn == 'N' ? 'selected' : ''}>비공개 (초대 전용)</option>
                            <option value="Y" ${group.groupOpenYn == 'Y' ? 'selected' : ''}>공개 (검색 허용)</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">정산 결과 화면 노출</label>
                        <select id="settingSettleUseYn" class="form-select">
                            <option value="Y" ${group.settleUseYn == 'Y' ? 'selected' : ''}>노출함 (1/N 정산 결과 표시)</option>
                            <option value="N" ${group.settleUseYn == 'N' ? 'selected' : ''}>노출 안 함 (지출 내역만 보관)</option>
                        </select>
                    </div>
                    <div class="text-end mt-3 border-top pt-2">
                        <button onclick="deleteGroup()" class="btn btn-link text-danger text-decoration-none p-0 fw-bold">이 방 삭제하기</button>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
                    <button onclick="saveGroupSettings()" class="btn btn-primary">저장</button>
                </div>
            </div>
        </div>
    </div>

    <!-- 멤버 초대 모달 -->
    <div class="modal fade" id="inviteModal" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold">멤버 초대하기</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <label class="form-label fw-bold">초대할 유저 아이디 검색</label>
                    <input type="text" id="searchUserId" class="form-control" placeholder="아이디를 정확히 입력하세요">
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
                    <button onclick="sendGroupInvite()" class="btn btn-success">초대장 발송</button>
                </div>
            </div>
        </div>
    </div>

    <!-- 멤버 관리 모달 -->
    <div class="modal fade" id="memberModal" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold">그룹 멤버 관리</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <ul id="memberListArea" class="list-group mb-3" style="max-height: 250px; overflow-y: auto;">
                        <!-- JS 렌더링 -->
                    </ul>
                    <c:if test="${isMember}">
                        <button onclick="leaveGroup()" class="btn btn-outline-danger w-100 fw-bold mt-2">이 방 나가기</button>
                    </c:if>
                </div>
            </div>
        </div>
    </div>

    <!-- 카테고리 관리 모달 -->
    <div class="modal fade" id="categoryManageModal" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold">공동 카테고리 관리</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="input-group mb-3">
                        <input type="text" id="newCategoryName" class="form-control" maxlength="20" placeholder="새 카테고리명 (20자 이내)">
                        <button onclick="addCategory()" class="btn btn-success fw-bold">추가</button>
                    </div>
                    <ul id="categoryListArea" class="list-group" style="max-height: 250px; overflow-y: auto;">
                        <!-- JS 렌더링 -->
                    </ul>
                </div>
            </div>
        </div>
    </div>
    
    <!-- 지출 등록 모달 -->
    <div class="modal fade" id="expenseModal" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold">새 지출 등록</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label fw-bold">결제 날짜</label>
                        <input type="date" id="expDate" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">카테고리</label>
                        <select id="expCategory" class="form-select"></select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">결제 금액 (원)</label>
                        <input type="number" id="expAmount" class="form-control" min="1" placeholder="금액을 입력하세요" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">메모 (선택)</label>
                        <input type="text" id="expMemo" class="form-control" maxlength="100" placeholder="100자 이내로 적어주세요">
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
                    <button onclick="saveExpense()" class="btn btn-primary">등록하기</button>
                </div>
            </div>
        </div>
    </div>
    
    <!-- 지출 수정 모달 -->
    <div class="modal fade" id="editExpenseModal" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold">지출 내역 수정</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <input type="hidden" id="editTransNum">
                    <div class="mb-3">
                        <label class="form-label fw-bold">결제 날짜</label>
                        <input type="date" id="editExpDate" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">카테고리</label>
                        <select id="editExpCategory" class="form-select"></select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">결제 금액 (원)</label>
                        <input type="number" id="editExpAmount" class="form-control" min="1" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">메모 (선택)</label>
                        <input type="text" id="editExpMemo" class="form-control" maxlength="100">
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
                    <button onclick="saveEditExpense()" class="btn btn-warning fw-bold">수정완료</button>
                </div>
            </div>
        </div>
    </div>
    
    <!-- 무결성 변경 이력 모달 -->
    <div class="modal fade" id="logModal" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold">무결성 변경 이력 <span class="fs-6 text-danger fw-normal">(Read-Only)</span></h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <ul id="logListArea" class="list-group list-group-flush" style="max-height: 60vh; overflow-y: auto;">
                        <!-- JS 렌더링 -->
                    </ul>
                </div>
            </div>
        </div>
    </div>
        
    <!-- 과거 정산 보관함 모달 -->
    <div class="modal fade" id="archiveModal" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold">🗂️ 과거 정산 보관함</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body" style="max-height: 70vh; overflow-y: auto;">
                    
                    <div id="archivePeriodList" class="d-grid gap-2"></div>
                    
                    <div id="archiveDetailArea" style="display:none;">
                        <button onclick="backToPeriodList()" class="btn btn-outline-secondary btn-sm mb-3">⬅️ 목록으로 돌아가기</button>
                        <h4 id="detailPeriodTitle" class="text-primary fw-bold mb-3"></h4>
                        
                        <div class="card bg-light mb-4 border-0 shadow-sm">
                            <div class="card-body">
                                <h6 class="fw-bold mb-3">💰 최종 정산 결과</h6>
                                <ul id="snapshotList" class="list-unstyled m-0"></ul>
                            </div>
                        </div>
                        
                        <h6 class="fw-bold mb-2">📝 상세 지출 내역</h6>
                        <ul id="archiveTransactionList" class="list-group list-group-flush border-top"></ul>
                    </div>

                </div>
            </div>
        </div>
    </div>

    <!-- 실시간 정산 미리보기 모달 -->
    <div class="modal fade" id="previewModal" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold">📊 실시간 정산 현황</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="alert alert-secondary py-2 fs-6 mb-3">
                        현재 진행 중인 회차의 1/N 실시간 시뮬레이션입니다.<br>
                        <small class="text-danger">(소수점 오차는 최대 지출자가 부담하여 맞춥니다.)</small>
                    </div>
                    <ul id="previewListArea" class="list-group list-group-flush" style="max-height:300px; overflow-y:auto;">
                        <!-- JS 렌더링 -->
                    </ul>
                </div>
            </div>
        </div>
    </div>

    <!-- JS 백엔드 환경 설정 -->
    <script>
        window.AppConfig = {
            contextPath: '${pageContext.request.contextPath}',
            groupNum: '${group.groupNum}',
            groupOwnerNum: parseInt('${group.groupOwnerNum}' || '0'),
            currentUserNum: parseInt('${loginUser.userNum}' || '0'),
            isMember: ${isMember}
        };
    </script>
    
    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <!-- 공통 AJAX 모듈 -->
    <script src="${pageContext.request.contextPath}/assets/js/common/ajaxUtil.js"></script>
    <!-- 분리된 커스텀 JS 연결 -->
    <script src="${pageContext.request.contextPath}/assets/js/group_ledger/group_main.js"></script>

</body>
</html>