import React, { useState, useEffect, useRef } from 'react';
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCamera } from "@fortawesome/free-solid-svg-icons"; // 추가: faCamera 아이콘 import
import '../../../src/posting.css'; // css 파일 import
import api from '../../api/api'; // axios import
import { useNavigate } from 'react-router-dom';

/*추가*/
import { useLocation } from "react-router-dom";
import styled from "styled-components";
import { faCalendarDay, faClock, faLocationDot, faX } from "@fortawesome/free-solid-svg-icons";
import { faCalendarCheck } from "@fortawesome/free-regular-svg-icons";
import { Map, MapMarker } from "react-kakao-maps-sdk";

const { kakao } = window;

// JSON 서버 API URL로 변경해야 함
// const apiUrl = "http://localhost:3001/postList"; // JSON 서버
const apiUrl = "https://pposong.ddns.net/api/board"; //백엔드 연동

function Posting({ onSave, editingPost, onUpdate }) {
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [startTime, setStartTime] = useState('');
  const [endTime, setEndTime] = useState('');
  const [location, setLocation] = useState('');
  const [price, setPrice] = useState('');
  const [image, setImage] = useState(null);
  const [places, setPlaces] = useState([]);
  const [selectedPlace, setSelectedPlace] = useState(null);
  const [isMapVisible, setIsMapVisible] = useState(false);
  const fileInputRef = useRef(null);
  const navigate = useNavigate();

  useEffect(() => {
    if (editingPost) {
      setTitle(editingPost.title);
      setContent(editingPost.content);
      setStartTime(editingPost.startTime);
      setEndTime(editingPost.endTime);
      setLocation(editingPost.location);
      setPrice(editingPost.price);
      setImage(editingPost.image || null);
    }
  }, [editingPost]);

  useEffect(() => {
    if (location) {
      const ps = new kakao.maps.services.Places();
      ps.keywordSearch(location, (data, status) => {
        if (status === kakao.maps.services.Status.OK) {
          setPlaces(data);
        }
      });
    } else {
      setPlaces([]);
    }
  }, [location]);

  const handleImageUpload = (e) => {
    const file = e.target.files[0];
    if (file) {
      setImage(file);
    }
  };

  const handlePlaceSelect = (place) => {
    setSelectedPlace(place);
    setLocation(place.place_name);
    setPlaces([]);
    setIsMapVisible(true);
    setShowContainer(false);
  };

  const addPost = async (post) => {
    const currentDate = new Date().toISOString().split('T')[0]; // 현재 날짜 (YYYY-MM-DD 형식)
    const startTimeString = `${currentDate}-${post.startTime}`;
    const endTimeString = `${currentDate}-${post.endTime}`;

    const postData = {
      writerId: localStorage.getItem('id'), // 나중에 writerId 전달받으면 수정
      title: post.title,
      content: post.content,
      startTimeString,
      endTimeString,
      address: {
        name: post.location,
        latitude: selectedPlace.y,
        longitude: selectedPlace.x,
        street: selectedPlace.road_address_name || selectedPlace.address_name,
      },
      price: post.price,
      isFreebie: false,
      // img: post.image ? URL.createObjectURL(post.image) : '', // base64 인코딩된 이미지 문자열 사용
    };

    const formData = new FormData();
    formData.append('boardDto', new Blob([JSON.stringify(postData)], { type: 'application/json' }));
    if (post.image) {
      formData.append('file', post.image);
    }

    try {
      const response = await api.post(apiUrl, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      console.log('Post added successfully:', response.data);
    } catch (error) {
      console.error("Error adding post", error);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const post = { title, content, startTime, endTime, location, price, image };

    if (editingPost) {
      onUpdate({ ...editingPost, ...post });
    } else {
      addPost(post); // 서버로 POST 요청
    }

    setTitle('');
    setContent('');
    setStartTime('');
    setEndTime('');
    setLocation('');
    setPrice('');
    setImage(null);

    navigate('/market'); // 게시글 등록 후 /market로 이동
  };

  /* 추가 */
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

  // const handlePlaceSelect = (place) => {
  //   setSelectedPlace(place);
  //   setPlace(place.place_name);
  //   setPlaces([]); // 장소가 선택되면 자동완성 리스트를 초기화
  //   setIsMapVisible(true);
  // };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') {
      e.preventDefault(); // 엔터 키 입력의 기본 동작 막기
    }
  };

  const [isVisible, setIsVisible] = useState(false);

  const handlePlaceComplete = () => {
    setSchedule({ ...schedule, place: selectedPlace.place_name, placeCompleted: true });
    setIsMapVisible(false);

  };

  const isFormComplete = () => {
    const { month, date, hour, min, placeCompleted } = schedule;
    return month && date && hour && min && placeCompleted;
  };

  const [showContainer, setShowContainer] = useState(true);

  const handleContainer = () => {
    setShowContainer(true);
  }


  return (
    <React.Fragment>
      <form onSubmit={handleSubmit} id="writeFrm">
        <div className="input-container" id="title-container">
          <input
            id="title-box"
            type="text"
            name="title"
            placeholder="제목을 입력하세요"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
          />
        </div>
        <div className="input-container" id="content-container">
          <input
            id="content-box"
            type="text"
            name="content"
            placeholder="내용을 입력하세요"
            value={content}
            onChange={(e) => setContent(e.target.value)}
            required
          />
        </div>
        <div className="image-container" id="image-container">
          <div className="image-upload" onClick={() => fileInputRef.current.click()}>
            {image ? (
              <img src={URL.createObjectURL(image)} alt="upload" style={{ width: '100%', height: '100%' }} />
            ) : (
              <FontAwesomeIcon icon={faCamera} style={{ fontSize: '30px', color: '#ccc' }} />
            )}
            <input
              type="file"
              id="file-input"
              accept="image/*"
              onChange={handleImageUpload}
              ref={fileInputRef}
              style={{ display: 'none' }}
            />
          </div>
        </div>
        {/* 이게 시간 */}
        <div className="input-container" id="start-time-container">
          <label htmlFor="startTime">거래 시작 시각:</label>
          <input
            type="time"
            id="startTime"
            name="startTime"
            value={startTime}
            onChange={(e) => setStartTime(e.target.value)}
          />
        </div>
        <div className="input-container" id="end-time-container">
          <label htmlFor="endTime">거래 종료 시각:</label>
          <input
            type="time"
            id="endTime"
            name="endTime"
            value={endTime}
            onChange={(e) => setEndTime(e.target.value)}
          />
        </div>

        {/* 이게 거래위치 */}
        <div className="input-container" id="location-container">
          <input
            id="location"
            type="text"
            name="location"
            placeholder="거래 위치를 입력하세요"
            value={location}
            onChange={(e) => setLocation(e.target.value)}
            onClick={() => handleContainer()}
          />

          {places.length > 0 && showContainer ? (
            <AutocompleteBox style={{ zIndex: "1" }}>
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
          ) : (<React.Fragment></React.Fragment>)}
        </div>

        <div className="input-container" id="price-container">
          <input
            id="price-box"
            type="text"
            name="price"
            placeholder="예상 거래 가격을 입력하세요"
            value={price}
            onChange={(e) => setPrice(e.target.value)}
          />
        </div>
        <div className="submit-container">
          <button type="submit" id="submit-btn">게시글 등록</button>
        </div>

        <Wrapper>

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
      </form>

      {/* 추가 코드 */}




    </React.Fragment>
  );
}

export default Posting;


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