import React, { useState, useEffect } from "react";
import styled from "styled-components";
import { motion } from "framer-motion";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faRotate, faEllipsisVertical, faBus, faSubway, faPersonWalking } from "@fortawesome/free-solid-svg-icons";
import { useNavigate } from "react-router-dom";
import { useRecoilState, useResetRecoilState } from "recoil";
import axios from "axios";

import { routeInfoState } from "../recoil/atoms";

const { kakao } = window;

const apiUrl = "http://localhost:3001/paths";

function SearchRoutes() {
  const [route, setRoute] = useRecoilState(routeInfoState);
  const resetRouteInfo = useResetRecoilState(routeInfoState);
  const navigate = useNavigate();

  const [serverResponse, setServerResponse] = useState(null);
  const [paths, setPaths] = useState([]);

  const sendRouteToServer = async (route) => {
    try {
      const response = await axios.post(apiUrl, {
        startDto: {
          name: route.origin[0].name,
          latitude: parseFloat(route.origin[0].lat),
          longitude: parseFloat(route.origin[0].lon),
        },
        endDto: {
          name: route.dest[0].name,
          latitude: parseFloat(route.dest[0].lat),
          longitude: parseFloat(route.dest[0].lon),
        },
      });
      console.log("서버 응답: ", response.data);
      setServerResponse(response.data);
      fetchPaths();
    } catch (error) {
      console.error("서버로 데이터 전송 실패: ", error);
    }
  };

  const fetchPaths = async () => {
    try {
      const response = await axios.get(apiUrl);
      setPaths(response.data);
    } catch (error) {
      console.error("Error fetching paths", error);
    }
  };

  useEffect(() => {
    if (route.origin[0].lat && route.dest[0].lat) {
      sendRouteToServer(route);
    }
  }, [route]);

  const onReverseClick = () => {
    setRoute((prev) => ({
      origin: prev.dest,
      dest: prev.origin,
    }));
  };

  const onResetClick = () => {
    resetRouteInfo();
  };

  console.log("경로 정보 확인: ", route);

  return (
    <React.Fragment>
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
        <Container>
          <FontAwesomeIcon icon={faRotate} onClick={onReverseClick} />
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
        <Container>
          <FontAwesomeIcon icon={faEllipsisVertical} onClick={onResetClick} />
        </Container>
      </SearchContainer>
      {paths && paths.length > 0 && paths.map((path, index) => (
        <ResultContainer key={index}>
          <OptionBar id="sortingByTransport">
            <button>전체</button>
            <button>버스 3</button>
            <button>지하철 2</button>
          </OptionBar>
          <SortingBar id="sortingOption">
            <div>오늘 오후 12:43 출발</div>
            <div>최소도보순</div>
          </SortingBar>

          <PathBox id="pathBox">
            <PathInfo id="timeAndPrice">
              <span>{path.totalTime}</span>
              <div>분 <p>|</p> 도보 {path.totalWalkTime}분 {path.totalWalkDistance}m<p>|</p>{path.price}원</div>
            </PathInfo>

            <SummaryBar id="summaryBar">
              <BarContainer>
                {path.subPathDtos && path.subPathDtos.map((subPath, index) => (
                  <React.Fragment key={index}>
                    {subPath.type === 'walk' ? (
                      <React.Fragment>
                        
                        <Bar
                          key={subPath.subPathId}
                          width={(subPath.time / path.totalTime) * 100}
                          color={'darkgray'}>
                            {/* <IconBox>
                              <TransportIcon icon={faPersonWalking} style={{ color: 'gray'}} />
                            </IconBox> */}
                          <p>{subPath.time}분</p>
                        </Bar>
                        
                      </React.Fragment>
                    ) : (
                      <React.Fragment>
                        <Bar
                          key={subPath.subPathId}
                          width={(subPath.time / path.totalTime) * 100}
                          color={subPath.type === 'subway' ? subPath.subwayColor : subPath.busColor}>
                            <IconBox>
                              <TransportIcon icon={subPath.type === 'bus' ? faBus : faSubway} style={{ color: subPath.type === 'bus' ? subPath.busColor : subPath.subwayColor }} />
                            </IconBox>
                            <p>{subPath.time}분</p>
                        </Bar>
                      </React.Fragment>
                    )}
                    <div style={{ position: 'absolute', left: '50%', transform: 'translateX(-50%)', bottom: '25px', fontWeight: 'bold' }}>{subPath.time}분</div>
                  </React.Fragment>
                ))}
              </BarContainer>
            </SummaryBar>

            <SubPathSummary>
              {path.subPathDtos && path.subPathDtos.filter(subPath => subPath.type !== "walk").map((subPath, subIndex) => (
                <SubPath key={subIndex}>
                  <EachSubPath id="subPath-component">
                    <IconColumn id="subPathIconColumn">
                      {subPath.type === "bus" && (
                        <FontAwesomeIcon icon={faBus} />
                      )}
                      {subPath.type === "subway" && (
                        <React.Fragment>
                        <FontAwesomeIcon icon={faSubway} style={{ color: subPath.subwayColor }} />
                        {subPath.type === "subway" && <div>{subPath.subwayName.split(' ').pop()}</div>}
                      </React.Fragment>
                      )}
                    </IconColumn>
                    <TextColumn id="subPathTextColumn">
                      <div>
                        {subPath.pointDtos[0] && subPath.pointDtos[0].pointInformationDto.name}
                        {subPath.type === "subway" && "역"}

                      </div>
                      {subPath.type === "bus" && <div>{subPath.busNo}</div>}
                      {/* {subPath.type === "subway" && <div>{subPath.subwayName}</div>} */}
                      <div>
                        {subPath.pointDtos[subPath.pointDtos.length - 1] && subPath.pointDtos[subPath.pointDtos.length - 1].pointInformationDto.name}
                        {subPath.type === "subway" && "역"}
                      </div>
                    </TextColumn>
                  </EachSubPath>
                </SubPath>
              ))}
            </SubPathSummary>
          </PathBox>
        </ResultContainer>
      ))}
    </React.Fragment>
  );
}

export default SearchRoutes;


// import React, { useState, useEffect } from "react";
// import styled from "styled-components";
// import { motion } from "framer-motion";
// import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
// import { faRotate, faEllipsisVertical, faClockRotateLeft, faBus, faSubway, faPersonWalking } from "@fortawesome/free-solid-svg-icons";
// import { useNavigate } from "react-router-dom";
// import { useRecoilState, useResetRecoilState } from "recoil";
// import { routeInfoState } from "../recoil/atoms";
// import axios from "axios";

// const { kakao } = window;

// // JSON 서버 API URL(json-server 이용한 프톤트 테스트 용)
// const apiUrl = "http://localhost:3001/paths";

// function SearchRoutes() {
//   const [route, setRoute] = useRecoilState(routeInfoState);
//   const resetRouteInfo = useResetRecoilState(routeInfoState);
//   const navigate = useNavigate();

//   const [serverResponse, setServerResponse] = useState(null); // 서버 응답 저장용
//   const [paths, setPaths] = useState([]);

//   // 서버에 출발지 목적지 위도 경도 데이터 전송
//   const sendRouteToServer = async (route) => {
//     try {
//       // const response = await axios.post("YOUR_SERVER_URL/api/route", {
//       const response = await axios.post(apiUrl, {
//         startDto: {
//           name: route.origin[0].name,
//           latitude: parseFloat(route.origin[0].lat),
//           longitude: parseFloat(route.origin[0].lon),
//         },
//         endDto: {
//           name: route.dest[0].name,
//           latitude: parseFloat(route.dest[0].lat),
//           longitude: parseFloat(route.dest[0].lon),
//         },
//       });
//       console.log("서버 응답: ", response.data);
//       setServerResponse(response.data); // 서버 응답 정보
//       console.log(response.data);
//       fetchPaths(); // 출발지 목적지 보내고 경로정보 get하는 함수 호출
//     } catch (error) {
//       console.error("서버로 데이터 전송 실패: ", error);
//     }
//   };

//   // 서버에서 경로 정보 가져오기
//   const fetchPaths = async () => {
//     try {
//       const response = await axios.get(apiUrl);
//       setPaths(response.data);
//     } catch (error) {
//       console.error("Error fetching paths", error);
//     }
//   };

//   // Recoil 상태가 변경될 때마다 서버로 데이터를 전송(출발지 목적지 위경도 정보 있을 때만)
//   useEffect(() => {
//     if (route.origin[0].lat && route.dest[0].lat) {
//       sendRouteToServer(route);
//     }
//   }, [route]);

//   // reverse 버튼 핸들러
//   const onReverseClick = () => {
//     setRoute((prev) => ({
//       origin: prev.dest,
//       dest: prev.origin,
//     }));
//   };

//   // reset 버튼 클릭 핸들러
//   const onResetClick = () => {
//     resetRouteInfo();
//   };

//   console.log("경로 정보 확인: ", route); // test용

//   return (
//     <React.Fragment>
//       <SearchContainer id="origin">
//         <Container>
//           <Input
//             type="text"
//             value={route.origin[0].name}
//             onClick={() => navigate('/search')}
//             readOnly
//             placeholder="출발지 입력"
//           />
//         </Container>
//         <Container>
//           <FontAwesomeIcon icon={faRotate} onClick={onReverseClick} />
//         </Container>
//       </SearchContainer>
//       <SearchContainer id="dest">
//         <Container>
//           <Input
//             type="text"
//             value={route.dest[0].name}
//             onClick={() => navigate('/search')}
//             readOnly
//             placeholder="도착지 입력"
//           />
//         </Container>
//         <Container>
//           <FontAwesomeIcon icon={faEllipsisVertical} onClick={onResetClick} />
//         </Container>
//       </SearchContainer>
//       {paths.map((path) => (
//         <ResultContainer key={path.totalTime}>
//           <OptionBar id="sortingByTransport">
//             <button>전체</button>
//             <button>버스 3</button>
//             <button>지하철 2</button>
//           </OptionBar>
//           <SortingBar id="sortingOption">
//             <div>오늘 오후 12:43 출발</div>
//             <div>최소도보순</div>
//           </SortingBar>

//           <PathBox id="pathBox">
//             <PathInfo id="timeAndPrice">
//               <span>{path.totalTime}</span>
//               <div>분 <p>|</p> 도보 {path.totalWalkTime}분 {path.totalWalkDistance}m<p>|</p>{path.price}원</div>
//             </PathInfo>

//             <SummaryBar id="summaryBar">
//               <BarContainer>
//                 {path.subPathDtos && path.subPathDtos.filter(subPath => subPath.type !== 'walk').map((subPath) => (
//                   <Bar
//                     key={subPath.subPathId}
//                     width={(subPath.time / path.totalTime) * 100}
//                     color={subPath.type === 'subway' ? subPath.subwayColor : subPath.busColor}
//                   />
//                 ))}
//               </BarContainer>
//             </SummaryBar>

//             <SubPathSummary>
//               {path.subPathDtos && path.subPathDtos.filter(subPath => subPath.type !== 'walk').map((subPath) => (
//                 <SubPath key={subPath.subPathId}>
//                   <div id="subPath-component">
//                     <div id="subPathIconColumn">
//                       {subPath.type === "bus" && <FontAwesomeIcon icon={faBus} style={{ color: subPath.busColor }} />}
//                       {subPath.type === "subway" && <FontAwesomeIcon icon={faSubway} style={{ color: subPath.subwayColor }} />}
//                       {subPath.type === "walk" && <FontAwesomeIcon icon={faPersonWalking} />}
//                     </div>
//                     <div id="subPathTextColumn">
//                       <div>{subPath.pointDtos[0] && subPath.pointDtos[0].pointInformationDto.name}</div>
//                       {subPath.type === "bus" && <div>{subPath.busNo}</div>}
//                       {subPath.type === "subway" && <div>{subPath.subwayName.split(' ').pop()}</div>}
//                       <div>{subPath.pointDtos[subPath.pointDtos.length - 1] && subPath.pointDtos[subPath.pointDtos.length - 1].pointInformationDto.name}</div>
//                     </div>
//                   </div>
//                 </SubPath>
//               ))}
//             </SubPathSummary>
//           </PathBox>
//         </ResultContainer>
//       ))}
//     </React.Fragment>
//   );
// }

// export default SearchRoutes;




//////

const SearchContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 15px 0px;
  width: 100%;
`;

const Container = styled.div`
  width: 65%;
  height: 40px;
  background-color: whitesmoke;
  padding: 0px 20px;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  border-radius: 15px;
  padding: 12px;
  margin: 0px 5px;
  &:last-child { //버튼
    width: 10%;
    border-radius: 5px;
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
  font-size: 17px;
  font-weight: 600;
  border: none;
  background-color: whitesmoke;
  margin-left: 10px;
  &:focus {
    outline: none;
    cursor: pointer;
  }
`;

const ResultContainer = styled.div`
  background-color: whitesmoke;
  min-height: 65vh;
  width: 100%;
  max-height: 65vh;
  overflow-y: scroll;
  bottom: 70px;
  left: 0;
  right: 0;
`;

const Icon = styled(FontAwesomeIcon)`
  width: 22px;
  height: 22px;
`;

const SearchedRoute = styled.div`
  display: flex;
  justify-content: flex-start;
  align-items: center;
  padding: 10px 20px;
  height: auto;
  min-height: 45px;
  color: black;
  font-weight: 300;
  font-size: 16px;
  border-bottom: 0.5px solid #aeaeae99;
  background-color: white;
`;

const RouteIcon = styled(FontAwesomeIcon)`
  color: white;
  width: 12px;
  height: 12px;
  padding: 10px;
  background-color: #a3a3a3;
  border-radius: 50%;
`;

const optionIcon = styled(FontAwesomeIcon)`
  
`;

const Box = styled.div`
  background-color: #88d5ff35;
  width: 100%;
  height: 45px;
`;

const RouteInfo = styled.div`
  margin: 0px 20px;
  font-weight: 500;
`;

//하단 경로 검색 결과 부분
const OptionBar = styled.div`
  width: 100%;
  height: 45px;
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
    padding: 5px 10px;
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
  border-top: 1px solid darkgrey;
  border-bottom: 1px solid darkgrey;
  margin-bottom: 5px;
`;

const PathInfo = styled.div`
  width: 100%;
  height: 40px;
  background-color: tomato;
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

const SubPathSummary = styled.div`
  /* background-color: tomato; */
  padding: 5px 0px;
  border-top: 1px solid darkgrey;

`;
const SubPath = styled.div`
  padding: 5px 0px; 
`;
const EachSubPath = styled.div`
  background-color: skyblue;
  display: flex;
`;
const IconColumn = styled.div`
  display: block;
  background-color: lemonchiffon;
`;
const TextColumn = styled.div`
  background-color: pink;
`;

//요약바
const SummaryBar = styled.div`
  /* background-color: pink; */
  padding: 10px 0px;
  display: flex;
  justify-content: center;
  align-items: center;
`;

const BarContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 20px;
  background-color: #dddddd;
  border-radius: 25px;
`;
const Bar = styled.div`
  height: 100%;
  background-color: ${(props) => props.color};
  width: ${(props) => props.width}%;
  border-radius: 25px;
  /* position: relative; */
  font-size: 12px;
  display: flex;
  justify-content: start;
  align-items: center;
  text-align: center;
  p{
    width: 100%;
    text-align: center;
    justify-content: center;
    font-weight: 500;
    color: white;
  }
`;
const TransportIcon = styled(FontAwesomeIcon)`
  font-size: 10px;
`;

const IconBox = styled.div`
  margin-left: -10px;
  min-width: 22px;
  max-width: 22px;
  min-height: 22px;
  max-height: 22px;
  background-color: white;
  border: 1px solid gray;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2;
`;