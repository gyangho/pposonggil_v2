import React, { useState, useEffect } from "react";
import { addressState, currentAddressState } from "../recoil/atoms";
import { useRecoilState, useRecoilValue } from "recoil";

import styled from "styled-components";
import { motion } from "framer-motion";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faLocationArrow, faWind, faDroplet, faSpinner } from "@fortawesome/free-solid-svg-icons";

const API_KEY = "341611f95d76874b2e5d207c40a6b07f";

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

const WeatherIcon = styled.img`
  width: 90px;
  height: 90px;
  background-color: #b2e5ff;
  border-radius: 50%;
`;

const Box = styled.div`
  border-radius: 22px;
  width: 70%;
  margin: 10px;
  padding: 12px;
`;

const IconBox = styled(Box)`
  width: auto;
  justify-content: center;
  align-items: center;
  text-align: center;
`;

const Description = styled.div`
  font-size: 20px;
  margin-top: 10px;
  color: #2e2e2e;
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
  margin: 0px 8px;

`;

const Hr = styled.hr`
  border: 1px dashed rgba(184, 184, 184, 0.611);
  margin: 15px;
  /* margin-top: 20px; */
`;

const Address = styled.div`
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 20px;
`;

const Temp = styled.div`
  font-size: 50px;
  padding-left: 10px;
  color: #2e2e2e;
`;

const WeatherInfo = styled.div`
  font-size:16px;
  margin-right: 15px;
  width: auto;
  background-color: white;
  box-shadow: 0px 0px 5px 3px rgba(109, 109, 109, 0.15);
  padding: 5px 10px;;
  border-radius: 20px;
`;

const TempBox = styled.div`
  display: flex;
  width: auto;
  margin-top: 15px;
`;

const WindIcon = styled(FontAwesomeIcon)`
  color: #0037a6;
  margin-right: 6px;
`;
const HumidIcon = styled(FontAwesomeIcon)`
  color: #79d9ff;
  margin-right: 6px;
`;

const WeatherBox = styled(Box)`
  box-shadow: 0px 0px 5px 3px rgba(109, 109, 109, 0.1);
  background-color: white;
  width: auto;
  font-size:16px;
  padding: 10px 12px;
  margin-top: 0px;
  margin-bottom: 15px;
`;

const Spinner = styled(motion.div)`
  font-size: 40px;
  color: #70ccfe;
  display:flex;
  justify-content: center;
  align-items: center;
  z-index: 200px;
  margin-top: 50px;
  overflow-y: hidden;
`;

function Weather() {
  const [weatherData, setWeatherData] = useState(null);
  const [error, setError] = useState(null);

  const address= useRecoilValue(addressState);
  const currentAddress = useRecoilValue(currentAddressState);

  useEffect(() => {
    navigator.geolocation.getCurrentPosition(onGeoSuccess, onGeoError);
  }, []);

  const onGeoSuccess = (position) => {
    const lat = position.coords.latitude;
    const lon = position.coords.longitude;
    const url = `https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&appid=${API_KEY}&units=metric&lang=kr`;

    fetch(url)
      .then((response) => response.json())
      .then((data) => {
        setWeatherData(data);
      })
      .catch((error) => {
        setError(error);
      });
  };

  const onGeoError = () => {
    setError("Error: 위치 추적을 허용해 주세요.");
  };

  if (error) {
    return <div>{error}</div>;
  }

  if (!weatherData) {
    return (
      <Spinner 
        initial={{ rotate: 0 }}
        animate={{ rotate: 360 }}
        transition={{ duration: 2, repeat: Infinity }}  
      >
        <FontAwesomeIcon icon={faSpinner} />
      </Spinner>
      
    );
  }

  const weatherIconCode = weatherData.weather[0].icon;
  const weatherIconUrl = `http://openweathermap.org/img/wn/${weatherIconCode}@2x.png`;

  // 기온과 체감 온도를 정수로 반올림
  const roundedTemp = Math.round(weatherData.main.temp);
  const roundedFeelsLike = Math.round(weatherData.main.feels_like);
  
  const roundedTempMax = Math.round(weatherData.main.temp_max);
  const roundedTempMin = Math.round(weatherData.main.temp_min);
  
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
            <p style={{color:"#216CFF", marginBottom: "5px"}}>현재 위치</p>
            {currentAddress.depth2} {currentAddress.depth3}
            <Icon icon={faLocationArrow} />
          </Address>
          <Temp>{roundedTemp}°</Temp>
          <TempBox>
            <WeatherInfo><span style={{ color: "gray" }}>체감</span> {roundedFeelsLike}°</WeatherInfo></TempBox>
          <TempBox>
            <WeatherInfo><span style={{ color: "tomato" }}>최고</span> {roundedTempMax}°</WeatherInfo>
            <WeatherInfo><span style={{ color: "#216CFF"}}>최저</span> {roundedTempMin}°</WeatherInfo>
          </TempBox>
        </Box>
        <IconBox>
          <WeatherIcon src={weatherIconUrl} />
          <Description>{weatherData.weather[0].description}</Description>
        </IconBox>
      </Row>
      <Hr/>
      <Row id="wind_humid">
        <WeatherBox><WindIcon icon={faWind} />풍속 {weatherData.wind.speed}m/s</WeatherBox>
        <WeatherBox><HumidIcon icon={faDroplet} />습도 {weatherData.main.humidity}%</WeatherBox>
      </Row>
      <Row id="wind_humid">
        <WeatherBox>날씨 정보</WeatherBox>
        <WeatherBox>날씨 정보</WeatherBox>
      </Row>
      
    </Container>
   );
  }

  export default Weather