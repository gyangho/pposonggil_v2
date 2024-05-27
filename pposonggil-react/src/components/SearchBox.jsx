import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useRecoilValue, useRecoilState } from "recoil";
import { addressState, locationBtnState, mapCenterState, markerState } from "../recoil/atoms";

import styled from "styled-components";
import { motion, AnimatePresence } from "framer-motion";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faMagnifyingGlass, faBars } from "@fortawesome/free-solid-svg-icons";

const SearchContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px 0px;
  width: 100%;
  z-index: 100;
  position: sticky;
  background-color: inherit;
`;

const Container = styled.div`
  width: 85%;
  height: 45px;
  background-color: whitesmoke;
  box-shadow: 0px 0px 5px 4px rgba(109, 109, 109, 0.15);
  /* padding: 0px 20px; */
  display: flex;
  justify-content: flex-start;
  align-items: center;
  border-radius: 15px;
  padding: 12px;
  margin: 0px 20px;
  &:last-child { //버튼 컨테이너
    width: 15%;
    padding: 0px;
    margin-left: 0px;
    justify-content: flex-end;
  }
`;

const Input = styled(motion.input)`
  text-align: left;
  width: 100%;
  height: 100%;
  font-size: 17px;
  border: none;
  background-color: inherit;
  &:focus {
    outline: none;
  }
`;

const Icon = styled(FontAwesomeIcon)`
  /* margin: 0px 20px 0px 10px ; */
  cursor: pointer;
  padding: 10px;
  &:first-child {
    margin-right: 5px;
  }
`;

const Btn = styled.button`
  border: none;
  cursor: pointer;
  text-align: center;
  background-color: #003E5E;
  width: 100%;
  height: 100%;
  border-radius: 15px;
  font-size: 14px;
  font-weight: 600;
  color: white;
`;


function SearchBox() {
  const mapCenterAddress = useRecoilValue(mapCenterState);
  const [placeholderText, setPlaceholderText] = useState("장소·주소 검색");
  const navigate = useNavigate();

  //검색창 placeholder 내용 동적 변경
  useEffect(() => {
    if (mapCenterAddress.depth2 && mapCenterAddress.depth3) {
      setPlaceholderText(`${mapCenterAddress.depth2} ${mapCenterAddress.depth3}`);
    }
  }, [mapCenterAddress]);

  const onChange = (e) => {
    setPlaceholderText(e.target.value);
  };

  const handleSearchBtn = () => {
    navigate('/search/routes');
  };

  const clickedBars = (e) => {
    //왼쪽에서 오른쪽으로 화면 절반정도 뽀송길 설명 페이지 토글되게
    //새로운 화면 아니고 map위에 zindex값으로 레이아웃처럼 위로 뜨게.
    //atom값으로 State 값 변경하고 Map.js에서 띄우는 걸로..!
  };

  const clickedSearch = (e) => {
    //왼쪽에서 오른쪽으로 화면 절반정도 뽀송길 설명 페이지 토글되게
    //새로운 화면 아니고 map위에 zindex값으로 레이아웃처럼 위로 뜨게.
    //atom값으로 State 값 변경하고 Map.js에서 띄우는 걸로..!
  };

  const handleInputClick = () => {
    navigate("/search");
  }

  return (
    <React.Fragment>
    <SearchContainer>
      <Container>
        <Icon 
          icon={faBars} 
          onClick={clickedBars}
        />
        <AnimatePresence>
          <Input 
            id="searchBox"
            type="text"
            onClick={handleInputClick}
            placeholder={placeholderText}
            key={placeholderText}
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ duration: 0.8 }}
          />
        </AnimatePresence>
        <Icon 
          icon={faMagnifyingGlass} 
          onClick={clickedSearch}
        />
      </Container>

      <Container>
        <Btn onClick={handleSearchBtn}>
          길찾기
        </Btn>
      </Container>
    </SearchContainer>
    </React.Fragment>

  );
}

export default SearchBox;