FROM docker.bin.sbb.ch/cloud/sbb-base-rhel-java:17-LATEST

LABEL mantainer="techsupport-atlas@sbb.ch"

RUN mkdir /deploy

COPY ./import/target/import-service-point-*.jar deploy/app.jar

RUN chmod 777 /deploy/app.jar

ENV JAVA_APP_JAR="deploy/app.jar"

EXPOSE 8089