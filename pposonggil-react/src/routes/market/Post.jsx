import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../../api/api";
import styled from "styled-components";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPlus, faCircleUser, faWonSign, faComments, faTemperatureHalf, faUmbrella, faDroplet, faWind, faGlassWaterDroplet, faBullhorn } from "@fortawesome/free-solid-svg-icons";
import { useSetRecoilState } from "recoil";
import { navState } from "../../recoil/atoms";

// JSON 서버 API URL, 백이랑 연동 시 수정 필요
// const apiUrl = "http://localhost:8080/api/boards"
const apiUrl = "http://localhost:8080/api/board/by-board"

const chatApiUrl = "http://localhost:8080/api/chatroom";

function Post() {
  const { boardId } = useParams();//url 뒤에서 boardId 가져옴
  const [post, setPost] = useState(null);
  const navigate = useNavigate();

  //setUser("sucocoa"); //임의로 현재 회원의 userId를 sucocoa로 설정


  // const setNav = useSetRecoilState(navState);
  //setNav("market");

  // useEffect(() => {
  //   setNav("market");
  // }, [setNav]);

  useEffect(() => {
    const fetchPost = async () => {
      try {
        const response = await api.get(`${apiUrl}/${boardId}`);//수정
        setPost(response.data);
        console.log("Post data fetched successfully", response.data);
      } catch (error) {
        console.error("Error fetching post", error);
      }
    };

    fetchPost();
  }, [boardId]);

  const handleChatRequest = async () => {
    try {
      // 먼저 GET 요청으로 해당 boardId에 맞는 채팅방이 있는지 확인
      const existingChatResponse = await api.get(`${chatApiUrl}/by-board/${post.boardId}`);
      if (existingChatResponse.data && existingChatResponse.data.chatRoomId) {
        // 기존 채팅방이 있는 경우 해당 채팅방으로 이동
        navigate(`/market/chat/${existingChatResponse.data.chatRoomId}`);
        // navigate(`/market/chat/${existingChatResponse.data.chatRoomId}`, { state: { user } });
      }
    } catch (error) {
      if (error.response && error.response.status === 500) {
        // 채팅방이 없는 경우 새로운 채팅방 생성
        try {
          const response = await api.post(chatApiUrl, {
            boardId: post.boardId,
            requesterId: 1 // 실제 요청자의 ID로 수정 필요
          });
          console.log('Chat room created:', response.data);
          // 채팅방이 생성된 후, 해당 채팅방으로 이동
          navigate(`/market/chat/${response.data.chatRoomId}`);
          // navigate(`/market/chat/${response.data.chatRoomId}`, { state: { user } });
        } catch (postError) {
          console.error('Error creating chat room', postError);
        }
      } else {
        console.error('Error checking existing chat room', error);
      }
    }
  };

  // 스피너(아직 구현 안함)
  if (!post) return <div>Loading...</div>;

  const expectedRain = localStorage.getItem('expectedRain');
  localStorage.removeItem('expectedRain');


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
          {/* <Date>{formatDate(post.createdAt)}</Date> */}
          <Date>{post.date}</Date>
          <br />
          <Content>{post.content}</Content>
          <TimeLocationWrapper>
            <div id="time" style={{ color: "gray", fontSize: "18px", fontWeight: "bold" }}>거래 시작 시간: {post.startTimeString}</div>
            <div id="time" style={{ color: "gray", fontSize: "18px", fontWeight: "bold" }}>거래 종료 시간: {post.endTimeString}</div>
            <div id="address" style={{ color: "gray", fontSize: "18px", fontWeight: "bold" }}>거래 장소: {post.address.name}</div>
          </TimeLocationWrapper>
        </DetailBox>
        <WeatherWrapper>
          <WeatherHeader>
            <FontAwesomeIcon icon={faBullhorn} />
            거래 장소까지의 기상정보
          </WeatherHeader>
          <WeatherInfo>
            {expectedRain && (
              <div className="bold-text">
                <FontAwesomeIcon icon={faDroplet} /> 예상 강수량: {expectedRain}mm
              </div>
            )}
            <div>
              <FontAwesomeIcon icon={faUmbrella} /> 강수: {post.forecastDto.rn1}mm
            </div>
            <div>
              <FontAwesomeIcon icon={faTemperatureHalf} /> 기온: {post.forecastDto.t1h}°C
            </div>
            <div>
              <FontAwesomeIcon icon={faWind} /> 풍속: {post.forecastDto.wsd}m/s
            </div>
            <div>
              <FontAwesomeIcon icon={faGlassWaterDroplet} /> 습도: {post.forecastDto.reh}%
            </div>
          </WeatherInfo>
        </WeatherWrapper>
        <BottomBar>
          <Price>
            <FontAwesomeIcon icon={faWonSign} />
            <div>{post.price}원</div>
          </Price>
          <ChatBtn onClick={handleChatRequest}>
            <FontAwesomeIcon icon={faComments} />
            <span>채팅하기</span>
          </ChatBtn>
        </BottomBar>
      </Wrapper>
    </React.Fragment >
  );
}


export default Post;

const Wrapper = styled.div`
  width: 100%;
  height:100%;
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
    // padding: 0px 80px;
    padding: 0 20px; /* 이미지와 경계 간의 패딩 조정 */
    height: 100%;
    width: 100%;
  }
`;

const DetailBox = styled(Box)`
  height: auto;
  align-items: start;
  display: block;
  // padding: 30px;
  padding: 20px; /* 세부 정보와 경계 간의 패딩 조정 */
`;

const BottomBar = styled.div`
  width: 100%;
  height: 100px;
  // padding: 0px 30px;
  padding: 0 20px; /* 하단 바와 경계 간의 패딩 조정 */
  position: absolute;
  bottom: 0;
  /* background-color: #EEF9FE; */
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
    // margin-right: 20px;
    margin-right: 10px; /* 가격 요소 간의 간격 조정 */
  }
`;

const ChatBtn = styled.div`
  font-size: 18px;
  color: white;
  padding: 15px 20px;
  background-color: #00b3fff5;
  /* background-color: #216CFF; */
  border: none;
  border-radius: 35px;
  right: 0;
  cursor: pointer;
  span {
    margin-left:10px;
  }
`;

const AuthorBox = styled(Box)`
  height: 80px;
  justify-content: space-between;
  // padding: 30px;
  padding: 20px; /* 작성자 박스와 경계 간의 패딩 조정 */
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
  font-size: 20px;
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

const WeatherInfo = styled.div`
// font-color: #004263;
color: #004263;
  margin: 10px 0;
  font-size: 18px;
  div {
    margin-bottom: 5px;
  }
  .bold-text {
    // font-weight: bold;
    font-size: 20px; /* 글씨 크기 조정 */
  }
`;

const WeatherWrapper = styled.div`
  position: absolute;
  top: 70%;
  right: 30px;
  transform: translateY(-50%);
  // background-color: #f9f9f9;
  background-color: #87cfeb44;
  padding: 10px;
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  font-size: 18px;
`;

const WeatherHeader = styled.div`
  display: flex;
  align-items: center;
  font-weight: bold;
  font-size: 20px;
  margin-bottom: 10px;
  color: #004263;

  svg {
    margin-right: 5px;
  }
`;
