FROM openjdk:8-jre-alpine3.9

ADD	service1/target/universal/service1.tgz /

RUN apk update && apk add bash

WORKDIR /service1

ENTRYPOINT ["bin/service1"]
