plugins {
    id("buildlogic.java-conventions")
    id("buildlogic.java-restdoc")
    alias(libs.plugins.wsdl2java)
    id("buildlogic.docker-java")
}

group = "ch.sbb.atlas"
version = "2.1169.0"

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-kafka")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.springframework.boot:spring-boot-starter-webservices")

    // Spring Cloud
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

    // Spring Security
    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-resource-server")

    // Spring WS
    implementation("org.springframework.ws:spring-ws-core")
    implementation("org.springframework.ws:spring-ws-support")
    implementation("org.springframework.ws:spring-ws-security")

    // Micrometer & Tracing
    implementation("org.springframework.boot:spring-boot-micrometer-tracing-brave")

    // Flyway
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    // Hibernate
    implementation("org.hibernate.orm:hibernate-processor")

    // Libraries
    implementation(libs.aws.s3)
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv")
    implementation("com.sun.xml.messaging.saaj:saaj-impl")

    // Project dependencies
    implementation(project(":base-atlas"))
    implementation(project(":kafka"))
    implementation(project(":user-administration-security"))

    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.hibernate.orm:hibernate-processor")

    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webservices-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.testcontainers:testcontainers-postgresql")

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
