import React, { useState, useCallback, useEffect, } from "react";
import styled from "styled-components";
import { motion } from "framer-motion";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faRotate, faEllipsisVertical, faClockRotateLeft } from "@fortawesome/free-solid-svg-icons";
import { useNavigate } from "react-router-dom";
import { useRecoilState, useResetRecoilState, useSetRecoilState } from "recoil";

import { routeInfoState, navState } from "../recoil/atoms";
import axios from "axios";

const { kakao } = window;

const SearchContainer = styled.div`
  position: sticky;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 20px 0px;
  width: 100%;
  &:last-child {
    margin-top: -10px;
  }
`;

const Container = styled.div`
  width: 65%;
  height: 45px;
  background-color: whitesmoke;
  padding: 0px 20px;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  border-radius: 15px;
  padding: 12px;
  margin: 0px 5px;
  &:last-child { //버튼
    width: 10%;
    background-color: #003E5E;
    justify-content: flex-end;
    color: white;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
  }
`;

const Input = styled(motion.input)`
  text-align: left;
  width: 100%;
  height: 100%;
  font-size: 17px;
  border: none;
  background-color: whitesmoke;
  &:focus {
    outline: none;
    cursor: pointer;
  }
`;

const ResultContainer = styled.div`
  background-color: whitesmoke;
  min-height: 65vh;
  width: 100%;
  max-height: 65vh;
  overflow-y: scroll;
  bottom: 70px;
  left: 0;
  right: 0;
`;

const Icon = styled(FontAwesomeIcon)`
  width: 22px;
  height: 22px;
`;

const SearchedRoute = styled.div`
  display: flex;
  justify-content: flex-start;
  align-items: center;
  padding: 10px 20px;
  height: auto;
  min-height: 45px;
  color: black;
  font-weight: 300;
  font-size: 16px;
  border-bottom: 0.5px solid #aeaeae99;
  background-color: white;
`;

const RouteIcon = styled(FontAwesomeIcon)`
  color: white;
  width: 12px;
  height: 12px;
  padding: 10px;
  background-color: #a3a3a3;
  border-radius: 50%;
`;

const optionIcon = styled(FontAwesomeIcon)`
  
`;

const Box = styled.div`
  background-color: #88d5ff35;
  width: 100%;
  height: 45px;
`;

const RouteInfo = styled.div`
  margin: 0px 20px;
  font-weight: 500;
`;

//////////////////////////////////////////////////////////////////////////

function SearchRoutes() {
  const [route, setRoute] = useRecoilState(routeInfoState);
  const resetRouteInfo = useResetRecoilState(routeInfoState);
  const navigate = useNavigate();

  const setNav = useSetRecoilState(navState);
  setNav("search");

  const [serverResponse, setServerResponse] = useState(null); //서버 응답 저장용

  // 서버에 출발지 목적지 위도 경도 데이터 전송
  const sendRouteToServer = async (route) => {
    try {
      const response = await axios.post("YOUR_SERVER_URL/api/route", {
        origin: {
          lat: route.origin.lat,
          lon: route.origin[0].lon,
        },
        dest: {
          lat: route.dest[0].lat,
          lon: route.dest[0].lon,
        },
      });
      console.log("서버 응답: ", response.data);
      setServerResponse(response.data);
    } catch (error) {
      console.error("서버로 데이터 전송 실패: ", error);
    }
  };

  // Recoil 상태가 변경될 때마다 서버로 데이터를 전송(출발지 목적지 위경도 정보 있을 때만)
  useEffect(() => {
    if (route.origin[0].lat && route.dest[0].lat) {
      sendRouteToServer(route);
    }
  }, [route]);

  // reverse 버튼 핸들러
  const onReverseClick = () => {
    setRoute((prev) => ({
      origin: prev.dest,
      dest: prev.origin,
    }));
  };

  // reset 버튼 클릭 핸들러
  const onResetClick = () => {
    resetRouteInfo();
  };

  console.log("경로 정보 확인: ", route); //test용

  return (
    <React.Fragment>
      <SearchContainer id="origin">
        <Container>
          <Input
            type="text"
            value={route.origin[0].name}
            onClick={()=>navigate('/search')}
            readOnly
            placeholder="출발지 입력"
          />
        </Container>
        <Container>
          <FontAwesomeIcon icon={faRotate} onClick={onReverseClick} />
        </Container>
      </SearchContainer>
      <SearchContainer id="dest">
        <Container>
          <Input
            type="text"
            value={route.dest[0].name}
            onClick={()=>navigate('/search')}
            readOnly
            placeholder="도착지 입력"
          />
        </Container>
        <Container>
          <FontAwesomeIcon icon={faEllipsisVertical} onClick={onResetClick} />
        </Container>
      </SearchContainer>

      <ResultContainer>

      </ResultContainer>
    </React.Fragment>
  );
}

export default SearchRoutes;

