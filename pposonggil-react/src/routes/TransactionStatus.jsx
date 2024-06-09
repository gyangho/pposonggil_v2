import React, { useEffect, useState, useRef, useCallback } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { useRecoilState } from 'recoil';
import { addressState, currentAddressState, markerState, routeInfoState } from '../recoil/atoms';
import axios from "axios";

import styled from "styled-components";
import { motion, sync } from "framer-motion";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faBus, faSubway, faDroplet, faCircleDot, faPersonWalking, faLocationDot, faWind, faGlassWaterDroplet, faCloudRain, faSun, faCloud, faDisplay, faRoute, faFlag } from "@fortawesome/free-solid-svg-icons";

import MapBtn from "../components/MapBtn";

const { kakao } = window;

function TransactionStatus() {
  const [map, setMap] = useState(null);
  const [infoWindow, setInfoWindow] = useState(null);
  const [isExpanded, setIsExpanded] = useState(false);

  const mapRef = useRef(null);
  const mapInstance = useRef(null);
  /*  여기서부터 MapBtn 컴포넌트 관련 */
  const markerInstance = useRef(null);
  const geocoder = useRef(null);

  const [currentAddress, setCurrentAddress] = useRecoilState(currentAddressState); // 현재 위치 추적 주소
  const [activeMarker, setActiveMarker] = useRecoilState(markerState);

  const [isLocationLoading, setIsLocationLoading] = useState(false);
  const [activeTracking, setActiveTracking] = useState(false);

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

  const getGridWeatherFromServer = async () => { //격자 구간별 날씨 정보 get
    const url = 'http://localhost:8080/api/forecasts';
    try {
      const response = await axios.get(url);
      setGridWeather(response.data);
    } catch(error) {
      console.error("격자 날씨 정보 get 에러", error);
    }
  };

  /* 지도 생성 */
  useEffect(() => {
    getGridWeatherFromServer();
    getCurrentAddress();
    kakao.maps.load(() => {
      const container = mapRef.current;
      const options = {
        center: new kakao.maps.LatLng(37.5642135, 127.0016985),
        level: 4,
      };
      mapInstance.current = new kakao.maps.Map(container, options);
      geocoder.current = new kakao.maps.services.Geocoder();
      setMap(mapInstance.current);
      /* 거래 장소로 마크업 및 지도 확대 */
      //
      console.log("지도 랜더링");
    });
  }, []);

  useEffect(() => { //그리드객체 showGrid로 업데이트 된 후에 적용되게 하기 위해서 useEffect 사용
    if (isGridActive && gridObjects.length > 0) {
      handleTimeBtn(0);
    }
  }, [gridObjects, isGridActive]);
  
  const handleGridBtn = useCallback(() => { //그리드 버튼 클릭 핸들러
    if (isGridActive) {
      gridObjects.forEach(rectangle => rectangle.setMap(null));
      setGridObjects([]);
    } else {
      showGrid();
    }
    setIsGridActive(!isGridActive);
  }, [isGridActive, gridObjects, showGrid]);
  
  const handleTimeBtn = (index) => { //클릭한 격자의 강수량 정보로 fillcolor 변경
    // index에 해당하는 키 가져옴(키값: 시간대)
    const Key = Object.keys(gridWeather)[index];
    // 키에 해당하는 배열. (총 30개 구간)
    const Array = gridWeather[Key]; 
    // 각 격자에 대해 강수량 반영 격자 색상 변경
    gridObjects.forEach((rectangle, _index) => {
      const item = Array[_index];
      const gridData = item.rn1;
      let fillColor = '#ffffff';
  
      if (gridData) {
        const rain = parseFloat(gridData);
        if (rain >= 0 && rain <=  10) {
          fillColor = 'rgba(255, 255, 255, 0.64)';
        } else if (rain > 10 && rain <= 15) {
          fillColor = 'rgba(77, 216, 255, 0.5)';
        } else if (rain > 15 && rain <= 20) {
          fillColor = 'rgba(0, 130, 255, 0.5)';
          // fillColor = 'rgba(0, 106, 255, 0.5)';
        } else if (rain > 25 && rain <= 30) {
          fillColor = 'rgba(0, 98, 255, 0.5)';
        } else if (rain > 30 && rain <= 35){
          fillColor = 'rgba(0, 59, 197, 0.5)';
        } else if (rain > 35 && rain <= 40) {
        } else {
          fillColor = 'rgba(3, 0, 197, 0.5)';
          console.log("남색으로 설정");
        }
      }
      rectangle.setOptions({
        fillColor,
      });
    });
  };
  
  const handleLocationBtn = useCallback(() => {//위치 추적 버튼 핸들러
    setIsLocationLoading(true);
    if (!activeTracking) { 
      if (navigator.geolocation) {
        // 현재 위치 추적 및 지도 확대/이동
        navigator.geolocation.getCurrentPosition((position) => {
          const lat = position.coords.latitude;
          const lon = position.coords.longitude;
          const curPosition = new kakao.maps.LatLng(lat, lon);

          mapInstance.current.setCenter(curPosition);
          mapInstance.current.setLevel(3);
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

  /* 6월 9일 오전 6시 추가 */
  // 거래장소 임의로 설정해놓고 코드 작성, 후에 서버측에서 정보 가져와서 바꿔줘야함
  const navigate = useNavigate();
  const [routeInfo, setRouteInfo] = useRecoilState(routeInfoState);
  const meetingPlace = {
    name: "거래 약속 장소(임시 서울 중심)",
    lat: 37.5642135,
    lon: 127.0016985,
  }

  const getCurrentAddress = () => {
    if (navigator.geolocation) {
      // 현재 위치 추적 및 지도 확대/이동
      navigator.geolocation.getCurrentPosition((position) => {
        const lat = position.coords.latitude; //float타입으로 반환해줌
        const lon = position.coords.longitude; 
        const curPosition = new kakao.maps.LatLng(lat, lon);
        
        // 좌표를 주소로 변환 + 현재 위치 주소 정보로 currentAddress atom값 업데이트
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
      });
    }
    console.log("현재 위치를 업데이트 했습니다");    
  };

  const onSearchBtnClick = () => {
    console.log("현재 위치: ", currentAddress);
    //현재 위치 (recoilState이용) 출발지, 거래장소 도착지로 routeInfoState atom값 설정한 후 serach/routes 페이지로 이동
    if(currentAddress.addr) {
      const searchOrigin = {
        name: currentAddress.addr,
        lat: currentAddress.lat,
        lon: currentAddress.lon,
      };
      const searchDest = meetingPlace;
      setRouteInfo({
        origin: [searchOrigin],
        dest: [searchDest]
      });
      navigate('/search/routes');
    } else {
      console.log("현재 위치 값이 없습니다.");
    }
  };

  useEffect(()=> {
    console.log("거래장소로 routeInfoState 업데이트: ", routeInfo);
  }, [routeInfo]);
  
  return (
    <React.Fragment>
      <MapContainer id="map" ref={mapRef} style={{position:"relative"}}>
        <TimeBtnBar>
        {isGridActive && (
          <TimeBtns
            initial={{ x: -50, opacity: 0 }}
            animate={{ x: 0, opacity: 1 }}
            exit={{ x: -50, opacity: 0 }}
            transition={{ duration: 0.5 }}
          >
            <TimeBtn onClick={()=>handleTimeBtn(0)}>현재</TimeBtn>
            <TimeBtn onClick={()=>handleTimeBtn(1)}>+ 1시간</TimeBtn>
            <TimeBtn onClick={()=>handleTimeBtn(2)}>+ 2시간</TimeBtn>
            <TimeBtn onClick={()=>handleTimeBtn(3)}>+ 3시간</TimeBtn>
            <TimeBtn onClick={()=>handleTimeBtn(4)}>+ 4시간</TimeBtn>
            <TimeBtn onClick={()=>handleTimeBtn(5)}>+ 5시간</TimeBtn>
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
      </MapContainer>
      <TimeBar
          initial={{ height: "0%" }}
          animate={{ height: isExpanded ? "20%" : "0%" }}
          transition={{ duration: 0.4 }}
          >
              <RouteBtn onClick={() => onSearchBtnClick()}>
                <FontAwesomeIcon icon={faRoute} style={{color: "#63CAFF", paddingRight: "5px"}}/>
                <p>길찾기</p>
              </RouteBtn>
      </TimeBar>
      <BottomContainer
        initial={{ height: "40%" }}
        animate={{ height: isExpanded ? "50%" : "40%" }}
        transition={{ duration: 0.4 }}
        onClick={() => setIsExpanded(!isExpanded)}
      >

        <ToggleBar><TBar /></ToggleBar>
        <TransactionBox>
          <Block>
            <TransactionNotice>
              <div>
                <h1>거래 시간이<br />다가오고 있어요<br /></h1>
                <p>서둘러 약속 장소로 이동해주세요!</p>
              </div>
            </TransactionNotice>
            <TransactionTime>
              <Text>
                <h2>남은시간</h2>
                <h1>190분</h1>
              </Text>
            </TransactionTime>
          </Block>
          <Block>
            <FontAwesomeIcon icon={faFlag} style={{fontSize: "18px", paddingRight: "5px", paddingBottom: "5px"}}/>
            <span>숭실대학교 정문</span>
          </Block>
          <Block>
            <p style={{fontSize: "16px", fontWeight: "500", color: "#656565"}}>동작구 상도동</p>
          </Block>
          {/* 거래 진행 상황 상태 바 */}
          <StatusBar id="StatusBar">
            <BarContainer style={{justifyContent: "space-between"}}>
              <Bar style={{width: "50%", backgroundColor: "#E6E6E6", justifyContent:"start"}}>
                {/* 나의 위치 상태 바 */}
                <Bar style={{width: "80%", backgroundColor: "#63CAFF", justifyContent:"end"}}>
                  <p>200m</p>
                  <IconBox>
                    <FontAwesomeIcon icon={faPersonWalking} style={{color: "white"}} />
                  </IconBox>
                </Bar>
              </Bar>  
              <DestIconBox><FontAwesomeIcon icon={faFlag}/></DestIconBox>
              
              <Bar style={{width: "50%", backgroundColor: "#E6E6E6", justifyContent: "end"}}>
                <Bar style={{width: "50%", backgroundColor: "#FFCE1F"}}>
                  <IconBox>
                    <FontAwesomeIcon icon={faPersonWalking} style={{color: "white"}} />
                  </IconBox>
                  <p>200m</p>
                </Bar>
              </Bar>
                {/* {path.subPathDtos.map((subPath, subIndex) => (
                  subPath.time !== 0 && (
                    <React.Fragment key={subIndex}>
                      <Bar
                        width={(subPath.time / path.totalTime) * 100}
                        color={subPath.type === 'walk' ? 'darkgray' : (subPath.type === 'subway' ? subPath.subwayColor : subPath.busColor)}
                      >
                        {subPath.type !== 'walk' && (
                          <IconBox style={{backgroundColor: subPath.type === 'bus' ? subPath.busColor : subPath.subwayColor}}>
                            <FontAwesomeIcon
                              icon={subPath.type === 'bus' ? faBus : faSubway}
                              style={{color: "white"}}
                            />
                          </IconBox>
                        )}
                        <p>{subPath.time}분</p>
                      </Bar>
                    </React.Fragment>
                  )
                ))} */}
              </BarContainer>
            </StatusBar>
            <StatusBarText
              style={{display: "flex", justifyContent: "space-between", paddingBottom: "20px"}}
            >
              <p>나</p><span>맥도날드</span><p>거래자</p>
            </StatusBarText>
        </TransactionBox>
      </BottomContainer>
    </React.Fragment>
  );
}

export default TransactionStatus;

const MapContainer = styled(motion.div)`
  width: 100%;
  height: 60%;
  position: relative;
`;

const TimeBtnBar = styled.div`
  width: 100%;
  /* height: 40px; */
  display: flex;
  justify-content: start;
  z-index: 500;
  position: sticky;
  padding: 20px 0px;
`;
const TimeBtns = styled(motion.div)`
  padding: 0px 20px;
  * {
    margin-right: 10px;
    margin-bottom: 5px;
  }
`;
const TimeBtn = styled.button`
  font-family: 'Bagel Fat One', cursive;
  border-radius: 25px;
  border: 2px solid #424040d2;
  border: none;
  box-shadow: 0px 0px 3px 3px rgba(0, 0, 0, 0.1);
  background-color: #424040d2;
  color: white;
  padding: 5px 10px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  /* margin: 10px 0px; */
  &:first-child {
    border: 2px solid #003E5E;  
    background-color: #003E5E;
    color: white;
    &:hover {
      background-color: #88D6FF;
      border-color: #88D6FF;
      color: white;
    }
  }
  &:not(:first-child) {
    
    &:focus {
      background-color: #88d5ffd1;
      color: #424040;
      border: 2px solid #424040d2;
    }
    &:hover {
      background-color: #88d5ffd1;
      color: white;
    }
  }
`;

/* 하단창 */
const BottomContainer = styled(motion.div)`
  width: 100%;
  height: 40%;
  background: white;
  position: absolute;
  bottom: 0;
  z-index: 1000;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.2);
  background-color: white;
  width: 100%;
  height: 100%;
  overflow-y: scroll;
`;
// 토글 바
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
  z-index: 1000;
`;
const TBar = styled.div` //중앙 막대기
  width: 10%;
  height: 6px;
  border-radius: 25px;
  background-color: #d9d9d9;
`;
// 하단창 토글바 제외 부분(컨텐츠 들어가는 부분)
const TransactionBox = styled.div`
  background-color: white;
  padding: 15px 20px;

  margin-bottom: 10px;
`;

/* 하단창 박스 contents */
const Block = styled.div` //row기준 박스
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 15px;
  &:first-child { //거래 안내 박스
    margin-bottom: 20px;
  }
  &:nth-child(2) { //거래장소 박스
    justify-content: start;
    font-size: 20px;
    font-weight: 600;
    margin-bottom: 5px;
  }
`;

const TransactionNotice = styled.div` //거래 안내 박스
  width: 70%;
  height: auto;
  font-weight: bold;
  display: flex;
  justify-content: start;
  div {
    display: block;
    justify-content: center;
    align-items: end;
    padding-bottom: 5px;
    height: 100%;
    h1 { //거래시간이 다가오고 있어요
      font-size: 25px;
      font-weight: 900;
    }
    p { //서둘러 약속장소로 이동해주세요
      color: #656565ea;
      font-size: 15px;
      margin-top: 4px;
    }
  }
`;
const TransactionTime = styled.div` //시간 박스
  display: block;
  height: 80px;
  width: 80px;
  justify-content: center;
  align-items: center;
  text-align: center;
  background-color: #88d5ff79;
  border-radius: 50%;
`;

const Text = styled.div` //시간
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  h1 {
    font-weight: 900;
    font-size: 23px;
    color: #004263;
    margin-top: 2px;
  }
  h2 {
    font-weight: bold;
    font-size: 16px;
    color: #004263;
    margin-top: 5px;
  }
`;

/* 나~거래장소~거래자 거리 현황 바 */
const StatusBar = styled.div`
  padding: 10px 0px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 10px;
`;
const IconBox = styled.div`
  min-width: 28px;
  max-width: 28px;
  min-height: 28px;
  max-height: 28px;
  border: 2px solid whitesmoke;
  background-color: inherit;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2;
  font-size: 13px;
  font-weight: 900;
`;
const DestIconBox = styled(IconBox)` //거래장소 아이콘
  background-color: black;
  color: #E6E6E6;
  border: none;
  z-index: 5;
`;

const StatusBarText = styled.div`
  display: flex;
  justify-content: space-between;
  padding-bottom: 20px;
  font-size: 16px;
  p {
    font-family: 'Bagel Fat One', cursive;
    font-weight: 300;
    width: 20%;
    &:last-child {
      text-align: end;
    }
  }
  span {
    width: 60%;
    color: #003E5E;
    font-weight: 900;
    text-align: center;
  }
`;

const BarContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 18px;
  background-color: #E6E6E6;
  border-radius: 25px;
  margin-top: 30px;
`;

const Bar = styled.div`
  height: 100%;
  background-color: ${(props) => props.color};
  width: ${(props) => props.width}%;
  min-width: 14px;
  border-radius: 25px;
  position: relative;
  display: flex;
  justify-content: start;
  align-items: center;
  text-align: center;
  p {
    width: 100%;
    text-align: center;
    justify-content: center;
    font-weight: 900;
    font-size: 11px;
    color: black;
  }
`;



/* Btn 컴포넌트 추가*/
const TimeBar = styled(motion.div)`
  width: 90%;
  /* max-width: 75%; */
  /* height: 10%; */
  height: auto;
  padding: 0px 10px;
  display: flex;
  justify-content: start;
  align-items: center;
  position: absolute;
  bottom: 44%;
  background-color: none;
  z-index: 1000;
`;
const RouteBtn = styled.button`
  width: auto;
  height: 40px;
  margin-bottom: 5px;
  border-radius: 25px;
  font-family: 'Bagel Fat One', cursive;
  font-size: 14px;
  box-shadow: 0px 0px 5px 3px rgba(0, 0, 0, 0.1);
  background-color: #003E5E;
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  padding: 0px 12px;
  cursor: pointer;
  &:hover {
    background-color: white;
    border: 2px solid #003E5E;
    color: #003E5E;
  }
  &:focus {
    background-color: white;
    border: 2px solid #003E5E;
    color: #003E5E;
  }
`;




