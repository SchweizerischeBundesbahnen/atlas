import java.util.*

plugins {
    id("buildlogic.java-conventions")
    id("buildlogic.java-restdoc")
}

group = "ch.sbb.atlas"
version = "2.403.0"

description = "Atlas Scheduling Service"
extra["shedlockVersion"] = "5.16.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${property("springOpenapiUiVersion")}")
    implementation("commons-codec:commons-codec")

    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.flywaydb:flyway-core")

    implementation(project(":base-atlas"))
    implementation(project(":kafka"))
    implementation("org.springframework.kafka:spring-kafka")//get this dependency from :kafka use as api does not work
    implementation(project(":user-administration-security"))

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.springframework.kafka:spring-kafka-test")//get this dependency from :kafka use as api does not work
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation(project(":base-atlas", "test"))
    testImplementation(project(":auto-rest-doc"))

    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:postgresql")

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