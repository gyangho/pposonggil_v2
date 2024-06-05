import React, { useEffect, useState, useRef, useCallback } from "react";
import { useLocation, useParams } from "react-router-dom";
import { useRecoilState } from 'recoil';
import { addressState, currentAddressState, mapCenterState, locationBtnState, markerState } from '../recoil/atoms';
import axios from "axios";

import styled from "styled-components";
import { motion, sync } from "framer-motion";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faBus, faSubway, faDroplet, faCircleDot, faPersonWalking, faLocationDot, faWind, faCloudSunRain, faGlassWaterDroplet, faCloudRain } from "@fortawesome/free-solid-svg-icons";

import MapBtn from "../components/MapBtn";

const { kakao } = window;
const apiUrl = "http://localhost:3001/paths";

function RouteDetail() {
  const { index } = useParams();
  // const [path, setPath] = useState(null);
  const [map, setMap] = useState(null);
  const [isInfoVisible, setIsInfoVisible] = useState(false);
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

  const getGridWeatherFromServer = async () => {
    const now = new Date();
    const time = now.getHours().toString().padStart(2, '0') + now.getMinutes().toString().padStart(2, '0');
    const url = 'http://localhost:8080/api/forecasts';
    const params = { time: time };
    try {
      const response = await axios.get(url, {params});
      setGridWeather(response.data);
    } catch(error) {
      console.error("격자 날씨 정보 get 에러", error);
    }
  };

  //격자 내 색상 업데이트 
  const updateGridColors = (weatherData) => {
    gridObjects.forEach((rectangle, index) => {
      const gridData = weatherData[index];
      let fillColor = '#ffffff'; // 기본 색상 (흰색)

      if (gridData) {
        const reh = parseFloat(gridData.reh);
        if (reh > 0 && reh <= 5) {
          fillColor = 'rgba(135, 206, 250, 0.5)'; // 하늘색
        } else if (reh > 5 && reh <= 10) {
          fillColor = 'rgba(0, 0, 255, 0.5)'; // 파란색
        } else if (reh > 10) {
          fillColor = 'rgba(0, 0, 139, 0.5)'; // 남색
        }
      }

      rectangle.setOptions({
        fillColor,
        fillOpacity: 0.5,
      });
    });
  };
  
    //그리드 버튼 클릭 핸들러
    const handleGridBtn = useCallback(() => {
      if (isGridActive) {
        gridObjects.forEach(rectangle => rectangle.setMap(null));
        setGridObjects([]);
      } else {
        showGrid();
      }
      setIsGridActive(!isGridActive);
    }, [isGridActive, gridObjects, showGrid]);
  
    const handleTimeBtn = (index) => {
      //인덱스에 해당하는 순서의 격자 강수량 정보 가져와서 Grid의 fillcolor 변경
    
      // index에 해당하는 키 가져옴
      const Key = Object.keys(gridWeather)[index];
      console.log('Key:', Key); // 해당 시간대
    
      // 키에 해당하는 배열. (총 30개 구간)
      const Array = gridWeather[Key]; 
      console.log('Array:', Array); //해당 시간대의 30개의 격자 구역별 날씨정보
    
      // 배열의 첫 번째 요소 (30개 중 하나의 격자에 해당하는 정보 접근)
      const Element = Array[0];
      console.log('Element:', Element);
    
      // 각 격자에 대해 강수량 반영 격자 색상 변경
      gridObjects.forEach((rectangle, _index) => {
        const item = Array[_index];
        console.log(`Index: ${_index}, reh: ${item.reh}`);
    
        const gridData = item.reh;
        let fillColor = '#ffffff'; // 기본 색상 (흰색)
    
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
          fillOpacity: 0.5,
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

  /* 06.04 (화) 오전 3:20 수정사항 */
  const location = useLocation();
  const { path } = location.state || {};
  const [subPathsWeather, setSubPathsWeather] = useState([]);

  //서버로부터 path 데이터 fetch
  // useEffect(() => {
  //   const fetchPath = async () => {
  //     try {
  //       const response = await axios.get(apiUrl);
  //       const paths = response.data;
  //       const foundPath = paths.find((path) => String(path.index) === index);
  //       if (foundPath) {
  //         setPath(foundPath);
  //         /*선택한 경로의 도보 구간 날씨 서버로부터 post하고 fetch 하는 코드 추가 */
  //         console.log("선택한 path를 찾았습니다", foundPath);
  //         // 현재 시간 정보를 hhmm 형식으로 구하기
  //         const now = new Date();
  //         const hhmm = now.getHours().toString().padStart(2, '0') + now.getMinutes().toString().padStart(2, '0');
  //         // foundPath와 현재 시간 정보를 서버에 POST 요청으로 보내기
  //         try {
  //           const postResponse = await axios.post('http://localhost:3001/postExpected', 
  //           {
  //             pathDto: path,
  //             selectTime: hhmm
  //           });
  //           console.log("POST 응답:", postResponse.data);
  //           //원래 백에서 response 온걸로 해야하지만 임시로 fetch하겠음
  //           fetchSubPathsWeather();
  //         } catch (postError) {
  //           console.error("Error posting data", postError);
  //         }

  //       } else {
  //         console.error(`Path with id ${index} not found.`);
  //       }
  //     } catch (error) {
  //       console.error("Error fetching path", error);
  //     }
  //   };
  //   fetchPath();
  // }, [index]);

  // const fetchSubPathsWeather = async () => {
  //   try {
  //     const response = await axios.get('http://localhost:3001/expected');
  //     setSubPathsWeather(response.data);
  //     console.log("Weather data fetched successfully!", subPathsWeather);
  //   } catch (error) {
  //     console.error("Error fetching weather data", error);
  //   }
  // };



  //지도 생성
  useEffect(() => {
    getGridWeatherFromServer();
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
  }, []);

  //지도에 경로 표시  
  useEffect(() => {
    if (path && map) {
      const bounds = new kakao.maps.LatLngBounds();
      path.subPathDtos.forEach((subPath, index) => {
        const isValidCoordinate = (lat, lon) => {
          return !isNaN(lat) && !isNaN(lon) && lat !== null && lon !== null;
        };
        if (path.subPathDtos) {
          path.subPathDtos.forEach((subPath, index) => {
            if(subPath.type==="walk" && subPath.time !== 0) { //도보 구간일 경우
              const walkPath = [];
              walkPath.push(new kakao.maps.LatLng(subPath.startDto.latitude, subPath.startDto.longitude));
              walkPath.push(new kakao.maps.LatLng(subPath.endDto.latitude, subPath.endDto.longitude));
              // 경로 지도에 선으로 표시
              const walkPolyline = new kakao.maps.Polyline({ 
                path: walkPath,
                strokeWeight: 6,
                strokeColor: "gray",
                strokeOpacity: 1,
                strokeStyle: 'dashed',
              });
              walkPolyline.setMap(map);
              //지도 bound 설정
              bounds.extend(new kakao.maps.LatLng(subPath.startDto.latitude, subPath.startDto.longitude));
              bounds.extend(new kakao.maps.LatLng(subPath.endDto.latitude, subPath.endDto.longitude));
            }
            
            if (subPath.pointDtos) { //버스나 지하철 구간일 경우
              const transportPath = [];
              subPath.pointDtos.forEach(point => {
                transportPath.push(new kakao.maps.LatLng(point.pointInformationDto.latitude, point.pointInformationDto.longitude));
                bounds.extend(new kakao.maps.LatLng(point.pointInformationDto.latitude, point.pointInformationDto.longitude));
              });
              const transportPolyline = new kakao.maps.Polyline({
                path: transportPath,
                strokeWeight: 8,
                strokeColor: subPath.type  === "bus" ? subPath.busColor : subPath.subwayColor,
                strokeOpacity: 0.4,
                strokeStyle: 'solid',
              });
              transportPolyline.setMap(map);
            }

            /* 경로 구간별 출발/도착지점 마커 표시 및 클릭 시 해당 구간으로 이동 */
            // subPath의 출발지/도착지 마커 이미지
            const subPathMarkerImg = new kakao.maps.MarkerImage(
              'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png', //별모양 마커 이미지
              // 'http://t1.daumcdn.net/localimg/localimages/07/2018/pc/img/marker_spot.png', //기본 마커 이미지
              new kakao.maps.Size(15, 25),
              { offset: new kakao.maps.Point(10, 15) } // 마커 이미지의 중앙 하단을 지도 좌표에 일치시키기 위한 옵션
            );

            // subPath의 출발구간 마커
            const subPathStartMarker = new kakao.maps.Marker({
              position: new kakao.maps.LatLng(subPath.startDto.latitude, subPath.startDto.longitude),
              map,
              zIndex: 10,
              image: subPathMarkerImg, // 출발지 마커 이미지 적용(별모양)
            });
            // 마커 클릭 이벤트 등록
            kakao.maps.event.addListener(subPathStartMarker, 'click', () => {
              // 클릭 시 해당 subPath로 지도 이동
              const subPathBounds = new kakao.maps.LatLngBounds();
              subPathBounds.extend(new kakao.maps.LatLng(subPath.startDto.latitude, subPath.startDto.longitude));
              subPathBounds.extend(new kakao.maps.LatLng(subPath.endDto.latitude, subPath.endDto.longitude));
              map.setBounds(subPathBounds);
            });
          });
        }
      });

      // 전체 경로 지도에 다 담기 위한 바운더리 설정
      map.setBounds(bounds);

      //경로의 출발지/목적지 마커 표시
      const pathStartMarkerImg = new kakao.maps.MarkerImage(
        'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/red_b.png',
        new kakao.maps.Size(40, 45),
        { offset: new kakao.maps.Point(10, 35) }
      );

      const pathEndMarkerImg = new kakao.maps.MarkerImage(
        'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/blue_b.png',
        new kakao.maps.Size(40, 45),
        { offset: new kakao.maps.Point(10, 35) }
      );

      const startMarker = new kakao.maps.Marker({
        position: new kakao.maps.LatLng(path.startDto.latitude, path.startDto.longitude),
        map,
        zIndex: 4,
        image: pathStartMarkerImg,
      });

      const endMarker = new kakao.maps.Marker({
        position: new kakao.maps.LatLng(path.endDto.latitude, path.endDto.longitude),
        map,
        zIndex: 4,
        image: pathEndMarkerImg,

      });

      //인포 윈도우에 띄울 내용
      const infoWindow = new kakao.maps.InfoWindow({
        content: `<div style="padding:5px;">출발지: ${path.startDto.name}<br>도착지: ${path.endDto.name}</div>`,
      });
      setInfoWindow(infoWindow);

      // 마커 클릭 이벤트: 출발지/목적지 클릭시 인포 윈도우 show 함수
      const addMarkerClickListener = (marker) => {
        kakao.maps.event.addListener(marker, 'click', () => {
          infoWindow.open(map, marker);
        });
      };
      addMarkerClickListener(startMarker); //경로 출발지 마커 클릭 이벤트 등록
      addMarkerClickListener(endMarker); //경로 도착지 마커 클릭 이벤트 등록
      
    }
  }, [path, map, isInfoVisible]);

  //구간별 경로 박스 클릭 시 해당 경로로 지도 중심 이동(아직 구현 안함)
  const focusSubPath = (subIndex) => {
    //subIndex에 해당하는 polㅛLine 그려진 구간으로 지도 확대
    console.log("해당 경로 구간으로 지도 부드럽게 이동: ", subIndex);
  }

  if (!path) return <div>Loading...</div>; //스피너

  return (
    <React.Fragment>
      <MapContainer id="map" ref={mapRef} style={{position:"relative"}}>
        {/* <button onClick={() => setIsInfoVisible(!isInfoVisible)} style={{zIndex: "1000", position: "absolute"}}>
          {isInfoVisible ? "인포윈도우 숨기기" : "인포윈도우 보기"}
        </button> */}
        <TimeBtnBar>
        {isGridActive && (
          <TimeBtns
            initial={{ x: -50, opacity: 0 }}
            animate={{ x: 0, opacity: 1 }}
            exit={{ x: -50, opacity: 0 }}
            transition={{ duration: 0.5 }}
          >
            <TimeBtn onClick={()=>handleTimeBtn(0)}>00:00</TimeBtn>
            <TimeBtn onClick={()=>handleTimeBtn(1)}>01:00</TimeBtn>
            <TimeBtn onClick={()=>handleTimeBtn(2)}>02:00</TimeBtn>
            <TimeBtn onClick={()=>handleTimeBtn(3)}>03:00</TimeBtn>
            <TimeBtn onClick={()=>handleTimeBtn(4)}>04:00</TimeBtn>
            <TimeBtn onClick={()=>handleTimeBtn(5)}>05:00</TimeBtn>
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
      <PathInfoContainer
        initial={{ height: "40%" }}
        animate={{ height: isExpanded ? "80%" : "40%" }}
        transition={{ duration: 0.5 }}
        onClick={() => setIsExpanded(!isExpanded)}
      >
      <ResultContainer>
        <ToggleBar><TBar /></ToggleBar>
          <PathBox id="pathBox">
            <PathSummary>
              <PathInfo id="timeAndPrice">
                <span>{path.totalTime}</span>
                <div>분 <p>|</p> 도보 {path.totalWalkTime}분 {path.totalWalkDistance}m<p>|</p>{path.price}원</div>
              </PathInfo>
              <PathWeatherInfo>
                <FontAwesomeIcon icon={faDroplet}/>
                <p>{path.totalRain}<span style={{fontSize:"11px"}}>mm</span></p>
              </PathWeatherInfo>
            </PathSummary>

            <SummaryBar id="summaryBar">
              <BarContainer>
                {path.subPathDtos.map((subPath, subIndex) => (
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
                ))}
              </BarContainer>
            </SummaryBar>

            <SubPathSummary>
              {path.subPathDtos.map((subPath, subIndex, array) => (
                <React.Fragment key={subIndex}>
                  {subPath.time !== 0 && (
                    <SubPath onClick={()=> focusSubPath(subIndex)}>
                      <IconColumn id="subPathIconColumn">
                        {subPath.type === "walk" && (
                          <React.Fragment>
                            {subIndex === 0 ? (
                              <React.Fragment>
                                <FontAwesomeIcon icon={faLocationDot} style={{ color: "tomato" }} />
                                <div style={{ color: "tomato" }}>출발</div>
                              </React.Fragment>
                            ) : (
                              <React.Fragment>
                                <FontAwesomeIcon icon={faPersonWalking} style={{ color: "gray" }} />
                                <div style={{ color: "gray" }}>도보</div>
                              </React.Fragment>
                            )}
                            
                          </React.Fragment>
                        )}
                        {subPath.type === "bus" && <FontAwesomeIcon icon={faBus} style={{ color: subPath.busColor, marginLeft: "-1.7px" }} />}
                        {subPath.busColor === "#0068b7" && <div style={{ color: subPath.busColor }}>간선</div>}
                        {subPath.busColor === "#53b332" && <div style={{ color: subPath.busColor }}>지선</div>}
                        {subPath.busColor === "#ffc600" && <div style={{ color: subPath.busColor }}>마을</div>}
                        
                        {subPath.type === "subway" && (
                          <React.Fragment>
                            <FontAwesomeIcon icon={faSubway} style={{ color: subPath.subwayColor }} />
                            <div style={{ color: subPath.subwayColor }}>{subPath.subwayName.split(' ').pop()}</div>
                          </React.Fragment>
                        )}
                      </IconColumn>
                      <TextColumn id="subPathTextColumn">
                        {subPath.type === "walk" && (
                          <React.Fragment>
                            <div>{subPath.startDto.name}</div>
                            <div>도보 {subPath.distance}m {subPath.time}분</div>
                            <br />
                            <div>{subPath.endDto.name}</div>
                            {/* <div> {subPathsWeather.forecast[0].expectedRain}</div> */}
                            {/* {removeFirstForecast()} */}
                          </React.Fragment>
                        )}
                        {subPath.type !== "walk" && (
                          <React.Fragment>
                            <div>
                              {subPath.startDto.name}
                              {subPath.type === "subway" && "역"}
                              {subPath.type !== "walk" && " 승차"}
                              {subPath.type === "subway" && <br />}
                              {subPath.type === "bus" && (
                                <div style={{display: "flex", paddingTop: "5px"}}>
                                  <FontAwesomeIcon icon={faBus} style={{color: subPath.busColor, paddingLeft: "0"}}/>
                                  <div style={{fontWeight: "900", paddingLeft: "5px"}}>{subPath.busNo}</div>
                                </div>
                              )}
                              <br/>
                              {subPath.endDto.name}
                              {subPath.type === "subway" && "역 하차"}
                              {subPath.type === "bus" && " 하차"}
                              </div>
                          </React.Fragment>
                        )}
                        
                      </TextColumn>
                      <WeatherColumn>
                        {subPath.type === "walk" && (
                          <React.Fragment>
                            <div><Icon><FontAwesomeIcon icon={faDroplet}/></Icon>예상 노출: --mm</div>
                            <div><Icon><FontAwesomeIcon icon={faCloudRain}/></Icon>시간당: --mm</div>
                            <div><Icon><FontAwesomeIcon icon={faGlassWaterDroplet}/></Icon>습도: --%</div>
                            <div><Icon><FontAwesomeIcon icon={faWind}/></Icon>풍속: --m/s</div>
                            {/* <div> {subPathsWeather.forecast[0].expectedRain}</div> */}
                            {/* {removeFirstForecast()} */}
                          </React.Fragment>
                        )}
                      </WeatherColumn>
                    </SubPath>
                  )}
                  {subIndex === array.length - 1 && (
                    <SubPath onClick={()=> focusSubPath(subIndex)}>
                      <IconColumn>
                        <FontAwesomeIcon icon={faCircleDot} style={{ color: "#216CFF" }} />
                        <div style={{ color: "#216CFF" }}>도착</div>
                      </IconColumn>
                      <TextColumn>
                        <div>{subPath.endDto.name}</div>
                      </TextColumn>
                    </SubPath>
                  )}
                </React.Fragment>
              ))}
            </SubPathSummary>
          </PathBox>
        </ResultContainer>
      </PathInfoContainer>
    </React.Fragment>
  );
}

export default RouteDetail;

const MapContainer = styled(motion.div)`
  width: 100%;
  height: 60%;
  position: relative;
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
    margin-right: 8px;
    margin-top: 15px;

  }
`;
const TimeBtn = styled.button`
  border-radius: 25px;
  /* border: 2px solid #626161; */
  border: none;
  background-color: #626161;
  color: #7ccdffc9;
  color: white;
  padding: 6px 7px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0px 0px 3px 3px rgba(0, 0, 0, 0.1);

`;

const PathInfoContainer = styled(motion.div)`
  width: 100%;
  height: 40%;
  background: white;
  position: absolute;
  bottom: 0;
  z-index: 1000;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.2);
  overflow-y: scroll;
`;

const ResultContainer = styled.div`
  background-color: whitesmoke;
  width: 100%;
`;

const OptionBar = styled.div`
  width: 100%;
  height: 40px;
  background-color: white;
  border-top: 1px solid darkgrey;
  border-bottom: 1px solid darkgrey;
  display: flex;
  justify-content: start;
  align-items: center;
  text-align: center;
  padding: 0px 20px;
  background-color: #cbf0ff5a;
  button {
    background-color: white;
    border-radius: 25px;
    margin-right: 15px;
    padding: 4px 8px;
    border: 1.5px solid #FFC512;
    font-weight: bold;
    color: #FFC512;
    &:focus {
      background-color: #FFC512;
      color: white;
    }
  }
`;

const SortingBar = styled(OptionBar)`
  justify-content: space-between;
  background-color: white;
  border: none;
`;

const PathBox = styled.div`
  background-color: white;
  padding: 15px 20px;

  margin-bottom: 10px;
`;

/* slide 바 */
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

const TBar = styled.div`
  width: 10%;
  height: 6px;
  border-radius: 25px;
  background-color: #d9d9d9;
`;

const PathSummary = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 15px;
`;
const PathInfo = styled.div`
  width: 100%;
  height: auto;
  font-weight: 600;
  display: flex;
  justify-content: start;
  align-items: end;
  span {
    font-size: 30px;
    font-weight: 900;
    margin-right: 2px;
  }
  div {
    display: flex;
    justify-content: center;
    align-items: end;
    padding-bottom: 5px;
    height: 100%;
  }
  p {
    padding: 0px 5px;
    color: darkgray;
  }
`;
const PathWeatherInfo = styled.div`
  display: block;
  height: auto;
  justify-content: center;
  align-items: center;
  text-align: center;
  color: #63CAFF;
  font-size: 25px;
  p {
    font-weight: 900;
    font-size: 14px;
    color: #004263;
  }
`;

/* 상세 경로 부분 */
const SubPathSummary = styled.div`
  width: 100%;
  padding: 5px 0px;
  font-weight: 300;
  font-size: 14px;
`;

const SubPath = styled.div`
  width: 100%;
  height: auto;
  padding: 5px 0px;
  padding: 10px 0px;
  display: flex;
  /* background-color: salmon; */
  border-top: 3px solid whitesmoke;
  font-weight: 600;
`;
const IconColumn = styled.div`
  width: 80px;
  width: 20%;
  display: flex;
  height: 100%;
  justify-content: center;
  /* background-color: pink; */
  font-weight: 700;
  font-size: 16px;
  * {
    margin-right: 6px;
  }
`;
const TextColumn = styled.div`
  width: 45%;
  border-left: 2px dashed darkgray;
  font-size: 14px;
  * {
    margin-bottom: 5px;
    padding-left: 10px;
  }
`;
const WeatherColumn = styled.div`
  font-size: 13px;
  font-weight: bold;
  width: 35%;

  background-color: #87cfeb44;
  color: #004263;
  * {
    margin-bottom: 3px;
  }
  div {
  display: flex;

  }
`;
const Icon = styled.div`
  padding-right: 5px;
`;

const SummaryBar = styled.div`
  padding: 10px 0px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 10px;
`;

const BarContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 18px;
  background-color: #dddddd;
  background-color: darkgray;
  border-radius: 25px;
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
    font-weight: 700;
    font-size: 10px;
    color: white;
  }
`;

const IconBox = styled.div`
  min-width: 20px;
  max-width: 20px;
  min-height: 20px;
  max-height: 20px;
  border: 1px solid whitesmoke;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2;
  font-size: 12px;
`;

/* Btn 컴포넌트 추가*/


