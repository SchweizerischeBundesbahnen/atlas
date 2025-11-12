import java.util.*

plugins {
    id("buildlogic.java-conventions")
    id("buildlogic.java-restdoc")
    id("buildlogic.docker")
}

group = "ch.sbb.atlas"
version = "2.947.0"

description = "Atlas Export Service"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-oauth2-authorization-server")
    implementation("org.springframework.boot:spring-boot-starter-batch")
    implementation("org.springframework.batch:spring-batch-integration")
    implementation("io.micrometer:micrometer-tracing")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation(libs.aws.s3)
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv")

    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    implementation(libs.bundles.geo.data)

    implementation("org.springframework.kafka:spring-kafka")
    implementation(project(":base-atlas"))
    implementation(project(":kafka"))
    implementation(project(":user-administration-security"))

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.batch:spring-batch-test")
    testImplementation(project(":base-atlas", "test"))
    testImplementation(project(":auto-rest-doc"))
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation(libs.mockito.inline)
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:postgresql")

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