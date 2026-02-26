import java.util.*

plugins {
    id("buildlogic.java-conventions")
    id("buildlogic.docker-java")
}

group = "ch.sbb.atlas"
version = "2.1130.0"

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-configuration-processor")

    // Spring Security
    implementation("org.springframework.boot:spring-boot-security-oauth2-client")

    // Spring Cloud
    implementation("org.springframework.cloud:spring-cloud-starter-gateway-server-webflux")

    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-test")
    testImplementation(libs.bundles.okhttp)

}

tasks.named<Jar>("jar") {
    enabled = false
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
