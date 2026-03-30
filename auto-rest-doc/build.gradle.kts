plugins {
    id("buildlogic.java-conventions")
}

group = "ch.sbb.atlas"
version = "2.1189.0"

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Libraries
    implementation(libs.swagger.core)
    implementation("org.springframework.restdocs:spring-restdocs-mockmvc")

    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
}

tasks.bootJar {
    enabled = false
}
