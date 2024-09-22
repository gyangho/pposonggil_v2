// utils.js
export async function fetchUserInfo() {//비동기 작업 수행
    try {
        const response = await fetch('/api/members', {
            method: 'GET',
            headers: {//요청에 포함될 헤더 정보
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });

        if (!response.ok) {
            throw new Error('Failed to fetch user info');
        }

        const userInfo = await response.json();
        localStorage.setItem('userInfo', JSON.stringify(userInfo)); // 로컬 스토리지에 사용자 정보 저장
        return userInfo;
    } catch (error) {
        console.error('Error fetching user info:', error);
    }
}
