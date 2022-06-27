FROM docker.bin.sbb.ch/cloud/sbb-base-rhel-java:11-LATEST

MAINTAINER techsupport-atlas@sbb.ch

ARG ARTIFACTORY_USER
ARG ARTIFACTORY_PW

RUN mkdir /deploy
ADD "https://${ARTIFACTORY_USER}:${ARTIFACTORY_PW}@bin.sbb.ch/artifactory/atlas.mvn/ch/sbb/mail/mail-poc/${VERSION}/mail-poc-${VERSION}.jar" deploy/app.jar
RUN chmod 777 /deploy/app.jar

ENV JAVA_APP_JAR="deploy/app.jar"

EXPOSE 8082