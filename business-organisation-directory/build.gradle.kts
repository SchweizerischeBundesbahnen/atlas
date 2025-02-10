plugins {
    id("buildlogic.java-conventions")
    id("buildlogic.java-restdoc")
    id("com.github.bjornvester.wsdl2java") version "2.0.2"
}

group = "ch.sbb.atlas"
version = "2.437.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("org.springframework.ws:spring-ws-core")
    implementation("org.springframework.ws:spring-ws-support")
    implementation("org.springframework.ws:spring-ws-security")
    implementation("com.sun.xml.messaging.saaj:saaj-impl")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${property("springOpenapiUiVersion")}")

    implementation("org.hibernate.orm:hibernate-jpamodelgen")
    annotationProcessor("org.hibernate.orm:hibernate-jpamodelgen:6.6.6.Final")

    runtimeOnly("org.postgresql:postgresql")

    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("software.amazon.awssdk:s3:${property("awsS3Version")}")

    implementation("org.springframework.kafka:spring-kafka")

    implementation(project(":base-atlas"))
    implementation(project(":kafka"))
    implementation(project(":user-administration-security"))

    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("org.testcontainers:postgresql")

    testImplementation(project(":base-atlas", "test"))
    testImplementation(project(":auto-rest-doc"))
}

wsdl2java {
    packageName.set("ch.sbb.business.organisation.directory.service.crd")
}

tasks.compileJava.get().dependsOn(tasks.wsdl2java)

springBoot {
    buildInfo()
}