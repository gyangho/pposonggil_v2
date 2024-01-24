const mysqlPromise = require("mysql2/promise");
const mysql = require("mysql2");

const dotenv = require("dotenv");
const path = require("path");

dotenv.config({ path: path.join(__dirname, "Keys/.env") });

const db = mysql.createConnection({
  host: process.env.MYSQL_HOST,
  user: process.env.MYSQL_USER,
  password: process.env.MYSQL_PASSWORD,
  database: process.env.MYSQL_DATABASE,
});
db.connect();
console.log("mysql connection success");

module.exports = db;
