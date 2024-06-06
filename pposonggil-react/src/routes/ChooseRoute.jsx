import React, { useState, useEffect, useCallback, useRef } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";
import styled from "styled-components";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faBolt, faCloud } from "@fortawesome/free-solid-svg-icons";

const { kakao } = window;

function ChooseRoute() {
  const navigate = useNavigate();
  const location = useLocation();
  const { path } = location.state || {};
  const defaultPath = path;
  const [paths, setPaths] = useState({
    defaultPath: [],
    pposongPath: []
  });

  /* 지도 생성 */
  const [map, setMap] = useState(null);
  const [infoWindow, setInfoWindow] = useState(null);
  const mapRef = useRef(null);
  const mapInstance = useRef(null);
  const geocoder = useRef(null);
  
  const getPathsFromServer = useCallback(async () => { // post 요청이 2개라 렌더링 최소화를 위해 useCallback으로 감쌈
    const defaultUrl = "http://localhost:8080/api/path/default";
    const pposongUrl = "http://localhost:8080/api/path/pposong";
    try {
      const [defaultResponse, pposongResponse] = await Promise.all([
        axios.post(defaultUrl, path, { headers: { 'Content-Type': 'application/json' } }),
        axios.post(pposongUrl, path, { headers: { 'Content-Type': 'application/json' } })
      ]);

      setPaths({
        defaultPath: defaultResponse.data,
        pposongPath: pposongResponse.data
      });

      console.log("default path 서버 응답: ", defaultResponse.data);
      console.log("pposong path 서버 응답: ", pposongResponse.data);
    } catch (error) {
      console.error("경로를 얻지 못했습니다.", error);
    }
  }, [path]);

  useEffect(() => {
    getPathsFromServer();
  }, [getPathsFromServer]);



  /* 지도 초기화 */
  useEffect(() => {
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
  }, []);

  /* 경로 표시(기본: defaultPath) */
  useEffect(() => {
    if (defaultPath && map) {
      const bounds = new kakao.maps.LatLngBounds();

      defaultPath.subPathDtos.forEach((subPath) => {
        if (subPath.type === "walk" && subPath.time !== 0) {
          const walkPath = [
            new kakao.maps.LatLng(subPath.startDto.latitude, subPath.startDto.longitude),
            new kakao.maps.LatLng(subPath.endDto.latitude, subPath.endDto.longitude)
          ];
          const walkPolyline = new kakao.maps.Polyline({
            path: walkPath,
            strokeWeight: 6,
            strokeColor: "gray",
            strokeOpacity: 1,
            strokeStyle: 'dashed',
          });
          walkPolyline.setMap(map);
          bounds.extend(new kakao.maps.LatLng(subPath.startDto.latitude, subPath.startDto.longitude));
          bounds.extend(new kakao.maps.LatLng(subPath.endDto.latitude, subPath.endDto.longitude));
        }

        if (subPath.pointDtos) {
          const transportPath = subPath.pointDtos.map(point =>
            new kakao.maps.LatLng(point.pointInformationDto.latitude, point.pointInformationDto.longitude)
          );
          const transportPolyline = new kakao.maps.Polyline({
            path: transportPath,
            strokeWeight: 9,
            strokeColor: subPath.type === "bus" ? subPath.busColor : subPath.subwayColor,
            strokeOpacity: 0.8,
            strokeStyle: 'solid',
          });
          transportPolyline.setMap(map);
          transportPath.forEach(point => bounds.extend(point));
        }
      });

      // 지도의 중심 및 레벨 재설정
      map.setBounds(bounds);
      mapInstance.current.panBy(0, 80); // 지도 중심 위로 60px 이동(경로 선택 박스 크기 반영)
      const currentLevel = mapInstance.current.getLevel();
      mapInstance.current.setLevel(currentLevel + 1);

      const pathStartMarkerImg = new kakao.maps.MarkerImage( // 출발지 마커 이미지
        'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/red_b.png',
        new kakao.maps.Size(40, 45),
        { offset: new kakao.maps.Point(10, 35) }
      );

      const pathEndMarkerImg = new kakao.maps.MarkerImage( // 도착지 마커 이미지
        'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/blue_b.png',
        new kakao.maps.Size(40, 45),
        { offset: new kakao.maps.Point(10, 35) }
      );

      const startMarker = new kakao.maps.Marker({ // 출발지 마커 생성
        position: new kakao.maps.LatLng(path.startDto.latitude, path.startDto.longitude),
        map,
        zIndex: 4,
        image: pathStartMarkerImg,
      });

      const endMarker = new kakao.maps.Marker({ // 도착지 마커 생성
        position: new kakao.maps.LatLng(path.endDto.latitude, path.endDto.longitude),
        map,
        zIndex: 4,
        image: pathEndMarkerImg,
      });

      // 출발지 목적지 클릭시 인포윈도우
      const infoWindowContent = `<div style="padding:5px;">출발지: ${path.startDto.name}<br>도착지: ${path.endDto.name}</div>`;
      const infoWindow = new kakao.maps.InfoWindow({
        content: infoWindowContent,
      });
      setInfoWindow(infoWindow);

      const addMarkerClickListener = (marker) => {
        kakao.maps.event.addListener(marker, 'click', () => {
          infoWindow.open(map, marker);
        });
      };

      addMarkerClickListener(startMarker);
      addMarkerClickListener(endMarker);
    }
    
  }, [path, map]);

  const chooseDefaultPath = () => {
    navigate('/search/detail', { state: { path: paths.defaultPath } });
  };

  const choosePposongPath = () => {
    navigate('/search/detail', { state: { path: paths.pposongPath } });
  };

  return (
    <React.Fragment>
      <Map id="map" ref={mapRef}>맵</Map>

      <BoxContainer>
        <Box id="defaultPath" onClick={chooseDefaultPath}>
          <div style={{padding: "0", marginBottom: "10px"}}>
            <FontAwesomeIcon icon={faBolt} style={{marginRight: "8px", color: "orange"}}/>
            <h1>기본 경로</h1>
          </div>
          <div><p style={{fontSize: "30px"}}>{paths.defaultPath.totalTime}</p>분</div>
          <div>도보 <p>{paths.defaultPath.totalWalkTime}</p>분 <p style={{marginLeft: "5px"}}>{paths.defaultPath.totalWalkDistance}</p>m</div>
          <div><p>{paths.defaultPath.price}</p>원</div>
        </Box>
        <Box id="pposongPath" onClick={choosePposongPath}>
        <div style={{padding: "0"}}>
            <FontAwesomeIcon icon={faCloud} style={{marginRight: "8px", color: "skyblue"}}/>
            <h2>뽀송 경로</h2>
          </div>
          <div><p style={{fontSize: "30px"}}>{paths.pposongPath.totalTime}</p>분</div>
          <div>도보 <p>{paths.pposongPath.totalWalkTime}</p>분 <p style={{marginLeft: "5px"}}>{paths.pposongPath.totalWalkDistance}</p>m</div>
          <div><p>{paths.pposongPath.price}</p>원</div>
        </Box>
      </BoxContainer>
    </React.Fragment>
  );
}

export default ChooseRoute;

/* styled */
const Map = styled.div`
  width: 100%;
  height: 100%;
  position: relative;
`;

const BoxContainer = styled.div`
  // position: absolute;
  position: sticky;
  width: 100%;
  height: 30%;
  display: flex;
  justify-content: center;
  align-items: center;
  bottom: 70px;
  z-index: 10;
  padding: 20px 10px;
`;

const Box = styled.div`
  width: 50%;
  height: 100%;
  background-color: #ffffffd7;
  box-shadow: 0px 0px 6px 6px rgba(68, 68, 68, 0.1);
  border-radius: 20px;
  margin: 20px 10px;
  padding: 20px;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  font-weight: 500;
  text-align: center;
  div {
    flex: 1; // 동일한 크기로 확장
    display: flex;
    align-items: center; //세로 가운데 정렬
    padding: 0px 10px;
    padding-bottom: 5px;
  }
  h1 {
    font-size: 20px;
    font-weight: bold;
    color: #F26500;
    padding-top: 4px;
  }
  h2 {
    font-size: 20px;
    font-weight: bold;
    color: #003E5E;
    padding-top: 4px;

  }
  p {
    font-weight: bold;
    font-size: 18px;
    padding: 0px 3px;
  }
`;

const Posong = styled.div`
  width: 50%;
  height: 30px;
  right: 0;
  position: sticky;
  z-index: 500;
  background-color: pink;
`;
