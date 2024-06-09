import React, { useEffect, useState } from 'react';
// import axios from 'axios';
import styled from 'styled-components';
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faBan } from "@fortawesome/free-solid-svg-icons";
import api from "../api/api";

const Container = styled.div`
  width: 100%;
  height: 100%;
  background-color: #E2E2E2;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 20px;
`;

// const Title = styled.h2`
//   margin-bottom: 20px;
// `;
const Title = styled.h1`
  margin-bottom: 20px;
  color: #333;
  font-weight: bold; /* 글씨를 굵게 */
  font-size:20px;
`;

// const BlockListform = styled.ul`
//   width: 80%;
//   list-style-type: none;
//   padding: 0;
// `;
const BlockListform = styled.ul`
  width: 80%;
  margin-top: 20px;
  background-color: white;
  border-radius: 10px;
  padding: 10px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
`;

const BlockItem = styled.li`
  width: 100%;
  padding: 10px;
  background-color: white;
  margin-bottom: 10px;
  border-radius: 5px;
  box-shadow: 0 0 5px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
    font-weight: bold; /* 글씨를 굵게 */
`;

const UnblockButton = styled.button`
  padding: 5px 10px;
  background-color: tomato;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  &:hover {
    background-color: darkred;
  }
`;

const NoTradesMessage = styled.div`
  margin-top: 20px;
  font-size: 18px;
  color: #555;
  font-weight: bold; /* 글씨를 굵게 */
`;

// const myId = localStorage.getItem('id');

function BlockList() {
    const [blockedUsers, setBlockedUsers] = useState([]);

    useEffect(() => {
        const fetchBlockedUsers = async () => {
            try {
                const myId = localStorage.getItem('id'); // 실제 사용자의 ID로 대체해야 합니다.
                const response = await api.get(`http://localhost:8080/api/blocks/by-subject/${myId}`);
                setBlockedUsers(response.data);
            } catch (error) {
                console.error('차단 목록을 불러오는 중 오류 발생:', error);
            }
        };

        fetchBlockedUsers();
    }, []);

    const handleUnblock = async (blockId) => {
        try {
            await api.delete(`http://localhost:8080/api/block/${blockId}`);
            setBlockedUsers(blockedUsers.filter(block => block.blockId !== blockId));
        } catch (error) {
            console.error('차단 해제 중 오류 발생:', error);
        }
    };

    return (
        <Container>
            <Title>내가 차단한 차단 목록 <FontAwesomeIcon icon={faBan} /></Title>
            {blockedUsers.length === 0 ? (
                <NoTradesMessage>차단한 사람이 없습니다.</NoTradesMessage>
            ) : (
                <BlockListform>
                    {blockedUsers.map((block) => (
                        <BlockItem key={block.blockId}>
                            {block.objectNickName}
                            <UnblockButton onClick={() => handleUnblock(block.blockId)}>
                                차단 해제
                            </UnblockButton>
                        </BlockItem>
                    ))}
                </BlockListform>
            )}
        </Container>
    );
}

export default BlockList;
