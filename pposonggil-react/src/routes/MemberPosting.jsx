// import React, { useEffect, useState } from 'react';
// import styled from 'styled-components';
// import { useNavigate, useParams } from 'react-router-dom';
// import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
// import { faListUl } from "@fortawesome/free-solid-svg-icons";
// import axios from 'axios';

// // const Container = styled.div`
// //   width: 100%;
// //   padding: 20px;
// //   background-color: #f9f9f9;
// // `;
// const Container = styled.div`
//   width: 100%;
//   height: 100%;
//   background-color: #E2E2E2;
//   display: flex;
//   flex-direction: column;
//   align-items: center;
//   padding-top: 20px;
// `;
// const Title = styled.h1`
//   margin-bottom: 20px;
//   color: #333;
//   font-weight: bold; /* 글씨를 굵게 */
//   font-size:20px;
// `;

// const PostList = styled.div`
//   // display: flex;
//   // flex-direction: column;
//   width: 80%;
//   margin-top: 20px;
//   background-color: white;
//   border-radius: 10px;
//   padding: 10px;
//   box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
// `;

// const Post = styled.div`
//   display: flex;
//   align-items: start;
//   margin-bottom: 15px;
//   padding: 10px;
//   border: 1px solid #ddd;
//   background-color: #fff;
//   border-radius: 5px;
//   cursor: pointer;
//   transition: background-color 0.3s;

//   &:hover {
//     background-color: #f1f1f1;
//   }
// `;

// const PostImage = styled.img`
//   width: 80px;
//   height: 80px;
//   border-radius: 8px;
//   object-fit: cover;
//   margin-right: 15px;
// `;

// const PostDetails = styled.div`
//   display: flex;
//   flex-direction: column;
//   width: 100%;
// `;

// const PostTitle = styled.h2`
//   margin: 0;
//   font-size: 18px;
//   color: #333;
//   font-weight: bold;
// `;

// const PostContent = styled.p`
//   margin: 5px 0 0;
//   font-size: 14px;
//   color: #777;
//   font-weight: bold;
// `;

// const PostTimeLocation = styled.div`
//   display: flex;
//   flex-direction: column;
//   margin: 5px 0;
//   font-weight: bold;
// `;

// const PostTime = styled.span`
//   font-size: 14px;
//   color: #777;
//   font-weight: bold;
// `;

// const PostLocation = styled.span`
//   font-size: 14px;
//   color: #777;
//   font-weight: bold;
// `;

// const PostPrice = styled.span`
//   margin-top: 5px;
//   font-size: 16px;
//   font-weight: bold;
//   color: #333;
// `;

// function MemberPosting() {
//   const [posts, setPosts] = useState([]);
//   const navigate = useNavigate();
//   const { writerId } = useParams(); // url에서 writerId 떼오기

//   useEffect(() => {
//     axios.get(`http://localhost:8080/api/boards/by-member/${writerId}`) // 백엔드 url로 변경
//       .then(response => {
//         setPosts(response.data);
//       })
//       .catch(error => {
//         console.error('Error fetching posts:', error);
//       });
//   }, [writerId]);

//   const handlePostClick = (boardId) => {
//     navigate(`/member-posting/post/${boardId}`);
//   };

//   return (
//     <Container>
//       <Title>내가 작성한 게시글 <FontAwesomeIcon icon={faListUl} /></Title>
//       {posts.length === 0 ? (
//         <p>No posts found.</p>
//       ) : (
//         <PostList>
//           {posts.map(post => (
//             <Post key={post.boardId} onClick={() => handlePostClick(post.boardId)}>
//               <PostImage src={post.imageUrl || "https://via.placeholder.com/80"} alt={post.title} />
//               <PostDetails>
//                 <PostTitle>{post.title}</PostTitle>
//                 <PostContent>{post.content}</PostContent>
//                 <PostTimeLocation>
//                   <PostTime>거래 가능 시간: {post.startTimeString} - {post.endTimeString}</PostTime>
//                   <PostLocation>거래 장소: {post.address.name}</PostLocation>
//                 </PostTimeLocation>
//                 <PostPrice>{post.price}원</PostPrice>
//               </PostDetails>
//             </Post>
//           ))}
//         </PostList>
//       )}
//     </Container>
//   );
// }

// export default MemberPosting;

import React, { useEffect, useState } from 'react';
import styled from 'styled-components';
import { useNavigate, useParams } from 'react-router-dom';
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faListUl } from "@fortawesome/free-solid-svg-icons";
import api from '../api/api';

const Container = styled.div`
  width: 100%;
  height: 100%;
  background-color: #E2E2E2;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 20px;
`;

const Title = styled.h1`
  margin-bottom: 20px;
  color: #333;
  font-weight: bold;
  font-size: 20px;
`;

const PostList = styled.div`
  width: 80%;
  margin-top: 20px;
  background-color: white;
  // background-color: rgb(162, 216, 248);
  border-radius: 10px;
  padding: 10px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
`;

const PostItem = styled.div`
  padding: 10px;
  margin-bottom: 15px;
  background-color: white;
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  display: flex;
  cursor: pointer;
  transition: background-color 0.3s;
  border: 1px solid #ccc;

  &:hover {
    background-color: #f9f9f9;
  }
`;

const PostImage = styled.img`
  width: 80px;
  height: 80px;
  border-radius: 8px;
  object-fit: cover;
  margin-right: 15px;
`;

const PostDetails = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  width: 100%;
`;

const PostTitle = styled.h2`
  margin: 0;
  font-size: 18px;
  color: #333;
  font-weight: bold;
`;

const PostContent = styled.p`
  margin: 5px 0 0;
  font-size: 14px;
  color: #777;
  font-weight: bold;
`;

const PostTimeLocation = styled.div`
  display: flex;
  flex-direction: column;
  margin: 5px 0;
  font-weight: bold;
`;

const PostTime = styled.span`
  font-size: 14px;
  color: #777;
  font-weight: bold;
`;

const PostLocation = styled.span`
  font-size: 14px;
  color: #777;
  font-weight: bold;
`;

const PostPrice = styled.span`
  margin-top: 5px;
  font-size: 16px;
  font-weight: bold;
  color: #333;
`;

function MemberPosting() {
  const [posts, setPosts] = useState([]);
  const navigate = useNavigate();
  const { writerId } = useParams(); // url에서 writerId 떼오기

  useEffect(() => {
    api.get(`http://localhost:8080/api/boards/by-member/${writerId}`) // 백엔드 url로 변경
      .then(response => {
        setPosts(response.data);
      })
      .catch(error => {
        console.error('Error fetching posts:', error);
      });
  }, [writerId]);

  const handlePostClick = (boardId) => {
    navigate(`/member-posting/post/${boardId}`);
  };

  return (
    <Container>
      <Title>내가 작성한 게시글 <FontAwesomeIcon icon={faListUl} /></Title>
      {posts.length === 0 ? (
        <p>No posts found.</p>
      ) : (
        <PostList>
          {posts.map(post => (
            <PostItem key={post.boardId} onClick={() => handlePostClick(post.boardId)}>
              <PostImage src={post.imageUrl || "https://via.placeholder.com/80"} alt={post.title} />
              <PostDetails>
                <PostTitle>{post.title}</PostTitle>
                <PostContent>{post.content}</PostContent>
                <PostTimeLocation>
                  <PostTime>거래 가능 시간: {post.startTimeString} - {post.endTimeString}</PostTime>
                  <PostLocation>거래 장소: {post.address.name}</PostLocation>
                </PostTimeLocation>
                <PostPrice>{post.price}원</PostPrice>
              </PostDetails>
            </PostItem>
          ))}
        </PostList>
      )}
    </Container>
  );
}

export default MemberPosting;
