FROM docker.bin.sbb.ch/cloud/sbb-base-rhel-java:21-LATEST

LABEL mantainer="techsupport-atlas@sbb.ch"

RUN mkdir /deploy

COPY ./export-service-point/target/export-service-point-*.jar deploy/app.jar

RUN chmod 777 /deploy/app.jar

# Directory used to store temporary csv and zip files
RUN mkdir -m 777 -p /usr/local/atlas/tmp

ENV JAVA_APP_JAR="deploy/app.jar"

EXPOSE 8092
