// import React, { useEffect, useState } from "react";
// import { useNavigate, useParams } from "react-router-dom";
// import axios from "axios";
// import styled from "styled-components";
// import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
// import { faPlus, faCircleUser, faWonSign, faComments, faTemperatureHalf } from "@fortawesome/free-solid-svg-icons";
// import { useSetRecoilState } from "recoil";
// import { navState } from "../../recoil/atoms";

// // JSON 서버 API URL, 백이랑 연동 시 수정 필요
// const apiUrl = "http://localhost:3001/postList";

// function Post() {
//   const { boardId } = useParams();
//   const [post, setPost] = useState(null);
//   const navigate = useNavigate();

//   const setNav = useSetRecoilState(navState);
//   setNav("market");

//   //작성자 정보 가져와서 저장
//   // const [author, setAuthor] = useState(null);

//   useEffect(() => {
//     const fetchPost = async () => {
//       try {
//         const response = await axios.get(`${apiUrl}`);
//         const postList = response.data;
//         const foundPost = postList.find((item) => item.boardId.toString() === boardId);
//         if (foundPost) {
//           setPost(foundPost);
//         } else {
//           console.error(`Post with boardId ${boardId} not found.`);
//         }
//       } catch (error) {
//         console.error("Error fetching post", error);
//       }
//     };

//     fetchPost();
//   }, [boardId]);

//   //후에 파라미터로 유저id 넣어서 채팅방으로 넘어가게 하기
//   const onChatClick = (author) => {
//     navigate(`/market/chat/${author}`);
//   }

//   // 스핀너
//   if (!post) return <div>Loading...</div>;

//   return (
//     <React.Fragment>
//       <Wrapper>
//         <ImgBox>
//           <img src={post.img} alt={post.title} />
//         </ImgBox>
//         <AuthorBox>
//           <div style={{ display: "flex", alignItems: "center" }}>
//             <div id="profileImg">
//               {/* <img src=""/> */}
//               <FontAwesomeIcon icon={faCircleUser} style={{ color: "gray", fontSize: "35px" }} />
//             </div>
//             <div id="name">{post.author}</div>
//           </div>
//           <div id="rating">
//             <span style={{ color: "orange" }}>4.5</span>
//             <FontAwesomeIcon icon={faTemperatureHalf} style={{ color: "tomato", marginRight: "0" }} />
//           </div>
//         </AuthorBox>
//         <DetailBox>
//           <Title>{post.title}</Title>
//           <Date>{post.date}</Date>
//           <br />
//           <Content>{post.content}</Content>
//         </DetailBox>
//         <BottomBar>
//           <Price>
//             <FontAwesomeIcon icon={faWonSign} />
//             <div>{post.price}</div>
//           </Price>
//           <ChatBtn>
//             <FontAwesomeIcon icon={faComments} />
//             <span onClick={() => onChatClick(post.author)}>채팅하기</span>
//           </ChatBtn>
//         </BottomBar>


//       </Wrapper>


//     </React.Fragment>
//   );
// }

// export default Post;

// //.css
// const Wrapper = styled.div`
//   width: 100%;
//   height:100%;
//   overflow-y: scroll;
//   position: relative;
// `;
// const Box = styled.div`
//   width: 100%;
//   display: flex;
//   align-items: center;
// `;

// const ImgBox = styled(Box)`
//   height: 40%;
//   background-color: whitesmoke;
//   justify-content: center;
//   z-index: 5;
//   img {
//     padding: 0px 80px;
//     height: 100%;
//     width: 100%;
//   }
// `;

// const DetailBox = styled(Box)`
//   height: auto;
//   align-items: start;
//   display: block;
//   padding: 30px;
// `;

// const BottomBar = styled.div`
//   width: 100%;
//   height: 100px;
//   padding: 0px 30px;
//   position: absolute;
//   bottom: 0;
//   /* background-color: #EEF9FE; */
//   border-top: 1px solid rgba(0, 0, 0, 0.1);  
//   display: flex;
//   align-items: center;
//   justify-content: space-between;
//   font-size: 22px;
//   font-weight: 700;
// `;

// const Price = styled.div`
//   display: flex;
//   * {
//     margin-right: 20px;
//   }
// `;

// const ChatBtn = styled.div`
//   font-size: 18px;
//   color: white;
//   padding: 15px 20px;
//   background-color: #00b3fff5;
//   /* background-color: #216CFF; */
//   border: none;
//   border-radius: 8px;
//   right: 0;
//   cursor: pointer;
//   span {
//     margin-left:10px;
//   }
// `;

// const AuthorBox = styled(Box)`
//   height: 80px;
//   justify-content: space-between;
//   padding: 30px;
//   font-size: 25px;
//   font-weight: 700;
//   text-align: center;
//   border-bottom: 2px solid rgba(0,0,0,0.1);
//   box-shadow: inset 0px 6px 13px rgba(0, 0, 0, 0.1);
//   * {
//     margin-right: 8px;
//   }
// `;

// const Title = styled.div`
//   font-weight: 700;
//   font-size: 22px;
//   margin-bottom: 5px;
// `

// const Date = styled.div`
//   font-weight: 400;
//   font-size: 18px;
//   color: gray;

// `;
// const Content = styled.div`
//   font-weight: 400; 
//   font-size: 19px;
// `;

import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import axios from "axios";
import styled from "styled-components";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPlus, faCircleUser, faWonSign, faComments, faTemperatureHalf } from "@fortawesome/free-solid-svg-icons";
import { useSetRecoilState } from "recoil";
import { navState } from "../../recoil/atoms";

// JSON 서버 API URL, 백이랑 연동 시 수정 필요
const apiUrl = "http://localhost:8080/api/boards"
const chatApiUrl = "http://localhost:8080/api/chatroom";

function Post() {
  const { boardId } = useParams();//url 뒤에서 boardId 가져옴
  const [post, setPost] = useState(null);
  const navigate = useNavigate();

  //const setNav = useSetRecoilState(navState);
  //setNav("market");

  useEffect(() => {
    const fetchPost = async () => {
      try {
        const response = await axios.get(`${apiUrl}`);
        const boards = response.data;
        const foundPost = boards.find((item) => String(item.boardId) === boardId);
        if (foundPost) {
          setPost(foundPost);
        } else {
          console.error(`Post with id ${boardId} not found.`);
        }
      } catch (error) {
        console.error("Error fetching post", error);
      }
    };

    fetchPost();
  }, [boardId]);

  //후에 파라미터로 유저id 넣어서 채팅방으로 넘어가게 하기
  // const onChatClick = (writerNickname) => {
  //   navigate(`/market/chat/${writerNickname}`);
  // }

  const handleChatRequest = async () => {
    try {
      const response = await axios.post(chatApiUrl, {
        boardId: post.boardId,
        // requesterId: post.memberId // 실제 요청자의 ID로 설정
        requesterId: 2
      });
      console.log('Chat room created:', response.data);

      // 채팅방이 생성된 후, 해당 채팅방으로 이동
      navigate(`/market/chat/${response.data.chatRoomId}`);
    } catch (error) {
      console.error('Error creating chat room', error);
    }
  };


  // 스피너(아직 구현 안함)
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
              {/* <img src=""/> */}
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
          <Date>{post.createdAt}</Date>
          <br />
          <Content>{post.content}</Content>
        </DetailBox>
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
    </React.Fragment>
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
    margin-right: 20px;
  }
`;

const ChatBtn = styled.div`
  font-size: 18px;
  color: white;
  padding: 15px 20px;
  background-color: #00b3fff5;
  /* background-color: #216CFF; */
  border: none;
  border-radius: 8px;
  right: 0;
  cursor: pointer;
  span {
    margin-left:10px;
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