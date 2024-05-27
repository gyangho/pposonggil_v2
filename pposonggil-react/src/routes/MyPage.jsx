import React from "react";
import styled from "styled-components";

const Div = styled.div`
  width: 100%;
  height: 100%;
  background-color: #ffa659;
  display: flex;
  justify-content: center;
  align-items: center;
`;


function MyPage() {
  return (
    <React.Fragment>
      <Div>MyPage</Div>
    </React.Fragment>
  )
}

export default MyPage