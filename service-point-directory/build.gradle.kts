import java.util.*

plugins {
    id("buildlogic.java-conventions")
    id("buildlogic.java-restdoc")
    id("org.openapi.generator") version "7.12.0"
    id("com.google.protobuf") version "0.9.5"
}

group = "ch.sbb.atlas"
version = "2.531.0"

description = "ServicePointDirectory"
extra["shedlockVersion"] = "5.16.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.hibernate.orm:hibernate-jpamodelgen")
    implementation(project(":base-atlas"))
    implementation("org.springframework.kafka:spring-kafka")//get this dependency from :kafka use as api does not work
    implementation(project(":kafka"))
    implementation(project(":user-administration-security"))
    implementation("org.locationtech.proj4j:proj4j:${property("proj4jVersion")}") //optional
    implementation("org.locationtech.proj4j:proj4j-epsg:${property("proj4jVersion")}") //optional
    implementation("org.locationtech.jts:jts-core:${property("jtsVersion")}") //optional
    implementation("com.google.protobuf:protoc:4.30.2")
    implementation("com.google.protobuf:protobuf-java:4.30.2")
    implementation("org.springdoc:springdoc-openapi-starter-common:${property("openapiStarterCommonVersion")}")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${property("springOpenapiUiVersion")}")
    implementation("org.springframework.security:spring-security-oauth2-client")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.flywaydb:flyway-core")

    implementation("com.google.protobuf:protobuf-java:4.30.2")
    annotationProcessor("org.hibernate.orm:hibernate-jpamodelgen:6.6.12.Final")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation(project(":base-atlas", "test"))
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation(project(":auto-rest-doc"))

    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:postgresql")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.30.2"
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