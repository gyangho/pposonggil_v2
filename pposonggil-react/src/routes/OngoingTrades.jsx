import React, { useEffect, useState } from "react";
import styled from "styled-components";
import { useNavigate } from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faUmbrella } from "@fortawesome/free-solid-svg-icons";
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
  font-weight: bold; /* 글씨를 굵게 */
  font-size:20px;
`;

const TradeList = styled.div`
  width: 80%;
  margin-top: 20px;
  background-color: white;
  border-radius: 10px;
  padding: 10px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
`;

const TradeItem = styled.div`
background-color: #f9f9f9;
padding: 20px;
border-radius: 10px;
margin-bottom: 10px;
box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
display: flex;
flex-direction: column;

&:last-child {
  margin-bottom: 0;
}

div {
  margin-bottom: 8px;
  font-size: 16px;

  &:last-child {
    margin-bottom: 0;
  }
}
`;

const Highlight = styled.span`
  font-weight: bold;
  color: #007BFF;
`;

const BoldLabel = styled.span`
  font-weight: bold;
`;

const NoTradesMessage = styled.div`
  margin-top: 20px;
  font-size: 18px;
  color: #555;
  font-weight: bold; /* 글씨를 굵게 */
`;



const ReservedTrades = () => {
  const navigate = useNavigate();
  const [trades, setTrades] = useState([]);
  const myId = 1; // 실제 사용자의 ID로 대체

  useEffect(() => {
    const fetchTrades = async () => {
      try {
        const response = await api.get(`http://localhost:8080/api/trades/by-member/${myId}`);
        setTrades(response.data);
      } catch (error) {
        console.error('Error fetching trades:', error);
      }
    };

    fetchTrades();
  }, [myId]);

  const handleTradeClick = (tradeId) => {
    navigate(`/tradeDetailed/${tradeId}`);//여기 나중에 거래 화면 페이지로 이동
  };


  return (
    <Container>
      <Title>현재 진행중인 중고우산 거래  <FontAwesomeIcon icon={faUmbrella} /></Title>
      {trades.length > 0 ? (
        <TradeList>
          {trades.map(trade => (
            <TradeItem key={trade.tradeId} onClick={() => handleTradeClick(trade.tradeId)}>
              <div><BoldLabel>거래 ID:</BoldLabel> <Highlight>{trade.tradeId}</Highlight></div>
              <div><BoldLabel>거래 상대:</BoldLabel><Highlight>{trade.objectNickName}</Highlight></div>
              <div><BoldLabel>거래 시간:</BoldLabel> <Highlight>{trade.startTimeString} - {trade.endTimeString}</Highlight></div>
              <div><BoldLabel>거래 장소:</BoldLabel> <Highlight>{trade.address.name}</Highlight></div>
            </TradeItem>
          ))}
        </TradeList>
      ) : (
        <NoTradesMessage>예약된 거래가 없습니다.</NoTradesMessage>
      )}
    </Container>
  );
};

export default ReservedTrades;
