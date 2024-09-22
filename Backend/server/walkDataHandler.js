const db = require("./db.js");

async function calwalkWeather(receivedData) {
  let resultData = [];
  try {
    const connection = await db();
    for (const walkData of receivedData) {
      const sectionData = [];
      let sum_RN1 = 0;
      for (const section of walkData) {
        let [weatherData] = await query(
          "SELECT * FROM FORECAST WHERE TIME = ? AND X = ? AND Y =  ?",
          [section.basetime, section.X, section.Y]
        );
        const section_RN1 = (Number(weatherData[0].RN1) * section.sectiontime) / 60;
        sum_RN1 += section_RN1;
        sectionData.push({
          DATE: weatherData[0].DATE,
          REH: weatherData[0].REH,
          RN1: weatherData[0].RN1,
          T1H: weatherData[0].T1H,
          TIME: weatherData[0].TIME,
          WSD: weatherData[0].WSD,
          X: weatherData[0].X,
          Y: weatherData[0].Y,
          section_RN1: section_RN1.toString(),
        });
      }
      const WalkWeatherData = {
        sum_RN1: sum_RN1.toString(),
        walkData: sectionData,
      };
      resultData.push(WalkWeatherData);
    }
    connection.destroy();
  } catch (error) {
    console.error("pposong/cal 에러: " + error);
  }
  return resultData;
}

module.exports = {
  calwalkWeather: calwalkWeather,
};
