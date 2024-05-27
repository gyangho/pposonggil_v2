import React, { useState, useEffect, useRef, useCallback } from 'react';
import { useRecoilState, useSetRecoilState } from 'recoil';
import { addressState, currentAddressState, gridState, locationBtnState, mapCenterState, markerState } from '../recoil/atoms';

import styled from "styled-components";
import { motion } from 'framer-motion';
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faLocationCrosshairs, faSpinner, faBorderAll , faCloudShowersHeavy, faL} from "@fortawesome/free-solid-svg-icons";

import SearchBox from './SearchBox';

const { kakao } = window;

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

const RainBtn =styled(LocationBtn)`
  margin: 15px;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  left: 0;
  bottom: 0;
  z-index: 100;
  position: sticky;
  color: #d1edff;
  background-color: gray;
`;

const KakaoMap = styled.div`
  width: 100%;
  height: 100%;
`;
///////////////////////////////////////////////////////

function Map() {
  const [isLoading, setIsLoading] = useState(false);
  const [isGridLoading, setIsGridLoading] = useState(false);
  const [activeGrid, setActiveGrid] = useState(false);

  const [address, setAddress] = useRecoilState(addressState); //마크업 및 지도 이동 시 지도 중심 위치 주소 (temp주소)
  const [currentAddress, setCurrentAddress] = useRecoilState(currentAddressState); // 현재 위치 추적 주소
  const [mapCenterAddress, setMapCenterAddress] = useRecoilState(mapCenterState);
  const [activeTracking, setActiveTracking] = useRecoilState(locationBtnState);
  const [activeMarker, setActiveMarker] = useRecoilState(markerState);


  
  const mapRef = useRef(null);
  const mapInstance = useRef(null);
  const markerInstance = useRef(null);
  const geocoder = useRef(null);

  // 지도 생성
  // useEffect(() => {
  //   setActiveMarker();
  //   setActiveTracking();
  //   setActiveGrid();
    
  //   const script = document.createElement('script');
  //   script.async = true;
  //   script.src = "//dapi.kakao.com/v2/maps/sdk.js?appkey=fa3cd41b575ec5e015970670e786ea86&autoload=false";
  //   document.head.appendChild(script);

  //   script.onload = () => {
  //     kakao.maps.load(() => {
  //       const container = mapRef.current;
  //       const options = {
  //         center: new kakao.maps.LatLng(37.566826, 126.9786567),
  //         level: 8,
  //       };
  //       mapInstance.current = new kakao.maps.Map(container, options);
  //       geocoder.current = new kakao.maps.services.Geocoder();

  //       // 현재 위치 주소 정보 currentAddressState atom에 저장
  //       if (navigator.geolocation) {
  //         navigator.geolocation.getCurrentPosition((position) => {
  //           const lat = position.coords.latitude;
  //           const lon = position.coords.longitude;
  //           const locPosition = new kakao.maps.LatLng(lat, lon);
  //           // 좌표를 주소로 변환
  //           geocoder.current.coord2Address(lon, lat, (result, status) => {
  //             if (status === kakao.maps.services.Status.OK) {
  //               setCurrentAddress({
  //                 depth2: result[0].address.region_2depth_name,
  //                 depth3: result[0].address.region_3depth_name,
  //                 addr: result[0].address.address_name,
  //                 lat: lat,
  //                 lon: lon,
  //               });
  //             }
  //           });
  //         });
  //       }
  //       // 지도 이동 이벤트 리스너 등록
  //       kakao.maps.event.addListener(mapInstance.current, 'idle', () => {
  //         searchAddrFromCoords(mapInstance.current.getCenter(), displayCenterInfo);
  //       });

  //       // 지도 클릭 이벤트 리스너 등록(마커 표시 및 지도 중심 이동)
  //       kakao.maps.event.addListener(mapInstance.current, 'click', (mouseEvent) => {
  //         const latLon = mouseEvent.latLng;
  //         searchDetailAddrFromCoords(latLon, (result, status) => {
  //           if (status === kakao.maps.services.Status.OK) {
  //             // const roadAddressName = result[0].road_address ? result[0].road_address.address_name : result[0].address.address_name;
              
  //             if (markerInstance.current) { //기존 마커 있으면 제거
  //               markerInstance.current.setMap(null);
  //               setActiveMarker(false);
  //             }
              
  //             if(result[0].road_address) { // 도로명 주소 있는 경우에만 지도 마크업
  //               markerInstance.current = new kakao.maps.Marker({
  //                 position: latLon,
  //                 map: mapInstance.current,
  //               });
  //               setAddress({
  //                 depth2: result[0].address.region_2depth_name,
  //                 depth3: result[0].address.region_3depth_name,
  //                 addr: result[0].address.address_name,
  //                 roadAddr: result[0].road_address.address_name,
  //                 lat: markerInstance.current.getPosition().getLat(),
  //                 lon: markerInstance.current.getPosition().getLng(),
  //               });
  //               setActiveTracking(false); //지도 클릭해서 마크업 시 위치 추적 버튼 비활성화
  //               setActiveMarker(true); //마커 활성화 상태 업데이트
  //               mapInstance.current.panTo(latLon); //마커 위치로 지도 중심 변경
  //             } else {
  //              setActiveMarker(false); 
  //             }
  //           }
  //         });
  //       });
        
  //     });
  //   };
  //   console.log("지도 랜더링")
  // }, []);
  useEffect(() => {
    setActiveMarker(false);
    setActiveTracking(false);
    setActiveGrid(false);
  
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
  
    const loadMap = ({ lat, lon }) => {
      const script = document.createElement('script');
      script.async = true;
      script.src = "//dapi.kakao.com/v2/maps/sdk.js?appkey=fa3cd41b575ec5e015970670e786ea86&autoload=false";
      document.head.appendChild(script);
  
      script.onload = () => {
        kakao.maps.load(() => {
          const container = mapRef.current;
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
  
          // 지도 이동 이벤트 리스너 등록
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
  }, [setActiveMarker, setActiveTracking, setActiveGrid, setCurrentAddress, setAddress]);
  
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
    setIsLoading(true);
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
          
          //현재 위치 주소 정보 currentAddress atom에 저장
          // geocoder.current.coord2Address(lon, lat, (result, status) => {
          //   if (status === kakao.maps.services.Status.OK) {
          //     for (let i = 0; i < result.length; i++) {
          //       if (result[i].region_type === 'H') {
          //         setCurrentAddress({
          //           depth2: result[i].region_2depth_name,
          //           depth3: result[i].region_3depth_name,
          //           addressName: result[i].address_name,
          //         });
          //         break;
          //       }
          //     }
          //   }
          // });
          //
          setIsLoading(false);
        }, () => {
          alert('위치를 가져올 수 없습니다.');
          setIsLoading(false);
        });
      } else {
        alert('Geolocation을 사용할 수 없습니다.');
        setIsLoading(false);
      }
    } else {
      if (markerInstance.current) { //마커 있으면 제거
        markerInstance.current.setMap(null);
      }
      setActiveTracking(false);// 위치 추적 상태 비활성화로 atom 업데이트
      setActiveMarker(false); //마커 상태 비활성화로 atom 업데이트
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
  
  // test용
  console.log("클릭 위치 주소 정보: ", address);
  // console.log("맵 중심 위치 주소 정보: ", mapCenterAddress);
  // console.log("위치 추적 주소는: ", currentAddress);
  // localStorage.clear();

  return (
    <KakaoMap id="map" ref={mapRef}>
      <SearchBox />
      <RainBtn><Icon icon={faCloudShowersHeavy}/></RainBtn>
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
            icon={isLoading ? faSpinner : faLocationCrosshairs}
            style={{ color: activeTracking ? "tomato" : "#216CFF" }}
          />
        </LocationBtn>
      </BtnContainer>
    </KakaoMap>
  );
}

export default Map;



