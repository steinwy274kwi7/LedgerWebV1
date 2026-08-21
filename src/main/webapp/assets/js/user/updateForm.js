/**
 * updateForm.js - 정보 수정 폼 스크립트
 */

document.addEventListener("DOMContentLoaded", function() {
    
    // 1. 서버 메시지 처리
    if (window.AppConfig && window.AppConfig.serverMessage && window.AppConfig.serverMessage.trim() !== '') {
        alert(window.AppConfig.serverMessage);
    }
    
    // 2. 화면 진입 시 새 비밀번호 칸에 포커스
    const userPwInput = document.getElementById('userPw');
    if (userPwInput) {
        userPwInput.focus();
    }

    // 3. 폼 유효성 검사 (숫자만 입력되도록 방어)
    const updateForm = document.getElementById('updateForm');
    if (updateForm) {
        updateForm.addEventListener('submit', function(event) {
            const phone = document.getElementById('userPhone').value.trim();
            const birth = document.getElementById('userBirth').value.trim();
            const numRegex = /^[0-9]+$/;

            if (!numRegex.test(phone)) {
                alert("전화번호는 '-' 기호 없이 숫자만 연속해서 입력해 주세요.");
                event.preventDefault();
                document.getElementById('userPhone').focus();
                return false;
            }

            if (!numRegex.test(birth) || birth.length !== 8) {
                alert("생년월일은 8자리 숫자만 입력해 주세요. (예: 20000101)");
                event.preventDefault();
                document.getElementById('userBirth').focus();
                return false;
            }
        });
    }
});