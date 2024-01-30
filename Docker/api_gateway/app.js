const express = require("express");
const https = require("https");
const dotenv = require("dotenv");
const path = require("path");
const fs = require("fs");
const fsPromises = require("fs").promises;
const bodyParser = require("body-parser");
const axios = require("axios");
const cors = require("cors");
const morgan = require("morgan");

const schedule = require("node-schedule");
const forecast = require("./Ultra_Forecast.js");
const td = require("./cal_time_date.js");
const db = require("./db.js");
const { GetRoot } = require("./public_transport.js");
// const { connect } = require("http2");
// const { Connection } = require("mysql2/typings/mysql/lib/Connection.js");

dotenv.config({ path: path.join(__dirname, "Keys/.env") });

const options = {
  key: fs.readFileSync(path.join(__dirname, "Keys/localhost-key.pem")),
  cert: fs.readFileSync(path.join(__dirname, "Keys/localhost.pem")),
};

const app = express();
const port = 8889;
// app.set("port", 8888);

app.use(morgan("dev"), express.static(path.join(__dirname, "public")));

app.use(cors());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

const locArr = [
  [61, 129],
  [62, 129],
  [59, 128],
  [60, 128],
  [61, 128],
  [62, 128],
  [57, 127],
  [58, 127],
  [59, 127],
  [60, 127],
  [61, 127],
  [62, 127],
  [63, 127],
  [57, 126],
  [58, 126],
  [59, 126],
  [60, 126],
  [61, 126],
  [62, 126],
  [63, 126],
  [57, 125],
  [58, 125],
  [59, 125],
  [60, 125],
  [61, 125],
  [62, 125],
  [63, 125],
  [58, 124],
  [59, 124],
  [61, 124],
];

const httpsServer = https.createServer(options, app);

httpsServer.listen(port, () => {
  console.log(`Example app listening on port ${port}`);
  scheduleUpdate();
});

app.get("/api/POI", async (req, res) => {
  input = req.query;
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
  res.send(response);
});

app.get("/api/map", async (req, res) => {
  const url = `https://dapi.kakao.com/v2/maps/sdk.js?appkey=${process.env.KAKAO_JAVASCRIPT_KEY}`;
  const response = await axios.get(url);
  res.send(response.data);
});

app.get("/api/Odsay", async (req, res) => {
  const endPoint = req.query;
  console.log(endPoint);
  const Routes = await GetRoot(
    endPoint.start_lon,
    endPoint.start_lat,
    endPoint.end_lon,
    endPoint.end_lat
  );
  res.send(Routes);
});

function scheduleUpdate() {
  schedule.scheduleJob("30 0,10,20,30,40,50 * * * *", async function () {
    try {
      const connection = await db();
      const input_date = td.getTimeStamp(1);
      const input_time = td.getTimeStamp(2);
      const promises = [];
      const HH = input_time.toString().substring(0, 2);
      const MM = input_time.toString().substring(2);
      const time = input_time.toString().substring(0, 2) + "00";

      console.log("Time: " + time);
      console.log("________________________________");
      console.log(`Forecast Updating Started[${HH}:${MM}]`);
      console.time(`Forecast Update[${HH}:${MM}] 소요시간`);

      for (let i = 0; i < 30; i++) {
        try {
          const input_x = locArr[i][0];
          const input_y = locArr[i][1];
          const Data = forecast.get_Ultra_Forecast_Data(input_date, input_time, input_x, input_y);
          promises.push(Data);
        } catch (error) {
          console.log("ERROR MERGED");
          console.error(error);
          i--;
        }
      }

      const ultra_forecast_datas = await Promise.all(promises);
      console.log(`Promise End([${HH}:${MM}]날씨 데이터)`);

      //40~44(분)인 경우, 현재 시간의 날씨 데이터를 사용해야 한다.
      if (40 <= input_time % 100 && input_time % 100 <= 44) {
        for (let i = 0; i < 30; i++) {
          for (let j = 0; j < 5; j++) {
            try {
              const data = {
                DATE: `${ultra_forecast_datas[i][j].Date}`,
                TIME: `${ultra_forecast_datas[i][j].Time}`,
                X: `${ultra_forecast_datas[i][j].X}`,
                Y: `${ultra_forecast_datas[i][j].Y}`,
                RN1: `${ultra_forecast_datas[i][j].RN1}`,
                T1H: `${ultra_forecast_datas[i][j].T1H}`,
                REH: `${ultra_forecast_datas[i][j].REH}`,
                WSD: `${ultra_forecast_datas[i][j].WSD}`,
                UPTIME: `${input_time}`,
              };
              const [results] = await connection.query("INSERT INTO forecast SET ?", [data]);
            } catch (error) {
              console.error(error);
            }
          }
        }

        //현재 기상정보가 넘어오지 않기 때문에 UPTIME만 수정해준다.
        try {
          let [results] = await connection.query("UPDATE forecast SET UPTIME = ? WHERE TIME = ?", [
            input_time,
            time,
          ]);
        } catch (error) {
          console.error(error);
        }
      } else {
        for (let i = 0; i < 30; i++) {
          for (let j = 0; j < 6; j++) {
            try {
              const data = {
                DATE: `${ultra_forecast_datas[i][j].Date}`,
                TIME: `${ultra_forecast_datas[i][j].Time}`,
                X: `${ultra_forecast_datas[i][j].X}`,
                Y: `${ultra_forecast_datas[i][j].Y}`,
                RN1: `${ultra_forecast_datas[i][j].RN1}`,
                T1H: `${ultra_forecast_datas[i][j].T1H}`,
                REH: `${ultra_forecast_datas[i][j].REH}`,
                WSD: `${ultra_forecast_datas[i][j].WSD}`,
                UPTIME: `${input_time}`,
              };
              const [results] = await connection.query("INSERT INTO forecast SET ?", [data]);
            } catch (error) {
              console.error(error);
            }
          }
        }
      }

      // input_time이 다른 모든 데이터 다 삭제
      try {
        const [results] = await connection.query("DELETE FROM forecast WHERE UPTIME != ?", [
          input_time,
        ]);
      } catch (error) {
        console.error(error);
      }
      connection.destroy();
      console.log("DONE!(DB forecast update)");
      console.timeEnd(`Forecast Update[${HH}:${MM}] 소요시간`);
    } catch (error) {
      console.error(error);
    }
  });
}
