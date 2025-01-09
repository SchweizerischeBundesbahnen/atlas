import java.util.*

plugins {
    id("buildlogic.java-conventions")
    id("buildlogic.java-restdoc")
}

group = "ch.sbb.atlas"
version = "2.380.0"

description = "Atlas Mail Service"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("io.swagger.core.v3:swagger-core:${property("swaggerCoreVersion")}")

    implementation(project(":kafka"))
    implementation("org.springframework.kafka:spring-kafka")//get this dependency from :kafka use as api does not work

    implementation(project(":base-atlas")){
        exclude("org.hibernate.orm","hibernate-jpamodelgen")
        exclude("org.springframework.boot","spring-boot-starter-data-jpa")
        exclude("org.springframework.boot","spring-boot-starter-security")
        exclude("org.springframework.boot","spring-boot-starter-oauth2-resource-server")
    }
    implementation("org.springframework.boot:spring-boot-starter-validation")//get this dependency from :kafka use as api does not work

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.icegreen:greenmail-junit5:2.1.2")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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