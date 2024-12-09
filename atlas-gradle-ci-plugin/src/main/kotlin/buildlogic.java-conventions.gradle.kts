import org.asciidoctor.gradle.jvm.AsciidoctorTask
import java.util.*
import java.text.SimpleDateFormat

plugins {
    java
    jacoco
    `java-library`
    `maven-publish`
    id("org.asciidoctor.jvm.convert")
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
        // ARTIFACTORY REPO can be esta.mvn and so on
        maven("https://bin.sbb.ch/artifactory/" + System.getenv("ARTIFACTORY_REPO")) {
            val usr = System.getenv("ARTIFACTORY_USER")
            val pwd = System.getenv("ARTIFACTORY_PASS")
            val apiKey = System.getenv("ARTIFACTORY_API_KEY")
            if (usr != null && pwd != null) {
                credentials {
                    username = usr
                    password = pwd
                }
            } else if (apiKey != null) {
                credentials(HttpHeaderCredentials::class) {
                    name = "Authorization"
                    value = "Bearer " + apiKey
                }
                authentication {
                    create<HttpHeaderAuthentication>("header")
                }
            } else {
                logger.warn("Cannot publish!! No credentials found for Artifactory! Either provide ARTIFACTORY_USER and ARTIFACTORY_PASS or ARTIFACTORY_API_KEY as environment variables.")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifactId = project.name
            groupId = project.group.toString()
            version = project.version.toString()
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

tasks.named<AsciidoctorTask>("asciidoctor") {
    dependsOn(tasks.test) // tests are required to run before generating the report

    options(
        mapOf(
            "doctype" to "book",
            "backend" to "html"
        )
    )

    attributes(
        mapOf(
            "buildTime" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
            "name" to project.name,
            "version" to project.version,
            "snippets" to "${layout.buildDirectory.get()}/generated-snippets"
        )
    )

    setSourceDir("${project.projectDir}/src/docs/asciidocs")
    setOutputDir("${layout.buildDirectory.get()}/classes/static")

    // For .adoc files to be able to use relative includes we need to set the baseDir to the sourceFile
    baseDirFollowsSourceFile()
}

task<Copy>("copyRestDocs") {
    dependsOn("asciidoctor")
    mustRunAfter(tasks.jacocoTestReport)
    mustRunAfter(tasks.getByName("resolveMainClassName"))

    from("${tasks.asciidoctor.get().outputDir}")
    from("${project.rootProject.projectDir}/auto-rest-doc/src/main/resources/layout/images/logo-atlas.svg")
    into("${layout.buildDirectory.get()}/resources/main/static")
}

tasks.jar {
    mustRunAfter("copyRestDocs")
}

tasks.getByName("bootJar").dependsOn("copyRestDocs")
tasks.getByName("bootRun").dependsOn("copyRestDocs")