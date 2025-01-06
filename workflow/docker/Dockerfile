FROM esta.docker.bin.sbb.ch/esta/ubi9/java-21:latest

MAINTAINER techsupport-atlas@sbb.ch

RUN mkdir /deploy

COPY ./workflow/build/libs/workflow-*.jar deploy/app.jar

RUN chmod 777 /deploy/app.jar

ENV JAVA_APP_JAR="deploy/app.jar"

EXPOSE 8087