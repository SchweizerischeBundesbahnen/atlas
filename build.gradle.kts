plugins {
    id("org.sonarqube") version "7.0.1.6134"
}

group = "ch.sbb.atlas"
version = "2.924.0"

extra["awsS3Version"] = "2.37.1"
extra["jaxbApiVersion"] = "2.4.0-b180830.0359"
extra["pdfboxVersion"] = "3.0.6"
extra["okhttpVersion"] = "5.2.1"

extra["swaggerCoreVersion"] = "2.2.40"
extra["openapiStarterCommonVersion"] = "2.8.13"

// Geo Data Libs
extra["proj4jVersion"] = "1.4.1"
extra["jtsVersion"] = "1.20.0"

// Spring Versions
extra["springOpenapiUiVersion"] = "2.8.13"
extra["springCloudVersion"] = "2025.0.0"

subprojects {
    sonar {
        properties {
            property("sonar.projectKey", "ch.sbb.atlas:atlas")
            property("sonar.projectVersion", project.version)
            property("sonar.dynamicAnalysis", "reuseReports")
            property("sonar.java.coveragePlugin", "jacoco")
            property(
                "sonar.exclusions",
                "**/node_modules/**,**/*.spec.ts,**/*.module.ts,**/*.routes.ts,**/karma.conf.js," +
                        "**/instana.js,**/polyfills.ts,**/cypress/**,**/db/migration/**/*,**/*.kts"
            )
        }
    }
    if (project.name == "frontend") {
        sonar {
            properties {
                property("sonar.projectKey", "ch.sbb.atlas:atlas")
                property("sonar.projectVersion", project.version)
                property(
                    "sonar.exclusions",
                    "**/node_modules/**,**/*.spec.ts,**/*.module.ts,**/*.routes.ts,**/karma.conf.js,**/*.kts"
                )
                property("sonar.sources", "./")
                property("sonar.language", "ts")
                property("sonar.profile", "TsLint")
                property("sonar.verbose", "true")
                property("sonar.test.inclusion", "**/*.spec.ts")
                property("sonar.ts.tslint.configPath", "tslint.json")
                property("sonar.typescript.lcov.reportPaths",
                    "${project.projectDir}/components/coverage/atlas-workspaces/lcov.info,${project.projectDir}/coverage/atlas-frontend/lcov.info")
                property("sonar.coverage.exclusions", "**/*.spec.ts,**/cypress/**,/**/*.module.ts")
            }
        }
    }
}
