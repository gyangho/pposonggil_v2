import React from "react";
import styled from "styled-components";
import { useNavigate } from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faListUl, faBookmark, faUserCircle } from "@fortawesome/free-solid-svg-icons";
import api from "../api/api";

const Container = styled.div`
width: 100%;
height: 100%;
background-color: #E2E2E2;
display: flex;
flex-direction: column;
align-items: center;
padding-top: 20px;
`;

const ProfileSection = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 40px;
  padding-top: 30px;
`;

const ProfileIcon = styled(FontAwesomeIcon)`
  font-size: 80px;
  color: gray;
`;

const UserName = styled.div`
  font-size: 24px;
  font-weight: bold;
  margin-top: 10px;
`;

const Button = styled.button`
width: 80%;
padding: 15px;
background-color: tomato;
color: white;
border: none;
border-radius: 5px;
cursor: pointer;
display: flex;
align-items: center;
font-size: 18px;
margin-top: 20px;
justify-content: center;

&:hover {
  background-color: darkred;
}

svg {
  margin-left: 10px;
}
`;


function MyPage() {
  const navigate = useNavigate();
  const writerId = 1; // 실제 사용자의 writerId로 대체해야 함

  const handleButtonClick = async () => {
    if (!writerId) {
      alert('사용자 정보를 불러오는 중입니다. 잠시만 기다려주세요.');
      return;
    }
    try {
      const response = await api.get(`http://localhost:8080/api/boards/by-member/1`);
      const userPosts = response.data.filter(post => post.writerId === writerId);
      if (userPosts.length > 0) {
        navigate(`/member-posting/${writerId}`);//이게 맞나..
      } else {
        alert('작성한 게시글이 없습니다.');
      }
    } catch (error) {
      console.error('Error fetching posts:', error);
    }
  };

  return (
    <React.Fragment>
      <Container>
        <ProfileSection>
          <ProfileIcon icon={faUserCircle} />
          <UserName>nickName1</UserName>
        </ProfileSection>
        <Button onClick={() => handleButtonClick('member-posting')}>
          내가 작성한 게시글
          <FontAwesomeIcon icon={faListUl} />
        </Button>
        <Button onClick={() => handleButtonClick('saved-routes')}>
          내가 저장한 뽀송 경로
          <FontAwesomeIcon icon={faBookmark} />
        </Button>
      </Container>
    </React.Fragment>
  )
};

export default MyPage;
