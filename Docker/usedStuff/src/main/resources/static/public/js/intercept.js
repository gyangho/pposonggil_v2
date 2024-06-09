import axios from 'axios';
// 요청 인터셉터 추가
axios.interceptors.request.use(request => {
    // localStorage에서 토큰 가져오기
    const token = localStorage.getItem('token');
    if (token) {
        // 요청 헤더에 Authorization 추가
        request.headers['Authorization'] = `Bearer ${token}`;
    }
    return request;
}, error => {
    // 요청 에러 처리
    return Promise.reject(error);
});