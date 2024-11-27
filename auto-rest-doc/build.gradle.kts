plugins {
    id("org.openapi.generator") version "7.10.0"
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

extra["springCloudVersion"] = "2023.0.3"
extra["swaggerCoreVersion"] = "2.2.25"
extra["openapiStarterCommonVersion"] = "2.6.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")//need it?
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springdoc:springdoc-openapi-starter-common:${property("openapiStarterCommonVersion")}")
    implementation("io.swagger.core.v3:swagger-core:${property("swaggerCoreVersion")}")
    implementation("org.springframework.restdocs:spring-restdocs-mockmvc")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.mockito:mockito-core")

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
