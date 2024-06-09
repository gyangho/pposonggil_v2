function Welcome() {
    function getQueryParam(param) {
        let urlParams = new URLSearchParams(window.location.search);
        return urlParams.get(param);
    }
    // URL에서 accessToken 값을 가져오기
    let accessToken = getQueryParam('accessToken');
    if (accessToken) {
        localStorage.setItem('token', accessToken)
        window.location.replace('http://localhost:3000')
    }
    else {
        window.location.replace('http://localhost:3000/login');
    }
    return null;
}

export default Welcome