const db = require("./db.js");

// 예제: 프로미스 패턴 사용
async function get6WeatherData(x, y) {
  try {
    const connection = await db();
    let [results] = await connection.query("SELECT * FROM forecast WHERE X = ? AND Y = ?", [x, y]);
    connection.destroy();
    return results;
  } catch (error) {
    console.error(error);
  }
}

// 예제: 프로미스 패턴 사용
async function get1WeatherData(x, y, time) {
  try {
    const connection = await db();
    let [results] = await connection.query(
      "SELECT * FROM forecast WHERE X = ? AND Y = ? AND TIME = ?",
      [x, y, time]
    );
    connection.destroy();
    return results;
  } catch (error) {
    console.error(error);
  }
}

module.exports = {
  get6WeatherData: get6WeatherData,
  get1WeatherData: get1WeatherData,
};
