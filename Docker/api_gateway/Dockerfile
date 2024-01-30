FROM node:21

WORKDIR /usr/src/app/api_gateway

COPY package.json ./

RUN npm install 

COPY ./ ./

CMD ["npm", "start"]
