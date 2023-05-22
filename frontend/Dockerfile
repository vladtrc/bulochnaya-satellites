FROM node:16-alpine

WORKDIR /app
COPY package.json /app/package.json
RUN npm i
COPY . /app
RUN npm run build

ENTRYPOINT ["npm", "run" , "start", "--port", "8080"]
