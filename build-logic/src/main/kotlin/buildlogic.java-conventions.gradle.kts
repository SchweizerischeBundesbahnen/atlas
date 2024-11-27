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
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

