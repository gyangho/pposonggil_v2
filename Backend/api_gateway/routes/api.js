const express = require("express");
const router = express.Router();
const path = require("path");
const dotenv = require("dotenv");
const axios = require("axios");

const { GetRoot } = require("../public_transport.js");
const { GetWalk } = require("../convertWalkData.js");

dotenv.config({ path: path.join(__dirname, "../Keys/.env") });

router.get("/POI", async (req, res) => {
  const input = req.query;
  const url = `https://dapi.kakao.com/v2/local/search/keyword.json?query=${encodeURIComponent(
    input.query
  )}`;
  const config = {
    headers: {
      Authorization: `KakaoAK ${process.env.KAKAO_REST_KEY}`,
    },
  };
  const apiResponse = await axios.get(url, config);
  const response = {
    documents: apiResponse.data.documents,
    status: apiResponse.status,
  };
  console.log(input.query);
  return res.send(response);
});

router.get("/map", async (req, res) => {
  const url = `https://dapi.kakao.com/v2/maps/sdk.js?appkey=${process.env.KAKAO_JAVASCRIPT_KEY}`;
  const response = await axios.get(url);
  return res.send(response.data);
});

router.get("/Odsay", async (req, res) => {
  const endPoint = req.query;
  console.log(endPoint);
  const Routes = await GetRoot(
    endPoint.start_lon,
    endPoint.start_lat,
    endPoint.end_lon,
    endPoint.end_lat
  );
  return res.send(Routes);
});

router.get("/osrm", async (req, res) => {
  const { sLat, sLon, eLat, eLon } = req.query;
  const walkData = await GetWalk(sLat, sLon, eLat, eLon);

  return res.send(walkData);
});

module.exports = router;
