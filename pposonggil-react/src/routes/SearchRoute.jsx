// import React, { useEffect, useState, useCallback } from "react";
// import { useRecoilState, useRecoilValue } from "recoil";

// import styled from "styled-components";
// import { motion } from "framer-motion";
// import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
// import { faRotate, faEllipsisVertical, faClockRotateLeft } from "@fortawesome/free-solid-svg-icons";

// import { currentAddressState, routeInfo } from "../components/atoms";


// const SearchContainer = styled.div`
//   position: sticky;
//   display: flex;
//   justify-content: center;
//   align-items: center;
//   margin: 20px 0px;
//   width: 100%;
//   &:last-child {
//     margin-top: -10px;
//   }
// `;

// const Container = styled.div`
//   width: 65%;
//   height: 45px;
//   background-color: whitesmoke;
//   padding: 0px 20px;
//   display: flex;
//   justify-content: flex-start;
//   align-items: center;
//   border-radius: 15px;
//   padding: 12px;
//   margin: 0px 5px;
//   &:last-child { //버튼
//     width: 10%;
//     background-color: #003E5E;
//     justify-content: flex-end;
//     color: white;
//     display: flex;
//     justify-content: center;
//     align-items: center;
//     cursor: pointer;
//   }
// `;

// const Input = styled(motion.input)`
//   text-align: left;
//   width: 100%;
//   height: 100%;
//   font-size: 17px;
//   border: none;
//   background-color: whitesmoke;
//   &:focus {
//     outline: none;
//   }
// `;

// const ResultContainer = styled.div`
//   background-color: whitesmoke;
//   min-height: 65vh;
//   width: 100%;
//   max-height: 65vh;
//   overflow-y: scroll;
//   bottom: 70px;
//   left: 0;
//   right: 0;
// `;

// const Icon = styled(FontAwesomeIcon)`
//   width: 22px;
//   height: 22px;
// `;

// const SearchedRoute = styled.div`
//   display: flex;
//   justify-content: flex-start;
//   align-items: center;
//   padding: 10px 20px;
//   height: auto;
//   min-height: 45px;
//   color: black;
//   font-weight: 300;
//   font-size: 16px;
//   border-bottom: 0.5px solid #aeaeae99;
//   background-color: white;
// `;

// const RouteIcon = styled(FontAwesomeIcon)`
//   color: white;
//   width: 12px;
//   height: 12px;
//   padding: 10px;
//   background-color: #a3a3a3;
//   border-radius: 50%;
// `;

// const optionIcon = styled(FontAwesomeIcon)`
  
// `;

// const Box = styled.div`
//   background-color: #88d5ff35;
//   width: 100%;
//   height: 45px;
// `;

// const RouteInfo = styled.div`
//   margin: 0px 20px;
//   font-weight: 500;
// `;


// function Search() {
//   const curAddr = useRecoilValue(currentAddressState);
//   const [startEnd, setStartEnd] = useRecoilState(routeInfo);

//   const isReverseBtnClicked = useState(false);
//   const isResetBtnClicked = useState(false);
//   const [startInputText, setStartInputText] = useState(curAddr.addressName);
//   const [endInputText, setEndInputText] = useState("");

//   const handleStartInputChange = (event) => {
//     setStartInputText(event.target.value);
//   };

//   const handleEndInputChange = (event) => {
//     setEndInputText(event.target.value);
//   };

//   const handleReverseBtn = useCallback(() => {
//     console.log("목적지 출발지 전환");
//     setStartInputText(endInputText);
//     setEndInputText(startInputText);
//   }, [isReverseBtnClicked]);

//   const handleResetBtn = useCallback(() => {
//     console.log("입력 장소 초기화");
//     setStartInputText(curAddr.addressName);
//     setEndInputText("");
//   }, [isResetBtnClicked]);

  
//   return (
//     <React.Fragment>
//       <SearchContainer>
//         <Container>
//           <Input
//             type="text"
//             value={startInputText}
//             onChange={handleStartInputChange}
//             placeholder={curAddr.addressName}
//           />
//         </Container>
//         <Container>
//           <Icon onClick={handleReverseBtn} icon={faRotate} />
//         </Container>
//       </SearchContainer>
//       <SearchContainer>
//         <Container>
//           <Input
//             type="text"
//             value={endInputText}
//             onChange={handleEndInputChange}
//             placeholder="도착지 입력"
//           />
//         </Container>
//         <Container>
//           <Icon onClick={handleResetBtn} icon={faEllipsisVertical} />   
//         </Container>
//       </SearchContainer>
//       <Box></Box>
//       <ResultContainer>
//         {/* map으로 로컬 스토리지에 저장된 검색 기록 리스트 업 하기*/}
//         <SearchedRoute>
//           <RouteIcon icon={faClockRotateLeft}/>
//           <RouteInfo>숭실대학교 ~ 숭실대학교 정보과학관</RouteInfo>
//         </SearchedRoute>

        

//       </ResultContainer>
//     </React.Fragment>
//   )
// }

// export default Search

// import React, { useState, useCallback, useEffect } from "react";
// import styled from "styled-components";
// import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
// import { faRotate, faEllipsisVertical, faClockRotateLeft } from "@fortawesome/free-solid-svg-icons";

// const { kakao } = window;

// const SearchContainer = styled.div`
//   display: flex;
//   justify-content: space-between;
//   margin-bottom: 10px;
// `;

// const Container = styled.div`
//   width: 45%;
// `;

// const Input = styled.input`
//   width: 100%;
//   padding: 10px;
//   font-size: 16px;
//   border-radius: 5px;
//   border: 1px solid #ccc;
// `;

// const ResultContainer = styled.div`
//   background-color: #f9f9f9;
//   padding: 10px;
// `;

// const SearchedRoute = styled.div`
//   display: flex;
//   align-items: center;
//   padding: 5px;
//   border-bottom: 1px solid #ccc;
// `;

// const RouteIcon = styled(FontAwesomeIcon)`
//   margin-right: 10px;
// `;

// const RouteInfo = styled.span`
//   font-size: 16px;
// `;

// function Search() {
//   const [startInputText, setStartInputText] = useState("");
//   const [endInputText, setEndInputText] = useState("");
//   const [searchResults, setSearchResults] = useState([]);
//   const [map, setMap] = useState(null);
//   const [markers, setMarkers] = useState([]);
//   const [infowindow, setInfowindow] = useState(null);

//   const handleReverseBtn = useCallback(() => {
//     setStartInputText(endInputText);
//     setEndInputText(startInputText);
//   }, [startInputText, endInputText]);

//   const handleResetBtn = useCallback(() => {
//     setStartInputText("");
//     setEndInputText("");
//   }, []);

//   const handleSearch = useCallback(() => {
//     if (!startInputText && !endInputText) {
//       return;
//     }

//     const ps = new kakao.maps.services.Places();
//     const keyword = `${startInputText} ${endInputText}`.trim();

//     ps.keywordSearch(keyword, (data, status, pagination) => {
//       if (status === kakao.maps.services.Status.OK) {
//         setSearchResults(data);
//       } else if (status === kakao.maps.services.Status.ZERO_RESULT) {
//       } else if (status === kakao.maps.services.Status.ERROR) {
//         alert("검색 결과 중 오류가 발생했습니다.");
//       }
//     });
//   }, [startInputText, endInputText]);

//   useEffect(() => {
//     const delayDebounceFn = setTimeout(() => {
//       handleSearch();
//     }, 300); // 300ms 딜레이

//     return () => clearTimeout(delayDebounceFn);
//   }, [startInputText, endInputText, handleSearch]);

//   return (
//     <React.Fragment>
//       <SearchContainer>
//         <Container>
//           <Input
//             type="text"
//             value={startInputText}
//             onChange={(e) => setStartInputText(e.target.value)}
//             placeholder="출발지 입력"
//           />
//         </Container>
//         <Container>
//           <FontAwesomeIcon icon={faRotate} onClick={handleReverseBtn} />
//         </Container>
//       </SearchContainer>
//       <SearchContainer>
//         <Container>
//           <Input
//             type="text"
//             value={endInputText}
//             onChange={(e) => setEndInputText(e.target.value)}
//             placeholder="도착지 입력"
//           />
//         </Container>
//         <Container>
//           <FontAwesomeIcon icon={faEllipsisVertical} onClick={handleResetBtn} />
//         </Container>
//       </SearchContainer>
//       <ResultContainer>
//         {searchResults.map((result) => (
//           <SearchedRoute key={result.id}>
//             <RouteIcon icon={faClockRotateLeft} />
//             <RouteInfo>{result.place_name}</RouteInfo>
//           </SearchedRoute>
//         ))}
//       </ResultContainer>
//       <div id="map" style={{ width: "100%", height: "400px" }}></div>
//     </React.Fragment>
//   );
// }

// export default Search;

import React, { useState, useCallback, useEffect } from "react";
import styled from "styled-components";
import { motion } from "framer-motion";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faRotate, faEllipsisVertical, faClockRotateLeft } from "@fortawesome/free-solid-svg-icons";
import { useNavigate } from "react-router-dom";

const { kakao } = window;

// const SearchContainer = styled.div`
//   display: flex;
//   justify-content: space-between;
//   margin-bottom: 10px;
// `;

// const Container = styled.div`
//   width: 45%;
// `;

// const Input = styled.input`
//   width: 100%;
//   padding: 10px;
//   font-size: 16px;
//   border-radius: 5px;
//   border: 1px solid #ccc;
// `;

// const ResultContainer = styled.div`
//   background-color: #f9f9f9;
//   padding: 10px;
// `;

// const SearchedRoute = styled.div`
//   display: flex;
//   align-items: center;
//   padding: 5px;
//   border-bottom: 1px solid #ccc;
//   cursor: pointer;
// `;

// const RouteIcon = styled(FontAwesomeIcon)`
//   margin-right: 10px;
// `;

// const RouteInfo = styled.span`
//   font-size: 16px;
// `;


const SearchContainer = styled.div`
  position: sticky;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 20px 0px;
  width: 100%;
  &:last-child {
    margin-top: -10px;
  }
`;

const Container = styled.div`
  width: 65%;
  height: 45px;
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
  border: none;
  background-color: whitesmoke;
  &:focus {
    outline: none;
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

function SearchRoute() {
  const [startInputText, setStartInputText] = useState("");
  const [endInputText, setEndInputText] = useState("");
  const [searchResults, setSearchResults] = useState([]);
  const [isSearchingStart, setIsSearchingStart] = useState(true);
  const [shouldSearch, setShouldSearch] = useState(true);

  const navigate = useNavigate();

  const handleReverseBtn = useCallback(() => {
    setStartInputText(endInputText);
    setEndInputText(startInputText);
    setShouldSearch(false);
  }, [startInputText, endInputText]);

  const handleResetBtn = useCallback(() => {
    setStartInputText("");
    setEndInputText("");
  }, []);

  const handleSearch = useCallback(() => {
    if (!startInputText && !endInputText) {
      return;
    }

    const ps = new kakao.maps.services.Places();
    const keyword = isSearchingStart ? startInputText : endInputText;

    ps.keywordSearch(keyword, (data, status, pagination) => {
      if (status === kakao.maps.services.Status.OK) {
        setSearchResults(data);
      } else if (status === kakao.maps.services.Status.ZERO_RESULT) {
        setSearchResults([]);
      } else if (status === kakao.maps.services.Status.ERROR) {
        alert("검색 결과 중 오류가 발생했습니다.");
      }
    });
  }, [startInputText, endInputText, isSearchingStart]);

  useEffect(() => {
    if (!shouldSearch) {
      setShouldSearch(true); // Reset the flag
      return;
    }

    const delayDebounceFn = setTimeout(() => {
      handleSearch();
    }, 300); // 300ms delay

    return () => clearTimeout(delayDebounceFn);
  }, [startInputText, endInputText, handleSearch, shouldSearch]);

  const handleResultClick = (placeName) => {
    if (isSearchingStart) {
      setStartInputText(placeName);
    } else {
      setEndInputText(placeName);
    }
    setSearchResults([]);
  };

  const handleStartInputChange = (e) => {
    setStartInputText(e.target.value);
    setIsSearchingStart(true);
    setShouldSearch(true);
  };

  const handleEndInputChange = (e) => {
    setEndInputText(e.target.value);
    setIsSearchingStart(false);
    setShouldSearch(true);
  };

  return (
    <React.Fragment>
      <div>search/routes 페이지 입니다</div>
      <SearchContainer>
        <Container>
          <Input
            type="text"
            value={startInputText}
            // onChange={handleStartInputChange}
            placeholder="출발지 검색"
            onClick={()=> navigate('/search')}
          />
        </Container>
        <Container>
          <FontAwesomeIcon icon={faRotate} onClick={handleReverseBtn} />
        </Container>
      </SearchContainer>

      <SearchContainer>
        <Container>
          <Input
            type="text"
            value={endInputText}
            // onChange={handleEndInputChange}
            readOnly
            placeholder="도착지 검색"
          />
        </Container>
        <Container>
          <FontAwesomeIcon icon={faEllipsisVertical} onClick={handleResetBtn} />
        </Container>
      </SearchContainer>


      <ResultContainer>
        {searchResults.map((result) => (
          <SearchedRoute key={result.id} onClick={() => handleResultClick(result.place_name)}>
            <RouteIcon icon={faClockRotateLeft} />
            <RouteInfo>{result.place_name}</RouteInfo>
          </SearchedRoute>
        ))}
      </ResultContainer>
    </React.Fragment>
  );
}

export default SearchRoute;

