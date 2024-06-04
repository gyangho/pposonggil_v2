import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import styled from 'styled-components';

import RouteDetail from './routes/RouteDetail';
import SearchRoutes from './routes/SearchRoutes';
import SearchPlace from './routes/SearchPlace';
import Search from './routes/Search';
import Home from './routes/Home';
import Login from './routes/Login';
import Header from './layouts/Header';
import Navigation from './layouts/Navigation';

import MyPage from './routes/MyPage';
import LogIn from './routes/Login';
import KakaoRedirect from './components/KakaoRedirect';
import Bookmark from './routes/Bookmark';

/* 중고 우산 거래 */
import Board from './routes/market/Board';
import Post from './routes/market/Post';
import Posting from './routes/market/Posting';
import Chat from './routes/market/Chat';
import TransactionSchedule from './routes/market/TransactionSchedule';

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
          <Route path="/bookmark" element={<Bookmark/>} />
          <Route path="/mypage" element={<MyPage/>} />

          
          <Route path="/market/schedule" element={<TransactionSchedule />} />
          <Route path="/market/chat/:author"  exact    element={<Chat />} />         
          <Route path="/market/post/:boardId" exact    element={<Post />} />
          <Route path="/market/posting" element={<Posting/>} />
          <Route path="/market" element={<Board />} />

          {/* <Route path="/search/routes/route-detail" element={<RouteDetail />} /> */}
          <Route path="/search/detail" element={<RouteDetail />} />

          <Route path="/search/routes" element={<SearchRoutes />} />
          <Route path="/search/place" element={<SearchPlace />} />
          <Route path="/search" element={<Search />} />
          
          <Route path="/home" element={<Home />} />

          <Route path="/login" element={<LogIn />} />
          <Route path="/oauth/kakao/callback" element={<KakaoRedirect />} />
          {/* 로그인 페이지로 수정 필요 */}
          <Route path="/" element={<Home />} />

        </Routes>      
      </Wrapper>
      <Navigation />
    </Router>
  );
}
export default App;
