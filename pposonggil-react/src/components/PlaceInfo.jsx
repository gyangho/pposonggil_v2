import React, { useState, useEffect } from "react";
import { useNavigate } from 'react-router-dom';
import { addressState, currentAddressState, locationBtnState, routeInfoState } from "../recoil/atoms";
import { useRecoilState, useRecoilValue, useSetRecoilState } from "recoil";

import styled from "styled-components";
import { motion } from "framer-motion";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faLocationDot, faMagnifyingGlassArrowRight } from "@fortawesome/free-solid-svg-icons";


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

const IconBox = styled(Box)`
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

const Icon = styled(FontAwesomeIcon)`
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

function PlaceInfo() {
  const addr = useRecoilValue(addressState);
  const curAddr = useRecoilValue(currentAddressState);
  const locationBtn = useRecoilValue(locationBtnState);

  const setRouteInfo = useSetRecoilState(routeInfoState);

  const navigate = useNavigate();
  const [place, setPlace] = useState({
    depth2: "", // 구
    depth3: "", // 동
    addr: "", // 지번
    roadAddr: "", // 도로명
    lat: "", // 위도
    lon: "", // 경도
  });

  useEffect(() => {
    if(locationBtn) {
      setPlace({
        depth2: curAddr.depth2,
        depth3: curAddr.depth3,
        addr: curAddr.addr,
        roadAddr: curAddr.roadAddr,
        lat: curAddr.lat,
        lon: curAddr.lon,
      });
    } else {
      setPlace({
        depth2: addr.depth2,
        depth3: addr.depth3,
        addr: addr.addr,
        roadAddr: addr.roadAddr,
        lat: addr.lat,
        lon: addr.lon,
      });
    }
  }, [addr, curAddr, locationBtn]);

  const onOriginClick = () => {
    const newOrigin = {
      name: place.addr,
      lat: place.lat,
      lon: place.lon,
    };

    setRouteInfo((prev) => ({
      ...prev,
      origin: [newOrigin],
    }));

    navigate('/search/routes');
  };

  const onDestClick = () => {
    const newDest = {
      name: place.addr,
      lat: place.lat,
      lon: place.lon,
    };
    //도착지로 설정하는 경우 현재 위치를 출발지로 자동 설정
    const newOrigin = {
      name: curAddr.addr,
      lat: curAddr.lat,
      lon: curAddr.lon,
    };

    setRouteInfo({
      origin: [newOrigin],
      dest: [newDest],
    });

    navigate('/search/routes');
  };

  return (
    <Container 
      initial={{ y: 20, opacity: 0 }}
      animate={{ y: 0, opacity: 1 }}
      exit={{ y: 20, opacity: 0 }}
      transition={{ duration: 1 }}
    >
      <Row id="address_weather">
        <Box>
          <Address>
            <FontAwesomeIcon icon={faLocationDot} style={{color: "#216CFF", marginRight: "8px" }}/> 
            {place.addr}
          </Address>
          <AddressBox>
            <Info>
              <span style={{ color: "#5f5f5f" }}>
                도로명: {place.roadAddr}
              </span>
            </Info>
          </AddressBox>
          <AddressBox>
            <Btn onClick={onOriginClick}><span style={{ color: "#02C73C" }}>출발</span></Btn>
            <Btn onClick={onDestClick}><span style={{ color: "#216CFF"}}>도착</span></Btn>
          </AddressBox>
        </Box>
        <IconBox>
          <Icon icon={faMagnifyingGlassArrowRight}/>
        </IconBox>
      </Row> 
    </Container>
   );
  }

  export default PlaceInfo