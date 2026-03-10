import java.util.*

plugins {
    id("buildlogic.java-conventions")
    id("buildlogic.docker-java")
}

group = "ch.sbb.atlas"
version = "2.1153.0"

description = "Atlas Mail Service"

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-kafka")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Libraries
    implementation(libs.swagger.core)

    // Project dependencies
    implementation(project(":kafka"))
    implementation(project(":base-atlas")){
        exclude("org.hibernate.orm","hibernate-processor")
        exclude("org.springframework.boot","spring-boot-starter-data-jpa")
        exclude("org.springframework.boot","spring-boot-starter-security")
        exclude("org.springframework.boot","spring-boot-starter-security-oauth2-resource-server")
    }

    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(libs.greenmail.junit5)
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
