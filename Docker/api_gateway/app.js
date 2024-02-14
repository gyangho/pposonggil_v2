const express = require("express");
const https = require("https");
const path = require("path");
const fs = require("fs");
const bodyParser = require("body-parser");
const cors = require("cors");
const morgan = require("morgan");

const { scheduleUpdate } = require("./schedule.js");
const apiRouter = require("./routes/api.js");

const options = {
  key: fs.readFileSync(path.join(__dirname, "Keys/localhost-key.pem")),
  cert: fs.readFileSync(path.join(__dirname, "Keys/localhost.pem")),
};

const app = express();
const port = 8889;
// app.set("port", 8888);

app.use(
  morgan("dev"),
  express.static(path.join(__dirname, "public")),
  cors(),
  bodyParser.urlencoded({ extended: false }),
  bodyParser.json()
);

// 인증 라우터
app.use("/api", apiRouter);

https.createServer(options, app).listen(port, () => {
  console.log(`Example app listening on port ${port}`);
  scheduleUpdate();
});
