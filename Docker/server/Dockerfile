FROM node:latest

WORKDIR /usr/src/app/server

COPY  .  .

RUN npm install

COPY . .

Expose 8080 

CMD ["npm", "start"]
