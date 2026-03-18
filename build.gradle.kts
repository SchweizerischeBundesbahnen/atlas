plugins {
    id("org.sonarqube") version "7.2.3.7755"
}

group = "ch.sbb.atlas"
version = "2.1169.0"

subprojects {
    sonar {
        properties {
            property("sonar.projectKey", "ch.sbb.atlas:atlas-github-playground")
            property("sonar.projectVersion", project.version)
            property("sonar.dynamicAnalysis", "reuseReports")
            property("sonar.java.coveragePlugin", "jacoco")
            property(
                "sonar.exclusions",
"components/coverage/**/*,coverage/**,build/**/*," +
                "**/node_modules/**,**/*.spec.ts,**/*.module.ts,**/*.routes.ts,**/karma.conf.js," +
                        "**/instana.js,**/polyfills.ts,**/cypress/**,**/db/migration/**/*,**/*.kts,**/*Config.java"
            )
        }
    }
    if (project.name == "frontend") {
        sonar {
            properties {
                property("sonar.projectKey", "ch.sbb.atlas:atlas-github-playground")
                property("sonar.projectVersion", project.version)
                property(
                    "sonar.exclusions",
"components/coverage/**/*,coverage/**,build/**/*," +
                    "**/node_modules/**,**/*.spec.ts,**/*.module.ts,**/*.routes.ts,**/karma.conf.js,**/*.kts,**/src/main.ts, " +
                            "**/eslint.config.js, **/tick-async.ts"
                )
                property("sonar.sources", "./")
                property("sonar.language", "ts")
                property("sonar.profile", "TsLint")
                property("sonar.verbose", "true")
                property("sonar.test.inclusion", "**/*.spec.ts")
                property("sonar.ts.tslint.configPath", "tslint.json")
                property("sonar.typescript.lcov.reportPaths",
                    "${project.projectDir}/coverage/components/lcov.info,${project.projectDir}/coverage/atlas-frontend/lcov.info"
                )
                property("sonar.coverage.exclusions", "**/*.spec.ts,**/cypress/**,/**/*.module.ts,**/src/main.ts")
            }
        }
    }
}
