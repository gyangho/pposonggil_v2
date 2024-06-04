import React from "react";
import styled from "styled-components";
import { useNavigate } from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faListUl } from "@fortawesome/free-solid-svg-icons";
import axios from "axios";

const Container = styled.div`
width: 100%;
height: 100%;
background-color: #E2E2E2;
display: flex;
flex-direction: column;
align-items: center;
padding-top: 20px;
`;

const Button = styled.button`
padding: 10px 20px;
background-color: tomato;
color: white;
border: none;
border-radius: 5px;
cursor: pointer;
display: flex;
align-items: center;
font-size: 18px;
margin-top: 20px;

&:hover {
  background-color: darkred;
}

svg {
  margin-left: 10px;
}
`;


function MyPage() {
  const navigate = useNavigate();
  const writerId = 1; // 실제 사용자의 writerId로 대체해야 함

  const handleButtonClick = async () => {
    if (!writerId) {
      alert('사용자 정보를 불러오는 중입니다. 잠시만 기다려주세요.');
      return;
    }
    try {
      const response = await axios.get(`http://localhost:3001/postList`);
      const userPosts = response.data.filter(post => post.writerId === writerId);
      if (userPosts.length > 0) {
        navigate(`/member-posting/${writerId}`);//이게 맞나..
      } else {
        alert('작성한 게시글이 없습니다.');
      }
    } catch (error) {
      console.error('Error fetching posts:', error);
    }
  };

  // const handleTestButtonClick = () => {
  //   navigate('/Test');
  // };

  return (
    <React.Fragment>
      <Container>
        <Button onClick={handleButtonClick}>
          작성한 게시글 목록 보기
          <FontAwesomeIcon icon={faListUl} />
        </Button>

        {/* <Button onClick={handleTestButtonClick}>
          Test 페이지로 이동
        </Button> */}
      </Container>
    </React.Fragment>
  )
}

export default MyPage;

////////////////////////////////////////////////////////////

//나중에 백 연동할 때
// import React, { useState, useEffect } from "react";
// import styled from "styled-components";
// import { useNavigate } from "react-router-dom";
// import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
// import { faListUl } from "@fortawesome/free-solid-svg-icons";
// import axios from 'axios';

// const Container = styled.div`
//   width: 100%;
//   height: 100%;
//   background-color: #E2E2E2;
//   display: flex;
//   flex-direction: column;
//   align-items: center;
//   padding-top: 20px;
// `;

// const Button = styled.button`
//   padding: 10px 20px;
//   background-color: tomato;
//   color: white;
//   border: none;
//   border-radius: 5px;
//   cursor: pointer;
//   display: flex;
//   align-items: center;
//   font-size: 18px;
//   margin-top: 20px;

//   &:hover {
//     background-color: darkred;
//   }

//   svg {
//     margin-left: 10px;
//   }
// `;

// function MyPage() {
//   const navigate = useNavigate();
//   const [writerId, setWriterId] = useState(null); // 상태로 writerId 관리

//   useEffect(() => {
//     const fetchUserData = async () => {
//       try {
//         const response = await axios.get('http://localhost:3001/api/user', {
//           headers: {
//             Authorization: `Bearer ${localStorage.getItem('token')}` // 인증 토큰 포함
//           }
//         });
//         setWriterId(response.data.writerId);
//       } catch (error) {
//         console.error('Error fetching user data:', error);
//       }
//     };

//     fetchUserData();
//   }, []);

//   const handleButtonClick = async () => {
//     if (!writerId) {
//       alert('사용자 정보를 불러오는 중입니다. 잠시만 기다려주세요.');
//       return;
//     }

//     try {
//       const response = await axios.get('http://localhost:3001/postList');
//       const userPosts = response.data.filter(post => post.writerId === writerId);
//       if (userPosts.length > 0) {
//         navigate(`/member-posting/${writerId}`);
//       } else {
//         alert('작성한 게시글이 없습니다.');
//       }
//     } catch (error) {
//       console.error('Error fetching posts:', error);
//     }
//   };

//   return (
//     <React.Fragment>
//       <Container>
//         <Button onClick={handleButtonClick}>
//           작성한 게시글 목록 보기
//           <FontAwesomeIcon icon={faListUl} />
//         </Button>
//       </Container>
//     </React.Fragment>
//   );
// }

// export default MyPage;
