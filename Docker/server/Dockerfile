FROM node:21 AS builder

WORKDIR /build

COPY package.json .

RUN npm install 

FROM node:21

WORKDIR /usr/src/app/server

COPY --from=builder /build/node_modules/ /usr/src/app/server/node_modules/

COPY . .

CMD ["npm", "start"]
