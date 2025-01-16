plugins {
    id("org.sonarqube") version "6.0.1.5171"
}

group = "ch.sbb.atlas"
version = "2.383.0"

subprojects {
        sonar {
            properties {
                property("sonar.projectKey", "ch.sbb.atlas:atlas")
                property("sonar.projectVersion", project.version)
                property("sonar.dynamicAnalysis", "reuseReports")
                property("sonar.java.coveragePlugin", "jacoco")
                property(
                    "sonar.exclusions",
                    "**/node_modules/**,**/src/app/api/**,**/*.spec.ts,**/*.module.ts,**/*.routes.ts,**/karma.conf.js," +
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