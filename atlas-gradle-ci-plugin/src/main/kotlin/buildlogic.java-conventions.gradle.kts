/*
 * This project uses @Incubating APIs which are subject to change.
 */
plugins {
    java
    jacoco
    `java-library`
    `maven-publish`
}

group = "ch.sbb.atlas"
version = "2.350.0"

extra["awsS3Version"] = "2.29.1"
extra["swaggerCoreVersion"] = "2.2.25"
extra["openapiStarterCommonVersion"] = "2.6.0"
//Geo Data Libs
extra["proj4jVersion"] = "1.3.0"
extra["jtsVersion"] = "1.20.0"
extra["springOpenapiUiVersion"] = "2.6.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
}

dependencies {
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
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
            artifact("jar")
        }
    }
}

tasks.withType<Test> {
    failFast = true
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}
tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
    reports {
        xml.required = true
    }
}
tasks.withType<Jar> {
    enabled = true
    archiveClassifier.set("")
}