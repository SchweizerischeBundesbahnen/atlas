import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    id("io.spring.dependency-management") version "1.1.6"
    id("org.springframework.boot") version "3.4.0"
    id("org.sonarqube") version "6.0.1.5171"
}

group = "ch.sbb.atlas"
version = "2.373.0"

subprojects {
    if (project.name != "frontend") {
        apply(plugin = "org.sonarqube")
        apply(plugin = "org.springframework.boot")
        apply(plugin = "io.spring.dependency-management")

        extra["springCloudVersion"] = "2023.0.4"

        dependencyManagement {
            imports {
                mavenBom(SpringBootPlugin.BOM_COORDINATES)
                mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
            }
        }
        sonar {
            properties {
                property("sonar.dynamicAnalysis", "reuseReports")
                property("sonar.java.coveragePlugin", "jacoco")
                property(
                    "sonar.exclusions",
                    "**/node_modules/**,**/src/app/api/**,**/*.spec.ts,**/*.module.ts,**/*.routes.ts,**/karma.conf.js," +
                            "**/instana.js,**/polyfills.ts,**/cypress/**,**/db/migration/**/*,**/*.kts"
                )
            }
        }
    }

    if (project.name == "frontend") {
        sonar {
            properties {
                property(
                    "sonar.exclusions",
                    "**/node_modules/**,**/src/app/api/**,**/*.spec.ts,**/*.module.ts,**/*.routes.ts,**/karma.conf.js,**/*.kts"
                )
                property("sonar.sources", "./")
                property("sonar.language", "ts")
                property("sonar.profile", "TsLint")
                property("sonar.verbose", "true")
                property("sonar.test.inclusion", "**/*.spec.ts")
                property("sonar.ts.tslint.configPath", "tslint.json")
                property("sonar.ts.coverage.lcovReportPath", "coverage/atlas-frontend/lcov.info")
                property("sonar.typescript.lcov.reportPaths", "${project.projectDir}/coverage/atlas-frontend/lcov.info")
                property("sonar.coverage.exclusions", "**/*.spec.ts,**/src/app/api/**,**/cypress/**,/**/*.module.ts")
            }
        }
    }
}

