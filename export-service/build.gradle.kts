import java.util.*

plugins {
    id("buildlogic.java-conventions")
    id("buildlogic.java-restdoc")
    id("buildlogic.docker-java")
}

group = "ch.sbb.atlas"
version = "2.1153.0"

description = "Atlas Export Service"

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-json")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-batch")

    implementation("org.springframework.boot:spring-boot-starter-kafka")
    implementation("org.springframework.boot:spring-boot-starter-flyway")

    // Spring Cloud
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

    // Spring Security
    implementation("org.springframework.security:spring-security-oauth2-authorization-server")

    // Spring Batch
    implementation("org.springframework.batch:spring-batch-integration")

    // Micrometer & Tracing
    implementation("org.springframework.boot:spring-boot-micrometer-tracing-brave")

    // Flyway
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    // Libraries
    implementation(libs.bundles.geo.data)
    implementation(libs.aws.s3)
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv")

    // Project dependencies
    implementation(project(":base-atlas"))
    implementation(project(":kafka"))
    implementation(project(":user-administration-security"))

    runtimeOnly("org.postgresql:postgresql")

    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-batch-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.testcontainers:testcontainers-postgresql")
    testImplementation(libs.mockito.inline)
    testImplementation(project(":base-atlas", "test"))
    testImplementation(project(":auto-rest-doc"))
}

springBoot {
    buildInfo {
        properties {
            additional.set(mapOf(
                    "time" to "${Date()}"
            ))
        }
    }
}
