import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    java
    jacoco
    `java-library`
    `maven-publish`
    id("io.spring.dependency-management")
    id("org.springframework.boot")
}

apply(plugin = "io.spring.dependency-management")
apply(plugin = "org.springframework.boot")

extra["awsS3Version"] = "2.29.1"
extra["swaggerCoreVersion"] = "2.2.25"
extra["openapiStarterCommonVersion"] = "2.7.0"
//Geo Data Libs
extra["proj4jVersion"] = "1.3.0"
extra["jtsVersion"] = "1.20.0"
extra["springOpenapiUiVersion"] = "2.7.0"
extra["springCloudVersion"] = "2024.0.0"

val mockitoAgent: Configuration = configurations.create("mockitoAgent")

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencyManagement {
    imports {
        mavenBom(SpringBootPlugin.BOM_COORDINATES)
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
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
    mockitoAgent("org.mockito:mockito-core") {
        isTransitive = false
    }
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

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
    testLogging{
        events("passed", "skipped", "failed","standardOut", "standardError")
        showCauses = true
    }
    jvmArgs = listOf("-javaagent:${mockitoAgent.asPath}","-Xshare:off")
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.withType<JavaCompile>().configureEach {
    options.isIncremental = true
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
    reports {
        xml.required = true
    }
}