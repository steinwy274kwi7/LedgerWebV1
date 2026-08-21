/**
 * createForm.js - 새 공동 가계부 만들기 스크립트 (AjaxUtil 적용 완결판)
 * 의존성: window.AppConfig, AjaxUtil
 */

// 1. 순수 폼 데이터 유효성 검사 함수 (기존 로직 유지)
function validateForm() {
    const nameInput = document.getElementById('groupName');
    const name = nameInput.value.trim();
    
    if (!name) {
        alert("방 이름을 입력해 주세요.");
        nameInput.focus();
        return false;
    }
    
    if (name.length > 20) {
        alert("방 이름은 20자를 초과할 수 없습니다.");
        nameInput.focus();
        return false;
    }
    
    return true;
}

// 2. DOM 로드 완료 후 폼 Submit 이벤트 가로채기
document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('createGroupForm');
    
    if (form) {
        form.addEventListener('submit', function(event) {
            // 브라우저의 기본 폼 전송(새로고침) 동작을 막음
            event.preventDefault(); 
            
            // 1단계: 유효성 검사 통과 여부 확인
            if (!validateForm()) {
                return;
            }
            
            // 2단계: 폼 안에 있는 모든 데이터를 수집 (name 속성 기준)
            const formData = new FormData(form);
            const params = {};
            formData.forEach((value, key) => {
                params[key] = value;
            });
            
            // 3단계: AjaxUtil을 통해 서버로 POST 전송
            AjaxUtil.request(AppConfig.contextPath + '/group/create.do', params)
            .then(data => {
                if (data.success) {
                    alert('성공적으로 새로운 공동 가계부를 만들었습니다!');
                    // 성공 시: 백엔드에서 내려준 생성된 방 번호(groupNum)로 즉시 이동
                    // (※주의: 백엔드 GroupController의 create.do가 JSON {success: true, groupNum: 123} 형태로 응답하도록 수정이 필요할 수 있습니다.)
                    if(data.groupNum) {
                        window.location.href = AppConfig.contextPath + '/group/main.do?groupNum=' + data.groupNum;
                    } else {
                        // groupNum을 못 받았다면 안전하게 리스트로 이동
                        window.location.href = AppConfig.contextPath + '/group/list.do';
                    }
                } else {
                    // 실패 시: 화면 깜빡임 없이 에러 메시지만 띄우고 입력한 데이터는 그대로 유지
                    alert("방 생성 실패: " + data.message);
                }
            });
        });
    }
});