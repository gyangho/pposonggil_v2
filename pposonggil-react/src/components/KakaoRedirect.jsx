import React, { useEffect } from 'react';
import { useSetRecoilState } from 'recoil';
import { useNavigate } from 'react-router-dom';
import { userState } from '../recoil/atoms';

const KakaoRedirect = () => {
  const setUser = useSetRecoilState(userState);
  const navigate = useNavigate();
  const code = new URL(window.location.href).searchParams.get('code');

  const KakaoLogin = async (code) => {
    try {
      const response = await fetch(`http://your-server.com/kakao/callback?code=${code}`, {
        method: 'POST',
      });
      if (!response.ok) {
        throw new Error('Failed to fetch');
      }
      const data = await response.json();
      return data;
    } catch (error) {
      console.error('Error:', error);
      throw error;
    }
  };

  useEffect(() => {
    const fetchUserData = async () => {
      try {
        const userData = await KakaoLogin(code);
        setUser(userData);
        navigate('/home');
      } catch (error) {
        console.error('로그인 실패:', error);
      }
    };

    fetchUserData();
  }, [code, setUser, navigate]);

  return (
    <div>Logging...</div>
  );
}

export default KakaoRedirect;
