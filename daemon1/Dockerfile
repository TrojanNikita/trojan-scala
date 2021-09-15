FROM openjdk:8-jre-alpine3.9

ADD	daemon1/target/universal/daemon1.tgz /

RUN apk update && apk add bash

WORKDIR /daemon1

ENTRYPOINT ["bin/daemon1"]
