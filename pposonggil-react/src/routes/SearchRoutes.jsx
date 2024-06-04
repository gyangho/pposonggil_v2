import React, { useState, useEffect } from "react";
import { motion } from "framer-motion";
import styled from "styled-components";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faRotate, faEllipsisVertical, faBus, faSubway, faDroplet, faCircleDot } from "@fortawesome/free-solid-svg-icons";
import { useNavigate } from "react-router-dom";
import { useRecoilState, useResetRecoilState } from "recoil";
import axios from "axios";

import { routeInfoState } from "../recoil/atoms";

const { kakao } = window;

const apiUrl = "http://localhost:3001/paths"; //출발지 목적지 보내는 url

function SearchRoutes() {
  const [route, setRoute] = useRecoilState(routeInfoState);
  const resetRouteInfo = useResetRecoilState(routeInfoState);
  const navigate = useNavigate();

  const [serverResponse, setServerResponse] = useState(null);
  const [paths, setPaths] = useState([]); //서버로부터 받아온 경로 검색 결과 저장
  const [filterOption, setFilterOption] = useState("all");
  const [sortOption, setSortOption] = useState("walk"); // "walk" for 최소도보순, "time" for 최단시간순, "transit" for 최소환승순
  const [activeButton, setActiveButton] = useState("all"); // 현재 활성화된 버튼 상태

  // Recoil 상태가 변경될 때마다 서버로 데이터를 전송(출발지 목적지 위경도 정보 있을 때만)
  useEffect(() => {
    if (route.origin[0].lat && route.dest[0].lat) {
      sendRouteToServer(route);
    }
  }, [route]);

  const sendRouteToServer = async (route) => {
    // 현재 시간 정보를 hhmm 형식
    const now = new Date();
    const hhmm = now.getHours().toString().padStart(2, '0') + now.getMinutes().toString().padStart(2, '0');
    //서버에 출발지/목적지/현재시간 정보 전달(post)
    // try {
    // const response = await axios.post("http://localhost:8080/api/paths/by-member/1", { //실제 url로 바꿔야함
    //   startDto: {
    //     name: route.origin[0].name,
    //     latitude: parseFloat(route.origin[0].lat), //atom에는 다 문자열로 저장해놔서 변환 필요..
    //     longitude: parseFloat(route.origin[0].lon),
    //     x: 0,
    //     y: 0
    //   },
    //   endDto: {
    //     name: route.dest[0].name,
    //     latitude: parseFloat(route.dest[0].lat),
    //     longitude: parseFloat(route.dest[0].lon),
    //     x: 0,
    //     y: 0
    //   },
    //   selectTime : hhmm
    // });
    // console.log("서버 응답: ", response.data);
    // setServerResponse(response.data); // 서버에서 response 값으로 경로 검색 결과 줄거임. 이거 setPaths로
    // fetchPaths(); //테스트용
    // } catch (error) {
    // console.error("서버로 데이터 전송 실패: ", error);
    // }
    // }




    const url = 'http://localhost:8080/api/paths/by-member/1';

    // form-data 객체 생성
    const formData = new FormData();
  
    // 첫 번째 form-data 추가
    const startDto = {
      "name": "숭실대",
      "latitude": 37.4948,
      "longitude": 126.9598,
      "x": 0,
      "y": 0
    };
    formData.append('startDto', new Blob([JSON.stringify(startDto)], { type: 'application/json' }));

    // 두 번째 form-data 추가
    const endDto = {
      "name": "이수역주변",
      "latitude": 37.4857,
      "longitude": 126.9815,
      "x": 0,
      "y": 0
    };
    formData.append('endDto', new Blob([JSON.stringify(endDto)], { type: 'application/json' }));

    // 세 번째 form-data 추가
    formData.append('selectTime', '0014');

    try {
      const response = await axios.post(url, formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      });
      console.log('Response:', response.data);
      setServerResponse(response.data); // 서버에서 response 값으로 경로 검색 결과 줄거임. 이거 setPaths로

    } catch (error) {
      console.error('Error:', error);
    }
  };
  //임시로 서버에서 데이터 받는 것 구현.
  const fetchPaths = async () => {
    try {
      const response = await axios.get(apiUrl);
      setPaths(response.data);
    } catch (error) {
      console.error("Error fetching paths", error);
    }
  };

  const onReverseClick = () => {
    setRoute((prev) => ({
      origin: prev.dest,
      dest: prev.origin,
    }));
  };

  const onResetClick = () => {
    resetRouteInfo();
    setPaths([]);
  };

  // 경로 검색 결과 필터 버튼 3개
  const filterPaths = () => {
    if (filterOption === "all") {
      return paths;
    } else if (filterOption === "bus") {
      return paths.filter(path => path.subPathDtos.every(subPath => subPath.type === "walk" || subPath.type === "bus"));
    } else if (filterOption === "subway") {
      return paths.filter(path => path.subPathDtos.every(subPath => subPath.type === "walk" || subPath.type === "subway"));
    }
  };
  // 정렬 기준 옵션(최소도보, 최단시간, 최소 환승)
  const sortPaths = (filteredPaths) => {
    if (sortOption === "walk") {
      return filteredPaths.sort((a, b) => a.totalWalkDistance - b.totalWalkDistance);
    } else if (sortOption === "time") {
      return filteredPaths.sort((a, b) => a.totalTime - b.totalTime);
    } else if (sortOption === "transit") {
      return filteredPaths.sort((a, b) => a.totalTransitCount - b.totalTransitCount);
    }
  };
  //버스 버튼 => 도보 + 버스만 포함한 경로, 지하철 버튼 => 도보 + 지하철만 포함한 경로 필터링
  const busPathsCount = paths.filter(path => path.subPathDtos.every(subPath => subPath.type === "walk" || subPath.type === "bus")).length;
  const subwayPathsCount = paths.filter(path => path.subPathDtos.every(subPath => subPath.type === "walk" || subPath.type === "subway")).length;

  const filteredPaths = filterPaths();
  const sortedPaths = sortPaths(filteredPaths);

  //클릭한 경로의 index url 파라미터로 전달 (전: pathId, 현: index)
  const goToRouteDetail = (index) => {
    navigate(`/search/routes/${index}`);
  };

  return (
    <Wrapper>
      <SearchContainer id="origin">
        <Container>
          <Input
            type="text"
            value={route.origin[0].name}
            onClick={() => navigate('/search')}
            readOnly
            placeholder="출발지 입력"
          />
        </Container>
        <Container onClick={onReverseClick}>
          <FontAwesomeIcon icon={faRotate} />
        </Container>
      </SearchContainer>
      <SearchContainer id="dest">
        <Container>
          <Input
            type="text"
            value={route.dest[0].name}
            onClick={() => navigate('/search')}
            readOnly
            placeholder="도착지 입력"
          />
        </Container>
        <Container onClick={onResetClick}>
          <FontAwesomeIcon icon={faEllipsisVertical} />
        </Container>
      </SearchContainer>
     
      <OptionBar id="sortingByTransport">
        <button
          className={activeButton === "all" ? "active" : ""}
          onClick={() => { setFilterOption("all"); setActiveButton("all"); }}
        >
          전체
        </button>
        <button
          className={activeButton === "bus" ? "active" : ""}
          onClick={() => { setFilterOption("bus"); setActiveButton("bus"); }}
        >
          버스 {busPathsCount}
        </button>
        <button
        className={activeButton === "subway" ? "active" : ""}
        onClick={() => { setFilterOption("subway"); setActiveButton("subway"); }}
        >
          지하철 {subwayPathsCount}
        </button>
      </OptionBar>

      <SortingBar id="sortingOption">
        <div>{new Date().toLocaleString("ko-KR", { hour: "numeric", minute: "numeric" })} 출발</div>
        <select onChange={(e) => setSortOption(e.target.value)}>
          <option value="walk">최소도보순</option>
          <option value="time">최단시간순</option>
          <option value="transit">최소환승순</option>
        </select>
      </SortingBar>

      <ResultContainer>
        {sortedPaths.map((path, index) => (
          <PathBox id="pathBox" key={index} onClick={() => goToRouteDetail(path.index)}>
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
                {path.subPathDtos && path.subPathDtos.map((subPath, subIndex) => (
                  subPath.time !== 0 && (
                    <React.Fragment key={subIndex}>
                      <Bar
                        width={(subPath.time / path.totalTime) * 100}
                        color={subPath.type === 'walk' ? 'darkgray' : (subPath.type === 'subway' ? subPath.subwayColor : subPath.busColor)}
                      >
                        {subPath.type !== 'walk' && (
                          <IconBox style={{backgroundColor: subPath.type === 'bus' ? subPath.busColor : subPath.subwayColor}}>
                            <FontAwesomeIcon
                              icon={subPath.type === 'bus' ? faBus : (subPath.type === 'subway' && faSubway)}
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
              {path.subPathDtos && path.subPathDtos.filter(subPath => subPath.type !== 'walk').map((subPath, subIndex, array) => (
                <React.Fragment key={subIndex}>
                  <SubPath>
                    <IconColumn id="subPathIconColumn">
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
                      <div>
                        {subPath.startDto.name}
                        {subPath.type === "subway" && "역"}
                      </div>
                      {subPath.type === "bus" && (
                        <div style={{display: "flex", paddingTop: "5px"}}>
                          <FontAwesomeIcon icon={faBus} style={{color: subPath.busColor, marginRight: "3px"}}/>
                          <div style={{fontWeight: "600"}}>{subPath.busNo}</div>
                        </div>
                      )}
                    </TextColumn>
                  </SubPath>
                      
                  {/* 마지막 subPath인 경우 하차 정보를 표시 */}
                  {subIndex === array.length - 1 && (
                    <SubPath>
                      <IconColumn>
                        <FontAwesomeIcon icon={faCircleDot} style={{ color: "darkgray" }} />
                        <div style={{ color: "darkgray" }}>하차</div>
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
        ))}
      </ResultContainer>
    </Wrapper>
  ); 
}
export default SearchRoutes;

const Wrapper = styled.div`
  overflow-y: scroll;
  height: 100%;
  width: 100%;
`;

const SearchContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 15px 0px;
  width: 100%;
`;

const Container = styled.div`
  width: 65%;
  height: 35px;
  background-color: whitesmoke;
  padding: 0px 20px;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  border-radius: 15px;
  margin: 0px 5px;
  border-radius: 5px;

  &:last-child {
    width: 10%;
    background-color: #003E5E;
    justify-content: flex-end;
    color: white;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
  }
`;

const Input = styled(motion.input)`
  text-align: left;
  width: 100%;
  height: 100%;
  font-size: 15px;
  font-weight: 600;
  border: none;
  background-color: whitesmoke;
  margin: 0px 5px s;
  &:focus {
    outline: none;
    cursor: pointer;
  }
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
  /* background-color: salmon; */
  font-weight: 400;
`;

const IconColumn = styled.div`
  width: 80px;
  display: flex;

  /* margin-left: 5px; */
  justify-content: start;
  /* background-color: pink; */
  font-weight: 700;
  font-size: 15px;
  * {
    margin-right: 6px;
  }
`;

const TextColumn = styled.div`
  width: 75%;

  /* background-color: skyblue; */
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

