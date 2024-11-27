plugins {
    id("org.sonarqube") version "5.0.0.4638"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.springframework.boot") version "3.3.4"
}

subprojects {
    apply(plugin= "java")
    apply(plugin= "java-library")
    apply(plugin= "jacoco")
    apply(plugin= "maven-publish")
    apply(plugin= "org.sonarqube")
    apply(plugin= "org.springframework.boot")
    apply(plugin= "io.spring.dependency-management")

    dependencyManagement {
        imports {
            mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
        }
    }

    repositories {
        mavenCentral()
        maven { url = uri("https://repo.spring.io/milestone") }
    }
}