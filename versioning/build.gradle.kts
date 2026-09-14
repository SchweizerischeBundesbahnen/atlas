plugins {
    id("buildlogic.java-conventions")
}

group = "ch.sbb.atlas"
version = "2.1558.0"

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Project dependencies
    implementation(project(":base-atlas"))

    // Test dependencies
    testImplementation(project(":base-atlas", "test"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
}

tasks.bootJar {
    enabled = false
}
