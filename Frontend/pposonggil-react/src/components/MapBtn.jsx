import React from 'react';
import styled from 'styled-components';
import { motion } from 'framer-motion';
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faLocationCrosshairs, faSpinner, faBorderAll, faCloudShowersHeavy } from "@fortawesome/free-solid-svg-icons";

const MapBtn = ({
  isGridActive,
  isGridLoading,
  isLocationLoading,
  activeTracking,
  handleGridBtn,
  handleLocationBtn
}) => {
  return (
    <BtnContainer>
      {isGridActive && (
        <RainBtn
          whileTap={{ scale: 0.7 }}
          initial={{ y: 50, opacity: 0 }}
          animate={{ y: 0, opacity: 1 }}
          exit={{ y: 50, opacity: 0 }}
          transition={{ duration: 0.4 }}
        >
          <Icon icon={faCloudShowersHeavy} />
        </RainBtn>
      )}
      <GridBtn
        whileTap={{ scale: 0.9 }}
        isGridLoading={isGridLoading}
        onClick={handleGridBtn}
        style={{ backgroundColor: isGridActive ? "#ffdf6a" : "white" }}
      >
        <Icon
          icon={isGridLoading ? faSpinner : faBorderAll}
          style={{ color: "#006aa3" }}
        />
      </GridBtn>

      <LocationBtn
        onClick={handleLocationBtn}
        isLocationLoading={isLocationLoading}
        whileTap={{ scale: 0.9 }}
      >
        <Icon
          icon={isLocationLoading ? faSpinner : faLocationCrosshairs}
          style={{ color: activeTracking ? "tomato" : "#216CFF" }}
          initial={{ rotate: 0 }}
          animate={{ rotate: isLocationLoading ? 360 : 0 }}
          transition={{ duration: 1, repeat: isLocationLoading ? Infinity : 0 }}
        />
      </LocationBtn>
    </BtnContainer>
  );
};

export default MapBtn;

const BtnContainer = styled.div`
  z-index: 100;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  align-items: flex-end;
  bottom: 20px;
  right: 20px;
  position: absolute;
`;

const LocationBtn = styled(motion.button)`
  all: unset;
  margin-top: 15px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  right: 0;
  bottom: 0;
  z-index: 100;
  position: sticky;
  border-radius: 50%;
  background-color: white;
  padding: 9px;
  box-shadow: 0px 0px 3px 3px rgba(0, 0, 0, 0.1);
  cursor: ${props => (props.isLocationLoading ? 'not-allowed' : 'pointer')};
`;

const GridBtn = styled(LocationBtn)`
  cursor: ${props => (props.isGridLoading ? 'not-allowed' : 'pointer')};
`;

const Icon = styled(motion(FontAwesomeIcon))`
  width: 22px;
  height: 22px;
  transition: color 0.2s ease;
`;

const RainBtn = styled(motion(LocationBtn))`
  color: #7ccdff;
  background-color: #424040d2;
  cursor: pointer;
`;
