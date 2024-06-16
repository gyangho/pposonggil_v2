// import React, { useState, useEffect, useRef } from 'react';
// import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
// import { faCamera } from "@fortawesome/free-solid-svg-icons";
// import '../../../src/posting.css'; //css파일 import
// import axios from 'axios';
// import { useNavigate, useLocation } from 'react-router-dom';

// const apiUrl = "http://localhost:3001/postList";

// function EditPost() {
//     const location = useLocation();
//     const editingPost = location.state?.post;

//     const [title, setTitle] = useState('');
//     const [content, setContent] = useState('');
//     const [startTime, setStartTime] = useState('');
//     const [endTime, setEndTime] = useState('');
//     const [locationInput, setLocationInput] = useState('');
//     const [price, setPrice] = useState('');
//     const [image, setImage] = useState('');
//     const fileInputRef = useRef(null);
//     const navigate = useNavigate();

//     useEffect(() => {
//         if (editingPost) {
//             setTitle(editingPost.title);
//             setContent(editingPost.content);
//             setStartTime(editingPost.startTime);
//             setEndTime(editingPost.endTime);
//             setLocationInput(editingPost.location);
//             setPrice(editingPost.price);
//             setImage(editingPost.image || '');
//         }
//     }, [editingPost]);

//     const handleImageUpload = (e) => {
//         const file = e.target.files[0];
//         if (file) {
//             const reader = new FileReader();
//             reader.onload = (e) => {
//                 setImage(e.target.result);
//             };
//             reader.readAsDataURL(file);
//         }
//     };

//     const updatePost = async (post) => {
//         try {
//             const response = await axios.put(`${apiUrl}/${editingPost.id}`, post);
//             console.log('Post updated successfully:', response.data);
//             navigate('/member-posting');
//         } catch (error) {
//             console.error("Error updating post", error);
//         }
//     };

//     const handleSubmit = (e) => {
//         e.preventDefault();
//         const post = { title, content, startTime, endTime, location: locationInput, price, image };

//         updatePost(post);
//     };

//     return (
//         <form onSubmit={handleSubmit} id="writeFrm">
//             <div className="input-container" id="title-container">
//                 <input
//                     id="title-box"
//                     type="text"
//                     name="title"
//                     value={title}
//                     onChange={(e) => setTitle(e.target.value)}
//                     required
//                 />
//             </div>
//             <div className="input-container" id="content-container">
//                 <input
//                     id="content-box"
//                     type="text"
//                     name="content"
//                     value={content}
//                     onChange={(e) => setContent(e.target.value)}
//                     required
//                 />
//             </div>
//             <div className="image-container" id="image-container">
//                 <div className="image-upload" onClick={() => fileInputRef.current.click()}>
//                     {image ? (
//                         <img src={image} alt="upload" style={{ width: '100%', height: '100%' }} />
//                     ) : (
//                         <FontAwesomeIcon icon={faCamera} style={{ fontSize: '30px', color: '#ccc' }} />
//                     )}
//                     <input
//                         type="file"
//                         id="file-input"
//                         accept="image/*"
//                         onChange={handleImageUpload}
//                         ref={fileInputRef}
//                         style={{ display: 'none' }}
//                     />
//                 </div>
//             </div>
//             <div className="input-container" id="start-time-container">
//                 <label htmlFor="startTime">거래 시작 시각:</label>
//                 <input
//                     type="time"
//                     id="startTime"
//                     name="startTime"
//                     value={startTime}
//                     onChange={(e) => setStartTime(e.target.value)}
//                 />
//             </div>
//             <div className="input-container" id="end-time-container">
//                 <label htmlFor="endTime">거래 종료 시각:</label>
//                 <input
//                     type="time"
//                     id="endTime"
//                     name="endTime"
//                     value={endTime}
//                     onChange={(e) => setEndTime(e.target.value)}
//                 />
//             </div>
//             <div className="input-container" id="location-container">
//                 <input
//                     id="location"
//                     type="text"
//                     name="location"
//                     value={locationInput}
//                     onChange={(e) => setLocationInput(e.target.value)}
//                 />
//             </div>
//             <div className="input-container" id="price-container">
//                 <input
//                     id="price-box"
//                     type="text"
//                     name="price"
//                     value={price}
//                     onChange={(e) => setPrice(e.target.value)}
//                 />
//             </div>
//             <div className="submit-container">
//                 <button type="submit" id="submit-btn">저장하기</button>
//             </div>
//         </form>
//     );
// }

// export default EditPost;

import React, { useState, useEffect, useRef } from 'react';
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCamera } from "@fortawesome/free-solid-svg-icons";
import '../../../src/posting.css'; //css파일 import
import api from "../../api/api";
import { useNavigate, useLocation, useParams } from 'react-router-dom';

const apiUrl = "/board";

function EditPost() {
    const location = useLocation();
    const editingPost = location.state?.post;
    const boardId = useParams();

    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [startTime, setStartTime] = useState('');
    const [endTime, setEndTime] = useState('');
    const [locationInput, setLocationInput] = useState('');
    const [price, setPrice] = useState('');
    const [image, setImage] = useState('');
    const [imagePreview, setImagePreview] = useState('');
    const fileInputRef = useRef(null);
    const navigate = useNavigate();

    useEffect(() => {
        if (editingPost) {
            setTitle(editingPost.title);
            setContent(editingPost.content);

            // startTimeString과 endTimeString에서 시간 부분만 추출
            const startTime = editingPost.startTimeString?.split('-')[3];
            const endTime = editingPost.endTimeString?.split('-')[3];

            setStartTime(startTime);
            setEndTime(endTime);

            setLocationInput(editingPost.address?.name);
            setPrice(editingPost.price);
            setImage(editingPost.imageUrl || '');
            setImagePreview(editingPost.img || '');
        }
    }, [editingPost]);

    const handleImageUpload = (e) => {
        const file = e.target.files[0];
        // if (file) {
        //     // const reader = new FileReader();
        //     // reader.onload = (e) => {
        //     //     setImage(e.target.result);
        //     setImage(file);
        // };
        // // reader.readAsDataURL(file);
        if (file) {
            setImage(file);
            const reader = new FileReader();
            reader.onloadend = () => {
                setImagePreview(reader.result);
                // setImage(reader.result);
            };
            reader.readAsDataURL(file);
        }
    };


    const handleSubmit = (e) => {
        e.preventDefault();
        const currentDate = new Date().toISOString().split('T')[0]; // 현재 날짜 (YYYY-MM-DD 형식)

        const post = {
            ...editingPost, // 기존의 게시글 정보를 그대로 포함
            title,
            content,
            startTimeString: `${currentDate}-${startTime}`,
            endTimeString: `${currentDate}-${endTime}`,
            address: {
                name: locationInput,
                latitude: editingPost.address?.latitude,
                longitude: editingPost.address?.longitude,
                street: editingPost.address?.street
            },
            price,
            img: imagePreview // image는 file input의 파일 객체여야 합니다.
        };

        const formData = new FormData();
        formData.append('boardDto', new Blob([JSON.stringify(post)], { type: 'application/json' }));
        if (image instanceof File) {
            formData.append('file', image);
        }

        api.put(`${apiUrl}/${editingPost.boardId}`, formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        })
            .then(response => {
                console.log('Post updated successfully', response.data);
                navigate(`/member-posting/post/${editingPost.boardId}`);
            })
            .catch(error => {
                console.error('Error updating post', error);
            });
    };


    return (
        <form onSubmit={handleSubmit} id="writeFrm">
            <div className="input-container" id="title-container">
                <input
                    id="title-box"
                    type="text"
                    name="title"
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
                    value={content}
                    onChange={(e) => setContent(e.target.value)}
                    required
                />
            </div>
            {/* <div className="image-container" id="image-container">
                <div className="image-upload" onClick={() => fileInputRef.current.click()}>
                    {image ? (
                        <img src={image} alt="upload" style={{ width: '100%', height: '100%' }} />
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
            </div> */}
            <div className="image-container" id="image-container">
                <div className="image-upload" onClick={() => fileInputRef.current.click()}>
                    {image ? (
                        // <img src={imagePreview} alt="upload" style={{ width: '100%', height: '100%' }} />
                        <img src={editingPost.imageUrl} alt="upload" style={{ width: '100%', height: '100%' }} />
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
                    value={locationInput}
                    onChange={(e) => setLocationInput(e.target.value)}
                />
            </div>
            <div className="input-container" id="price-container">
                <input
                    id="price-box"
                    type="text"
                    name="price"
                    value={price}
                    onChange={(e) => setPrice(e.target.value)}
                />
            </div>
            <div className="submit-container">
                <button type="submit" id="submit-btn">저장하기</button>
            </div>
        </form>
    );
}

export default EditPost;
