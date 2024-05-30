import React, { useState, useCallback, useRef } from "react";
import styled from "styled-components";
import { motion } from "framer-motion";

import Map from "../components/Map";
import SearchBox from "../components/SearchBox";
import Weather from "../components/Weather";
import PlaceInfo from "../components/PlaceInfo";
import { useRecoilState, useResetRecoilState, useSetRecoilState } from "recoil";
import { gridState, locationBtnState, markerState, navState, routeInfoState } from "../recoil/atoms";

const ContentBox = styled(motion.div)`
  overflow-x: hidden;
  overflow-y: scroll;
  display: box;
  justify-content: center;
  align-items: center;
  background-color: whitesmoke;
  position: sticky;
  bottom: 70px;
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
  margin-bottom: 5px;
  background-color: whitesmoke;
`;

const Bar = styled.div`
  width: 10%;
  height: 6px;
  border-radius: 25px;
  background-color: #d9d9d9;
`;

const MapBox = styled(motion.div)`
  /* width: 100%; */
`;

function Home() {
  const [slideUp, setSlideUp] = useState(false);
  const contentBoxRef = useRef(null);

  const [marker, setMarker] = useRecoilState(markerState);
  const [nav, setNav] = useRecoilState(navState);
  const resetRouteInfo = useResetRecoilState(routeInfoState);
  const resetNav = useResetRecoilState(navState);
  
  resetRouteInfo(); //이전 출발지/목적지 디폴트 값으로 초기화
  resetNav(); //네비게이션 바 위치 디폴트 값으로 초기화

  const changeHeight = useCallback(() => {
    setSlideUp(prev => !prev);
    if (contentBoxRef.current) {
      contentBoxRef.current.scrollTo({ top: 0, behavior: 'smooth' });
    }

  }, [slideUp]);

  return (
    <React.Fragment>
      <MapBox
        layout
        initial={{ height: "70%" }}
        animate={{ height: slideUp ? "45%" : "65%" }}
        transition={{ duration: 0.3 }}
      >
        <Map/>
      </MapBox>
      <ContentBox
        layout
        initial={{ height: "30%" }}
        animate={{ height: slideUp ? "55%" : "35%" }}
        transition={{ duration: 0.3 }}
        onClick={changeHeight}
        ref={contentBoxRef}
      >
        <ToggleBar><Bar /></ToggleBar>
        {marker ? <PlaceInfo /> : <Weather />}
      </ContentBox>
    </React.Fragment>
  );
}

export default Home;
