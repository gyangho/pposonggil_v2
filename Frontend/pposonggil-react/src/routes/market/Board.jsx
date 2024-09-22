import React, { useEffect, useState } from "react";
import { useRecoilState, useRecoilValue, useSetRecoilState } from "recoil";
import styled from "styled-components";
import { motion } from "framer-motion";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPlus, faRotateRight, faDroplet } from "@fortawesome/free-solid-svg-icons";
import { currentAddressState, navState, userState } from "../../recoil/atoms";
import { useNavigate } from "react-router-dom";
import api from "../../api/api";

//JSON 서버 API URL(json-server 이용한 프톤트 테스트 용)
// const apiUrl = "http://localhost:3001/boards"
// const apiUrl = "http://localhost:3001/postList"

//서버 제공 url(실제 url)
const myId = localStorage.getItem('id');
const apiUrl = `/boards/with-expected-rain/${myId}`; // myId를 포함


function Board() {
  const [isRotating, setIsRotating] = useState(false);
  const [posts, setPosts] = useState([]);
  const [editPost, setEditPost] = useState(null);
  const [sorted, setSorted] = useState(false);//sorting


  const curAddr = useRecoilValue(currentAddressState);
  const setNav = useSetRecoilState(navState);
  // const [user, setUser] = useRecoilState(userState); //초기 유저 세팅

  //const user = useRecoilValue(userState); //다른 페이지에서 유저의 id나 닉네임 값을 불러오고 싶을 때
  // user.userId => 유저 아이디 필요할 때,  user.userNickName => 유저 닉네임 필요할 때

  const navigate = useNavigate();

  //추가
  // const sortPostsByRain = () => {
  //   const sortedPosts = [...posts].sort((a, b) => a.expectedRain - b.expectedRain);
  //   setPosts(sortedPosts);
  //   setSorted(true);
  // };


  //게시물 가져오기
  useEffect(() => {
    setNav("market");
    fetchPosts();
  }, []);

  // useEffect(() => {
  //   const fetchPosts = async () => {
  //     try {
  //       const response = await axios.get(apiUrl); // 게시글 목록 가져오기
  //       setPosts(response.data);
  //       console.log("Posts data fetched successfully", response.data);
  //     } catch (error) {
  //       console.error("Error fetching posts", error);
  //     }
  //   };

  //   fetchPosts();
  // }, []);



  const fetchPosts = async () => {
    // const url = 'https://pposong.ddns.net/api/boards/with-expected-rain/1'; //postman이랑 매치해서 꼭 재확인 할 것!!
    // const url = "https://pposong.ddns.net/api/board"
    const formData = new FormData(); // form-data 객체 생성

    const startDto = {
      "latitude": parseFloat(curAddr.lat),
      "longitude": parseFloat(curAddr.lon)
    };

    formData.append('startDto', new Blob([JSON.stringify(startDto)], { type: 'application/json' }));
    console.log("BEFORE SEND POST");

    // FormData 내용 출력
    // for (let [key, value] of formData.entries()) {
    //   console.log("서버로 보낸 데이터: ");
    //   console.log(`${key}:`, value);
    //   if (value instanceof Blob) {
    //     value.text().then(text => console.log(`${key} content:`, text));
    //   }
    // }

    try {
      const response = await api.post(apiUrl, formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      });
      console.log('Response:', response.data);
      setPosts(response.data);
    } catch (error) {
      console.error('Error:', startDto.latitude, startDto.longitude);
    }
  }


  const addPost = async (post) => {
    try {
      const response = await api.post(apiUrl, post);
      setPosts([...posts, response.data]);
    } catch (error) {
      console.error("Error adding post", error);
    }
  };

  const onPostClick = (post) => {//특정 게시글 클릭시
    localStorage.setItem('expectedRain', post.expectedRain);
    console.log('Expected Rain:', localStorage.getItem('expectedRain'));
    navigate(`/market/post/${post.boardId}`);
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
    fetchPosts();
  };

  const sortPostsByRain = () => {//최소 예상 강수량순으로 정렬
    const sortedPosts = [...posts].sort((a, b) => a.expectedRain - b.expectedRain);
    setPosts(sortedPosts);
    setSorted(true);
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
        <SortRainButton onClick={sortPostsByRain}>
          최소 예상 강수량순 <FontAwesomeIcon icon={faDroplet} style={{ color: "#74C0FC", fontSize: "20px" }} /></SortRainButton>
      </TopBar>

      <ListBox>
        <PostList>
          {posts.map((post) => (
            <Post key={post.boardId} onClick={() => onPostClick(post)}>
              <ImgBox>
                {post.imageUrl ? (
                  <img src={post.imageUrl} alt={post.title} />
                ) : (
                  <img src="https://via.placeholder.com/110" alt="Example" />
                )
                }
              </ImgBox>
              <TextBox>
                <TextContent>
                  <div id="title">{post.title}</div>
                  <div id="content" style={{ color: "gray", fontSize: "14px" }}>{post.content}</div>
                  <TimeLocationWrapper>
                    {/* <div id="time" style={{ color: "gray", fontSize: "14px" }}>거래 가능 시간: {post.startTimeString} - {post.endTimeString}</div> */}
                    <div id="time" style={{ color: "gray", fontSize: "14px" }}>거래 시작 시간: {post.startTimeString}</div>
                    <div id="time" style={{ color: "gray", fontSize: "14px" }}>거래 종료 시간: {post.endTimeString}</div>
                    <div id="address" style={{ color: "gray", fontSize: "14px" }}>거래 장소: {post.address.name}</div>
                  </TimeLocationWrapper>
                  <div id="price" style={{ fontWeight: "800", fontSize: "16px" }}>{post.price}원</div>
                </TextContent>
                <RainWrapper>
                  <FontAwesomeIcon icon={faDroplet} style={{ color: "#74C0FC", fontSize: "32px" }} />
                  <div id="expectedRain" style={{ color: "gray", fontSize: "14px", marginTop: "4px" }}>{post.expectedRain}mm</div>
                </RainWrapper>
              </TextBox>

            </Post>
          ))}
        </PostList>

        <PostBtn onClick={() => navigate("/market/posting")}>
          <Btn>
            <FontAwesomeIcon icon={faPlus} style={{ marginRight: "3px" }} />
            <div style={{ paddingTop: "2px" }}>글쓰기</div>
          </Btn>
        </PostBtn>
      </ListBox>
    </Wrapper>
  );
}


export default Board

const SortRainButton = styled.button`
  background: #004263;
  opacity: 0.8; /* 투명도 설정 (0.0 - 1.0) */
  border: none;
  color: white;
  font-size: 16px;
  cursor: pointer;
  margin-left: auto; /* 추가: 자동으로 오른쪽 정렬 */
  margin-right:30px;
  padding: 10px 20px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  &:hover {
    background: #00334e;
  }
  svg {
    margin-left: 8px; /* 아이콘과 텍스트 사이 간격 */
  }
`;

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
  // justify-content: start;
  justify-content: space-between;//양끝으로 정렬
  align-items: center;
  font-weight: 800;
  font-size: 22px;
  position: sticky;
  /* background-color: #70ccfed2; */
  /* background-color: #D1EDFF; */
  /* background-color: #FFCE1F; */
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.15);
`;

// const Location = styled.div`
//   display: flex;
//   align-items: center;
// `;

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
  // width: 70%;
  // height: 100%;
  // font-weight: 500;
  // font-size: 18px;
  // flex-direction: column; // 추가
  // justify-content: space-between; // 추가
  width: calc(100% - 110px - 40px); // 추가: 이미지 박스와 여백을 제외한 나머지 너비
  display: flex;
  justify-content: space-between; // 변경: 두 개의 자식 요소를 양쪽 끝으로 정렬
  align-items: center; // 추가: 수직 정렬
`;

// const TextContent = styled.div`
//   width: calc(100% - 60px); // 추가: 강수량 박스를 제외한 너비
//   height: 100%;
//   font-weight: 500;
//   font-size: 18px;
//   flex-direction: column; // 추가
//   justify-content: space-between; // 추가
// `;

const TextContent = styled.div`
  width: calc(100% - 60px);
  height: 100%;
  font-weight: 500;
  font-size: 18px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding-right: 10px;
`;

const TimeLocationWrapper = styled.div`
  display: flex;
  flex-direction: column;
  margin-bottom: 10px;
`;

// const RainWrapper = styled.div`
//   display: flex;
//   flex-direction: column;
//   align-items: center;
//   justify-content: flex-end; // 변경: 우측 정렬을 위한 스타일
// `;
const RainWrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-width: 60px;
  min-height: 60px; // 추가: 최소 높이 설정
  margin-left:30px
  
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
  // right: 20px;
  display: flex;
  // justify-content: end;
  justify-content: center;//글쓰기 버튼 가운데로 정렬
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

//버튼 추가
const Header = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  font-weight: bold;
  font-size: 20px;
  color: #004263;
`;

const SortButton = styled.button`
  background: none;
  border: none;
  color: #004263;
  font-size: 16px;
  cursor: pointer;

  &:hover {
    text-decoration: underline;
  }
`;

