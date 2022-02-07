FROM node:16-bullseye AS builder
RUN apt-get update && apt-get -q -y install \
      openjdk-17-jdk \
      curl \
    && curl -s https://download.clojure.org/install/linux-install-1.10.3.1075.sh | bash \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /usr/app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build:server

FROM node:16-alpine
WORKDIR /usr/app
COPY --from=builder /usr/app/build/server.js ./server.js
COPY --from=builder /usr/app/package* ./
RUN NODE_ENV=production npm ci
EXPOSE 3000
CMD [ "node", "server.js" ]
