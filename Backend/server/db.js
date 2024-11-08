const mysqlPromise = require("mysql2/promise");
const dotenv = require("dotenv");
const path = require("path");

dotenv.config({ path: path.join(__dirname, "Keys/.env") });

const db = async () => {
  try {
    const connection = await mysqlPromise.createConnection({
      host: process.env.MYSQL_HOST,
      user: process.env.MYSQL_USER,
      password: process.env.MYSQL_PASSWORD,
      database: process.env.MYSQL_DATABASE,
    });

    console.log("mysql connection success");
    return connection;
  } catch (error) {
    console.log(error);
  }
};

module.exports = db;
