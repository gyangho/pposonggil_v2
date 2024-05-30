import React, { useEffect, useState } from "react";
import styled from "styled-components";

import Header from "../layouts/Header";
import Navigation from "../layouts/Navigation";

const Div = styled.div`
  width: 100%;
  height: 100%;
  background-color: #face7b;
  display: flex;
  justify-content: center;
  align-items: center;
`;


function Market() {
  return (
    <React.Fragment>
      <Div>Market</Div>
    </React.Fragment>
  )
}

export default Market
