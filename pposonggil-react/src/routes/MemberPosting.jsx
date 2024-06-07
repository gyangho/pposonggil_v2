import React, { useEffect, useState } from 'react';
import styled from 'styled-components';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';

const Container = styled.div`
  width: 100%;
  padding: 20px;
  background-color: #f9f9f9;
`;

const PostList = styled.div`
  display: flex;
  flex-direction: column;
`;

const Post = styled.div`
  display: flex;
  align-items: start;
  margin-bottom: 15px;
  padding: 10px;
  border: 1px solid #ddd;
  background-color: #fff;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;

  &:hover {
    background-color: #f1f1f1;
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
  width: 100%;
`;

const PostTitle = styled.h2`
  margin: 0;
  font-size: 18px;
  color: #333;
`;

const PostContent = styled.p`
  margin: 5px 0 0;
  font-size: 14px;
  color: #777;
`;

const PostTimeLocation = styled.div`
  display: flex;
  flex-direction: column;
  margin: 5px 0;
`;

const PostTime = styled.span`
  font-size: 14px;
  color: #777;
`;

const PostLocation = styled.span`
  font-size: 14px;
  color: #777;
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
    axios.get(`http://localhost:8080/api/boards/by-member/${writerId}`) // 백엔드 url로 변경
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
      {posts.length === 0 ? (
        <p>No posts found.</p>
      ) : (
        <PostList>
          {posts.map(post => (
            <Post key={post.boardId} onClick={() => handlePostClick(post.boardId)}>
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
            </Post>
          ))}
        </PostList>
      )}
    </Container>
  );
}

export default MemberPosting;
