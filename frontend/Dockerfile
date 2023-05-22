FROM node:16-alpine

WORKDIR /app
COPY package.json /app/package.json
RUN npm i
COPY . /app
RUN npm run build
RUN npm install -g serve@12.0.1

CMD ["serve", "-s", "build"]
#ENTRYPOINT ["npm", "run" , "start", "--port", "8080"]
