import java.util.*

plugins {
    id("org.openapi.generator") version "7.10.0"
    id("buildlogic.java-conventions")
}

extra["swaggerCoreVersion"] = "2.2.25"
extra["openapiStarterCommonVersion"] = "2.6.0"
extra["hibernateVersion"] = "6.5.3.Final" //get from spring dependencies!!

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation(project(":base-atlas"))

    //to remove?
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.security:spring-security-oauth2-client:6.4.1")
    implementation("org.springframework.security:spring-security-oauth2-authorization-server:1.4.0")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(project(":auto-rest-doc"))
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc:3.0.2")
    testImplementation("org.testcontainers:postgresql")
    testImplementation(project(":base-atlas", "test"))

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.bootJar {
    enabled = false
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