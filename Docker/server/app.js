const express = require("express");
const https = require("https");
const dotenv = require("dotenv");
const path = require("path");
const fs = require("fs");
const morgan = require("morgan");
const session = require("express-session");
const bodyParser = require("body-parser");
const FileStore = require("session-file-store")(session);
const { nextTick } = require("process");

const authRouter = require("./routes/auth.js");
const mainRouter = require("./routes/main.js");
const indexRouter = require("./routes/index.js");

dotenv.config({ path: path.join(__dirname, "Keys/.env") });

const options = {
  key: fs.readFileSync(path.join(__dirname, "Keys/localhost-key.pem")),
  cert: fs.readFileSync(path.join(__dirname, "Keys/localhost.pem")),
};

const app = express();
const port = 8888;
// app.set("port", 8888);

app.use(
  morgan("dev"),
  express.static(path.join(__dirname, "public")),
  express.static(path.join(__dirname, "views")),
  bodyParser.urlencoded({ limit: "10mb", extended: true }),
  bodyParser.json({ limit: "10mb" }),

  session({
    secret: `${process.env.SESSION_KEY}`, // 비밀키
    resave: false,
    saveUninitialized: false,
    store: new FileStore({ reapInterval: 60 * 60 }), //세션 파일 삭제 주기
    cookie: {
      expires: 1000 * 60 * 60, //세션유지 1시간
      httpOnly: true,
    },
  })
);

app.use("/script", express.static(__dirname + "/public"));

// 인증 라우터
app.use("/", indexRouter);
app.use("/auth", authRouter);
app.use("/main", mainRouter);

app.use((err, req, res, next) => {
  console.error(err);
  res.send("이용에 불편을 드려 죄송합니다.");
});

https.createServer(options, app).listen(port, () => {
  console.log(`Server is running at https://localhost:${port}/`);
});
