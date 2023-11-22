FROM docker.bin.sbb.ch/cloud/sbb-base-rhel-java:21-LATEST

MAINTAINER techsupport-atlas@sbb.ch

RUN mkdir /deploy

COPY ./gateway/target/gateway-*.jar deploy/app.jar

RUN chmod 777 /deploy/app.jar

ENV JAVA_APP_JAR="deploy/app.jar"

EXPOSE 8888