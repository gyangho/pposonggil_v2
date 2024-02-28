const axios = require("axios");
const { decodePolyline } = require("./decodePolyline");

const options = {
  overview: false,
  steps: true,
};

async function GetWalk(sLat, sLon, eLat, eLon) {
  const url = `https://routing.openstreetmap.de/routed-foot/route/v1/foot/${sLat},${sLon};${eLat},${eLon}`;

  // 직접 build할때 사용, 실행시 매번 (https://ea51-211-201-168-202.ngrok-free.app) 변경해야함
  // 추후 개선 예정
  // const url = `https://ea51-211-201-168-202.ngrok-free.app/route/v1/foot/${sLat},${sLon};${eLat},${eLon}`;

  try {
    const response = await axios.get(url, { params: options });

    const { routes } = response.data;
    const { totalDistance, totalTime, steps } = {
      totalDistance: routes[0]?.distance || [],
      totalTime: routes[0]?.duration || [],
      steps: routes[0]?.legs[0]?.steps || [],
    };

    let totalWalkLanLon = [];
    for (const step of steps) {
      let walkLanLons = decodePolyline(step.geometry);
      for (const walkLanLon of walkLanLons) {
        totalWalkLanLon.push(walkLanLon);
      }
    }

    return totalWalkLanLon;
  } catch (err) {
    console.error(err);
  }
}

module.exports = {
  GetWalk: GetWalk,
};
