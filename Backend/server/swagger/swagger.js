const express = require("express");
const router = express.Router();
const path = require("path");

const swaggerUi = require("swagger-ui-express");
const swaggerJsdoc = require("swagger-jsdoc");

const authPaths = {
  "/auth/login": {
    get: {
      summary: "로그인 화면",
      description: "뽀송길 로그인 페이지",
      responses: {
        200: {
          description: "성공",
        },
      },
    },
  },
  "/auth/login_process": {
    post: {
      summary: "로그인 프로세스",
      description: "뽀송길 로그인 프로세스",
      responses: {
        200: {
          description: "성공",
        },
      },
    },
  },
  "/auth/logout": {
    get: {
      summary: "로그아웃 화면",
      description: "뽀송길 로그아웃 페이지",
      responses: {
        200: {
          description: "성공",
        },
      },
    },
  },
  "/auth/register": {
    get: {
      summary: "회원가입 화면",
      description: "뽀송길 회원가입 페이지",
      responses: {
        200: {
          description: "성공",
        },
      },
    },
  },
  "/auth/register_process": {
    post: {
      summary: "회원가입 프로세스",
      description: "뽀송길 회원가입 프로세스",
      responses: {
        200: {
          description: "성공",
        },
      },
    },
  },
  "/auth/inquiry": {
    get: {
      summary: "비밀번호 찾기",
      description: "비밀번호 찾기",
      responses: {
        200: {
          description: "성공",
        },
      },
    },
  },
  "/auth/inquiry_process": {
    post: {
      summary: "비밀번호 찾기 프로세스",
      description: "비밀번호 찾기 프로세스",
      responses: {
        200: {
          description: "성공",
        },
      },
    },
  },
  "/auth/delete": {
    get: {
      summary: "계정삭제 화면",
      description: "계정삭제 화면",
      responses: {
        200: {
          description: "성공",
        },
      },
    },
  },
  "/auth/delete_process": {
    post: {
      summary: "계정삭제 프로세스",
      description: "계정삭제 프로세스",
      responses: {
        200: {
          description: "성공",
        },
      },
    },
  },
  "/auth/alter": {
    get: {
      summary: "비밀번호 변경",
      description: "비밀번호 변경",
      responses: {
        200: {
          description: "성공",
        },
      },
    },
  },
  "/auth/alter_process": {
    post: {
      summary: "비밀번호 변경 프로세스",
      description: "비밀번호 변경 프로세스",
      responses: {
        200: {
          description: "성공",
        },
      },
    },
  },
};

const mainPaths = {
  "/main": {
    get: {
      summary: "메인 페이지",
      description: `인증된 사용자에게 메인 페이지를 반환하고,인증되지 않은 경우 로그인 페이지로 리디렉션됩니다.`,
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
  "/main/mypage": {
    get: {
      description: "마이페이지",
      responses: {
        200: {
          description: "마이페이지를 성공적으로 불러옴",
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
  "/main/mypage/bookmark": {
    get: {
      summary: "북마크",
      description: "북마크",
      responses: {
        200: {
          description: "성공",
        },
      },
    },
  },
  "/main/POI": {
    get: {
      summary: "장소 검색",
      description: "장소 검색",
      responses: {
        200: {
          description: "성공",
        },
      },
    },
  },
  "/main/POI/result": {
    get: {
      summary: "검색 결과",
      description: "검색 결과",
      responses: {
        200: {
          description: "성공",
        },
      },
    },
  },
  "/main/POI/result/pposong": {
    get: {
      summary: "뽀송타임",
      description: "뽀송타임",
      responses: {
        200: {
          description: "성공",
        },
      },
    },
  },
  "/main/POI/result/pposong/cal": {
    post: {
      summary: "뽀송타임 계산",
      description: "타임 계산",
      responses: {
        200: {
          description: "성공",
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
      description: "뽀송길(server) API 문서 ",
    },
    servers: [
      {
        url: "https://localhost/",
        description: "pposonggil server",
      },
    ],
    paths: {
      "/": {
        get: {
          summary: "첫 페이지",
          description: "뽀송길 시작 페이지",
          responses: {
            200: {
              description: "성공",
            },
          },
        },
      },
      ...authPaths,
      ...mainPaths,
    },
  },
  apis: [path.resolve(__dirname, "../routes/*.js")],
};

const specs = swaggerJsdoc(options);

router.use("/server", swaggerUi.serve, swaggerUi.setup(specs));

module.exports = router;
