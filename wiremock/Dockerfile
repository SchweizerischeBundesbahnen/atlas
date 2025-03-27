# Build Stage used to download software (with credentials if necessary) and prepare environment for the final docker image
FROM esta.docker.bin.sbb.ch/esta/ubi9/java-21:latest as build

ENV TZ=Europe/Busingen

RUN curl  https://bin.sbb.ch/artifactory/mvn/org/wiremock/wiremock-standalone/3.9.1/wiremock-standalone-3.9.1.jar \
    -o /usr/bin/wiremock-standalone.jar

EXPOSE 8080

COPY mappings /mappings
COPY __files /__files

CMD ["java", "-jar", "/usr/bin/wiremock-standalone.jar", "--port", "8080", "--local-response-templating", "--verbose"]
