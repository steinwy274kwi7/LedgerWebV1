/**
 * myPage.js - 마이페이지 스크립트
 */

document.addEventListener("DOMContentLoaded", function() {
    
    // 1. 백엔드에서 전달된 메시지(수정 완료 등)가 있으면 Bootstrap Modal 띄우기
    if (window.AppConfig && window.AppConfig.serverMessage && window.AppConfig.serverMessage.trim() !== '') {
        const modalEl = document.getElementById('successModal');
        if (modalEl) {
            bootstrap.Modal.getOrCreateInstance(modalEl).show();
        }
    }
});

// 2. 회원 탈퇴 로직
function withdrawUser() {
    if (confirm("정말로 탈퇴하시겠습니까?\n탈퇴 시 모든 개인정보가 즉시 파기되며 복구할 수 없습니다.")) {
        location.href = window.AppConfig.contextPath + '/user/withdraw.do';
    }
}