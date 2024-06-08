import React, { useState, useEffect, useRef, useCallback } from 'react';
import styled from 'styled-components';
import { motion } from 'framer-motion';
import { useRecoilState, useResetRecoilState } from 'recoil';
import { addressState, currentAddressState, mapCenterState, locationBtnState, markerState } from '../recoil/atoms';
import api from "../api/api";
import SearchBox from './SearchBox';
import MapBtn from './MapBtn';

const { kakao } = window;

function Map() {
  const [isLocationLoading, setIsLocationLoading] = useState(false); //지도 하단 버튼 2개 로딩 상태 관리
  const [address, setAddress] = useRecoilState(addressState); //마크업 및 지도 이동 시 위치 주소
  const [currentAddress, setCurrentAddress] = useRecoilState(currentAddressState); // 현재 위치 추적 주소
  const [mapCenterAddress, setMapCenterAddress] = useRecoilState(mapCenterState);
  const [activeTracking, setActiveTracking] = useRecoilState(locationBtnState);
  const [activeMarker, setActiveMarker] = useRecoilState(markerState);
  const resetMapCenterAddr = useResetRecoilState(mapCenterState); // 지도 리렌더링 시 남아있던 지도 중심 법정구/행정구 정보 초기화

  const mapRef = useRef(null);
  const mapInstance = useRef(null);
  const markerInstance = useRef(null);
  const geocoder = useRef(null);

  const [isGridLoading, setIsGridLoading] = useState(false); // 그리드 버튼 로딩 상태
  const [isGridActive, setIsGridActive] = useState(false); // 그리드 활성화 상태
  const [gridObjects, setGridObjects] = useState([]); // 기본 그리드, (29개 격자, 반투명 fillcolor)
  const [gridWeather, setGridWeather] = useState([]); // 서버로부터 격자당 시간별 강수 정보 받아온 데이터 저장
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
  // 격자 지도 위에 그리고 기본값으로 세팅
  const showGrid = useCallback(() => {
    const newGridObjects = gridBounds.map(bounds => {
      const rectangle = new kakao.maps.Rectangle({
        bounds: bounds,
        strokeWeight: 2,
        strokeColor: '#004c80',
        strokeOpacity: 0.8,
        strokeStyle: 'solid',
        fillColor: '#ffffff',
        fillOpacity: 0.5,
      });
      rectangle.setMap(mapInstance.current);
      return rectangle;
    });
    setGridObjects(newGridObjects);  // 그리드 객체 상태 업데이트
  }, [gridBounds]);

  const getGridWeatherFromServer = async () => {
    const now = new Date();
    const time = now.getHours().toString().padStart(2, '0') + now.getMinutes().toString().padStart(2, '0');
    const url = 'http://localhost:8080/api/forecasts';
    try {
      const response = await api.get(url);
      setGridWeather(response.data);
    } catch (error) {
      console.error("격자 날씨 정보 get 에러", error);
    }
  };

  //그리드 버튼 클릭 핸들러(우측 하단 격자 버튼)
  const handleGridBtn = useCallback(() => {
    if (isGridActive) {
      gridObjects.forEach(rectangle => rectangle.setMap(null));
      setGridObjects([]);
      mapInstance.current.setLevel(3);
    } else {
      mapInstance.current.setLevel(10);
      showGrid();
    }
    setIsGridActive(!isGridActive);
  }, [isGridActive, gridObjects, showGrid]);

  //인덱스에 해당하는 순서의 격자 강수량 정보 가져와서 Grid의 fillcolor 변경
  const handleTimeBtn = (index) => {
    const Key = Object.keys(gridWeather)[index]; // index에 해당하는 키 가져옴 (키:시간대)
    console.log('Key:', Key); // 해당 시간대

    const Array = gridWeather[Key]; // 키에 해당하는 배열(총 30개 격자 구간)
    console.log('Array:', Array); //해당 시간대의 30개의 격자 구역별 날씨정보

    const Element = Array[0]; // 30개 중 하나의 격자에 해당하는 정보 접근(여기선 데이터 확인용으로 임의로 첫번째 접근)
    console.log('Element:', Element);

    gridObjects.forEach((rectangle, _index) => {  // 각 격자에 대해 강수량 반영 격자 색상 변경
      const item = Array[_index]; //하나의 시간대에 30개 격자 구간들 중 하나씩 접근
      console.log(`Index: ${_index}, reh: ${item.reh}`);

      const gridData = item.reh; //키에 해당하는 시간대의 index번째 격자 구간에 해당하는 reh값
      let fillColor = '#ffffff'; // 우선 디폴트 색상 (흰색)

      if (gridData) {
        const reh = parseFloat(gridData);
        if (reh > 0 && reh <= 40) {
          fillColor = 'rgba(61, 213, 255, 0.5)'; // 하늘색
          console.log('하늘색으로 변경')
        } else if (reh > 40 && reh <= 60) {
          fillColor = 'rgba(0, 60, 255, 0.5)'; // 파란색
          console.log("파란색으로 변경");
        } else if (reh > 60) {
          fillColor = 'rgba(58, 0, 203, 0.5)'; // 남색
          console.log("남색으로 변경");
        }
      }
      rectangle.setOptions({
        fillColor,
        fillOpacity: 0.5, //모든 격자 구간 내 색상 반투명하게 설정
      });
    });
  };

  // 맵 로드
  useEffect(() => {
    setActiveMarker(false);
    setActiveTracking(false);
    setIsGridActive(false);
    resetMapCenterAddr();

    //현재 시각 기준 시간 당 격자구간별 예상 강수량 데이터 서버에서부터 가져오기 
    getGridWeatherFromServer();
    // 현재 위치 주소 정보를 가져오기
    const loadCurrentPosition = () => {
      return new Promise((resolve, reject) => {
        if (navigator.geolocation) {
          navigator.geolocation.getCurrentPosition((position) => {
            const lat = position.coords.latitude;
            const lon = position.coords.longitude;
            resolve({ lat, lon });
          }, reject);
        } else {
          reject(new Error("error: Geolocation이 지원되지 않습니다."));
        }
      });
    };
    //현재 위치 정보로 지도 생성
    const loadMap = ({ lat, lon }) => {
      // const script = document.createElement('script');
      // script.async = true;
      // script.src = "//dapi.kakao.com/v2/maps/sdk.js?appkey=fa3cd41b575ec5e015970670e786ea86&autoload=false";
      // document.head.appendChild(script);

      // script.onload = () => {
      kakao.maps.load(() => {
        const container = mapRef.current;
        if (!container) {
          return;
        }
        const options = {
          center: new kakao.maps.LatLng(lat, lon),
          level: 2,
        };
        mapInstance.current = new kakao.maps.Map(container, options);
        geocoder.current = new kakao.maps.services.Geocoder();

        // 현재 위치에 마커 표시 및 주소 정보 저장
        const locPosition = new kakao.maps.LatLng(lat, lon);
        geocoder.current.coord2Address(lon, lat, (result, status) => {
          if (status === kakao.maps.services.Status.OK) {
            setCurrentAddress({
              depth2: result[0].address.region_2depth_name,
              depth3: result[0].address.region_3depth_name,
              addr: result[0].address.address_name,
              roadAddr: result[0].road_address ? result[0].road_address.address_name : '',
              lat: lat,
              lon: lon,
            });

            markerInstance.current = new kakao.maps.Marker({
              position: locPosition,
              map: mapInstance.current,
            });
            // setActiveMarker(true);
            setActiveTracking(true);
          }
        });

        // 지도 이동 이벤트 리스너 등록(지도 이동 시 지도 중심 위치 정보 상단 검색창에 띄우려는 용도)
        kakao.maps.event.addListener(mapInstance.current, 'idle', () => {
          searchAddrFromCoords(mapInstance.current.getCenter(), displayCenterInfo);
        });

        // 지도 클릭 이벤트 리스너 등록(마커 표시 및 지도 중심 이동)
        kakao.maps.event.addListener(mapInstance.current, 'click', (mouseEvent) => {
          const latLon = mouseEvent.latLng;
          searchDetailAddrFromCoords(latLon, (result, status) => {
            if (status === kakao.maps.services.Status.OK) {
              if (markerInstance.current) { // 기존 마커 있으면 제거
                markerInstance.current.setMap(null);
                setActiveMarker(false);
              }

              if (result[0].road_address) { // 도로명 주소 있는 경우에만 지도 마크업
                markerInstance.current = new kakao.maps.Marker({
                  position: latLon,
                  map: mapInstance.current,
                });
                setAddress({
                  depth2: result[0].address.region_2depth_name,
                  depth3: result[0].address.region_3depth_name,
                  addr: result[0].address.address_name,
                  roadAddr: result[0].road_address.address_name,
                  lat: markerInstance.current.getPosition().getLat(),
                  lon: markerInstance.current.getPosition().getLng(),
                });
                setActiveTracking(false); // 지도 클릭해서 마크업 시 위치 추적 버튼 비활성화
                setActiveMarker(true); // 마커 활성화 상태 업데이트
                mapInstance.current.panTo(latLon); // 마커 위치로 지도 중심 변경
              } else {
                setActiveMarker(false);
              }
            }
          });
        });

      });
      // };
    };

    // 현재 위치 정보를 가져온 후 지도를 로드
    loadCurrentPosition()
      .then(position => loadMap(position))
      .catch(error => console.error("Error fetching current position:", error));

    console.log("지도 랜더링");
  }, [setActiveMarker, setActiveTracking, setIsGridActive, setCurrentAddress, setAddress]);

  const searchAddrFromCoords = useCallback((coords, callback) => {  // 좌표로 주소 검색
    geocoder.current.coord2RegionCode(coords.getLng(), coords.getLat(), callback);
  }, []);

  const searchDetailAddrFromCoords = useCallback((coords, callback) => {  // 좌표로 상세 주소 검색
    geocoder.current.coord2Address(coords.getLng(), coords.getLat(), callback);
  }, []);

  const displayCenterInfo = useCallback((result, status) => {  //지도 중심 이동 시 중심 좌표 위치 법정구 행정동 정보 atom에 저장
    if (status === kakao.maps.services.Status.OK) {
      for (let i = 0; i < result.length; i++) {
        if (result[i].region_type === 'H') {
          setMapCenterAddress({
            depth2: result[i].region_2depth_name,
            depth3: result[i].region_3depth_name,
          });
          break;
        }
      }
    }
  }, [setMapCenterAddress]);

  const handleLocationBtn = useCallback(() => { //위치 추적 버튼 핸들러
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
          if (!markerInstance.current) {
            markerInstance.current = new kakao.maps.Marker({
              position: curPosition,
              map: mapInstance.current,
            });
          } else {
            markerInstance.current.setPosition(curPosition);
            markerInstance.current.setMap(mapInstance.current);
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
      if (markerInstance.current) { //마커 있으면 제거
        markerInstance.current.setMap(null);
      }
      setActiveTracking(false);// 위치 추적 상태 비활성화로 atom 업데이트
      setActiveMarker(false); //마커 상태 비활성화로 atom 업데이트
      setIsLocationLoading(false);
    }
  }, [activeTracking]);



  return (
    <KakaoMap id="map" ref={mapRef}>
      <SearchBox />
      <TimeBtnBar>
        {isGridActive && (
          <TimeBtns
            initial={{ x: -50, opacity: 0 }}
            animate={{ x: 0, opacity: 1 }}
            exit={{ x: -50, opacity: 0 }}
            transition={{ duration: 0.5 }}
          >
            <TimeBtn onClick={() => handleTimeBtn(0)}>현재</TimeBtn>
            <TimeBtn onClick={() => handleTimeBtn(1)}>+ 1시간</TimeBtn>
            <TimeBtn onClick={() => handleTimeBtn(2)}>+ 2시간</TimeBtn>
            <TimeBtn onClick={() => handleTimeBtn(3)}>+ 3시간</TimeBtn>
            <TimeBtn onClick={() => handleTimeBtn(4)}>+ 4시간</TimeBtn>
            <TimeBtn onClick={() => handleTimeBtn(5)}>+ 5시간</TimeBtn>
          </TimeBtns>
        )}
      </TimeBtnBar>

      <MapBtn
        isGridActive={isGridActive}
        isGridLoading={isGridLoading}
        isLocationLoading={isLocationLoading}
        activeTracking={activeTracking}
        handleGridBtn={handleGridBtn}
        handleLocationBtn={handleLocationBtn}
      />
    </KakaoMap>
  );
}

export default Map;

const KakaoMap = styled.div`
  width: 100%;
  height: 100%;
`;

const TimeBtnBar = styled.div`
  width: 100%;
  height: 40px;
  display: flex;
  justify-content: start;
  z-index: 500;
  position: sticky;
`;
const TimeBtns = styled(motion.div)`
  padding: 0px 20px;
  * {
    margin-right: 10px;
  }
`;
const TimeBtn = styled.button`
  border-radius: 25px;
  border: 1.5px solid black;
  background-color: skyblue;
  padding: 5px 10px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  &:hover {
    background-color: #626161;
    color: #7ccdffc9;
  }
`;
