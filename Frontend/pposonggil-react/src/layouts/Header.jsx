import React from "react";
import styled from "styled-components";
import { Link, useNavigate } from 'react-router-dom';
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCircleLeft, faCircleRight } from "@fortawesome/free-regular-svg-icons";

const HeaderWrapper= styled.div`
  font-family: 'Bagel Fat One', cursive;
  font-size: 25px;
  color: black;
  background-color: white;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  width: 100%;
  height: 70px;
  z-index: 1000;
  box-shadow: 0px 2px 4px rgba(0, 0, 0, 0.1);
`;
const HeaderCol = styled.div`
  width: 20%;
  display: flex;
  justify-content: center;
  align-items: center;
  &:first-child {
    margin-right: auto;
    display: flex;
    justify-content: flex-start;
  }
  &:nth-child(2) {
    width: 60%;
    text-align: center;
  }
  &:last-child {
    margin-left: auto;
    display: flex;
    justify-content: flex-end;
  }
`;
const ArrowIcon = styled(FontAwesomeIcon)`
  cursor: pointer;
  &:hover {
    color: skyblue;
  }
`;

function Header() {
  const navigate = useNavigate();
  return (
    <HeaderWrapper>
      <HeaderCol><ArrowIcon icon={faCircleLeft} onClick={() => navigate(-1)} /></HeaderCol>
      <HeaderCol>뽀송길</HeaderCol>
      <HeaderCol><ArrowIcon icon={faCircleRight} onClick={() => navigate(1)}/></HeaderCol>
    </HeaderWrapper>
  )
}

export default Header