import React, { useState, useEffect, useRef, useCallback } from 'react';
import styled from 'styled-components';
import { useRecoilState } from 'recoil';
import { addressState, currentAddressState, mapCenterState, locationBtnState, markerState } from '../recoil/atoms';

import SearchBox from './SearchBox';
import MapBtn from './MapBtn';

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
  const markerInstance = useRef(null);
  const geocoder = useRef(null);

  useEffect(() => {
    setActiveMarker(false);
    setActiveTracking(false);
    setIsGridActive(false);
  
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
      const script = document.createElement('script');
      script.async = true;
      script.src = "//dapi.kakao.com/v2/maps/sdk.js?appkey=fa3cd41b575ec5e015970670e786ea86&autoload=false";
      document.head.appendChild(script);
      
      script.onload = () => {
        kakao.maps.load(() => {
          const container = mapRef.current;
          if(!container) {
            return;
          }
          const options = {
            center: new kakao.maps.LatLng(lat, lon),
            level: 4,
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
      };
    };
  
    // 현재 위치 정보를 가져온 후 지도를 로드
    loadCurrentPosition()
      .then(position => loadMap(position))
      .catch(error => console.error("Error fetching current position:", error));
  
    console.log("지도 랜더링");
  }, [setActiveMarker, setActiveTracking, setIsGridActive, setCurrentAddress, setAddress]);
  
  // 좌표로 주소 검색
  const searchAddrFromCoords = useCallback((coords, callback) => {
    geocoder.current.coord2RegionCode(coords.getLng(), coords.getLat(), callback);
  }, []);

  // 좌표로 상세 주소 검색
  const searchDetailAddrFromCoords = useCallback((coords, callback) => {
    geocoder.current.coord2Address(coords.getLng(), coords.getLat(), callback);
  }, []);

  //지도 중심 이동 시 중심 좌표 위치 법정구 행정동 정보 atom에 저장
  const displayCenterInfo = useCallback((result, status) => {
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

  // 새로운 함수 추가 - 그리드 보여주기
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

  return (
    <KakaoMap id="map" ref={mapRef}>
      <SearchBox />
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
