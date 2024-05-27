import React from 'react';
import { useNavigate } from 'react-router-dom';

import { useRecoilState, useSetRecoilState } from 'recoil';
import { searchPlace, navState, routeInfoState } from '../recoil/atoms';

import styled from "styled-components";
import { motion } from 'framer-motion';
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faLocationCrosshairs, faSpinner, faX, faLocationDot, faMagnifyingGlassArrowRight, faMapLocationDot } from "@fortawesome/free-solid-svg-icons";

import Map2 from '../components/Map2';

const MapWrapper = styled.div`
  height: 55%;
  position: relative;
`;

const ContentBoxWrapper = styled.div`
  height: 35%;
  box-shadow: 0px -4px 8px rgba(0, 0, 0, 0.15);
  z-index: 200;
  bottom: 70px;
  position: sticky;
  overflow-y: scroll;
  background-color: whitesmoke;
`;

const ContentBox = styled(motion.div)`
  overflow-x: hidden;
  overflow-y: scroll;
  display: box;
  justify-content: center;
  align-items: center;
  background-color: whitesmoke;
  position: sticky;
  bottom: 70px;
  left: 0;
  right: 0;
  z-index: 200;
  box-shadow: 0px -4px 8px rgba(0, 0, 0, 0.15);
`;

const ToggleBar = styled.div`
  width: 100%;
  height: 25px;
  display: flex;
  justify-content: center;
  align-items: center;
  position: sticky;
  top:0;
  margin-bottom: 5px;
  background-color: whitesmoke;
`;

const Bar = styled.div`
  width: 10%;
  height: 6px;
  border-radius: 25px;
  background-color: #d9d9d9;
`;

const SearchBoxWrapper = styled.div`
  width:100%;
  height: 10%;
  padding: 20px;
  display:flex;
  justify-content: center;
  align-items:center;
  background-color: white;
  position: sticky;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
  z-index: 200;
`;

const Input = styled.input`
  text-align: left;
  width: 90%;
  height: 45px;
  font-size: 20px;
  font-weight: 500;
  border: none;
  border-radius:15px;
  padding: 0px 20px;
  margin-right: 10px;
  background-color: whitesmoke;
  &:focus {
    outline: none;
  }
`;

/* 하단창 */

const Container = styled(motion.div)`
  font-family: 'Open Sans', Arial, sans-serif;
  font-weight: 600;
  padding: 12px;
  padding-top: 6px;
  height: 100%;
  width: 100%;
  display: block;
  justify-content: start;
  align-items: center;
  font-size: 20px;
`;

const Box = styled.div`
  border-radius: 22px;
  width: 80%;
  margin: 20px;
  padding: 12px;;
`;

const MarkerIconBox = styled(Box)`
  width: 20%;
  justify-content: center;
  align-items: center;
  text-align: center;

`;

const Row = styled.div`
  display: flex;
  &:first-child {
    justify-content: space-between;
    background-color: white;
    box-shadow: 0px 0px 10px 3px rgba(109, 109, 109, 0.1);
    border-radius: 25px;
    margin: 0px 10px;
  }
`;

const MarkerIcon = styled(FontAwesomeIcon)`
  width: 25px;
  height: 25px;
  padding: 20px;
  background-color: #003E5E;
  border-radius: 50%;
  color: white;
`;

const Address = styled.div`
  font-size: 22px;
  font-weight: 700;
`;


const Info = styled.div`
  font-size:16px;
  margin-right: 15px;
  width: auto;
  margin-bottom: 10px;
  font-weight: 500;
`;

const Btn = styled(Info)`
  background-color: white;
  width: auto;
  padding: 8px 24px;
  border-radius: 25px;
  text-align: center;
  border: 0.5px solid #00000039;
  box-shadow: 0px 0px 5px 3px rgba(109, 109, 109, 0.15);
  font-weight: 700;
  margin-bottom: 0;
  cursor: pointer;
`;

const AddressBox = styled.div`
  display: flex;
  width: auto;
  margin-top: 15px;
`;

const Icon = styled(FontAwesomeIcon)`
  cursor: pointer;
  padding: 10px;
  font-size: 28px;
`;

function SearchPlace() {
  const [place, setPlace] = useRecoilState(searchPlace);
  const [routeInfo, setRouteInfo] = useRecoilState(routeInfoState);
  const setNav = useSetRecoilState(navState);
  const navigate = useNavigate();

  setNav("search");

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

    navigate('/search/routes');
  };

  const onDestClick = () => {
    const newDest = {
      name: place.place_name,
      lat: place.lat,
      lon: place.lon,
    };

    setRouteInfo((prevState) => ({
      ...prevState,
      dest: [newDest],
    }));

    navigate('/search/routes');
  };

  return (
    <React.Fragment>
      <SearchBoxWrapper>
        <Input 
          id="searchPlace"
          value={place.place_name} 
          placeholder="장소 주소 검색"
          onClick={()=>navigate("/search")}
          readOnly

        />
        <Icon icon={faX} onClick={()=>navigate("/")}></Icon>
      </SearchBoxWrapper>
      <MapWrapper>
        <Map2 />  
      </MapWrapper>
      <ContentBoxWrapper>
        <ToggleBar><Bar /></ToggleBar>
        {/* <PlaceInfo />  // 하단창 실험 용으로 잠시 Test로 바꿔놓음*/}
        {/* <Test></Test>  */}
        <Container 
          initial={{ y: 20, opacity: 0 }}
          animate={{ y: 0, opacity: 1 }}
          exit={{ y: 20, opacity: 0 }}
          transition={{ duration: 1 }}
        >
        <Row id="address_weather">
          <Box>
            <Address>
            <FontAwesomeIcon icon={faMapLocationDot} style={{color: "#003E5E", marginRight: "8px" }}/> 
            {place.place_name}
            </Address>
            <AddressBox>
            <Info>
              <span style={{ color: "#5f5f5f" }}>
                장소명: {place.place_name} <br/>
                카테고리: {place?.category_group_name} <br/>
                지번: {place.address_name} <br/>
                도로명: {place.road_address_name} <br/>
                전화번호: {place?.phone} <br/>
                위도: {place.lat} <br/>
                경도: {place.lon}
              </span>
            </Info>
            </AddressBox>
            <AddressBox>
            <Btn onClick={onOriginClick}><span style={{ color: "#02C73C" }}>출발</span></Btn>
            <Btn onClick={onDestClick}><span style={{ color: "#216CFF"}}>도착</span></Btn>
            </AddressBox>
          </Box>
          <MarkerIconBox>
          <MarkerIcon MarkerIcon={faMagnifyingGlassArrowRight}/>
          </MarkerIconBox>
        </Row> 
        </Container>
      </ContentBoxWrapper>
    </React.Fragment>
  );
}

export default SearchPlace;



