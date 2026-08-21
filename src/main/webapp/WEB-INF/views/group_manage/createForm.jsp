<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>새 공동 가계부 만들기</title>
    <!-- Bootstrap 5 CSS CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- 커스텀 CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/group_manage/createForm.css">
</head>
<body class="bg-light">

    <div class="container mt-5">
        <div class="card shadow-sm mx-auto border-0" style="max-width: 550px; border-radius: 12px;">
            <div class="card-body p-5">
                <h2 class="text-center fw-bold mb-4 text-dark">✨ 새 공동 가계부 만들기</h2>
                
                <!-- 기존 JSP 렌더링 에러 메시지(msg) 공간은 남겨두지만, AJAX 처리 시에는 커스텀 Alert을 사용합니다 -->
                <c:if test="${not empty msg}">
                    <div class="alert alert-danger text-center fw-bold py-2 mb-4" role="alert">
                        ${msg}
                    </div>
                </c:if>

                <!-- 🌟 변경 포인트: action과 method 속성을 지우고, id="createGroupForm"을 부여 -->
                <form id="createGroupForm">
                    
                    <div class="mb-4">
                        <label for="groupName" class="form-label fw-bold text-secondary">방 이름 <span class="text-danger">*</span> (최대 20자)</label>
                        <input type="text" name="groupName" id="groupName" class="form-control form-control-lg fs-6" maxlength="20" placeholder="예: ✈️ 제주도 여행계, 🏠 자취방" required>
                    </div>

                    <div class="mb-4">
                        <label for="groupDesc" class="form-label fw-bold text-secondary">간단한 설명 (선택)</label>
                        <textarea name="groupDesc" id="groupDesc" class="form-control" rows="3" maxlength="150" placeholder="어떤 목적으로 사용하는 방인지 적어주세요."></textarea>
                    </div>

                    <div class="mb-4">
                        <label class="form-label fw-bold text-secondary mb-3">정산 방식 선택</label>
                        <div class="row g-3">
                            <div class="col-6">
                                <input type="radio" class="btn-check" name="groupType" id="typeM" value="M" checked>
                                <label class="custom-radio-label w-100 h-100 p-3 text-center" for="typeM">
                                    <div class="radio-title fs-6 mb-1">📅 매월 정산형</div>
                                    <small class="text-muted fw-normal">월마다 모임비/회비를 정산해요</small>
                                </label>
                            </div>
                            <div class="col-6">
                                <input type="radio" class="btn-check" name="groupType" id="typeI" value="I">
                                <label class="custom-radio-label w-100 h-100 p-3 text-center" for="typeI">
                                    <div class="radio-title fs-6 mb-1">💸 자유 정산형</div>
                                    <small class="text-muted fw-normal">건별로 자유롭게 쓰고 1/N 해요</small>
                                </label>
                            </div>
                        </div>
                    </div>

                    <div class="mb-5">
                        <label for="groupOpenYn" class="form-label fw-bold text-secondary">공개 여부 (검색 허용)</label>
                        <select name="groupOpenYn" id="groupOpenYn" class="form-select form-select-lg fs-6">
                            <option value="N">비공개 (초대로만 참여 가능)</option>
                            <option value="Y">공개 (아이디 검색으로 방 노출)</option>
                        </select>
                    </div>

                    <div class="d-grid gap-2">
                        <!-- 버튼 타입을 submit으로 유지하여 브라우저 기본 required 유효성 검사를 활용 -->
                        <button type="submit" class="btn btn-success btn-lg fw-bold">만들기</button>
                        <a href="${pageContext.request.contextPath}/group/list.do" class="btn btn-secondary btn-lg">취소</a>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- JS 백엔드 환경 설정 -->
    <script>
        window.AppConfig = {
            contextPath: '${pageContext.request.contextPath}'
        };
    </script>
    
    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <!-- 공통 AJAX 모듈 -->
    <script src="${pageContext.request.contextPath}/assets/js/common/ajaxUtil.js"></script>
    <!-- 더블클릭 방지 모듈 -->
    <script src="${pageContext.request.contextPath}/assets/js/common/uiUtil.js"></script>
    <!-- 분리된 커스텀 JS 연결 -->
    <script src="${pageContext.request.contextPath}/assets/js/group_manage/createForm.js"></script>

</body>
</html>