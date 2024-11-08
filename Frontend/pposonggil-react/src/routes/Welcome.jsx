function Welcome() {
    function getQueryParam(param) {
        let urlParams = new URLSearchParams(window.location.search);
        return urlParams.get(param);
    }
    // URL에서 accessToken 값을 가져오기
    let accessToken = getQueryParam('accessToken');
    let id = getQueryParam('id');
    let nickname = getQueryParam('nickname');
    if (accessToken) {
        localStorage.setItem('token', accessToken);
        localStorage.setItem('id', id);
        localStorage.setItem('nickname', nickname);
        window.location.replace('/')

    }
    else {
        window.location.replace(window.location.hostname + '/login');
    }
    return null;
}

export default Welcome;