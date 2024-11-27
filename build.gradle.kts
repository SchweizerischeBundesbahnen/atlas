plugins {
    id("org.sonarqube") version "5.0.0.4638"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.springframework.boot") version "3.3.4"
}

subprojects {
    apply(plugin = "org.sonarqube")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    extra["springCloudVersion"] = "2023.0.3"
    extra["awsS3Version"] = "2.29.1"
    extra["swaggerCoreVersion"] = "2.2.25"
    extra["openapiStarterCommonVersion"] = "2.6.0"
    //Geo Data Libs
    extra["proj4jVersion"] = "1.3.0"
    extra["jtsVersion"] = "1.20.0"

    dependencyManagement {
        imports {
            mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
        }
    }

    repositories {
        mavenCentral()
        maven { url = uri("https://repo.spring.io/milestone") }
    }
}