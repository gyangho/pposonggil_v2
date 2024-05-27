import React, { useState, useCallback, useEffect } from "react";
import styled from "styled-components";
import { motion } from "framer-motion";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faHouse, faCloud, faUmbrella, faBookmark, faUser, faRoute } from "@fortawesome/free-solid-svg-icons";
import { useRecoilState } from "recoil";
import { navState } from "../recoil/atoms";
import { Link } from 'react-router-dom';

const Nav = styled.div`
  outline: none;
  display: flex;
  flex-direction: column;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  width: 100%;
  height: 70px;
  background-color: white;
  z-index: 1000;
  box-shadow: 0px -2px 4px rgba(0, 0, 0, 0.1);
`;

const Items = styled.ul`
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 70px;
  background-color: white;
  padding: 0px 5px;
  position: relative;
`;

const Item = styled(motion.li)`
  width: 20%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  margin: 0px 5px;
  position: relative;
`;

const NavIcon = styled(motion.div)`
  font-size: 25px;
  color: ${(props) => (props.isActive ? "#003E5E" : "#4a4a4a")};
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
`;

const NavStateBar = styled(motion.div)`
  width: 100%;
  height: 4px;
  background-color: #003E5E;
  /* background-color: #41bbf8; */
  position: absolute;
  top: 0px;
`;

function Navigation() {
  const [nav, setNav] = useRecoilState(navState);
  const [activeIndex, setActiveIndex] = useState(0);

  useEffect(() => {
    const index = items.findIndex(item => item.nav === nav);
    if (index !== -1) {
      setActiveIndex(index);
    }
  }, [nav]);

  const items = [
    { nav: "home", to: "/", icon: faHouse },
    { nav: "search", to: "/search", icon: faRoute },
    { nav: "market", to: "/market", icon: faUmbrella },
    { nav: "bookmark", to: "/bookmark", icon: faBookmark },
    { nav: "mypage", to: "/mypage", icon: faUser },
  ];

  const onClick = useCallback((index) => {
    setNav(items[index].nav);
  }, [setNav]);

  return (
    <Nav>
      <Items>
        {items.map((item, index) => (
          <Item key={item.nav}>
              <Link to={item.to} style={{ textDecoration: 'none', width: "100%", height: "100%" }}>
                <NavIcon
                  whileTap={{ scale: 0.85 }}
                  onClick={() => onClick(index)}
                  isActive={index === activeIndex}
                >
                  <FontAwesomeIcon icon={item.icon} />
                </NavIcon>
              </Link>
            {index === activeIndex && <NavStateBar layout layoutId="stateBar" activeIndex={activeIndex} />}
          </Item>
        ))}
      </Items>
    </Nav>
  );
}

export default Navigation;
