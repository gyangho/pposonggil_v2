import React from 'react';
import { useNavigate } from 'react-router-dom';

import { useRecoilState, useSetRecoilState } from 'recoil';
import { searchPlace, navState, routeInfoState, currentAddressState } from '../recoil/atoms';

import styled from "styled-components";
import { motion } from 'framer-motion';
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faX, faPhone, faMagnifyingGlass, faMapLocationDot } from "@fortawesome/free-solid-svg-icons";

import Map2 from '../components/Map2';

function SearchPlace() {
  const [place, setPlace] = useRecoilState(searchPlace);
  const [curAddr, setCurAddr] = useRecoilState(currentAddressState);
  const [routeInfo, setRouteInfo] = useRecoilState(routeInfoState);
  const setNav = useSetRecoilState(navState);
  const navigate = useNavigate();

  // setNav("search");
  const onOriginClick = () => {
    const newOrigin = {
      name: place.place_name,
      lat: place.lat,
      lon: place.lon,
    };
    setRouteInfo((prevState) => ({
      ...prevState,
      origin: [newOrigin],
    }));
    console.log("출발지 설정 클릭: ", routeInfo);
    navigate('/search/routes');
  };

  const onDestClick = () => {
    const newDest = {
      name: place.place_name,
      lat: place.lat,
      lon: place.lon,
    };
    if(!routeInfo.origin[0].lat) {
      const newOrigin = {
        name: curAddr.addr,
        lat: curAddr.lat,
        lon: curAddr.lon,
      };
      setRouteInfo({
        origin: [newOrigin],
        dest: [newDest]
      })
    } else {
      setRouteInfo((prevState) => ({
        ...prevState,
        dest: [newDest],
      }));
    }
    console.log("도착지 설정 클릭: ", routeInfo);
    navigate('/search/routes');
  };

  return (
    <React.Fragment>
      <SearchBoxWrapper>
        <Column>
          <FontAwesomeIcon 
            icon={faMagnifyingGlass}
            style={{padding: "0px 10px", fontSize: "16px"}}
           />
          <Input 
            id="searchPlace"
            value={place.place_name} 
            placeholder="장소 주소 검색"
            onClick={()=>navigate("/search")}
            readOnly
          />
        </Column>
        <Column>
          <Icon icon={faX} onClick={()=>navigate("/")}></Icon>
        </Column>
      </SearchBoxWrapper>
      <MapWrapper>
        <Map2 />  
      </MapWrapper>
      <ContentBoxWrapper>
        <ToggleBar><Bar /></ToggleBar>
        <Container 
          initial={{ y: 20, opacity: 0 }}
          animate={{ y: 0, opacity: 1 }}
          exit={{ y: 20, opacity: 0 }}
          transition={{ duration: 0.8 }}
        >
        <Box>
          <Address>
            <FontAwesomeIcon icon={faMapLocationDot} style={{color: "#003E5E", marginRight: "8px" }}/> 
            <span>
              {place.place_name}
              <p> | {place?.category_group_name}</p>
            </span>
          </Address>
          <DetailInfo>
          <Info>
            <div>{place.road_address_name}</div>
            <div style={{ color: "gray" }}>지번: {place.address_name}</div>
            {place.phone && (
              <div>
                <FontAwesomeIcon icon={faPhone} style={{color: "#5f5f5f", marginRight: "8px"}}/>
                {place?.phone}
              </div>
            )}
          </Info>
          </DetailInfo>
          <DetailInfo>
            <Btn onClick={onOriginClick}><span style={{ color: "#02C73C" }}>출발</span></Btn>
            <Btn onClick={onDestClick}><span style={{ color: "#216CFF"}}>도착</span></Btn>
          </DetailInfo>
        </Box>
        </Container>
      </ContentBoxWrapper>
    </React.Fragment>
  );
}

export default SearchPlace;


const SearchBoxWrapper = styled.div`
  width:100%;
  height: auto;
  padding: 20px 0px;
  padding-bottom: 30px;
  display:flex;
  justify-content: space-between;
  align-items: center;
  background-color: #ffffff97;
  position: absolute;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
  z-index: 200;
`;
const Column = styled.div`
  width: 90%;
  height: 45px;
  background-color: #e9e9e9;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  border-radius: 15px;
  border: 0.1px solid rgba(0, 0, 0, 0.1);
  padding: 12px;
  margin: 0px 20px;
  font-size: 17px;
  &:last-child {
    all: unset;
    height: 45px;
    width: 10%;
    display: flex;
    justify-content: center;
    align-items: center;
    margin: 0;
    margin-right: 20px;
  }
`;

const Input = styled.input`
  all: unset;
  height: 45px;
  text-align: left;
  font-weight: 600;
  background-color: inherit;
  padding-left: 8px;
`;

const Icon = styled(FontAwesomeIcon)`
  cursor: pointer;
  font-size: 28px;
`;
/* 지도 컨테이너 */
const MapWrapper = styled.div`
  height: 50%;
  height: calc(100% - 95px - 235px); /* 남은 높이를 계산하여 설정 */
  height: calc(100% - 235px); /* 남은 높이를 계산하여 설정 */

  position: relative;
  /* top:95px; */
`;
/* 하단창 */
const ContentBoxWrapper = styled.div` 
  width: 100%;
  height: 235px;
  box-shadow: 0px -4px 8px rgba(0, 0, 0, 0.15);
  z-index: 200;
  position: absolute;
  bottom: 0;
  background-color: whitesmoke;
  padding-bottom: 30px;
`;

const ToggleBar = styled.div`
  width: 100%;
  height: 25px;
  display: flex;
  justify-content: center;
  align-items: center;
  position: sticky;
  top: 0;
  background-color: whitesmoke;
`;

const Bar = styled.div`
  width: 10%;
  height: 6px;
  border-radius: 25px;
  background-color: #d9d9d9;
`;
const Container = styled(motion.div)`
  height: 100%;
  width: 100%;
  display: block;
  justify-content: start;
  align-items: center;
  padding: 0px 20px;
  margin-top: 10px;
`;

const Box = styled.div`
  width: 100%;
  display: block;
  height: auto;
  background-color: white;
  box-shadow: 0px 0px 10px 3px rgba(109, 109, 109, 0.1);
  border-radius: 25px;
  padding: 20px;
`;

const Address = styled.div`
  font-size: 20px;
  font-weight: 700;
  display: flex;
  span {
    display: flex;
    justify-content: center;
    align-items: center;
  }
  p {
    color: gray;
    font-size: 13px;
    margin-left: 6px;
    padding-top: 4px;
  }
`;

const Info = styled.div`
  font-size:15px;
  margin-right: 15px;
  width: auto;
  margin-bottom: 10px;
  font-weight: 400;
  div {
    &:first-child{
      margin-bottom: 2px;
      font-weight: 500;
    }
    &:last-child {
      margin-bottom: 0px;
      color: #216CFF;
      margin-top: 8px;
    }
  }
`;

const DetailInfo = styled.div`
  display: flex;
  width: 100%;
  margin-top: 10px;
  &:last-child {
    margin-top: 0;
    justify-content: end;
  }
`;

const Btn = styled.div`
  background-color: white;
  width: auto;
  padding: 6px 20px;
  border-radius: 25px;
  text-align: center;
  border: 0.5px solid #00000039;
  box-shadow: 0px 0px 5px 3px rgba(109, 109, 109, 0.15);
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 0;
  cursor: pointer;
  margin-left: 15px;
`;



