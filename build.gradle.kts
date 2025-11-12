plugins {
    id("org.sonarqube") version "7.0.1.6134"
}

group = "ch.sbb.atlas"
version = "2.947.0"

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
