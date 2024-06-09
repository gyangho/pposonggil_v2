import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import styled from "styled-components";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCalendarDay, faClock, faLocationDot, faX } from "@fortawesome/free-solid-svg-icons";
import { faCalendarCheck } from "@fortawesome/free-regular-svg-icons";
import { Map, MapMarker } from "react-kakao-maps-sdk";

const { kakao } = window;

function TransactionSchedule() {
  const months = Array.from({ length: 12 }, (_, i) => i + 1);
  const days = Array.from({ length: 31 }, (_, i) => i + 1);
  const hours = Array.from({ length: 24 }, (_, i) => i);
  const minutes = [0, 10, 20, 30, 40, 50];

  const [schedule, setSchedule] = useState({
    month: "",
    date: "",
    hour: "",
    min: "",
    place: "",
    placeCompleted: false
  });

  //지도
  const [place, setPlace] = useState("");
  const [places, setPlaces] = useState([]);
  const [selectedPlace, setSelectedPlace] = useState(null);
  const [isMapVisible, setIsMapVisible] = useState(false);

  const navigate = useNavigate();
  const location = useLocation();

  const handleConfirm = () => {
    // 이전 페이지로 이동 + 리랜더링 (채팅방 업데이트 필요하니까)
    navigate(-1, { state: { from: location.pathname, reload: true } });
  };

  useEffect(() => {
    if (place) {
      const ps = new kakao.maps.services.Places();
      ps.keywordSearch(place, (data, status) => {
        if (status === kakao.maps.services.Status.OK) {
          setPlaces(data);
        }
      });
    } else {
      setPlaces([]);
    }
  }, [place]);

  const handlePlaceSelect = (place) => {
    setSelectedPlace(place);
    setPlace(place.place_name);
    setPlaces([]); // 장소가 선택되면 자동완성 리스트를 초기화
    setIsMapVisible(true);
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') {
      e.preventDefault(); // 엔터 키 입력의 기본 동작 막기
    }
  };

  const handlePlaceComplete = () => {
    setSchedule({ ...schedule, place: selectedPlace.place_name, placeCompleted: true });
    setIsMapVisible(false);
  };

  const isFormComplete = () => {
    const { month, date, hour, min, placeCompleted } = schedule;
    return month && date && hour && min && placeCompleted;
  };

  return (
    <Wrapper>
      <Banner>
        <Box>
          <div>거래 시간과 장소를<div></div>정해주세요</div>
        </Box>
        <Box>
          <FontAwesomeIcon icon={faCalendarCheck} />
        </Box>
      </Banner>
      <Schedule id="date">
        <ScheduleBox>
          <span>우리 이 <span style={{ color: "#FFCE1F" }}>날</span> 거래해요</span>
          <div>
            <Select
              required
              value={schedule.month}
              onChange={(e) => setSchedule({ ...schedule, month: e.target.value })}
            >
              <option value="" disabled hidden>--</option>
              {months.map(month => (
                <option key={month} value={month}>{month}</option>
              ))}
            </Select>
            <p>월</p>
            <Select
              required
              value={schedule.date}
              onChange={(e) => setSchedule({ ...schedule, date: e.target.value })}
            >
              <option value="" disabled hidden>--</option>
              {days.map(day => (
                <option key={day} value={day}>{day}</option>
              ))}
            </Select>
            <p>일</p>
          </div>
        </ScheduleBox>
        <ScheduleBox>
          <FontAwesomeIcon icon={faCalendarDay} style={{ color: "#FFCE1F" }} />
        </ScheduleBox>
      </Schedule>
      <Schedule id="time">
        <ScheduleBox>
          <span>이 <span style={{ color: "#13C87C" }}>시간</span>까지 와주세요</span>
          <div>
            <Select
              required
              value={schedule.hour}
              onChange={(e) => setSchedule({ ...schedule, hour: e.target.value })}
            >
              <option value="" disabled hidden>--</option>
              {hours.map(hour => (
                <option key={hour} value={hour}>{hour}</option>
              ))}
            </Select>
            <p>시</p>
            <Select
              required
              value={schedule.min}
              onChange={(e) => setSchedule({ ...schedule, min: e.target.value })}
            >
              <option value="" disabled hidden>--</option>
              {minutes.map(min => (
                <option key={min} value={min}>{min}</option>
              ))}
            </Select>
            <p>분</p>
          </div>
        </ScheduleBox>
        <ScheduleBox>
          <FontAwesomeIcon icon={faClock} style={{ color: "#13C87C" }} />
        </ScheduleBox>
      </Schedule>
      <Schedule id="place">
        <ScheduleBox>
          <span>우리 <span style={{ color: "#00aaff" }}>여기</span>에서 만나요</span>
          <div>
            <Input
              type="text"
              placeholder="장소를 입력하세요"
              value={place}
              onChange={(e) => setPlace(e.target.value)}
              onKeyDown={handleKeyDown}
              required
            />
          </div>
          {places.length > 0 && (
            <AutocompleteBox>
              {places.map((place) => (
                <AutocompleteItem
                  key={place.id}
                  onClick={() => handlePlaceSelect(place)}
                >
                  <span>{place.place_name}</span>
                  <p>{place.address_name}</p>
                </AutocompleteItem>
              ))}
            </AutocompleteBox>
          )}
        </ScheduleBox>
        <ScheduleBox>
          <FontAwesomeIcon icon={faLocationDot} style={{ color: "#00aaff" }} />
        </ScheduleBox>
      </Schedule>
      <Confirmation>
        <button onClick={handleConfirm} disabled={!isFormComplete()}>확정하기</button>
      </Confirmation>
      {isMapVisible && selectedPlace && (
        <MapOverlay>
          <Container>
            <MapInfo>
              <TopBar>
                <span>거래 장소의 위치를 확인해주세요</span>
                <button onClick={() => setIsMapVisible(false)}>
                  <FontAwesomeIcon icon={faX} />
                </button>
              </TopBar>
              <div>
                <FontAwesomeIcon icon={faLocationDot} style={{ color: "#00aaff" }} />
                {selectedPlace.place_name}
              </div>
              <p>지번: {selectedPlace.address_name}</p>
              <p>도로명: {selectedPlace.road_address_name}</p>
              <CompleteButton onClick={handlePlaceComplete}><div>선택 완료</div></CompleteButton>
            </MapInfo>
            <MapContainer>
              <Map
                center={{
                  lat: selectedPlace.y,
                  lng: selectedPlace.x,
                }}
                style={{ width: "100%", height: "100%" }}
              >
                <MapMarker position={{ lat: selectedPlace.y, lng: selectedPlace.x }} />
              </Map>
            </MapContainer>
          </Container>
        </MapOverlay>
      )}
    </Wrapper>
  );
}

export default TransactionSchedule;

const Wrapper = styled.div`
  width: 100%;
  height: 100%;
  background-color: whitesmoke;
  display: block;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow-y: scroll;
`;

const Banner = styled.div`
  width: 100%;
  height: 180px;
  background-color: #88d5ff7d;
  padding: 30px;
  font-weight: 900;
  font-size: 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  * {
    margin-bottom: 15px;
  }
`;

const Box = styled.div`
  text-align: left;
  &:last-child {
    padding-right: 30px;
    font-size: 70px;
  }
`;

const Schedule = styled.form`
  width: 100%;
  height: 150px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30px;
  font-size: 25px;
  font-weight: 700;
`;

const ScheduleBox = styled.div`
  display: block;
  align-items: flex-end;
  &:last-child {
    display: flex;
    justify-content: center;
    align-items: start;
    font-size: 50px;
    width: 60px;
    height: 60px;
  }
  div {
    display: flex;
    align-items: center;
    padding-top: 10px;
  }
  p {
    padding-left: 5px;
    padding-right: 20px;
  }
`;

const Select = styled.select`
  width: 60px;
  height: 50px;
  text-align: center;
  margin-right: 5px;
  border: none;
  font-size: 20px;
  font-weight: 900;
  border-bottom: 2px solid gray;
  overflow: auto;
  option {
    overflow: auto;
  }
  &:focus {
    outline: none;
  }
`;

const Input = styled.input`
  all: unset;
  width: 200px;
  height: 40px;
  padding: 0px 2px;
  border-bottom: 2px solid rgba(0, 0, 0, 0.6);
  font-size: 22px;
`;

const AutocompleteBox = styled.div`
  position: absolute;
  background: white;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  z-index: 0;
  max-height: 150px;
  overflow-y: auto;
  width: 200px;
  display: flex;
  flex-direction: column;
  text-align: left;
`;

const AutocompleteItem = styled.div`
  width: 100%;
  padding: 10px;
  display: flex;
  flex-direction: column;
  border-bottom: 2px solid darkgray;
  font-size: 18px;
  text-align: left;
  cursor: pointer;
  &:hover {
    background: #f0f0f0;
  }
  span {
    padding: 0;
    width: 100%;
  }
  p {
    width: 100%;
    color: gray;
    font-size: 15px;
    padding: 0;
    margin-top: 5px;
  }
`;

const Confirmation = styled.div`
  height: 60px;
  width: 100%;
  margin-top: 60px;
  margin-bottom: 30px;
  display: flex;
  justify-content: center;
  align-items: center;
  button {
    width: 25%;
    height: 100%;
    background-color: #003e5e;
    color: white;
    font-weight: 900;
    border-radius: 30px;
    text-align: center;
    font-size: 20px;
    border: none;
    cursor: pointer;
    box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.2);
    &:hover {
      color: #003e5e;
      background-color: white;
    }
    &:disabled {
      background-color: gray;
      cursor: not-allowed;
    }
  }
`;

const MapOverlay = styled.div`
  position: fixed;
  bottom: 30px;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
`;

const MapContainer = styled.div`
  width: 100%;
  height: 60%;
  background: white;
  position: relative;
  z-index: 1000;
`;

const MapInfo = styled.div`
  width: 100%;
  /* height: 40%; */
  height: auto;
  background-color: white;
  padding: 15px;
  div {
    font-size: 19px;
    font-weight: bold;
    padding: 10px 0px;
    * {
      padding-right: 8px;
    }
  }
  p {
    font-size: 17px;
    padding-top: 2px;
    font-weight: 400;
    color: gray;
  }
  button {
    border: none;
    padding: 0px 10px;
    margin-top: 10px;
    border-radius: 20px;
    width: 100%;
    cursor: pointer;
    color: #13C87C;;
    background-color: white;
    border: 1px solid rgba(0,0,0,0.1);
    &:hover {
      color: white;
      background-color: #13C87C;
    }
  }
`;

const TopBar = styled.div`
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid darkgray;
  span {
    font-size: 20px;
  }
  button {
    all: unset;
    cursor: pointer;
    font-size: 22px;
    padding-right: 0;
  }
`;

const Container = styled.div`
  width: 70%;
  height: 60%;
`;

const CompleteButton = styled.button`
  
`;