import React, { useState, useEffect, useCallback } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";
import styled from "styled-components";

function ChooseRoute() {
  const navigate = useNavigate();
  const location = useLocation();
  const { path } = location.state || {};

  const [paths, setPaths] = useState({
    defaultPath: [],
    pposongPath: []
  });

  console.log("전달 받은 경로 정보: ", path);

  //post 요청이 2개라 렌더링 최소화를 위해 useCallback으로 감쌈
  const getPathsFromServer = useCallback(async () => {
    const defaultUrl = "http://localhost:8080/api/path/default";
    const pposongUrl = "http://localhost:8080/api/path/pposong";

    try {
      const [defaultResponse, pposongResponse] = await Promise.all([
        axios.post(defaultUrl, path, { headers: { 'Content-Type': 'application/json' }}),
        axios.post(pposongUrl, path, { headers: { 'Content-Type': 'application/json' }})
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

  console.log("디폴트: ", paths.defaultPath);
  console.log("뽀송: ", paths.pposongPath);

  const chooseDefaultPath = () => {
    navigate('/search/detail', { state: { path: paths.defaultPath } });
  };

  const choosePposongPath = () => {
    navigate('/search/detail', { state: { path: paths.pposongPath } });
  };


  return (
    <React.Fragment>
      <Map>맵</Map>
      <BoxContainer>
        <Box id="defaultPath" onClick={chooseDefaultPath}>
        </Box>
        <Box id="pposongPath" onClick={choosePposongPath}>
        </Box>
      </BoxContainer>
        
      
    </React.Fragment>
  );
}

export default ChooseRoute;

/* styled */
const Wrapper = styled.div`
  width: 100%;
  height: 100%;
  background-color: lemonchiffon;
`;

const Map = styled.div`
  width: 100%;
  height: 100%;
  position: relative;
`;

const BoxContainer = styled.div`
  position: absolute;
  width: 100%;
  height: 30%;
  display: flex;
  justify-content: center;
  align-items: center;
  position: sticky;
  z-index: 500;
  bottom: 70px;
`;

const Box = styled.div`
  width: 50%;
  height: 100%;
  /* background-color: #ffffffa0; */
  background-color: skyblue;
  padding: 20px;
`;
