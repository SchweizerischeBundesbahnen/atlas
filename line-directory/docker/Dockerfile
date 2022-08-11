FROM docker.bin.sbb.ch/cloud/sbb-base-rhel-java:17-LATEST

MAINTAINER techsupport-atlas@sbb.ch

ARG ARTIFACTORY_USER
ARG ARTIFACTORY_PW

RUN mkdir /deploy
ADD "https://${ARTIFACTORY_USER}:${ARTIFACTORY_PW}@bin.sbb.ch/artifactory/atlas.mvn/ch/sbb/atlas/line-directory/${VERSION}/line-directory-${VERSION}.jar" deploy/app.jar
RUN chmod 777 /deploy/app.jar

# Directory used to store temporary csv and zip files
RUN mkdir -m 777 -p /usr/local/atlas/tmp

ENV JAVA_APP_JAR="deploy/app.jar"

EXPOSE 8082