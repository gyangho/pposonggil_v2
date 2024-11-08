import styled from "styled-components"
import googleImage from "../assets/google_login.png";
import kakaoImage from "../assets/kakao_login.png"
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCloud } from "@fortawesome/free-solid-svg-icons";
import { useResetRecoilState } from "recoil";
import { navState } from "../recoil/atoms";


export const KAKAO_AUTH_URL = `/oauth2/authorization/kakao`
export const GOOGLE_AUTH_URL = `/oauth2/authorization/google`;

function Login() {
  const resetNav = useResetRecoilState(navState);
  resetNav();
  const handlekakaoLogin = () => {
    window.location.href = KAKAO_AUTH_URL;
  };
  const handlegoogleLogin = () => {
    window.location.href = GOOGLE_AUTH_URL;
  };

  return (
    <LoginContainer>
      <LoginBox>
        <Logo><FontAwesomeIcon icon={faCloud} /></Logo>
        <Title>뽀송길</Title>
        <SubTitle>뽀송길과 함께 시작하는<br /> 쾌적한 경로 탐색</SubTitle>

        <GoogleBtn>
          <img
            style={{ cursor: "pointer", width: "40%" }}
            src={googleImage}
            onClick={handlegoogleLogin}
          >
          </img>
        </GoogleBtn>
        <KakaoBtn>
          <img
            style={{ cursor: "pointer", width: "40%" }}
            src={kakaoImage}
            onClick={handlekakaoLogin}
          >
          </img>
        </KakaoBtn>

        <Footer>
          <div style={{ marginBottom: "5px" }}>안경과 수건</div>
          <div>2024 캡스톤 디자인 프로젝트 2</div>

        </Footer>
      </LoginBox>
    </LoginContainer>
  );
}

export default Login


const LoginContainer = styled.div`
  display: flex;
  height: 100%;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-color: #ddf3fe;

`;

const LoginBox = styled.div`
  font-family: 'Bagel Fat One', cursive;
  display: sticky;
  top: 0;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 80%;
  width: 70%;
  background-color: whitesmoke;
  border-radius: 15px;
  position: relative;
`;



const Title = styled.div`
  font-size: 40px;
  color: #052133;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items:center;
`;

const SubTitle = styled.div`
  font-size: 14px;
  color: gray;
  margin-top: 5%;
  margin-bottom: 25%;;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items:center;
  text-align: center;
`;

const Logo = styled(Title)`
  margin-top: 20%;
  height: 10%;
  margin-bottom: 10px;
  font-size: 40px;
  color: skyblue;
  /* background-color: tomato; */
`;

const KakaoBtn = styled.div`
  bottom: 35%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: sticky;

  img {
    cursor: pointer;
    width: 40%;
  }

  @media (max-width: 768px) {
    img {
      width: 60%;
    }
  }

  @media (max-width: 480px) {
    img {
      width: 80%;
    }
  }
`;

const GoogleBtn = styled.div`
  bottom: 40%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: sticky;

  img {
    cursor: pointer;
    width: 40%;
  }

  @media (max-width: 768px) {
    img {
      width: 60%;
    }
  }

  @media (max-width: 480px) {
    img {
      width: 80%;
    }
  }
`;
const Footer = styled(Title)`
  bottom: 10%;
  display: block;
  justify-content: center;
  align-items: center;
  text-align: center;
  font-size: 12px;
  color: #003f5e;
  position: absolute;
`;