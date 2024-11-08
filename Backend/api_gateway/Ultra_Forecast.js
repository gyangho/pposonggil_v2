// 초단기예보
// 기온(T1H), 1시간 강수량(RN1), 하늘 상태(SKY), 습도(REH), 강수형태(PTY), 풍속(WSD)

const { response } = require("express");
const calculate = require("./cal_time_date.js");

const axios = require("axios");
const path = require("path");
const dotenv = require("dotenv");
dotenv.config({ path: path.join(__dirname, "Keys/.env") });

function fill_existData(existingData, category, fcstValue) {
  if (category === "RN1") {
    if (fcstValue === "강수없음") existingData[category] = 0;
    else if (fcstValue === "1.0mm 미만") existingData[category] = 1;
    else if (fcstValue === "30.0~50.0mm") existingData[category] = 30;
    else if (fcstValue === "50.0mm 이상") existingData[category] = 50;
    else existingData[category] = parseInt(fcstValue.replace("mm", ""));
  } else {
    existingData[category] = fcstValue;
  }
}

function fill_newData(data, category, fcstValue) {
  if (category === "RN1") {
    if (fcstValue === "강수없음") data[category] = 0;
    else if (fcstValue === "1.0mm 미만") data[category] = 1;
    else if (fcstValue === "30.0~50.0mm") data[category] = 30;
    else if (fcstValue === "50.0mm 이상") data[category] = 50;
    else data[category] = parseInt(fcstValue.replace("mm", ""));
  } else {
    data[category] = fcstValue;
  }
}

async function fill_nowCast(
  items,
  ultra_forecast_datas,
  cur_base_date,
  cur_base_time_str,
  input_x,
  input_y
) {
  let t1h, rn1, reh, wsd;
  items.forEach((item) => {
    // 기온(T1H), 1시간 강수량(RN1), 습도(REH), 풍속(WSD)
    if (item.category === "RN1") {
      if (item.obsrValue === "강수없음") rn1 = 0;
      else if (item.obsrValue === "1.0mm 미만") rn1 = 1;
      else if (item.obsrValue === "30.0~50.0mm") rn1 = 30;
      else if (item.obsrValue === "50.0mm 이상") rn1 = 50;
      else rn1 = parseInt(item.obsrValue.replace("mm", ""));
    } else if (item.category === "T1H") t1h = parseInt(item.obsrValue);
    else if (item.category === "REH") reh = parseInt(item.obsrValue);
    else if (item.category === "WSD") wsd = parseInt(item.obsrValue);
  });
  ultra_forecast_datas.push({
    Date: parseInt(cur_base_date),
    Time: cur_base_time_str,
    X: input_x,
    Y: input_y,
    RN1: rn1,
    T1H: t1h,
    REH: reh,
    WSD: wsd,
  });
}

function get_nextBasedate(next_time, prev_base_date) {
  if (next_time == 2400) {
    next_time = 0;
    prev_base_date = calculate.get_next_basedate(prev_base_date);
  }
  return { next_time, prev_base_date };
}

async function get_Ultra_Forecast_Data(input_date, input_time, input_x, input_y) {
  let {
    cur_time,
    cur_base_time, //  input_time과 가장 가까운 base_time
    prev_base_date,
  } = calculate.get_basetime_basedate(input_date, input_time, 60, 100);
  // 초단기예측 API제공시간 : 1시간 주기로 매 40분 --> time_to_add를 60, time_to_divide를 100으로 한다.
  const cur_base_time_str = cur_base_time.toString().padStart(4, "0");

  const queryParams = new URLSearchParams({
    serviceKey: `${process.env.PUBLIC_KEY}`,
    numOfRows: "100",
    dataType: "JSON",
    base_date: prev_base_date,
    base_time: cur_base_time_str,
    nx: input_x,
    ny: input_y,
  });
  const f_url = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst"; // 초단기예보 URL
  const n_url = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst"; // 초단기실황 URL
  let maxAttempts = 3; // 최대 시도 횟수
  const ultra_forecast_datas = [];

  // 0~40(분) -> 초단기예측
  // 13:30 검색 --> 13, 14, 15, 16, 17, 18 데이터 제공
  // 40~44(분) --> 초단기예측()
  // 13:42 검색 --> 14, 15, 16, 17, 18, 19 데이터 제공
  // 초단기실황 사용 못함(45분 이후 발표)
  if (input_time % 100 < 45) {
    for (let attempt = 0; attempt < maxAttempts; attempt++) {
      // 오류 발생시 try catch로 초단기예보API 최대 3번 재호출
      try {
        const f_response = await axios.get(f_url, { params: queryParams });
        const items = f_response.data.response.body.items.item;

        // 40~44(분)인 경우, cur_time + 100을 하여 1시간 뒤의 날씨 데이터들 받아옴
        if (40 <= input_time % 100 && input_time % 100 < 45) {
          cur_time = Math.floor(cur_time / 100) * 100 + 100;
          if (cur_time === 2400) cur_time = 0;
        }

        let next_time = cur_time;
        let check = 0,
          check2 = 0;

        for (const item of items) {
          const fcstTime = item.fcstTime;
          const category = item.category;
          const fcstValue = item.fcstValue;

          if (next_time === Number(fcstTime) && ["T1H", "RN1", "REH", "WSD"].includes(category)) {
            // 이미 존재하는 객체인지 확인
            const existingData = ultra_forecast_datas.find((data) => data.Time === fcstTime);
            if (existingData) fill_existData(existingData, category, fcstValue);
            else {
              if (check2 === 0 && fcstTime === "0000")
                // 첫 fcstTime이 0시일때 base_date+1
                prev_base_date = calculate.get_next_basedate(prev_base_date);
              check2++;

              // 새로운 객체를 생성하고 배열에 추가
              const data = { Date: prev_base_date, Time: fcstTime, X: input_x, Y: input_y };
              fill_newData(data, category, fcstValue);
              ultra_forecast_datas.push(data);
            }
            check++;
            if (check == 6) {
              next_time = cur_time;
              let get_next_time_date = get_nextBasedate(next_time, prev_base_date);
              next_time = get_next_time_date.next_time;
              prev_base_date = get_next_time_date.prev_base_date;
              check = 0;
            } else {
              next_time += 100; // 100 분을 더해서 다음 시간으로 이동
              let get_next_time_date = get_nextBasedate(next_time, prev_base_date);
              next_time = get_next_time_date.next_time;
              prev_base_date = get_next_time_date.prev_base_date;
            }
          } else {
            next_time = cur_time;
            let get_next_time_date = get_nextBasedate(next_time, prev_base_date);
            next_time = get_next_time_date.next_time;
            prev_base_date = get_next_time_date.prev_base_date;
            check = 0;
          }
        }
        return ultra_forecast_datas;
      } catch (error) {
        console.error(`forecast[${input_x}][${input_y}] Attempt[${attempt}] failed`, error);
        if (attempt >= maxAttempts - 1)
          console.error("forecast[${input_x}][${input_y}] Max attempts 초과");
      }
    }
  }

  // 45~59(분) --> 초단기실황 + 초단기예측
  // 13:50 검색 --> 13(초단기실황) + 14, 15, 16, 17, 18, 19(초단기예보)
  else {
    let {
      cur_base_time, //  input_time과 가장 가까운 base_time
      cur_base_date, //  input_date
      prev_base_date,
    } = calculate.get_basetime_basedate(input_date, input_time, 55, 100);
    // 초단기실황 API제공시간 : 1시간 주기로 매 45분 --> time_to_add를 55, time_to_divide를 100으로 한다.
    const cur_base_time_str = cur_base_time.toString().padStart(4, "0");

    const prevqueryParams = new URLSearchParams({
      serviceKey: `${process.env.PUBLIC_KEY}`,
      numOfRows: "20",
      dataType: "JSON",
      base_date: cur_base_date,
      base_time: cur_base_time_str,
      nx: input_x,
      ny: input_y,
    });
    const ultra_forecast_datas = [];

    for (let attempt = 0; attempt < maxAttempts; attempt++) {
      // 오류 발생시 try catch로 초단기실황API 최대 3번 재호출
      try {
        const n_response = await axios.get(n_url, { params: prevqueryParams });
        const items = n_response.data.response.body.items.item;
        await fill_nowCast(
          items,
          ultra_forecast_datas,
          cur_base_date,
          cur_base_time_str,
          input_x,
          input_y
        );
        break;
      } catch (error) {
        console.error(`nowcast[${input_x}][${input_y}] Attempt[${attempt}] failed`, error);
        if (attempt >= maxAttempts - 1) {
          console.error("nowcast[${input_x}][${input_y}] Max attempts 초과");
        }
      }
    }

    for (let attempt = 0; attempt < maxAttempts; attempt++) {
      // 오류 발생시 try catch로 초단기예보API 최대 3번 재호출
      try {
        const f_response = await axios.get(f_url, { params: queryParams });
        const items2 = f_response.data.response.body.items.item;

        cur_time = next_time;
        let check = 0;

        for (const item of items2) {
          const fcstTime = item.fcstTime;
          const category = item.category;
          const fcstValue = item.fcstValue;

          if (next_time === Number(fcstTime) && ["T1H", "RN1", "REH", "WSD"].includes(category)) {
            // 이미 존재하는 객체인지 확인
            const existingData = ultra_forecast_datas.find((data) => data.Time === fcstTime);

            if (existingData)
              // 이미 존재하는 객체라면 해당 프로퍼티를 추가
              fill_existData(existingData, category, fcstValue);
            else {
              // 새로운 객체를 생성하고 배열에 추가
              const data = { Date: prev_base_date, Time: fcstTime, X: input_x, Y: input_y };
              fill_newData(data, category, fcstValue);
              ultra_forecast_datas.push(data);
            }

            check++;
            if (check == 5) {
              // 초단기실황 정보가 있으니 5번만 반복
              next_time = cur_time;
              let get_next_time_date = get_nextBasedate(next_time, prev_base_date);
              next_time = get_next_time_date.next_time;
              prev_base_date = get_next_time_date.prev_base_date;
              check = 0;
            } else {
              next_time += 100; // 100 분을 더해서 다음 시간으로 이동
              let get_next_time_date = get_nextBasedate(next_time, prev_base_date);
              next_time = get_next_time_date.next_time;
              prev_base_date = get_next_time_date.prev_base_date;
            }
          } else {
            next_time = cur_time;
            let get_next_time_date = get_nextBasedate(next_time, prev_base_date);
            next_time = get_next_time_date.next_time;
            prev_base_date = get_next_time_date.prev_base_date;
            check = 0;
          }
        }
        return ultra_forecast_datas;
      } catch (error) {
        console.error(`forecast[${input_x}][${input_y}] Attempt[${attempt}] failed`, error);
        if (attempt >= maxAttempts - 1)
          console.error("forecast[${input_x}][${input_y}] Max attempts 초과");
      }
    }
  }
}

module.exports = {
  get_Ultra_Forecast_Data: get_Ultra_Forecast_Data,
};
