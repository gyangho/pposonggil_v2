import axios from "axios";

const api = axios.create({
  // baseURL: 'https://pposong.ddns.net/api' //배포용
  baseURL: "https://localhost/api", //개발용
});

api.interceptors.request.use(
  (config) => {
    // 백엔드 서버로의 요청일 경우에만 Authorization 헤더 추가
    const token = localStorage.getItem("token");
    if (token) {
      console.log("TOKEN", token);
      config.headers.Authorization = `Bearer ${token}`;
    } else {
      window.location.href("/login");
    }

    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error) => {
    const originalRequest = error.config;
    try {
      if (error.response.status === 401 && !originalRequest._retry) {
        originalRequest._retry = true;

        const data = error.response.data;
        if (data.accessToken) {
          // 새로 발급된 토큰 저장
          let currentAccessToken = data.accessToken;
          localStorage.setItem("token", currentAccessToken);
          originalRequest.headers.Authorization = `Bearer ${currentAccessToken}`;
          alert("토큰 정보가 만료되었습니다.");
          window.location.href = "/";
        } else {
          // 로그인 페이지로 리다이렉션 등
          window.location.href = "/login";
        }
      } else if (error.response.status === 999) {
        alert("차단되었습니다.");
        window.location.href = "/login";
      } else if (error.response.status === 405) {
        alert("내 정보를 불러올 수 없습니다");
        window.location.href = "/login";
      } else if (error.response.status === 406) {
        alert("인증정보가 일치하지 않습니다.");
        window.location.href = "/";
      }
    } catch (refreshError) {
      alert("토큰 정보가 없습니다.");
      window.location.href = "/login";
    }

    return Promise.reject(error);
  }
);

export default api;
