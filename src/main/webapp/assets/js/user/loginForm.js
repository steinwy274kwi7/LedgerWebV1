/**
 * loginForm.js - 로그인 폼 스크립트
 */

document.addEventListener("DOMContentLoaded", function() {
    
    // 백엔드에서 전달된 에러 메시지(로그인 실패 등)가 있으면 alert 띄우기
    if (window.AppConfig && window.AppConfig.serverMessage) {
        alert(window.AppConfig.serverMessage);
    }
    
    // 입력창 포커스 편의성: 화면 로드 시 아이디 입력창에 자동 포커스
    const userIdInput = document.getElementById('userId');
    if (userIdInput) {
        userIdInput.focus();
    }
    
});