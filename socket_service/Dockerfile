FROM openjdk:8-jre-alpine3.9

ADD	socket_service/target/universal/socket_service.tgz /

RUN apk update && apk add bash

WORKDIR /socket_service

ENTRYPOINT ["bin/socket_service"]
