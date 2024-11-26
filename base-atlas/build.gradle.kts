plugins {
    java
    `java-library`
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.openapi.generator") version "7.10.0"
    jacoco
    `maven-publish`
    id("org.sonarqube") version "5.0.0.4638"
}

group = "ch.sbb.atlas"
version = "2.350.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    withSourcesJar()
}


configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
    create("test")
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
}

extra["springCloudVersion"] = "2023.0.3"
extra["swaggerCoreVersion"] = "2.2.25"
extra["openapiStarterCommonVersion"] = "2.6.0"
extra["hibernateVersion"] = "6.5.3.Final" //get from spring dependencies!!

dependencies {
// For BaseVersion
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.hibernate.orm:hibernate-jpamodelgen:${property("hibernateVersion")}")
// For UserService
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
// For correlation id
    implementation("io.micrometer:micrometer-tracing")
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
// Feign Client Specific Micrometer
    implementation("io.github.openfeign:feign-micrometer")
// Service Point and ExportService
    implementation("org.locationtech.proj4j:proj4j:1.3.0") //optional && use spring boot dependency management
    implementation("org.locationtech.proj4j:proj4j-epsg:1.3.0") //optional && use spring boot dependency management
    implementation("org.locationtech.jts:jts-core:1.20.0") //optional && use spring boot dependency management

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.security:spring-security-oauth2-client")
// API
    implementation("org.springdoc:springdoc-openapi-starter-common:${property("openapiStarterCommonVersion")}")
    implementation("io.swagger.core.v3:swagger-core:${property("swaggerCoreVersion")}")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv")
    implementation("software.amazon.awssdk:s3:2.29.1")//use spring boot dependency management
    implementation("javax.xml.bind:jaxb-api:2.4.0-b180830.0359")

    implementation("org.springframework.kafka:spring-kafka:3.3.0")//get this dependencyy from :kafka use as api does not work
    implementation(project(":kafka"))

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    testImplementation(project(":auto-rest-doc"))
    testImplementation("org.springframework.kafka:spring-kafka-test:3.2.4")//get this dependencyy from :kafka use as api does not work
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc:3.0.2")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.mockito:mockito-core")

    testImplementation("org.springframework.boot:spring-boot-testcontainers")//use testImplementation?
    testImplementation("org.testcontainers:postgresql")//use testImplementation?

    testRuntimeOnly("org.postgresql:postgresql")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.getByName("assemble").dependsOn("testJar")
tasks.register<Jar>("testJar") {
    archiveFileName.set("base-atlas-$version-tests.jar")//use submodule name
//    include("com/eulenspiegel/helpers/*")
    from(project.the<SourceSetContainer>()["test"].output)
}
artifacts {
    add("test", tasks["testJar"])
}

tasks.bootJar {
    enabled = false
}

publishing {
    repositories {
        maven("https://bin.sbb.ch/artifactory/" + System.getenv("ARTIFACTORY_REPO")) {
            credentials {
                username = System.getenv("ARTIFACTORY_USER")
                password = System.getenv("ARTIFACTORY_PASS")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifactId = rootProject.name
            groupId = project.group.toString()
            version = project.version.toString()
        }
    }
}
