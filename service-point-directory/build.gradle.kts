import java.util.*

plugins {
    id("buildlogic.java-conventions")
    id("buildlogic.java-restdoc")
    alias(libs.plugins.openapi.generator)
    alias(libs.plugins.protobuf)
    id("buildlogic.docker-java")
}

group = "ch.sbb.atlas"
version = "2.1169.0"

description = "ServicePointDirectory"

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-kafka")
    implementation("org.springframework.boot:spring-boot-starter-flyway")

    // Spring Cloud
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

    // Spring Security
    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-resource-server")
    implementation("org.springframework.security:spring-security-oauth2-client")

    // Flyway
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.flywaydb:flyway-core")

    // Hibernate
    implementation("org.hibernate.orm:hibernate-processor")

    // Libraries
    implementation(libs.bundles.geo.data) //optional
    implementation(libs.bundles.protobuf)

    // Project dependencies
    implementation(project(":base-atlas"))
    implementation(project(":kafka"))
    implementation(project(":user-administration-security"))

    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.hibernate.orm:hibernate-processor")

    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-kafka-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.testcontainers:testcontainers-postgresql")
    testImplementation(libs.mockito.inline)
    testImplementation(project(":base-atlas", "test"))
    testImplementation(project(":auto-rest-doc"))

    testRuntimeOnly("org.postgresql:postgresql")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${libs.versions.protobufVersion.get()}"
    }
    generateProtoTasks {
        ofSourceSet("main")
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
        proto {
            // In addition to the default 'src/main/proto'
            srcDir("${projectDir}/src/main/resources/protobuf")
        }
    }
}

tasks.compileJava.get().dependsOn(tasks.openApiGenerate)

springBoot {
    buildInfo {
        properties {
            additional.set(
                mapOf(
                    "time" to "${Date()}"
                )
            )
        }
    }
}
