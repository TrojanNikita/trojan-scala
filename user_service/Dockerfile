FROM openjdk:8-jre-alpine3.9

ADD	user_service/target/universal/user_service.tgz /

RUN apk update && apk add bash

WORKDIR /user_service

ENTRYPOINT ["bin/user_service"]
