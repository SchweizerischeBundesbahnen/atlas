import java.util.*

plugins {
    id("buildlogic.java-conventions")
    id("buildlogic.java-restdoc")
}

group = "ch.sbb.atlas"
version = "2.505.0"

description = "Atlas Scheduling Service"
extra["shedlockVersion"] = "6.3.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-oauth2-client")
    implementation("org.springframework.retry:spring-retry")
    implementation("org.springframework:spring-aspects")
    implementation("net.javacrumbs.shedlock:shedlock-spring:${property("shedlockVersion")}")
    implementation("net.javacrumbs.shedlock:shedlock-provider-jdbc-template:${property("shedlockVersion")}")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.flywaydb:flyway-core")

    //to remove?
    implementation("org.springframework.security:spring-security-oauth2-client")
    implementation("org.springframework.security:spring-security-oauth2-authorization-server")

    implementation(project(":base-atlas"))
    implementation("io.micrometer:micrometer-tracing")//get this dependency from :kafka use as api does not work
    implementation(project(":kafka"))
    implementation("org.springframework.kafka:spring-kafka")//get this dependency from :kafka use as api does not work

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-inline:5.2.0")

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