plugins {
    id("buildlogic.java-conventions")
}

group = "ch.sbb.atlas"
version = "2.543.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-logging")
    implementation("io.swagger.core.v3:swagger-core:${property("swaggerCoreVersion")}")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")

}

tasks.bootJar {
    enabled = false
}

