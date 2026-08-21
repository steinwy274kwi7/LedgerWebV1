/**
 * findPwForm.js - 비밀번호 찾기 폼 스크립트
 */

document.addEventListener("DOMContentLoaded", function() {
    
    // 1. 백엔드에서 전달된 메시지가 있으면 alert 띄우기 (회원정보 불일치 등)
    if (window.AppConfig && window.AppConfig.serverMessage) {
        alert(window.AppConfig.serverMessage);
    }

    // 2. 폼 제출 시 프론트엔드 유효성 검사
    const form = document.getElementById('findPwForm');
    if (form) {
        form.addEventListener('submit', function(event) {
            const phoneInput = document.getElementById('userPhone').value.trim();
            
            // 전화번호에 하이픈(-) 등 숫자 이외의 문자가 있는지 검사
            const phoneRegex = /^[0-9]+$/;
            
            if (!phoneRegex.test(phoneInput)) {
                alert("전화번호는 '-' 기호 없이 숫자만 연속해서 입력해 주세요.");
                event.preventDefault(); // 폼 제출 차단
                document.getElementById('userPhone').focus();
                return false;
            }
        });
    }
});