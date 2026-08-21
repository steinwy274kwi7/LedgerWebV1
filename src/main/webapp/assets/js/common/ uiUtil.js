/**
 * uiUtil.js - 프로젝트 전역 UI 제어 유틸리티
 */
document.addEventListener("DOMContentLoaded", function() {
    
    // 글로벌 더블클릭(중복 전송) 방지 로직
    document.addEventListener('submit', function(event) {
        const form = event.target;
        
        // 제출(submit) 이벤트가 발생한 대상이 form 태그일 경우
        if (form.tagName.toLowerCase() === 'form') {
            const submitBtn = form.querySelector('button[type="submit"], input[type="submit"]');
            
            if (submitBtn) {
                // 이미 제출된 상태라면 이벤트를 강제로 막음
                if (submitBtn.getAttribute('data-submitted') === 'true') {
                    event.preventDefault();
                    return false;
                }
                
                // 첫 제출 시 플래그 설정 및 시각적 피드백(버튼 비활성화 느낌) 부여
                submitBtn.setAttribute('data-submitted', 'true');
                submitBtn.style.opacity = '0.7';
                submitBtn.style.pointerEvents = 'none'; // 클릭 방지
                
                // 🌟 button 태그와 input 태그를 구분해서 텍스트 변경
                if (submitBtn.tagName.toLowerCase() === 'button') {
                    submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> 처리 중...'; 
                } else {
                    submitBtn.value = '처리 중...'; // input 태그용 안전장치
                }
            }
        }
    });

});