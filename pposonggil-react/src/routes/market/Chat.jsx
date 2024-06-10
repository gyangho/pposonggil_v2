
import React, { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useSetRecoilState } from "recoil";
import { navState } from "../../recoil/atoms";
import styled from "styled-components";
import api from "../../api/api";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faUserCircle, faBan, faFlag } from "@fortawesome/free-solid-svg-icons";

function Chat() {
  const { chatRoomId } = useParams();
  const setNav = useSetRecoilState(navState);
  const navigate = useNavigate();
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");
  const [blocked, setBlocked] = useState(false);
  const [showReportModal, setShowReportModal] = useState(false);
  const [reportReason, setReportReason] = useState("");
  const [otherUserId, setOtherUserId] = useState(null);
  const [otherUserNickName, setOtherUserNickName] = useState(null);
  const [subjectId, setSubjectId] = useState(null);
  const [objectId, setObjectId] = useState(null);


  // const myId = localStorage.getItem('id');
  const myId = parseInt(localStorage.getItem('id'), 10);
  const myNickName = localStorage.getItem('nickname');
  //const otherUserId = 2;

  useEffect(() => {
    setNav("market");
    fetchChatRoomInfo();
  }, [setNav]);

  const fetchChatRoomInfo = async () => {//우산 사는 사람, 파는 사람 ID 받아오기
    try {
      const response = await api.get(`https://pposong.ddns.net/api/chatroom/by-chatroom/${chatRoomId}`);
      const { writerId, requesterId } = response.data;
      setSubjectId(writerId);
      setObjectId(requesterId);
      fetchMessages(writerId, requesterId);
    } catch (error) {
      console.error('Failed to fetch chat room info:', error);
    }
  };

  const handleConfirmTrade = async () => {
    try {
      const tradeData = {
        chatRoomId: chatRoomId,
        subjectId: subjectId,
        objectId: objectId,
      };

      const response = await api.post(`https://pposong.ddns.net/api/trade`, tradeData);
      alert('거래가 확정되었습니다.');
      // navigate('/mypage/ongoings/status', { state: { transaction: trade } }); // 거래 확정 후 이동할 페이지를 설정하세요.
    } catch (error) {
      console.error('Failed to confirm trade:', error);
      alert('거래 확정 중 오류가 발생했습니다.');
    }
  };


  const fetchMessages = async () => {//메시지 가져오기
    try {
      const response = await api.get(`https://pposong.ddns.net/api/messages/by-chatroom/${chatRoomId}`);
      const sortedMessages = response.data.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt));
      const formattedMessages = response.data.map(msg => ({
        ...msg,
        timestamp: new Date(msg.createdAt).toLocaleTimeString(),
      }));


      // otherUserId 설정(이따 다시 보기)
      const otherUser = response.data.find(msg => msg.senderId !== myId);
      if (otherUser) {
        setOtherUserId(otherUser.senderId);
        setOtherUserNickName(otherUser.senderNickName);
      }

      setMessages(formattedMessages);
    } catch (error) {
      console.error('Failed to fetch messages:', error);
    }
  };

  useEffect(() => {
    fetchMessages();
    const interval = setInterval(fetchMessages, 2000);
    return () => clearInterval(interval);
  }, [chatRoomId]);

  const handleSendMessage = async () => {//메시지 보내기
    if (input.trim() && !blocked) {
      const newMessage = {
        //   chatRoomId: parseInt(chatRoomId, 10),
        chatRoomId: chatRoomId,
        senderId: myId,
        senderNickName: myNickName,
        content: input,
      };

      try {
        await api.post('https://pposong.ddns.net/api/message', newMessage);
        setMessages(prevMessages => [
          ...prevMessages,
          { ...newMessage, timestamp: new Date().toLocaleTimeString() }
        ]);
        setInput("");
      } catch (error) {
        console.error('Failed to send message:', error);
      }
    }
  };

  const handleKeyPress = (event) => {//엔터치면 메시지 보내짐
    if (event.key === "Enter") {
      handleSendMessage();
    }
  };

  const handleBlockUser = async () => {//회원 차단
    try {
      const blockData = {
        subjectId: myId,
        objectId: otherUserId
      };
      const response = await api.post('https://pposong.ddns.net/api/block', blockData);
      console.log('Block ID:', response.data.blockId);
      setBlocked(true);
      alert(response.data.message);
    } catch (error) {
      console.error('Failed to block user:', error);
    }
  };

  const handleReportUser = async () => {//회원 신고
    try {
      const reportData = {
        subjectId: myId,
        objectId: otherUserId,
        reportType: reportReason
      };
      const response = await api.post('https://pposong.ddns.net/api/report', reportData);
      alert('상대를 신고하였습니다.');
      setShowReportModal(false);
    } catch (error) {
      console.error('Failed to report user:', error);
    }
  };

  const openReportModal = () => {
    setShowReportModal(true);
  };

  const closeReportModal = () => {
    setShowReportModal(false);
  };

  return (
    <React.Fragment>
      <Header>
        <UserInfo>
          <FontAwesomeIcon icon={faUserCircle} size="2x" />
          <Nickname>{otherUserNickName}</Nickname>
        </UserInfo>
        <ActionButtons>
          <Button onClick={handleBlockUser}>
            <FontAwesomeIcon icon={faBan} /> 차단하기
          </Button>
          <Button onClick={openReportModal}>
            <FontAwesomeIcon icon={faFlag} /> 신고하기
          </Button>
        </ActionButtons>
      </Header>
      <ChatContainer>
        {messages.map((msg, index) => (
          <Message key={index} isMe={msg.senderId === myId}>
            <strong>{msg.senderNickName}</strong>: {msg.content}
            <Timestamp>{msg.timestamp}</Timestamp>
          </Message>
        ))}
      </ChatContainer>
      <InputContainer>
        <ChatInput
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyPress={handleKeyPress}
          placeholder="메시지를 입력하세요..."
          disabled={blocked}
        />
        <SendButton onClick={handleSendMessage}>전송</SendButton>
      </InputContainer>
      <ScheduleBtn onClick={handleConfirmTrade}>거래 예약하기</ScheduleBtn>

      {showReportModal && (
        <Modal>
          <ModalContent>
            <h2>신고 사유 선택</h2>
            <StyledSelect value={reportReason} onChange={(e) => setReportReason(e.target.value)}>
              <option value="">사유를 선택하세요</option>
              <option value="욕설">욕설</option>
              <option value="광고">광고</option>
              <option value="노쇼">노쇼</option>
              <option value="지각">지각</option>
              <option value="불량우산">불량우산</option>
            </StyledSelect>
            <ModalButtonContainer>
              <ModalButton onClick={handleReportUser}>신고하기</ModalButton>
              <ModalButton onClick={closeReportModal}>취소</ModalButton>
            </ModalButtonContainer>
          </ModalContent>
        </Modal>
      )}
    </React.Fragment>
  );
}

export default Chat;

const Header = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  background-color: #f0f0f0;
  border-bottom: 1px solid #ccc;
`;

const UserInfo = styled.div`
  display: flex;
  align-items: center;
`;

const Nickname = styled.span`
  margin-left: 10px;
  font-size: 18px;
  font-weight: bold;
`;

const ActionButtons = styled.div`
  display: flex;
`;

const Button = styled.button`
  background-color: #ff6b6b;
  color: white;
  border: none;
  border-radius: 5px;
  padding: 10px 15px;
  margin-left: 10px;
  cursor: pointer;
  &:hover {
    background-color: #ff4b4b;
  }
`;

const ScheduleBtn = styled.div`
  background-color: #87cfeb44;
  padding: 15px 30px;
  margin: auto;
  border-radius: 25px;
  max-width: 300px;
  cursor: pointer;
  text-align: center;
  &:hover {
    background-color: #ff7f50;
  }
`;

const ChatContainer = styled.div`
  display: flex;
  flex-direction: column;
  padding: 10px;
  height: 60vh;
  overflow-y: auto;
  background-color: #f0f0f0;
  border: 1px solid #ccc;
`;

const Message = styled.div`
  align-self: ${props => (props.isMe ? 'flex-end' : 'flex-start')};
  background-color: ${props => (props.isMe ? '#daf8da' : '#fff')};
  padding: 10px;
  margin: 5px;
  border-radius: 10px;
  max-width: 60%;
  text-align: ${props => (props.isMe ? 'right' : 'left')};
`;

const Timestamp = styled.span`
  display: block;
  font-size: 0.7em;
  color: #888;
`;

const InputContainer = styled.div`
  display: flex;
  padding: 10px;
  border-top: 1px solid #ccc;
  background-color: #fff;
`;

const ChatInput = styled.input`
  flex: 1;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  margin-right: 10px;
`;

const SendButton = styled.button`
  padding: 10px 20px;
  background-color: tomato;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  &:hover {
    background-color: #e67e22;
  }
`;

const Modal = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
`;

const ModalContent = styled.div`
  background: #fff;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  text-align: center;

   h2 {
    font-weight: bold; /* 글씨를 굵게 만듭니다 */
  }
`;

const ModalButtonContainer = styled.div`
  display: flex;
  justify-content: space-around;
  margin-top: 20px;
`;

const StyledSelect = styled.select`
  padding: 10px;
  margin-top: 10px;
  border-radius: 5px;
  border: 1px solid #ccc;
  font-size: 16px;
`;

const ModalButton = styled.button`
  padding: 10px 20px;
  background-color: tomato;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  &:hover {
    background-color: #ff4b4b;
  }
`;
