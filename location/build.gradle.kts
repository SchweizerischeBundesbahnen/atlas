import java.util.*

plugins {
    id("buildlogic.java-conventions")
    id("buildlogic.java-restdoc")
    id("buildlogic.docker-java")
    alias(libs.plugins.openapi.generator)
}

group = "ch.sbb.atlas"
version = "2.1462.0"

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.springframework.cloud:spring-cloud-starter-vault-config")


    // Spring Cloud
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

    // Spring Security
    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-authorization-server")

    // Flyway
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    // Project dependencies
    implementation(project(":base-atlas"))
    implementation(project(":kafka"))

    runtimeOnly("org.postgresql:postgresql")

    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.testcontainers:testcontainers-postgresql")
    testImplementation(project(":auto-rest-doc"))
    testImplementation(project(":base-atlas", "test"))
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

openApiGenerate {
    generatorName.set("spring")
    inputSpec.set("${projectDir}/src/main/resources/journey-pois.yaml")
    apiPackage.set("org.openapitools.api")
    outputDir.set("${project.layout.buildDirectory.get()}/generated-sources/openapi")
    configOptions.putAll(
        mapOf(
            Pair("interfaceOnly", "true"),
            Pair("modelPackage", "ch.sbb.atlas.journey.poi.model"),
            Pair("apiPackage", "ch.sbb.atlas.journey.poi.api"),
            Pair("useSpringBoot3", "true"),
            Pair("generatedConstructorWithRequiredArgs", "false"),
            Pair("openApiNullable", "false"),
        )
    )
    library.set("spring-cloud")
    generateApiTests.set(false)
}

sourceSets {
    main {
        java {
            srcDir(files("${project.layout.buildDirectory.get()}/generated-sources/openapi"))
        }
    }
}

tasks.compileJava.get().dependsOn(tasks.openApiGenerate)
