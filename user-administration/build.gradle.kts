import java.util.*

plugins {
    id("buildlogic.java-conventions")
    id("buildlogic.java-restdoc")
    id("buildlogic.docker-java")
}

group = "ch.sbb.atlas"
version = "2.1143.0"

description = "Atlas User Administration"

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-kafka")
    implementation("org.springframework.boot:spring-boot-starter-flyway")

    // Spring Security
    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-authorization-server")

    // Flyway
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    // Hibernate
    implementation("org.hibernate.orm:hibernate-processor")

    // Libraries
    implementation(libs.microsoft.graph)
    implementation(libs.azure.identity)

    // Project dependencies
    implementation(project(":base-atlas"))
    implementation(project(":kafka"))

    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.hibernate.orm:hibernate-processor")

    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
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
