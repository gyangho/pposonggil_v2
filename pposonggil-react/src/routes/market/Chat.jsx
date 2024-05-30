import React from "react"
import { useNavigate, useParams } from "react-router-dom";
import { useSetRecoilState } from "recoil";
import { navState } from "../../recoil/atoms";
import styled from "styled-components";

function Chat() {
  const { author } = useParams();

  const setNav = useSetRecoilState(navState);
  setNav("market");

  const navigate = useNavigate();

  const onScheduleBtnClick = () => {
    navigate('/market/schedule');
  }

  return (
    <React.Fragment>
      <div>Chat 페이지 입니다.</div>
      <ScheduleBtn onClick={()=>onScheduleBtnClick()}>거래 일정 잡기</ScheduleBtn>
    </React.Fragment>
  )
}

export default Chat

const ScheduleBtn = styled.div`
  background-color: tomato;
  padding: 30px;
  cursor: pointer;
  &:hover {
    background-color: whitesmoke;
  }
`;