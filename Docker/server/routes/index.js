const express = require("express");
const router = express.Router();

//첫페이지
router.get("/", (req, res) => {
  if (!authCheck.isOwner(req, res)) {
    // 로그인 안되어있으면 로그인 페이지로 이동시킴
    res.redirect("/auth/login");
    // return false;
    return;
  } else {
    // 로그인 되어있으면 메인 페이지로 이동시킴
    res.redirect("/main");
    // return false;
    return;
  }
});

module.exports = router;
