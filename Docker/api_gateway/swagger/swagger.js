const express = require("express");
const router = express.Router();
const path = require("path");

const swaggerUi = require("swagger-ui-express");
const swaggerJsdoc = require("swagger-jsdoc");

const apiPaths = {
  "/api/POI": {
    get: {
      summary: "POI",
      description: `검색.`,
      responses: {
        200: {
          description: "메인 페이지를 반환 성공",
          content: {
            "text/html": {
              schema: {
                type: "string",
                example: `
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <title>메인 페이지</title>
                </head>
                <body>
                    <h1>메인 페이지</h1>
                    <p>이 페이지는 메인 화면을 나타냅니다.</p>
                </body>
                </html>`,
              },
            },
          },
        },
        302: {
          description: "로그인 페이지로 리디렉션",
          headers: {
            Location: {
              description: "리디렉션할 위치",
              schema: {
                type: "string",
                example: "/auth/login",
              },
            },
          },
        },
      },
    },
  },
  "/api/map": {
    get: {
      description: "카카오맵",
      responses: {
        200: {
          description: "카카오맵을 성공적으로 불러옴",
          content: {
            "application/json": {
              schema: {
                type: "object",
              },
            },
          },
        },
      },
    },
  },
  "/api/Odsay": {
    get: {
      description: "Odsay API",
      responses: {
        200: {
          description: "으아아",
          content: {
            "application/json": {
              schema: {
                type: "object",
              },
            },
          },
        },
      },
    },
  },
};

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
        url: "https://localhost/",
        description: "pposonggil api_gateway",
      },
    ],
    paths: { ...apiPaths },
  },
  apis: [path.resolve(__dirname, "../routes/*.js")],
};

const specs = swaggerJsdoc(options);

router.use("/api_gateway", swaggerUi.serve, swaggerUi.setup(specs));

module.exports = router;
