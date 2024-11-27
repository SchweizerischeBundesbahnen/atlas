plugins {
    id("org.openapi.generator") version "7.10.0"
    id("buildlogic.java-conventions")
}

extra["swaggerCoreVersion"] = "2.2.25"
extra["openapiStarterCommonVersion"] = "2.6.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.kafka:spring-kafka:3.3.0")//use spring boot dependency management
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-logging")
    implementation("io.swagger.core.v3:swagger-core:${property("swaggerCoreVersion")}")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.mockito:mockito-core")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.bootJar {
    enabled = false
}

