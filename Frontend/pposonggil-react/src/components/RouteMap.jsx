/*원본 코드 */
import React, { useState, useEffect, useRef, useCallback } from 'react';
import { useRecoilState, useSetRecoilState } from 'recoil';
import { addressState, currentAddressState, gridState, locationBtnState, mapCenterState, markerState } from '../recoil/atoms';

import styled from "styled-components";
import { motion } from 'framer-motion';
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faLocationCrosshairs, faSpinner, faBorderAll , faCloudShowersHeavy, faL} from "@fortawesome/free-solid-svg-icons";

import SearchBox from './SearchBox';

const { kakao } = window;

function Map() {
  const [isLocationLoading, setIsLocationLoading] = useState(false); //지도 하단 버튼 2개 로딩 상태 관리

  const [address, setAddress] = useRecoilState(addressState); //마크업 및 지도 이동 시 지도 중심 위치 주소 (temp주소)
  const [currentAddress, setCurrentAddress] = useRecoilState(currentAddressState); // 현재 위치 추적 주소
  const [mapCenterAddress, setMapCenterAddress] = useRecoilState(mapCenterState);
  const [activeTracking, setActiveTracking] = useRecoilState(locationBtnState);
  const [activeMarker, setActiveMarker] = useRecoilState(markerState);

  const [isGridLoading, setIsGridLoading] = useState(false); //그리드 버튼 로딩 상태
  const [isGridActive, setIsGridActive] = useState(false); //그리드 활성화 상태
  const [gridObjects, setGridObjects] = useState([]); //그리드 객체 관리

  // 그리드 경계 데이터
  const latLine = [37.69173846, 37.64577895, 37.60262058, 37.55674696, 37.51063517, 37.46494092, 37.42198141];
  const lonLine = [126.7851093, 126.8432583, 126.9010823, 126.9599082, 127.0180783, 127.0766389, 127.1340031, 127.1921521];
  const gridBounds = [];

   // 그리드 경계 좌표 생성
  for (let latIdx = 1; latIdx < latLine.length; latIdx++) {
    for (let lonIdx = 0; lonIdx < lonLine.length - 1; lonIdx++) {
      if (latIdx === 1 && lonIdx !== 4 && lonIdx !== 5) continue;
      if (latIdx === 2 && lonIdx !== 2 && lonIdx !== 3 && lonIdx !== 4 && lonIdx !== 5) continue;
      if (latIdx === 6 && lonIdx !== 1 && lonIdx !== 2 && lonIdx !== 4) continue;

      const sw = new kakao.maps.LatLng(latLine[latIdx - 1], lonLine[lonIdx]);
      const ne = new kakao.maps.LatLng(latLine[latIdx], lonLine[lonIdx + 1]);
      gridBounds.push(new kakao.maps.LatLngBounds(sw, ne));
    }
  }

  const mapRef = useRef(null);
  const mapInstance = useRef(null);
  const locationMarker = useRef(null);
  const geocoder = useRef(null);

  useEffect(() => {
    setActiveMarker(false);
    setActiveTracking(false);
    setIsGridActive(false);
  
    // // 현재 위치 주소 정보를 가져오기
    // const loadCurrentPosition = () => {
    //   return new Promise((resolve, reject) => {
    //     if (navigator.geolocation) {
    //       navigator.geolocation.getCurrentPosition((position) => {
    //         const lat = position.coords.latitude;
    //         const lon = position.coords.longitude;
    //         resolve({ lat, lon });
    //       }, reject);
    //     } else {
    //       reject(new Error("error: Geolocation이 지원되지 않습니다."));
    //     }
    //   });
    // };

    const script = document.createElement('script');
    script.async = true;
    script.src = "//dapi.kakao.com/v2/maps/sdk.js?appkey=fa3cd41b575ec5e015970670e786ea86&libraries=services&autoload=false";
    document.head.appendChild(script);
    
    script.onload = () => {
      kakao.maps.load(() => {
        const container = mapRef.current;
        const options = {
          center: new kakao.maps.LatLng(33.450701, 126.570667),
          level: 4,
        };
        mapInstance.current = new kakao.maps.Map(container, options);
        geocoder.current = new kakao.maps.services.Geocoder();
        setMap(mapInstance.current);
        console.log("지도 랜더링");
      });
    };
    
  }, [setActiveMarker, setActiveTracking, setIsGridActive, setCurrentAddress, setAddress]);
  
  // 좌표로 주소 검색
  const searchAddrFromCoords = useCallback((coords, callback) => {
    geocoder.current.coord2RegionCode(coords.getLng(), coords.getLat(), callback);
  }, []);

  // 좌표로 상세 주소 검색
  const searchDetailAddrFromCoords = useCallback((coords, callback) => {
    geocoder.current.coord2Address(coords.getLng(), coords.getLat(), callback);
  }, []);

  //위치 추적 버튼 핸들러
  const handleLocationBtn = useCallback(() => {
    setIsLocationLoading(true);
    if (!activeTracking) { 
      if (navigator.geolocation) {
        // 현재 위치 추적 및 지도 확대/이동
        navigator.geolocation.getCurrentPosition((position) => {
          const lat = position.coords.latitude;
          const lon = position.coords.longitude;
          const curPosition = new kakao.maps.LatLng(lat, lon);

          mapInstance.current.setCenter(curPosition);
          mapInstance.current.setLevel(2);
          setActiveTracking(true);

          // 좌표를 주소로 변환
          //현재 위치 주소 정보 currentAddress atom에 저장
          geocoder.current.coord2Address(lon, lat, (result, status) => {
            if (status === kakao.maps.services.Status.OK) {
              setCurrentAddress({
                depth2: result[0].address.region_2depth_name,
                depth3: result[0].address.region_3depth_name,
                addr: result[0].address.address_name,
                roadAddr: result[0].road_address.address_name,
                lat: lat,
                lon: lon,
              });
            }
          });
          
          //마커 업데이트
          if (!locationMarker.current) {
            locationMarker.current = new kakao.maps.Marker({
              position: curPosition,
              map: mapInstance.current,
            });
          } else {
            locationMarker.current.setPosition(curPosition);
            locationMarker.current.setMap(mapInstance.current);
          }

          setActiveMarker(true);
          setIsLocationLoading(false);
        }, () => {
          alert('위치를 가져올 수 없습니다.');
          setIsLocationLoading(false);
        });
      } else {
        alert('Geolocation을 사용할 수 없습니다.');
        setIsLocationLoading(false);
      }
    } else {
      if (locationMarker.current) { //마커 있으면 제거
        locationMarker.current.setMap(null);
      }
      setActiveTracking(false);// 위치 추적 상태 비활성화로 atom 업데이트
      setActiveMarker(false); //마커 상태 비활성화로 atom 업데이트
      setIsLocationLoading(false);
    }
  }, [activeTracking]);



//그리드 버튼 핸들러
const showGrid = useCallback(() => {
  const newGridObjects = gridBounds.map(bounds => {
    const rectangle = new kakao.maps.Rectangle({
      bounds: bounds,
      strokeWeight: 2,
      strokeColor: '#004c80',
      strokeOpacity: 0.8,
      strokeStyle: 'solid',
      fillColor: '#fff',
      fillOpacity: 0.5,
    });
    rectangle.setMap(mapInstance.current);
    return rectangle;
  });
  setGridObjects(newGridObjects);  // 그리드 객체 상태 업데이트
}, [gridBounds]);

const hideGrid = useCallback(() => {
  gridObjects.forEach(rectangle => {
    rectangle.setMap(null);  // 그리드 객체 맵에서 제거
  });
  setGridObjects([]);  // 그리드 객체 상태 초기화
}, [gridObjects]);

// 그리드 버튼 핸들러
const handleGridBtn = () => {
  setIsGridActive(!isGridActive);  // 그리드 활성화 상태 토글
  if (!isGridActive) {
    showGrid();  // 그리드 활성화
  } else {
    hideGrid();  // 그리드 비활성화
  }
};
  // test용
  // console.log("클릭 위치 주소 정보: ", address);
  // console.log("맵 중심 위치 주소 정보: ", mapCenterAddress);
  // console.log("위치 추적 주소는: ", currentAddress);
  // localStorage.clear();

  return (
    <KakaoMap id="map" ref={mapRef}>
      <SearchBox />
      <BtnContainer>
        {isGridActive && (
        <RainBtn
          whileTap={{ scale: 0.7 }}
          initial={{ y: 50, opacity: 0 }}
          animate={{ y: 0, opacity: 1 }}
          exit={{ y: 50, opacity: 0 }}
          transition={{ duration: 0.4 }}
        >
          <Icon 
            icon={faCloudShowersHeavy}
          />  
        </RainBtn>
        )}
        <GridBtn
          whileTap={{ scale: 0.9 }}
          isGridLoading={isGridLoading}  // 그리드 버튼 로딩 상태 전달
          onClick={handleGridBtn}
          style={{ backgroundColor: isGridActive ? "#ffdf6a" : "white" }}

        >
          <Icon
            icon={isGridLoading ? faSpinner : faBorderAll}
            // style={{ color: isGridActive ? "#219fff" : "#0078b9" }}
            style={{ color: "#006aa3"}}

          />
        </GridBtn>

        <LocationBtn
          onClick={handleLocationBtn}
          isLocationLoading={isLocationLoading}
          whileTap={{ scale: 0.9 }}
          
        >
          <Icon
            icon={isLocationLoading ? faSpinner : faLocationCrosshairs}
            style={{ color: activeTracking ? "tomato" : "#216CFF" }}
            initial={{ rotate: 0 }}
            animate={{ rotate: isLocationLoading ? 360 : 0 }}
            transition={{ duration: 1, repeat: isLocationLoading ? Infinity : 0 }}
          />
        </LocationBtn>
      </BtnContainer>
    </KakaoMap>
  );
}

export default Map;

const BtnContainer = styled.div`
  /* width: auto; */
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
  cursor: ${props => (props.isLocationLoading ? 'not-allowed' : 'pointer')};
`;

const GridBtn = styled(LocationBtn)`
  cursor: ${props => (props.isGridLoading ? 'not-allowed' : 'pointer')};
`;

const Icon = styled(motion(FontAwesomeIcon))`
  width: 22px;
  height: 22px;
  transition: color 0.2s ease;
`;

const RainBtn =styled(motion(LocationBtn))`
  color: #7ccdff;
  background-color: #424040d2;
  cursor: pointer;
`;

const KakaoMap = styled.div`
  width: 100%;
  height: 100%;
`;