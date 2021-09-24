FROM openjdk:8-jre-alpine3.9

ADD	user_daemon/target/universal/user_daemon.tgz /

RUN apk update && apk add bash

WORKDIR /user_daemon

ENTRYPOINT ["bin/user_daemon"]
