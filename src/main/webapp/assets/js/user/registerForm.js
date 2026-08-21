/**
 * registerForm.js - 회원가입 폼 스크립트
 */

document.addEventListener("DOMContentLoaded", function() {
    
    // 1. 백엔드에서 전달된 에러 메시지(중복 아이디 등)가 있으면 alert 띄우기
    if (window.AppConfig && window.AppConfig.serverMessage && window.AppConfig.serverMessage.trim() !== '') {
        alert(window.AppConfig.serverMessage);
    }
    
    // 2. 화면 진입 시 사용자가 바로 입력할 수 있도록 아이디 입력창에 자동 포커스
    const userIdInput = document.getElementById('userId');
    if (userIdInput) {
        userIdInput.focus();
    }
    
});