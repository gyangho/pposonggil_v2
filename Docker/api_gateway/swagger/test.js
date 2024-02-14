const express = require("express");
const router = express.Router();
const path = require("path");

const swaggerUi = require("swagger-ui-express");
const swaggerJsdoc = require("swagger-jsdoc");

const options = {
  swaggerDefinition: {
    openapi: "3.0.0",
    info: {
      title: "pposonggil swagger",
      version: "0.6.2",
      description: "뽀송길(api_gateway) API 문서 ",
    },
    servers: [
      {
        url: "https://localhost",
        description: "pposonggil api_gateway",
      },
    ],
    paths: {
      "/test": {
        get: {
          summary: "POI",
          description: `검색.`,
          responses: {
            200: {
              description: "성공",
            },
          },
        },
      },
    },
  },
  apis: [path.resolve(__dirname, "../*.js")],
};

const specs = swaggerJsdoc(options);

router.use("/api_gateway", swaggerUi.serve, swaggerUi.setup(specs));

module.exports = router;
