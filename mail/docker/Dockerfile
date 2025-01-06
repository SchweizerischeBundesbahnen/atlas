FROM esta.docker.bin.sbb.ch/esta/ubi9/java-21:latest

MAINTAINER techsupport-atlas@sbb.ch

RUN mkdir /deploy

COPY ./mail/build/libs/mail-*.jar deploy/app.jar

RUN chmod 777 /deploy/app.jar

ENV JAVA_APP_JAR="deploy/app.jar"

EXPOSE 8084