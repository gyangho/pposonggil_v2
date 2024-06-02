import React, { useEffect, useState, useRef } from "react";
import { useParams } from "react-router-dom";
import axios from "axios";

import styled from "styled-components";
import { motion } from "framer-motion";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faBus, faSubway, faDroplet, faCircleDot, faPersonWalking, faLocationDot } from "@fortawesome/free-solid-svg-icons";

const { kakao } = window;
const apiUrl = "http://localhost:3001/paths";

function RouteDetail() {
  const { pathId } = useParams();
  const [path, setPath] = useState(null);
  const [map, setMap] = useState(null);
  const [isInfoVisible, setIsInfoVisible] = useState(false);
  const [infoWindow, setInfoWindow] = useState(null);
  const [isExpanded, setIsExpanded] = useState(false);

  const mapRef = useRef(null);
  const mapInstance = useRef(null);

  //서버로부터 path 데이터 fetch
  useEffect(() => {
    const fetchPath = async () => {
      try {
        const response = await axios.get(apiUrl);
        const paths = response.data;
        const foundPath = paths.find((path) => String(path.pathId) === pathId);
        if (foundPath) {
          setPath(foundPath);
          console.log("fetch 성공!", foundPath);
        } else {
          console.error(`Path with id ${pathId} not found.`);
        }
      } catch (error) {
        console.error("Error fetching path", error);
      }
    };
    fetchPath();
  }, [pathId]);

  //지도 생성
  useEffect(() => {
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
        setMap(mapInstance.current);
        console.log("지도 랜더링");
      });
    };
  }, []);

  //지도 위 경로 표시  
  useEffect(() => {
    if (path && map) {
      const bounds = new kakao.maps.LatLngBounds();
      path.subPathDtos.forEach((subPath, index) => {
        const isValidCoordinate = (lat, lng) => {
          return !isNaN(lat) && !isNaN(lng) && lat !== null && lng !== null;
        };
        if (path.subPathDtos) {
          path.subPathDtos.forEach((subPath, index) => {
            if(subPath.type==="walk") { //도보 구간일 경우
              const walkPath = [];
              // 일단 무시하고 수정될 거라 가정하고 코드 짜놓겠음
              // if(index === 0 || index === path.subPathDtos.length-1) {
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
                strokeOpacity: 0.2,
                strokeStyle: 'solid',
              });
              transportPolyline.setMap(map);
            }
          });
        }
      });

      // 전체 경로 지도에 다 담기 위한 바운더리 설정
      map.setBounds(bounds);

      //출발지 마커
      const startMarker = new kakao.maps.Marker({
        position: new kakao.maps.LatLng(path.startDto.latitude, path.startDto.longitude),
        map,
      });
      //도착지 마커 표시
      const endMarker = new kakao.maps.Marker({
        position: new kakao.maps.LatLng(path.endDto.latitude, path.endDto.longitude),
        map,
      });

      //인포 윈도우에 띄울 내용
      const infoWindow = new kakao.maps.InfoWindow({
        content: `<div style="padding:5px;">출발지: ${path.startDto.name}<br>도착지: ${path.endDto.name}</div>`,
      });
      setInfoWindow(infoWindow);

      // 마커 클릭 시 인포 윈도우 show
      if (isInfoVisible) {
        infoWindow.open(map, startMarker);
      }

      kakao.maps.event.addListener(startMarker, 'click', () => {
        infoWindow.open(map, startMarker);
      });

      kakao.maps.event.addListener(endMarker, 'click', () => {
        infoWindow.open(map, endMarker);
      });
    }
  }, [path, map, isInfoVisible]);

  if (!path) return <div>Loading...</div>;

  //구간별 경로 박스 클릭 시 해당 경로로 지도 중심 이동
  const focusSubPath = (subIndex) => {
    //subIndex에 해당하는 polㅛLine 그려진 구간으로 지도 확대
    console.log("해당 경로 구간으로 지도 부드럽게 이동");
  }

  return (
    <React.Fragment>
      <MapContainer id="map" ref={mapRef} style={{position:"relative"}}>
        <button onClick={() => setIsInfoVisible(!isInfoVisible)} style={{zIndex: "1000", position: "absolute"}}>
          {isInfoVisible ? "인포윈도우 숨기기" : "인포윈도우 보기"}
        </button>
      </MapContainer>
      
      <PathInfoContainer
        initial={{ height: "50%" }}
        animate={{ height: isExpanded ? "70%" : "50%" }}
        transition={{ duration: 0.5 }}
        onClick={() => setIsExpanded(!isExpanded)}
      >
        <ResultContainer>
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
                                <FontAwesomeIcon icon={faLocationDot} style={{ color: "darkgray" }} />
                                <div style={{ color: "darkgray" }}>출발</div>
                              </React.Fragment>
                            ) : (
                              <React.Fragment>
                                <FontAwesomeIcon icon={faPersonWalking} style={{ color: "darkgray" }} />
                                <div style={{ color: "darkgray" }}>도보</div>
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
                            <div>{subPath.startDto.name} 
                            </div>
                            <div>도보 {subPath.distance}m {subPath.time}분</div>
                          </React.Fragment>
                        )}
                        {subPath.type !== "walk" && (
                          <React.Fragment>
                            <div>
                          {subPath.startDto.name}
                          {subPath.type === "subway" && "역"}
                          {subPath.type !== "walk" && " 승차"}
                          {subPath.type === "bus" && (
                            <div style={{display: "flex", paddingTop: "5px"}}>
                              <FontAwesomeIcon icon={faBus} style={{color: subPath.busColor, marginRight: "3px"}}/>
                              <div style={{fontWeight: "600"}}>{subPath.busNo}</div>
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
                    </SubPath>
                  )}

                  {subIndex === array.length - 1 && (
                    <SubPath onClick={()=> focusSubPath(subIndex)}>
                      <IconColumn>
                        <FontAwesomeIcon icon={faCircleDot} style={{ color: "darkgray" }} />
                        <div style={{ color: "darkgray" }}>도착</div>
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

const MapContainer = styled.div`
  width: 100%;
  height: 50%;
`;

const PathInfoContainer = styled(motion.div)`
  width: 100%;
  background: white;
  position: absolute;
  bottom: 0;
  z-index: 10;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
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
  border-top: 1px solid #dddddd;
  border-bottom: 1px solid #dddddd;
  margin-bottom: 10px;
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
  border-top: 1px solid darkgrey;
  font-weight: 300;
  font-size: 14px;
`;

const SubPath = styled.div`
  width: 100%;
  height: auto;
  padding: 5px 0px;
  display: flex;
  background-color: salmon;
  border-bottom: 2px solid black;
  font-weight: 400;
`;
const IconColumn = styled.div`
  width: 80px;
  display: flex;
  height: 100%;
  /* margin-left: 5px; */
  justify-content: start;
  background-color: pink;
  font-weight: 700;
  font-size: 15px;
  * {
    margin-right: 6px;
  }
`;
const TextColumn = styled.div`
  width: 75%;
  border-bottom: 1px solid darkgray;
  background-color: skyblue;
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

