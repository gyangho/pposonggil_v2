import React, { useEffect, useState } from "react";
import styled from "styled-components";
import { useRecoilState, useSetRecoilState } from "recoil";
import { bookmarkRouteState, navState, routeInfoState } from "../recoil/atoms";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faArrowRight, faBookBookmark, faRoute } from "@fortawesome/free-solid-svg-icons";
import { faSquareMinus } from "@fortawesome/free-regular-svg-icons";

import { useNavigate } from "react-router-dom";




function Bookmark() {
  const [bookmarkList, setBookmarkList] = useState([]);
  const [route, setRoute] = useRecoilState(routeInfoState);
  const navigate = useNavigate();
  const setNav = useSetRecoilState(navState);

  useEffect(() => {
    setNav("bookmark");
  }, []);

  // 페이지가 로드될 때 로컬 스토리지에서 북마크 불러와서 설정
  useEffect(() => {
    const storedBookmark = loadBookmarkFromLocalStorage();
    setBookmarkList(storedBookmark);
  }, []);

  // 로컬 스토리지에서 북마크 불러오기
  const loadBookmarkFromLocalStorage = () => {
    const storedBookmark = localStorage.getItem('bookmark');
    return storedBookmark ? JSON.parse(storedBookmark) : [];
  };

  const onBookmarkedRouteClick = (bookmark) => { //북마크 경로 선택시 경로 탐색으로 이동 핸들러
    console.log('클릭한 route:', bookmark);
    setRoute({
      origin: [bookmark.origin],
      dest: [bookmark.dest],
    });    
    navigate("/search/routes");
  };

  const onRemoveBtnClick = (bookmark) => {
    // const updatedBookmarks = bookmarkList.filter(bm => bm !== bookmark);
    const updatedBookmarks = bookmarkList.filter(bm => 
      !(bm.origin.name === bookmark.origin.name && bm.dest.name === bookmark.dest.name)
    );
    setBookmarkList(updatedBookmarks);
    saveBookmarkToLocalStorage(updatedBookmarks);
  };
  
  const saveBookmarkToLocalStorage = (bookmarks) => {
    localStorage.setItem('bookmark', JSON.stringify(bookmarks));
  };

  
  return (
    <React.Fragment>
      <Title>
        <BookmarkIcon icon={faBookBookmark}/>
        저장한 경로 <p>{bookmarkList.length}</p>
      </Title>
      <Wrapper>
        {bookmarkList.map((bookmark, index) => (
          <Route key={index}>
            <Column onClick={()=>onBookmarkedRouteClick(bookmark)}>
              <Icon icon={faRoute}></Icon>
              <Content>
                {bookmark.origin.name}
                <FontAwesomeIcon icon={faArrowRight} style={{padding: "0px 10px"}}/>
                {bookmark.dest.name}
              </Content>
            </Column>
            <Column>
              <RemoveIcon icon={faSquareMinus} onClick={()=>onRemoveBtnClick(bookmark)}></RemoveIcon>
            </Column>
          </Route>
        ))}
      </Wrapper>
    </React.Fragment>
  )
}

export default Bookmark

const Wrapper = styled.div`
  width: 100%;
  height: 100%;
  background-color: whitesmoke;
  display: block;
  padding: 20px;
  overflow-y: scroll;
`;

const Route = styled.div`
  width: 100%;
  height: 60px;
  background-color: white;
  border-top-right-radius: 30px;
  border-bottom-right-radius: 30px;
  padding: 10px;
  font-size: 16px;
  font-weight: 700;
  display: flex;
  margin-bottom: 20px;
  cursor: pointer;
  box-shadow: 0px 0px 5px 3px rgba(109, 109, 109, 0.15);
  &:hover {
    background-color: #bbe8ff1e;
    border: 2.5px solid #003E5E;
  }
`;

const Title = styled.div`
  width: 100%;
  height: 100px;
  background-color: #003E5E;
  z-index: 100;
  font-size: 30px;
  font-weight: 300;
  padding: 20px 30px;
  display: flex;
  justify-content: start;
  align-items: center;
  color: white;
  font-family: 'Bagel Fat One', cursive;
  p {
    padding: 0px 10px;
    font-weight: 900;
  }
`;

const BookmarkIcon = styled(FontAwesomeIcon)`
  font-size: 30px;
  padding-right: 15px;
  color: #FFCE1F;
`;

const Column = styled.div`
  justify-content: start;
  align-items: center;
  height: 100%;
  cursor: pointer;
  &:first-child {
    width: 90%;
    min-width: 10%;
    padding-right: 5px;
    display: flex;
  }
  &:last-child {
    width: 10%;
  }
`;

const Content = styled.div`
  padding-left: 10px;
`;

const Icon = styled(FontAwesomeIcon)`
  padding: 10px;
  font-size: 15px;
  border-radius: 50%;
  &:first-child {
    background-color: #003E5E;
    color: whitesmoke;
  }
`;

const RemoveIcon = styled(FontAwesomeIcon)`
  padding: 5px;
  font-size: 25px;
  font-weight: 900;
  color: #003E5E;
  &:hover {
    color: tomato;
  }
`;
