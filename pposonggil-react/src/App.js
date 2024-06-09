import React, {useEffect, useState} from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import styled from 'styled-components';

import TransactionStatus from './routes/TransactionStatus';
import RouteDetail from './routes/RouteDetail';
import ChooseRoute from './routes/ChooseRoute';
import SearchRoutes from './routes/SearchRoutes';
import SearchPlace from './routes/SearchPlace';
import Search from './routes/Search';
import Home from './routes/Home';
import Header from './layouts/Header';
import Navigation from './layouts/Navigation';

import MyPage from './routes/MyPage';
import Bookmark from './routes/Bookmark';
import Login from './routes/Login';

/* 중고 우산 거래 */
import Board from './routes/market/Board';
import Post from './routes/market/Post';
import Posting from './routes/market/Posting';
import Chat from './routes/market/Chat';
import TransactionSchedule from './routes/market/TransactionSchedule';


/* (By 채수아)새로 추가된 컴포넌트 */
import MemberPosting from './routes/MemberPosting';
import MemberPostingDetailed from './routes/MemberPostingDetailed';
import EditPost from './routes/market/EditPost';


const Wrapper = styled.div`
  top: 70px;
  bottom: 70px;
  right:0;
  left:0;
  position: fixed;
`;

function App() {

  return (
    <Router>
      <Header />
      <Wrapper>
        <Routes>

          {/* 멤버가 작성한 게시글 페이지 추가 */}
          <Route path="/member-posting/:writerId" element={<MemberPosting />} />{/* 특정 멤버가 작성한 게시글 목록들 */}
          <Route path="/member-posting/post/:boardId" element={<MemberPostingDetailed />} />{/* 멤버 게시글 상세 */}
          <Route path="/market/edit-post/:boardId" element={<EditPost />} /> {/* 새로운 EditPost 경로 추가 */}

          <Route path="/market/schedule" element={<TransactionSchedule />} />
          {/* <Route path="/market/chat/:author"  exact    element={<Chat />} />          */}
          <Route path="/market/chat/:chatRoomId" element={<Chat />} /> {/* :author를 :nickname으로 변경 */}
          <Route path="/market/post/:boardId" exact element={<Post />} />
          <Route path="/market/posting" element={<Posting />} />
          <Route path="/market" element={<Board />} />

          {/* 구매자~거래장소~판매자 위치 및 거래 진행 현황 페이지 */}
          <Route path="/transaction" element={<TransactionStatus />} />

          <Route path="/search/detail" element={<RouteDetail />} />
          <Route path="/search/choose" element={<ChooseRoute />} />
          <Route path="/search/routes" element={<SearchRoutes />} />
          <Route path="/search/place" element={<SearchPlace />} />
          <Route path="/search" element={<Search />} />

          <Route path="/bookmark" element={<Bookmark />} />
          <Route path="/mypage" element={<MyPage />} />

          <Route path="/home" element={<Home />} />

          <Route path="/login" element={<Login/>} />
          {/* 로그인 페이지로 수정 필요 */}
          <Route path="/" element={<Home />} />

        </Routes>      
      </Wrapper>
      <Navigation />
    </Router>
  );
}
export default App;
