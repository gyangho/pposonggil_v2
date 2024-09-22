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
  "/api/osrm/": {
    get: {
      summary: "OSRM으로 도보경로 요청하기",
      description:
        "위도: lan, 경도 : lon<br>출발지의 위경도, 도착지의 위경도를 입력하여 도보경로를 받아옵니다.",
      parameters: [
        {
          in: "query",
          name: "sLat",
          description: "출발지 위도",
          required: true,
          schema: {
            type: "string",
            example: 126.9594,
          },
        },
        {
          in: "query",
          name: "sLon",
          description: "출발지 경도",
          required: true,
          schema: {
            type: "string",
            example: 37.4945,
          },
        },
        {
          in: "query",
          name: "eLat",
          description: "도착지 위도",
          required: true,
          schema: {
            type: "string",
            example: 126.96,
          },
        },
        {
          in: "query",
          name: "eLon",
          description: "도착지 경도",
          required: true,
          schema: {
            type: "string",
            example: 37.5068,
          },
        },
      ],
      responses: {
        200: {
          description: "도보경로는 [위도(lon), 경도(lat)] 형식의 배열로 구성되어 있습니다",
          content: {
            "application/json": {
              items: {
                type: "number",
                description: "위도, 경도",
              },
              example: [
                [37.49448, 126.95942000000001],
                [37.4945, 126.95946],
                [37.49492, 126.95954],
              ],
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
