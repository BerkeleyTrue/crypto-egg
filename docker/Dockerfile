FROM node:16-bullseye
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
EXPOSE 3000
CMD [ "node", "build/server.js" ]

