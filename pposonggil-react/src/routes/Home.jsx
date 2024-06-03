import React, { useState, useCallback, useRef } from "react";
import { useRecoilState, useRecoilValue, useResetRecoilState, useSetRecoilState } from "recoil";
import { markerState, navState, routeInfoState } from "../recoil/atoms";

import styled from "styled-components";
import { motion } from "framer-motion";

import Map from "../components/Map";
import Weather from "../components/Weather";
import PlaceInfo from "../components/PlaceInfo";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCloud } from "@fortawesome/free-solid-svg-icons";

function Home() {
  const [marker, setMarker] = useRecoilState(markerState);
  const [nav, setNav] = useRecoilState(navState);
  const resetRouteInfo = useResetRecoilState(routeInfoState);
  const resetNav = useResetRecoilState(navState);

  //하단 창 슬라이드 업 애니메이션 
  const [slideUp, setSlideUp] = useState(false);
  const contentBoxRef = useRef(null);

  resetRouteInfo(); //이전 출발지/목적지 디폴트 값으로 초기화
  resetNav(); //네비게이션 바 위치 디폴트 값으로 초기화

  const changeHeight = useCallback(() => {
    setSlideUp(prev => !prev);
    if (contentBoxRef.current) {
      contentBoxRef.current.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }, [slideUp]);

  //스피너 화면 (현재위치 위도/경도 찾은 경우에 스피너 화면 제거)
  //

  // if() {
  //   return (
  //     <Spinner>
  //       <Title>
  //         <FontAwesomeIcon icon={faCloud} />
  //         <span>뽀송길</span>
  //       </Title>
  //       <div>돌아가는 icon 넣고 애니메이션 넣기</div>
  //     </Spinner>
  //   );
  // }

  return (
    <React.Fragment>
      <MapBox
        layout
        initial={{ height: "60%" }}
        animate={{ height: slideUp ? "45%" : "60%" }}
        transition={{ duration: 0.3 }}
      >
        <Map/>
      </MapBox>
      <ContentBox
        layout
        initial={{ height: "40%" }}
        animate={{ height: slideUp ? "55%" : "40%" }}
        transition={{ duration: 0.3 }}
        onClick={changeHeight}
        ref={contentBoxRef}
      >
        <ToggleBar><Bar /></ToggleBar>
        {marker ? <PlaceInfo /> : <Weather />}
      </ContentBox>
    </React.Fragment>
  );
};
export default Home;

const ContentBox = styled(motion.div)`
  overflow-x: hidden;
  overflow-y: scroll;
  display: box;
  justify-content: center;
  align-items: center;
  background-color: whitesmoke;
  position: absolute;
  left: 0;
  right: 0;
  z-index: 200;
  box-shadow: 0px -4px 8px rgba(0, 0, 0, 0.15);
`;

const ToggleBar = styled.div`
  width: 100%;
  height: 20px;
  padding: 15px 0px;
  display: flex;
  justify-content: center;
  align-items: center;
  position: sticky;
  top:0;
  background-color: whitesmoke;
`;

const Bar = styled.div`
  width: 10%;
  height: 6px;
  border-radius: 25px;
  background-color: #d9d9d9;
`;

const MapBox = styled(motion.div)`
  position: relative;
  width: 100%;
`;

/* 스피너 화면 styled-components */
const Spinner = styled.div`
  width: 100%;
  height: 100%;
  padding: 50px;
  background-color: skyblue;
  font-size: 50px;
  font-weight: 900;

  display: block;
  justify-content: center;
  align-items: center;
`;

const Title = styled.div`
  font-family: 'Bagel Fat One', cursive;
`;