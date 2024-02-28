const express = require("express");
const router = express.Router();
const path = require("path");
const fsPromises = require("fs").promises;

const { renderWeatherPage } = require("../weatherHandler");
const { calwalkWeather } = require("../walkDataHandler");
const { createDynamicHTML } = require("../result");
const authCheck = require("../authCheck.js");

// 메인 페이지
router.get("/", async (req, res, next) => {
  if (!authCheck.isOwner(req, res)) {
    // 로그인 안되어있으면 로그인 페이지로 이동시킴
    res.redirect("/auth/login");
    // return false;
    return;
  }
  const filePath = path.join(__dirname, "../views/map.html");
  try {
    const data = await renderWeatherPage(filePath);
    return res.send(data);
  } catch (error) {
    next(error);
  }
});

// 마이페이지
router.get("/mypage", async (req, res) => {
  const loggedInUsername = req.session.is_logined ? req.session.nickname : "Guest";
  const filePath = path.join(__dirname, "../views/mypage.html");
  const fileContent = await fsPromises.readFile(filePath, "utf-8");

  try {
    const modifiedData = fileContent.replace(/{loggedInUsername}/g, loggedInUsername);
    return res.send(modifiedData);
  } catch (error) {
    next(error);
  }
});

// 북마크
router.get("/mypage/bookmark", async (req, res) => {
  const filePath = path.join(__dirname, "../views/bookmark.html");
  let Routes = {};
  res.send("수정예정");
  // try {
  //   Routes = await transport.getPublicTransport(126.9961, 37.5035, 126.96, 37.4946, 202307291200); //신반포역->정보관
  //   if (Routes.length < 1) {
  //     //경로없음
  //     res.send("경로없음");
  //   } else {
  //   }
  // } catch (error) {
  //   console.error(error);
  // }
  // let sRoutes = JSON.stringify(Routes[0]);
  // let mRoutes = sRoutes.replace(/"/g, "@@");
  // fs.readFile(filePath, "utf8", (err, data) => {
  //   if (err) {
  //     res.status(500).send("Error reading file");
  //   } else {
  //     // 외부 HTML 파일 내부의 {T#}을 대체하여 전송
  //     const modifiedData = data.replace(/{{R}}/g, mRoutes);
  //     res.send(modifiedData);
  //   }
  // });
});

// 장소 검색
router.get("/POI", async (req, res) => {
  const filePath = path.join(__dirname, "../views/mainFunc.html");
  try {
    const fileContent = await fsPromises.readFile(filePath, "utf-8");
    return res.send(fileContent);
  } catch (error) {
    next(error);
  }
});

// 검색 결과
router.post("/POI/result", async (req, res, next) => {
  let resource = req.body;
  try {
    const html = await createDynamicHTML(resource);
    return res.send(html);
  } catch (error) {
    console.error("Error handling request:", error);
    res.status(500).send("Internal Server Error");
  }
});

// 뽀송타임
router.get("/POI/result/pposong", async (req, res, next) => {
  try {
    const filePath = path.join(__dirname, "../views/pposong.html");
    const data = await renderWeatherPage(filePath);
    return res.send(data);
  } catch (error) {
    next(error);
  }
});

// 뽀송타임 계산
router.post("/POI/result/pposong/cal", async (req, res) => {
  let receivedData = req.body.WalkData;
  try {
    const resultData = await calwalkWeather(receivedData);
    const strresult = JSON.stringify(resultData);
    return res.send(strresult);
  } catch (error) {
    next(error);
  }
});

module.exports = router;
