import java.util.*

plugins {
    id("buildlogic.java-conventions")
    id("buildlogic.java-restdoc")
    id("buildlogic.docker-java")
}

group = "ch.sbb.atlas"
version = "2.1189.0"

description = "Atlas Workflow Service"

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-kafka")
    implementation("org.springframework.boot:spring-boot-starter-flyway")

    // Spring Cloud
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

    // Spring Security
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    // Flyway
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.flywaydb:flyway-core")

    // Libraries
    implementation("commons-codec:commons-codec")
    implementation("org.springframework.kafka:spring-kafka")//get this dependency from :kafka use as api does not work

    // Project dependencies
    implementation(project(":base-atlas"))
    implementation(project(":kafka"))
    implementation(project(":user-administration-security"))

    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.hibernate.orm:hibernate-processor")

    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.springframework.boot:spring-boot-starter-security-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.testcontainers:testcontainers-postgresql")
    testImplementation(libs.mockito.inline)
    testImplementation(project(":base-atlas", "test"))
    testImplementation(project(":auto-rest-doc"))

    testRuntimeOnly("org.postgresql:postgresql")
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
