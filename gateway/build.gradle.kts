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
    implementation("org.springframework.boot:spring-boot-configuration-processor")

    // Spring Cloud
    implementation("org.springframework.cloud:spring-cloud-starter-gateway-server-webflux")

    // Micrometer & Tracing
    implementation("org.springframework.boot:spring-boot-micrometer-tracing-brave")
    implementation("io.micrometer:micrometer-tracing-bridge-brave")

    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.named<Jar>("jar") {
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
