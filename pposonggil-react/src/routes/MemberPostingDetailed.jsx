import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/api";
import styled from "styled-components";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCircleUser, faWonSign, faTemperatureHalf } from "@fortawesome/free-solid-svg-icons";
import { useSetRecoilState } from "recoil";
import { navState } from "../recoil/atoms";

// JSON 서버 API URL, 백이랑 연동 시 수정 필요
// const apiUrl = "http://localhost:8080/api/board/${boardId}";
const apiUrl = "http://localhost:8080/api/board/by-board";


function MemberPostingDetailed() {
  // const { postId } = useParams();
  const { boardId } = useParams();
  const [post, setPost] = useState(null);
  const navigate = useNavigate();

  const setNav = useSetRecoilState(navState);
  setNav("market");

  useEffect(() => {
    const fetchPost = async () => {
      try {
        const response = await api.get(`${apiUrl}/${boardId}`);
        // const postList = response.data; 백엔드 코드 합치면
        // const postList = response.data;
        // const foundPost = postList.find((item) => item.boardId.toString() === boardId);
        // if (foundPost) {
        setPost(response.data);
        // } else {
      } catch (error) {
        console.error("Error fetching post", error);
      }
    };

    fetchPost();
  }, [boardId]);

  // const handleEdit = () => {
  //     navigate(`/market/edit-post/${postId}`); // 기존의 수정 버튼 핸들러가 수정 페이지로 이동하도록 변경
  // };

  const handleEdit = () => {
    navigate(`/market/edit-post/${boardId}`, { state: { post } });
  };

  const handleDelete = async () => {
    try {
      await api.delete(`http://localhost:8080/api/board/${boardId}`);
      navigate(`/member-posting/${post.writerId}`);
    } catch (error) {
      console.error("Error deleting post", error);
    }
  };

  if (!post) return <div>Loading...</div>;

  return (
    <React.Fragment>
      <Wrapper>
        <ImgBox>
          <img src={post.imageUrl} alt={post.title} />
        </ImgBox>
        <AuthorBox>
          <div style={{ display: "flex", alignItems: "center" }}>
            <div id="profileImg">
              <FontAwesomeIcon icon={faCircleUser} style={{ color: "gray", fontSize: "35px" }} />
            </div>
            <div id="name">{post.writerNickName}</div>
          </div>
          <div id="rating">
            <span style={{ color: "orange" }}>{post.ratingScore}</span>
            <FontAwesomeIcon icon={faTemperatureHalf} style={{ color: "tomato", marginRight: "0" }} />
          </div>
        </AuthorBox>
        <DetailBox>
          <Title>{post.title}</Title>
          <Date>{post.date}</Date>
          <br />
          <Content>{post.content}</Content>
          <TimeLocationWrapper>
            <div id="time" style={{ color: "gray", fontSize: "18px", fontWeight: "bold" }}>거래 시작 시간: {post.startTimeString}</div>
            <div id="time" style={{ color: "gray", fontSize: "18px", fontWeight: "bold" }}>거래 종료 시간: {post.endTimeString}</div>
            <div id="address" style={{ color: "gray", fontSize: "18px", fontWeight: "bold" }}>거래 장소: {post.address.name}</div>
          </TimeLocationWrapper>
        </DetailBox>
        <BottomBar>
          <Price>
            <FontAwesomeIcon icon={faWonSign} />
            <div>{post.price}원</div>
          </Price>
          <Button onClick={handleEdit}>게시글 수정하기</Button>
          <Button onClick={handleDelete}>게시글 삭제하기</Button>
        </BottomBar>
      </Wrapper>
    </React.Fragment>
  );
}

export default MemberPostingDetailed;

const Wrapper = styled.div`
  width: 100%;
  height: 100%;
  overflow-y: scroll;
  position: relative;
`;
const Box = styled.div`
  width: 100%;
  display: flex;
  align-items: center;
`;

const ImgBox = styled(Box)`
  height: 40%;
  background-color: whitesmoke;
  justify-content: center;
  z-index: 5;
  img {
    padding: 0px 80px;
    height: 100%;
    width: 100%;
  }
`;

const DetailBox = styled(Box)`
  height: auto;
  align-items: start;
  display: block;
  padding: 30px;
`;

const BottomBar = styled.div`
  width: 100%;
  height: 100px;
  padding: 0px 30px;
  position: absolute;
  bottom: 0;
  border-top: 1px solid rgba(0, 0, 0, 0.1);  
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 22px;
  font-weight: 700;
`;

const Price = styled.div`
  display: flex;
  * {
    margin-right: 20px;
  }
`;

const Button = styled.button`
  font-size: 18px;
  color: white;
  padding: 15px 20px;
  background-color: #00b3fff5;
  border: none;
  border-radius: 35px;
  cursor: pointer;
  margin-left: 10px;
  &:hover {
    background-color: #007bbf;
  }
`;

const AuthorBox = styled(Box)`
  height: 80px;
  justify-content: space-between;
  padding: 30px;
  font-size: 25px;
  font-weight: 700;
  text-align: center;
  border-bottom: 2px solid rgba(0,0,0,0.1);
  box-shadow: inset 0px 6px 13px rgba(0, 0, 0, 0.1);
  * {
    margin-right: 8px;
  }
`;

const Title = styled.div`
  font-weight: 700;
  font-size: 22px;
  margin-bottom: 5px;
`

const Date = styled.div`
  font-weight: 400;
  font-size: 18px;
  color: gray;
`;

const Content = styled.div`
  font-weight: 400; 
  font-size: 19px;
`;

const TimeLocationWrapper = styled.div`
  display: flex;
  flex-direction: column;
  margin-top: 10px;
  color: gray;
  font-size: 16px;

  div {
    margin-bottom: 10px; /* 각 div 요소 사이의 간격을 10px로 설정 */
  }

  /* 마지막 div 요소의 하단 여백을 제거 */
  div:last-child {
    margin-bottom: 0;
  }
`;