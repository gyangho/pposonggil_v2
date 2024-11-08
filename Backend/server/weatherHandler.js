const fsPromises = require("fs").promises;

const { get6WeatherData } = require("./getWeatherData.js");

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

async function renderWeatherPage(filePath) {
  let gridData = [];
  try {
    for (let idx = 0; idx < 30; idx++) {
      const data = await get6WeatherData(locArr[idx][0], locArr[idx][1]);
      gridData.push(data);
    }
    const fileContent = await fsPromises.readFile(filePath, "utf-8");
    const updateData = fileContent.replace(`{{gridData}}`, JSON.stringify(gridData));
    return updateData;
  } catch (error) {
    console.error(error);
  }
}

module.exports = {
  renderWeatherPage: renderWeatherPage,
};
