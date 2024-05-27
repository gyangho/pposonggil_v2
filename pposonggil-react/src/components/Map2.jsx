import React, { useState, useEffect, useRef, useCallback } from 'react';
import {useRecoilValue } from 'recoil';
import { searchPlace } from '../recoil/atoms';

import styled from "styled-components";
import { motion } from 'framer-motion';
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faLocationDot, faSpinner, faBorderAll } from "@fortawesome/free-solid-svg-icons";

const { kakao } = window;

const BtnContainer = styled.div`
  z-index: 100;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  align-items: flex-end;
  bottom: 20px;
  right: 20px;
  position: absolute;
`;

const LocationBtn = styled(motion.button)`
  all: unset;
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  right: 0;
  bottom: 0;
  z-index: 100;
  position: sticky;
  border-radius: 50%;
  background-color: white;
  padding: 12px;
  box-shadow: 0px 0px 3px 3px rgba(0, 0, 0, 0.1);
  cursor: ${props => (props.isLoading ? 'not-allowed' : 'pointer')};
`;

const GridBtn = styled(LocationBtn)`
  cursor: ${props => (props.isGridLoading ? 'not-allowed' : 'pointer')};
`;

const Icon = styled(FontAwesomeIcon)`
  width: 22px;
  height: 22px;
  transition: color 0.2s ease;
`;

const KakaoMap = styled.div`
  width: 100%;
  height: 100%;
`;

// SearchPlace.js에 들어가는 지도 (홈화면 지도와 다른 설정)
function Map2() {
  const [isLoading, setIsLoading] = useState(false);
  const [isGridLoading, setIsGridLoading] = useState(false);
  const [activeTracking, setActiveTracking] = useState(true);
  const [activeMarker, setActiveMarker] = useState(true);
  const [activeGrid, setActiveGrid] = useState(false);

  const place = useRecoilValue(searchPlace);

  const mapRef = useRef(null);
  const mapInstance = useRef(null);
  const markerInstance = useRef(null);
  const geocoder = useRef(null);

  // 검색한 장소에 맞게 지도 생성
  useEffect(() => {
    // setActiveMarker();
    // setActiveTracking();
    // setActiveGrid();
    
    const script = document.createElement('script');
    script.async = true;
    script.src = "//dapi.kakao.com/v2/maps/sdk.js?appkey=fa3cd41b575ec5e015970670e786ea86&autoload=false";
    document.head.appendChild(script);

    script.onload = () => {
      kakao.maps.load(() => {
        const container = mapRef.current;
        const options = {
          center: new kakao.maps.LatLng(place.lat, place.lon),
          level: 2,
        };
        mapInstance.current = new kakao.maps.Map(container, options);
        geocoder.current = new kakao.maps.services.Geocoder();
        // 지도 중심 위치에 마커를 생성 및 설정
        markerInstance.current = new kakao.maps.Marker({
          position: new kakao.maps.LatLng(place.lat, place.lon),
          map: mapInstance.current,
        });
      });
    };
    console.log("지도 랜더링")
  }, []);

  //위치 추적 버튼 핸들러
  const handleLocationBtn = useCallback(() => {
    setIsLoading(true);
    if (!activeTracking) { 
      if (navigator.geolocation) {
        // 검색 장소 위치로 지도 중심 이동 및 마크업
        const lat = place.lat;
        const lon = place.lon;
        const locPosition = new kakao.maps.LatLng(lat, lon);
        
        mapInstance.current.setCenter(locPosition);
        mapInstance.current.setLevel(2);
        setActiveTracking(true);
          
        //마커 업데이트
        if (!markerInstance.current) {
            markerInstance.current = new kakao.maps.Marker({
              position: locPosition,
              map: mapInstance.current,
            });
        } else {
          markerInstance.current.setPosition(locPosition);
          markerInstance.current.setMap(mapInstance.current);
        }
        setActiveMarker(true);
          setIsLoading(false);    
      } else {
        alert('Geolocation을 사용할 수 없습니다.');
        setIsLoading(false);
      }
    } else {
      if (markerInstance.current) { //마커 있으면 제거
        markerInstance.current.setMap(null);
      }
      setActiveTracking(false);
      setActiveMarker(false);
      setIsLoading(false);
    }
  }, [activeTracking]);

  //격자 표시 함수(아직 구현 안함)
  const handleGridBtn = useCallback(() => {
    setIsGridLoading(true);
    //지도 중심 서울 중심으로 이동 및 지도 확대 레벨 변경
    const seoulPosition = new kakao.maps.LatLng(37.5665, 126.9780);
    mapInstance.current.setCenter(seoulPosition);
    mapInstance.current.setLevel(8);
    
    if(!activeGrid) {
      console.log("show grid!");
    } else {
      console.log("hide grid!");
    }
    setActiveGrid(prev=> !prev);
    setIsGridLoading(false);
  }, [activeGrid]);

  return (
    <KakaoMap id="map" ref={mapRef}>
      <BtnContainer>
        <GridBtn
          id="grid"
          onClick={handleGridBtn}
          isGridLoading={isGridLoading}
        >
          <Icon
            icon={isGridLoading ? faSpinner : faBorderAll}
            style={{ color: activeGrid ? "tomato" : "#216CFF" }}
          />
        </GridBtn>

        <LocationBtn
          id="location"
          onClick={handleLocationBtn}
          isLoading={isLoading}
          initial={{ rotate: 0 }}
          animate={{ rotate: isLoading ? 360 : 0 }}
          transition={{ duration: 1, repeat: isLoading ? Infinity : 0 }}
        >
          <Icon
            icon={isLoading ? faSpinner : faLocationDot}
            style={{ color: activeTracking ? "tomato" : "#216CFF" }}
          />
        </LocationBtn>
      </BtnContainer>
    </KakaoMap>
  );
}

export default Map2;



