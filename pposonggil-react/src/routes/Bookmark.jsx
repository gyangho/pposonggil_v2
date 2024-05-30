import React from "react";
import styled from "styled-components";
import { useSetRecoilState } from "recoil";
import { navState } from "../recoil/atoms";

const Div = styled.div`
  width: 100%;
  height: 100%;
  background-color: #ff599ed7;
  display: flex;
  justify-content: center;
  align-items: center;
`;


function Bookmark() {


  return (
    <React.Fragment>
      <Div>Bookmark<br />즐겨찾기한 경로 목록show</Div>
    </React.Fragment>
  )
}

export default Bookmark