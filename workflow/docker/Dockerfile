FROM docker.bin.sbb.ch/cloud/sbb-base-rhel-java:17-LATEST

MAINTAINER techsupport-atlas@sbb.ch

RUN mkdir /deploy

COPY ../workflow/target/workflow-*.jar deply/app.jar
RUN chmod 777 /deploy/app.jar

ENV JAVA_APP_JAR="deploy/app.jar"

EXPOSE 8087