import React, { useEffect, useState } from "react";
import { useRecoilValue, useSetRecoilState } from "recoil";
import styled from "styled-components";
import { motion } from "framer-motion";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPlus, faRotateRight } from "@fortawesome/free-solid-svg-icons";
import { currentAddressState, navState } from "../../recoil/atoms";
import { useNavigate } from "react-router-dom";
import axios from "axios";

//JSON 서버 API URL로 변경해야 함
const apiUrl ="http://localhost:3001/postList" 

const Wrapper = styled.div`
  width: 100%;
  height: 100%;
  background-color: whitesmoke;
  display: block;
  justify-content: center;
  align-items: center;
  position: relative;
`;

const TopBar = styled.div`
  width: 100%;
  height: 70px;
  padding: 0px 30px;
  display: flex;
  justify-content: start;
  align-items: center;
  font-weight: 800;
  font-size: 22px;
  position: sticky;
  /* background-color: #70ccfed2; */
  /* background-color: #D1EDFF; */
  /* background-color: #FFCE1F; */
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.15);


`;

const RefreshBtn = styled(motion.div)`
  margin-left: 7px;
  cursor: pointer;
`;

const PostList = styled.div`
  width: 100%;  
  height: 100%;
  padding: 20px;
  display: block;
  justify-content: center;
  align-items: center;
  text-align: left;
`;

const Post = styled.div`
  width: 100%;
  min-height: 150px;
  height: 150px;
  display: flex;
  justify-content: start;
  align-items: top;
  padding: 20px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.2);
  cursor: pointer;
  transition: background-color 0.4s ease;
  &:hover {
    background-color: #EEF9FE;
  }
`;

const ImgBox = styled.div`
  width: 110px;
  height: 110px;
  min-width: 110px;
  padding-right: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  img {
    width: 110px;
    height: 110px;
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); // 예시로 그림자 효과 추가
  }
`;

const TextBox = styled.div`
  width: 70%;
  height: 100%;
  font-weight: 500;
  font-size: 18px;
`;

const ChatBtn = styled.div`
  display: flex;
  justify-content: end;
  background-color: tomato;
  div {
    margin-left: 6px;
  }
`;

const ListBox = styled.div`
  width: 100%;
  height: 92%;
  max-height: 92%;
  overflow-y: scroll;
  background-color: white;
`;

const PostBtn = styled.div`
  z-index: 100;
  width: 100%;
  height: 50px;
  padding: 0px 20px;
  font-weight: 700;
  font-size: 18px;
  color: white;
  position: absolute;
  bottom: 20px;
  right: 20px;
  display: flex;
  justify-content: end;
  cursor: pointer;
`;

const Btn = styled.div`
  width: 110px;
  min-width: 110px;
  height: 100%;
  padding: 20px;
  background-color: #003E5E;
  /* background-color: #FFCE1F; */
  text-align: center;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 30px;
  box-shadow: 0px 0px 7px 3px rgba(0, 0, 0, 0.1);

`;

function Board() {
  const [isRotating, setIsRotating] = useState(false);
  const [posts, setPosts] = useState([]);
  const [editPost, setEditPost] = useState(null);

  const curAddr = useRecoilValue(currentAddressState);
  const setNav = useSetRecoilState(navState);
  setNav("market");
  
  const navigate = useNavigate();

  //게시물 가져오기
  useEffect(() => {
    fetchPosts();
  }, []);

  const fetchPosts = async () => {
    try {
      const response = await axios.get(apiUrl);
      setPosts(response.data);
    } catch (error) {
      console.error("Error fetching posts", error);
    }
  };

  const addPost = async (post) => {
    try {
      const response = await axios.post(apiUrl, post);
      setPosts([...posts, response.data]);
    } catch (error) {
      console.error("Error adding post", error);
    }
  };

  const onPostClick = (postId) => {
    navigate(`/market/post/${postId}`);
  };

  useEffect(() => {
    if (isRotating) {
      const timeout = setTimeout(() => {
        setIsRotating(false);
      }, 1000); // 애니메이션 지속 시간 후 상태를 false로 변경
      return () => clearTimeout(timeout);
    }
  }, [isRotating]);

  const handleRefreshClick = () => {
    //현재 위치 재탐색
    //재탐색한 위치에 해당하는 게시글 목록 다시 불러와야 함
    setIsRotating(true);
  };

  return (
    <Wrapper>
      <TopBar id="location">
        <div>{curAddr.depth3 || "현재 위치"}</div>
        <RefreshBtn
          onClick={handleRefreshClick}
          animate={{ rotate: isRotating ? 320 : 10 }}
          transition={{ duration: 0.8 }}
        >
          <FontAwesomeIcon icon={faRotateRight} />
        </RefreshBtn>
      </TopBar>
      <ListBox>
        <PostList>
          {posts.map((post) => (
            <Post key={post.id} onClick={() => onPostClick(post.id)}>
            <ImgBox><img src={post.img} alt="Example" /></ImgBox>
            <TextBox>
              <div id="title">{post.title}</div>
              <div id="time" style={{color: "gray", fontSize: "16px"}}>{post.date}</div>
              <br/>
              <div id="price" style={{fontWeight: "800"}}>{post.price}</div>
            </TextBox>
          </Post>
          ))}
          <Post id="sample" onClick={onPostClick}>
            <ImgBox><img src="https://via.placeholder.com/110" alt="Example" /></ImgBox>
            <TextBox>
              <div id="title">샘플입니다</div>
              <div id="time" style={{color: "gray", fontSize: "16px"}}>2024-05-27</div>
              <br/>
              <div id="price" style={{fontWeight: "800"}}>1억원</div>
            </TextBox>
          </Post>
        </PostList>

        <PostBtn onClick={() => navigate("/market/posting")}>
          <Btn>
            <FontAwesomeIcon icon={faPlus} style={{ marginRight: "4px" }} />
            <div style={{ paddingTop: "2px" }}>글쓰기</div>
          </Btn>
        </PostBtn>
      </ListBox>
    </Wrapper>
  );
}

export default Board
