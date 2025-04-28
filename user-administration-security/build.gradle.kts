plugins {
    id("buildlogic.java-conventions")
}

group = "ch.sbb.atlas"
version = "2.566.0"

description= "Atlas User Administration Security Handler"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation(project(":base-atlas"))
    implementation("org.springframework.security:spring-security-oauth2-client")
    implementation("org.springframework.security:spring-security-oauth2-authorization-server")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation(project(":kafka"))
    implementation("org.springframework.kafka:spring-kafka")//get this dependency from :kafka use as api does not work

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation(project(":base-atlas", "test"))
    testImplementation(project(":auto-rest-doc"))
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:postgresql")

    testRuntimeOnly("org.postgresql:postgresql")
}

tasks.bootJar {
    enabled = false
}

