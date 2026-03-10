import java.util.*

plugins {
    id("buildlogic.java-conventions")
    id("buildlogic.java-restdoc")
    id("buildlogic.docker-java")
}

group = "ch.sbb.atlas"
version = "2.1153.0"

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
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
    implementation(libs.aws.s3)
    implementation(libs.tika.core)
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv")

    // Project dependencies
    implementation(project(":base-atlas"))
    implementation(project(":user-administration-security"))
    implementation(project(":kafka"))

    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.hibernate.orm:hibernate-processor")

    // Test dependencies
    testImplementation(project(":base-atlas", "test"))
    testImplementation(project(":auto-rest-doc"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.testcontainers:testcontainers-postgresql")
}

springBoot {
    buildInfo {
        properties {
            additional.set(
                mapOf(
                    "time" to "${Date()}"
                )
            )
        }
    }
}
