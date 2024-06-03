import { atom } from "recoil";

//맵 중심 위치 주소 정보
export const mapCenterState = atom({
  key: "mapCenter",
  default: {
    depth2: "", //구
    depth3: "", //동
  }
})

// 마크업 주소 정보 (도로명 주소 있는 곳만 클릭 가능하게 해놔서 도로명 주소 저장)
export const addressState = atom({
  key: "clickedAddr",
  default: { 
    depth2: "", // 구
    depth3: "", // 동
    addr: "", // 지번
    roadAddr: "", // 도로명
    lat: "", // 위도
    lon: "", // 경도
  }
});

// 햔재 위치 주소 정보
export const currentAddressState = atom({
  key: "curAddr",
  default: { 
    depth2: "", // 구
    depth3: "", // 동
    addr: "", // 지번
    roadAddr: "", // 도로명
    lat: "", // 위도
    lon: "", // 경도
  }
})

//현재 위치 추적 버튼 활성화 상태 추적 atom
export const locationBtnState = atom({
  key: "LocationBtnState",
  default: false,
});

//마커 활성화 상태 추적 atom
export const markerState = atom({
  key: "markerState",
  default: false,
});

// 격자 활성화 상태 추적 atom
export const gridState = atom({
  key: "gridState",
  default: false,
})

//현재 네비게이션 위치 상태 추적 atom
export const navState = atom({
  key: "navState",
  default: "home",
})

//검색 장소 정보 저장 atom
export const searchPlace = atom({
  key: "searchPlace",
  default: {
    place_name: "",
    category_group_name: "",
    address_name: "",
    road_address_name: "",
    phone: "",
    lat: "",
    lon: "",
  }
})

//경로의 출발지 도착지 저장 atom
export const routeInfoState = atom({
  key: 'routeInfo',
  default: {
    origin: [{ 
      name: '', 
      lat: '', 
      lon: '',
    }],
    dest: [{ 
      name: '', 
      lat: '', 
      lon: '',
    }]
  }
});

//서버에서 받아온 사용자 정보 저장 atom
export const userState = atom({
  key: 'userState',
  default: null,
});
