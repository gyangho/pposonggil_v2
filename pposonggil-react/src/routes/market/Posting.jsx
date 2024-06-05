import React, { useState, useEffect, useRef } from 'react';
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCamera } from "@fortawesome/free-solid-svg-icons"; // 추가: faCamera 아이콘 import
import '../../../src/posting.css'; // css 파일 import
import axios from 'axios'; // axios import
import { useNavigate } from 'react-router-dom';

const { kakao } = window;

// JSON 서버 API URL로 변경해야 함
// const apiUrl = "http://localhost:3001/postList"; // JSON 서버
const apiUrl = "http://localhost:8080/api/board"; //백엔드 연동

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
  };

  const addPost = async (post) => {
    const currentDate = new Date().toISOString().split('T')[0]; // 현재 날짜 (YYYY-MM-DD 형식)
    const startTimeString = `${currentDate}-${post.startTime}`;
    const endTimeString = `${currentDate}-${post.endTime}`;

    const postData = {
      writerId: 1, // 나중에 writerId 전달받으면 수정
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

    console.log("여기까진 되나요?");

    try {
      const response = await axios.post(apiUrl, formData, {
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

  return (
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
      <div className="input-container" id="location-container">
        <input
          id="location"
          type="text"
          name="location"
          placeholder="거래 위치를 입력하세요"
          value={location}
          onChange={(e) => setLocation(e.target.value)}
        />
      </div>
      {places.length > 0 && (
        <div className="autocomplete-box">
          {places.map((place) => (
            <div
              key={place.id}
              className="autocomplete-item"
              onClick={() => handlePlaceSelect(place)}
            >
              <span>{place.place_name}</span>
              <p>{place.address_name}</p>
            </div>
          ))}
        </div>
      )}
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
    </form>
  );
}

export default Posting;


// 시간 10분 단위로 뜨게끔 하는 코드(css가 이상하게 나옴)
// import React, { useState, useEffect, useRef } from 'react';
// import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
// import { faCamera } from "@fortawesome/free-solid-svg-icons"; // 추가: faCamera 아이콘 import
// import '../../../src/posting.css'; // css 파일 import
// import axios from 'axios'; // axios import
// import { useNavigate } from 'react-router-dom';

// const { kakao } = window;

// // JSON 서버 API URL로 변경해야 함
// // const apiUrl = "http://localhost:3001/postList"; // JSON 서버
// const apiUrl = "http://localhost:8080/api/board";

// function Posting({ onSave, editingPost, onUpdate }) {
//   const [title, setTitle] = useState('');
//   const [content, setContent] = useState('');
//   const [startHour, setStartHour] = useState('');
//   const [startMinute, setStartMinute] = useState('');
//   const [endHour, setEndHour] = useState('');
//   const [endMinute, setEndMinute] = useState('');
//   const [location, setLocation] = useState('');
//   const [price, setPrice] = useState('');
//   const [image, setImage] = useState(null);
//   const [places, setPlaces] = useState([]);
//   const [selectedPlace, setSelectedPlace] = useState(null);
//   const [isMapVisible, setIsMapVisible] = useState(false);
//   const fileInputRef = useRef(null);
//   const navigate = useNavigate();

//   useEffect(() => {
//     if (editingPost) {
//       setTitle(editingPost.title);
//       setContent(editingPost.content);
//       setStartHour(editingPost.startTime.split(':')[0]);
//       setStartMinute(editingPost.startTime.split(':')[1]);
//       setEndHour(editingPost.endTime.split(':')[0]);
//       setEndMinute(editingPost.endTime.split(':')[1]);
//       setLocation(editingPost.location);
//       setPrice(editingPost.price);
//       setImage(editingPost.image || null);
//     }
//   }, [editingPost]);

//   useEffect(() => {
//     if (location) {
//       const ps = new kakao.maps.services.Places();
//       ps.keywordSearch(location, (data, status) => {
//         if (status === kakao.maps.services.Status.OK) {
//           setPlaces(data);
//         }
//       });
//     } else {
//       setPlaces([]);
//     }
//   }, [location]);

//   const handleImageUpload = (e) => {
//     const file = e.target.files[0];
//     if (file) {
//       setImage(file);
//     }
//   };

//   const handlePlaceSelect = (place) => {
//     setSelectedPlace(place);
//     setLocation(place.place_name);
//     setPlaces([]);
//     setIsMapVisible(true);
//   };

//   const addPost = async (post) => {
//     const currentDate = new Date().toISOString().split('T')[0]; // 현재 날짜 (YYYY-MM-DD 형식)
//     const startTimeString = `${currentDate}-${post.startHour}:${post.startMinute}`;
//     const endTimeString = `${currentDate}-${post.endHour}:${post.endMinute}`;

//     const postData = {
//       writerId: 1, // 나중에 writerId 전달받으면 수정
//       title: post.title,
//       content: post.content,
//       startTimeString,
//       endTimeString,
//       address: {
//         name: post.location,
//         latitude: selectedPlace.y,
//         longitude: selectedPlace.x,
//         street: selectedPlace.road_address_name || selectedPlace.address_name,
//       },
//       price: post.price,
//       isFreebie: false,
//       img: post.image ? URL.createObjectURL(post.image) : '', // base64 인코딩된 이미지 문자열 사용
//     };

//     try {
//       const response = await axios.post(apiUrl, postData, {
//         headers: {
//           'Content-Type': 'application/json',
//         },
//       });
//       console.log('Post added successfully:', response.data);
//     } catch (error) {
//       console.error("Error adding post", error);
//     }
//   };

//   const handleSubmit = (e) => {
//     e.preventDefault();
//     const post = {
//       title,
//       content,
//       startHour,
//       startMinute,
//       endHour,
//       endMinute,
//       location,
//       price,
//       image
//     };

//     if (editingPost) {
//       onUpdate({ ...editingPost, ...post });
//     } else {
//       addPost(post); // 서버로 POST 요청
//     }

//     setTitle('');
//     setContent('');
//     setStartHour('');
//     setStartMinute('');
//     setEndHour('');
//     setEndMinute('');
//     setLocation('');
//     setPrice('');
//     setImage(null);

//     navigate('/market'); // 게시글 등록 후 /market로 이동
//   };

//   return (
//     <form onSubmit={handleSubmit} id="writeFrm">
//       <div className="input-container" id="title-container">
//         <input
//           id="title-box"
//           type="text"
//           name="title"
//           placeholder="제목을 입력하세요"
//           value={title}
//           onChange={(e) => setTitle(e.target.value)}
//           required
//         />
//       </div>
//       <div className="input-container" id="content-container">
//         <input
//           id="content-box"
//           type="text"
//           name="content"
//           placeholder="내용을 입력하세요"
//           value={content}
//           onChange={(e) => setContent(e.target.value)}
//           required
//         />
//       </div>
//       <div className="image-container" id="image-container">
//         <div className="image-upload" onClick={() => fileInputRef.current.click()}>
//           {image ? (
//             <img src={URL.createObjectURL(image)} alt="upload" style={{ width: '100%', height: '100%' }} />
//           ) : (
//             <FontAwesomeIcon icon={faCamera} style={{ fontSize: '30px', color: '#ccc' }} />
//           )}
//           <input
//             type="file"
//             id="file-input"
//             accept="image/*"
//             onChange={handleImageUpload}
//             ref={fileInputRef}
//             style={{ display: 'none' }}
//           />
//         </div>
//       </div>
//       <div className="input-container" id="start-time-container">
//         <label htmlFor="startTime">거래 시작 시각:</label>
//         <div className="time-select">
//           <select
//             value={startHour}
//             onChange={(e) => setStartHour(e.target.value)}
//           >
//             <option value="" disabled hidden>시</option>
//             {[...Array(24).keys()].map(hour => (
//               <option key={hour} value={hour < 10 ? `0${hour}` : hour}>{hour < 10 ? `0${hour}` : hour}</option>
//             ))}
//           </select>
//           <select
//             value={startMinute}
//             onChange={(e) => setStartMinute(e.target.value)}
//           >
//             <option value="" disabled hidden>분</option>
//             {[10, 20, 30, 40, 50, 60].map(min => (
//               <option key={min} value={min}>{min}</option>
//             ))}
//           </select>
//         </div>
//       </div>
//       <div className="input-container" id="end-time-container">
//         <label htmlFor="endTime">거래 종료 시각:</label>
//         <div className="time-select">
//           <select
//             value={endHour}
//             onChange={(e) => setEndHour(e.target.value)}
//           >
//             <option value="" disabled hidden>시</option>
//             {[...Array(24).keys()].map(hour => (
//               <option key={hour} value={hour < 10 ? `0${hour}` : hour}>{hour < 10 ? `0${hour}` : hour}</option>
//             ))}
//           </select>
//           <select
//             value={endMinute}
//             onChange={(e) => setEndMinute(e.target.value)}
//           >
//             <option value="" disabled hidden>분</option>
//             {[10, 20, 30, 40, 50, 60].map(min => (
//               <option key={min} value={min}>{min}</option>
//             ))}
//           </select>
//         </div>
//       </div>
//       <div className="input-container" id="location-container">
//         <input
//           id="location"
//           type="text"
//           name="location"
//           placeholder="거래 위치를 입력하세요"
//           value={location}
//           onChange={(e) => setLocation(e.target.value)}
//         />
//       </div>
//       {places.length > 0 && (
//         <div className="autocomplete-box">
//           {places.map((place) => (
//             <div
//               key={place.id}
//               className="autocomplete-item"
//               onClick={() => handlePlaceSelect(place)}
//             >
//               <span>{place.place_name}</span>
//               <p>{place.address_name}</p>
//             </div>
//           ))}
//         </div>
//       )}
//       <div className="input-container" id="price-container">
//         <input
//           id="price-box"
//           type="text"
//           name="price"
//           placeholder="예상 거래 가격을 입력하세요"
//           value={price}
//           onChange={(e) => setPrice(e.target.value)}
//         />
//       </div>
//       <div className="submit-container">
//         <button type="submit" id="submit-btn">게시글 등록</button>
//       </div>
//     </form>
//   );
// }

// export default Posting;
