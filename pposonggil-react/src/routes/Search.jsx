import React, { useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { useRecoilValue, useSetRecoilState } from "recoil";
import { searchPlace, navState } from "../recoil/atoms";

import styled from "styled-components";
import { motion } from "framer-motion";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faMagnifyingGlass, faLocationDot, faClockRotateLeft } from "@fortawesome/free-solid-svg-icons";

const { kakao } = window;

const SearchContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px 0px;
  padding-bottom: 30px;
  width: 100%;
  z-index: 500;
  background-color: white;
  border-bottom: 10px solid rgba(0, 0, 0, 0.1);
  position: relative;
`;

const Container = styled.div`
  width: 100%;
  height: 45px;
  background-color: #e9e9e9;
  padding: 0px 20px;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  border-radius: 15px;
  border: 0.1px solid rgba(0, 0, 0, 0.1);
  padding: 12px;
  margin: 0px 20px;
`;

const Input = styled.input`
  text-align: left;
  width: 100%;
  height: 45px;
  font-size: 17px;
  border: none;
  background-color: inherit;
  &:focus {
    outline: none;
  }
`;

const Icon = styled(FontAwesomeIcon)`
  cursor: pointer;
  padding: 10px;
  margin-right: 5px;
`;

const ResultContainer = styled.div`
  background-color: whitesmoke;
  width: 100%;
  overflow-y: scroll;
  left: 0;
  right: 0;
  height: 90%;
  padding-bottom: 70px;
  /* position: absolute; */
  /* bottom: 0; */
`;

const ResultItem = styled.div`
  display: flex;
  justify-content: flex-start;
  align-items: center;
  text-align: left;
  padding: 10px 20px;
  height: auto;
  min-height: 45px;
  color: black;
  font-weight: 300;
  font-size: 16px;
  border-bottom: 0.5px solid #aeaeae99;
  background-color: white;
  transition: background-color 0.3s ease; /* 부드러운 배경색 변경을 위한 트랜지션 */
  cursor: pointer;
  &:hover {
    background-color: #f4f4f486;
  }
`;

const PlaceIcon = styled(FontAwesomeIcon)`
  color: white;
  width: 15px;
  height: 15px;
  padding: 8px;
  background-color: #003E5E;
  border-radius: 50%;
`;

const HistoryIcon = styled(PlaceIcon)`
  background-color: gray;
`; 

const PlaceInfo = styled.div`
  margin: 0px 20px;
  font-weight: 500;
`;

const InfoItem = styled.span`
  color: #4b4b4b;
  font-size: 14px;
  font-weight: 400;
  &:first-child {
    font-weight: 600;
    font-size: 16px;
    color: black;
  }
`;

/////////////////////////////////////////////////////////////////////////////////////

function Search() {
  const [inputText, setInputText] = useState(""); // 검색 입력 텍스트
  const [searchResults, setSearchResults] = useState([]); // 검색 결과(자동완성 기능)
  const [searchHistory, setSearchHistory] = useState([]); // 검색 기록(로컬스토리지)
  const [selectedPlaceInfo, setSelectedPlaceInfo] = useState(null);
  
  const setSearchPlace = useSetRecoilState(searchPlace); // 선택한 장소명 atom으로 관리
  const searchPlaceValue = useRecoilValue(searchPlace);

  const setNav = useSetRecoilState(navState);
  setNav("search");
  
  const navigate = useNavigate();
  // Kakao Maps API를 이용한 검색 함수
  const handleSearch = useCallback(() => {
    if (!inputText) {
      setSearchResults([]);
      return;
    }
    const ps = new kakao.maps.services.Places();
    const keyword = inputText;
    // 장소 검색 객체를 통해 키워드로 장소 검색 요청
    ps.keywordSearch(keyword, (data, status) => {
      if (status === kakao.maps.services.Status.OK) {
        setSearchResults(data);
      } else if (status === kakao.maps.services.Status.ZERO_RESULT) {
        setSearchResults([]);
      } else if (status === kakao.maps.services.Status.ERROR) {
        alert("검색 결과 중 오류가 발생했습니다.");
      }
    });
  }, [inputText]);

  // 검색 입력 텍스트 변경 시 딜레이를 두고 검색 수행
  useEffect(() => {
    const delayDebounceFn = setTimeout(() => {
      handleSearch();
    }, 200);

    return () => clearTimeout(delayDebounceFn);
  }, [inputText, handleSearch]);

  // 컴포넌트가 처음 렌더링될 때 로컬스토리지에서 검색 기록 불러오기
  useEffect(() => {
    const savedHistory = localStorage.getItem("searchHistory");
    if (savedHistory) {
      setSearchHistory(JSON.parse(savedHistory));
    }
  }, []);

  // 검색 결과 클릭 시 장소명 로컬스토리지에 저장 및 searchPlace atom 값 업데이트
  const handleResultClick = (result, index) => {
    // 선택한 장소 로컬스토리지에 저장(상세 정보까지 전부 다)
    const newHistory = [result, ...searchHistory.filter(item => item !== result)];
    setSearchHistory(newHistory);
    localStorage.setItem("searchHistory", JSON.stringify(newHistory));
    // 선택 장소명에 해당하는 상세 정보 atom에 저장
    setSearchPlace({
      place_name: result.place_name || result,
      category_group_name: result.category_group_name,
      address_name: result.address_name,
      road_address_name: result.road_address_name,
      phone: result.phone,
      lat: result.y,
      lon: result.x,
    });
    console.log("atom :", searchPlaceValue); //테스트용
    navigate(`/search/place`);
  };

  // 입력 텍스트 변경 핸들러
  const handleInputChange = (e) => {
    setInputText(e.target.value);
  };

  // 검색 결과가 없을 때 로컬스토리지의 검색 기록을 표시
  const resultsToShow = searchResults.length > 0 ? searchResults : searchHistory;

// test용

  return (
    <React.Fragment>
      <SearchContainer>
        <Container>
          <Icon icon={faMagnifyingGlass} />
          <Input
            type="text"
            value={inputText}
            onChange={handleInputChange}
            placeholder="장소·주소 검색"
          />
        </Container>
      </SearchContainer>

      <ResultContainer>
        {resultsToShow.map((result, index) => (
          <ResultItem 
            key={index} 
            onClick={() => handleResultClick(result, index)}
          >
            {searchResults.length > 0 ? (
              <PlaceIcon icon={faLocationDot} />
            ) : (
              <HistoryIcon icon={faClockRotateLeft} />
            )}
              <PlaceInfo>
               <InfoItem id="placeName">{result.place_name || result}</InfoItem>
               
               {searchResults.length > 0 && (
                  <>
                    <InfoItem id="address"><br/>{result.address_name}</InfoItem>
                    <InfoItem id="roadAddress"><br/>{result.road_address_name}</InfoItem>
                    <InfoItem id="phoneNumber"><br/>{result.phone}</InfoItem>
                    <InfoItem id="phoneNumber"><br/>{result.x}</InfoItem>
                    <InfoItem id="phoneNumber"><br/>{result.y}</InfoItem>
                  </>
                )}
             </PlaceInfo>
            </ResultItem>
        ))}
      </ResultContainer>
    </React.Fragment>
  );
}

export default Search