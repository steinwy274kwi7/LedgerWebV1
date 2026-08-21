<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>마이페이지 - 내 정보</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- 커스텀 CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/user/myPage.css">
</head>
<body class="bg-light d-flex align-items-center vh-100">

    <div class="container">
        <div class="row justify-content-center">
            <div class="col-md-6 col-lg-5">
                <div class="card shadow-sm border-0 custom-card">
                    <div class="card-body p-5">
                        
                        <h2 class="text-center fw-bold mb-4 text-dark">내 정보</h2>
                        
                        <!-- 🌟 1. 사용자 정보 테이블 -->
                        <div class="table-responsive mb-4">
                            <table class="table table-bordered align-middle m-0">
                                <tbody>
                                    <tr>
                                        <th class="bg-light text-secondary text-center" style="width: 30%;">아이디</th>
                                        <td class="fw-bold ps-3">${userInfo.userId}</td>
                                    </tr>
                                    <tr>
                                        <th class="bg-light text-secondary text-center">닉네임</th>
                                        <td class="ps-3">${userInfo.userNickname}</td>
                                    </tr>
                                    <tr>
                                        <th class="bg-light text-secondary text-center">이메일</th>
                                        <td class="ps-3">${userInfo.userEmail}</td>
                                    </tr>
                                    <tr>
                                        <th class="bg-light text-secondary text-center">전화번호</th>
                                        <td class="ps-3">${userInfo.userPhone}</td>
                                    </tr>
                                    <tr>
                                        <th class="bg-light text-secondary text-center">생년월일</th>
                                        <td class="ps-3">${userInfo.userBirth}</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                        
                        <!-- 🌟 2. 주요 버튼 영역 -->
                        <div class="d-grid gap-2 mb-4">
                            <button type="button" class="btn btn-primary fw-bold py-2" onclick="location.href='${pageContext.request.contextPath}/user/updateForm.do'">정보 수정</button>
                            <button type="button" class="btn btn-light border fw-bold py-2 text-secondary" onclick="location.href='${pageContext.request.contextPath}/main.do'">메인으로</button>
                        </div>
                        
                        <!-- 🌟 3. 회원 탈퇴 버튼 (우측 하단 작게 배치) -->
                        <div class="text-end border-top pt-3 mt-2">
                            <button type="button" class="btn btn-sm btn-outline-danger" onclick="withdrawUser()">회원 탈퇴</button>
                        </div>

                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- 🌟 4. 정보 수정 완료 모달 (기존 커스텀 팝업 대체) -->
    <div class="modal fade" id="successModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered modal-sm">
            <div class="modal-content border-0 shadow">
                <div class="modal-body text-center p-4">
                    <h4 class="text-success fw-bold mb-3">✅ 수정 완료</h4>
                    <p class="mb-4 text-dark fs-6">${msg}</p>
                    <button type="button" class="btn btn-success fw-bold px-4" data-bs-dismiss="modal">확인</button>
                </div>
            </div>
        </div>
    </div>

    <!-- JS 백엔드 환경 설정 -->
    <script>
        window.AppConfig = {
            contextPath: '${pageContext.request.contextPath}',
            serverMessage: '${msg}'
        };
    </script>
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <!-- 분리된 커스텀 JS -->
    <script src="${pageContext.request.contextPath}/assets/js/user/myPage.js"></script>

</body>
</html>