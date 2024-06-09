import React, { useState } from "react";
import styled from "styled-components";
import { useNavigate } from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faListUl, faBookmark, faUserCircle, faComments, faBan, faFlag, faPen, faUmbrella } from "@fortawesome/free-solid-svg-icons";
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
  margin-bottom: 20px;
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
// background-color: tomato;
// background-color: lightsteelblue;
background-color: steelblue;
color: white;
border: none;
border-radius: 5px;
cursor: pointer;
display: flex;
align-items: center;
font-size: 18px;
margin-top: 20px;
justify-content: center;
opacity: 0.8; /* 투명도 설정 (0.0 - 1.0) */
&:hover {
  background-color: lightsteelblue;
}

svg {
  margin-left: 10px;
}
`;

const myId = localStorage.getItem('id');
const myNickName = localStorage.getItem('nickname');

function MyPage() {
  const navigate = useNavigate();
  const writerId = myId; // 실제 사용자의 writerId로 대체해야 함

  const handleButtonClick = async (type) => {
    if (!writerId) {
      alert('사용자 정보를 불러오는 중입니다. 잠시만 기다려주세요.');
      return;
    }

    try {
      if (type === 'member-posting') {
        navigate(`/member-posting/${writerId}`);
      } else if (type === 'reserved-trades') {//진행중인 중고 거래
        navigate('/OngoingTrades');
      } else if (type === 'blocked-list') {//내가 차단한 사람 목록
        navigate('/blockList');
      } else if (type === 'reported-list') {//내가 차단한 사람 목록
        navigate('/reportList');
      } else {
        alert('해당 기능은 아직 구현되지 않았습니다.');
      }
    } catch (error) {
      console.error('Error fetching data:', error);
    }
  };

  return (
    <React.Fragment>
      <Container>
        <ProfileSection>
          <ProfileIcon icon={faUserCircle} />
          <UserName>{myNickName}</UserName>
        </ProfileSection>
        <Button onClick={() => handleButtonClick('member-posting')}>
          내가 작성한 게시글
          <FontAwesomeIcon icon={faListUl} />
        </Button>
        <Button onClick={() => handleButtonClick('reserved-trades')}>
          진행중인 중고우산 거래
          <FontAwesomeIcon icon={faUmbrella} />
        </Button>
        <Button onClick={() => handleButtonClick('blocked-list')}>
          내가 차단한 차단 목록
          <FontAwesomeIcon icon={faBan} />
        </Button>
        <Button onClick={() => handleButtonClick('reported-list')}>
          내가 신고한 신고 목록
          <FontAwesomeIcon icon={faFlag} />
        </Button>
        {/* <Button onClick={() => alert('내가 작성한 리뷰')}>
          내가 작성한 리뷰
          <FontAwesomeIcon icon={faPen} />
        </Button> */}
      </Container>
    </React.Fragment>
  )
};


export default MyPage;
