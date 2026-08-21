/**
 * ajaxUtil.js - 공통 Fetch API 모듈
 * 모든 비동기 통신(AJAX)을 일괄적으로 처리하고 에러를 중앙 집중식으로 관리합니다.
 */
const AjaxUtil = {
    /**
     * 기본 통신 옵션 (헤더 설정)
     */
    defaultOptions: {
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
            'X-Requested-With': 'XMLHttpRequest' // 백엔드(필터/인터셉터)에서 AJAX 요청임을 알 수 있게 함
        }
    },

    /**
     * 서버로 데이터를 전송하고 JSON 응답을 받는 공통 함수
     * 
     * @param {string} url - 요청을 보낼 주소 (예: AppConfig.contextPath + '/group/list.do')
     * @param {Object} params - 전송할 파라미터 (예: { groupNum: 1, userId: 'test' })
     * @param {string} method - 'GET' 또는 'POST' (기본값: 'POST')
     * @returns {Promise<Object>} - 백엔드의 응답 데이터(JSON) 반환
     */
    request: async function (url, params = {}, method = 'POST') {
        const options = { ...this.defaultOptions, method: method.toUpperCase() };

        try {
            // 1. GET 방식 처리 (URL 파라미터 조립)
            if (options.method === 'GET') {
                const queryString = new URLSearchParams(params).toString();
                if (queryString) {
                    url += (url.includes('?') ? '&' : '?') + queryString;
                }
            } 
            // 2. POST 방식 처리 (Body 데이터 조립)
            else {
                options.body = new URLSearchParams(params).toString();
            }

            // 3. 실제 Fetch API 호출
            const response = await fetch(url, options);

            // 4. HTTP 상태 코드 검사 (400, 401, 403, 500 등 에러 처리)
            if (!response.ok) {
                // 권한 없음(세션 만료 등) 일 경우 로그인 페이지로 튕겨내기 등 공통 처리 가능
                if (response.status === 401) {
                    alert('로그인이 만료되었습니다. 다시 로그인해주세요.');
                    window.location.href = '/login.do'; // 프로젝트 로그인 경로에 맞게 수정
                    throw new Error('Unauthorized');
                }
                throw new Error(`[HTTP Error] Status: ${response.status}`);
            }

            // 5. JSON 파싱 후 반환
            const data = await response.json();
            return data;

        } catch (error) {
            // 6. 네트워크 오류 및 기타 예외 중앙 처리
            console.error('🚀 AJAX 통신 에러:', error);
            
            // HTTP 에러가 아닌 순수 네트워크/문법 에러일 때만 기본 얼럿
            if (error.message !== 'Unauthorized') {
                 alert('서버와 통신하는 중 문제가 발생했습니다.');
            }
            
            // 호출한 쪽에서도 에러를 인지할 수 있도록 그대로 던짐
            throw error;
        }
    }
};