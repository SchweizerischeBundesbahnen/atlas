plugins {
    id("buildlogic.java-conventions")
}

group = "ch.sbb.atlas"
version = "2.1189.0"

description= "Atlas User Administration Security Handler"

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Spring Security
    implementation("org.springframework.security:spring-security-oauth2-client")
    implementation("org.springframework.security:spring-security-oauth2-authorization-server")

    // Libraries
    implementation("org.springframework.kafka:spring-kafka")//get this dependency from :kafka use as api does not work

    // Project dependencies
    implementation(project(":base-atlas"))
    implementation(project(":kafka"))

    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-test")
    testImplementation("org.testcontainers:testcontainers-postgresql")
    testImplementation(project(":base-atlas", "test"))
    testImplementation(project(":auto-rest-doc"))

    testRuntimeOnly("org.postgresql:postgresql")
}

tasks.bootJar {
    enabled = false
}
