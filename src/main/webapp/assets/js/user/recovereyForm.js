/**
 * recoveryForm.js - 휴면 계정 해제 폼 스크립트
 */

document.addEventListener("DOMContentLoaded", function() {
    
    // 1. 취소 버튼 클릭 시 로그인 화면으로 복귀
    const btnCancel = document.getElementById('btnCancel');
    if (btnCancel) {
        btnCancel.addEventListener('click', function() {
            location.href = window.AppConfig.contextPath + '/user/loginForm.do';
        });
    }

    // 2. 폼 제출 시 간단한 확인창 띄우기 (UX 디테일 향상)
    const recoveryForm = document.getElementById('recoveryForm');
    if (recoveryForm) {
        recoveryForm.addEventListener('submit', function(event) {
            if (!confirm("휴면 상태를 해제하고 다시 서비스를 이용하시겠습니까?")) {
                event.preventDefault(); // 확인을 누르지 않으면 폼 전송 차단
            }
        });
    }
});