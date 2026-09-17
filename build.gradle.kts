plugins {
    id("org.sonarqube") version "7.5.0.8588"
}

group = "ch.sbb.atlas"
version = "2.1571.0"

// A subproject is considered a runnable Spring Boot service (rather than a shared library module)
// when it applies the "buildlogic.docker-java" convention plugin, recognizable by its "prepareJavaDockerContext" task.
// This is the same signal used to build/publish Docker images for services.
val bootRunAllTask = tasks.register("bootRunAll") {
    group = "application"
    description = "Starts all Spring Boot services in this monorepo with the 'local' profile. " +
            "Must be run with --parallel, e.g. './gradlew bootRunAll --parallel'."
    val parallelEnabled = gradle.startParameter.isParallelProjectExecutionEnabled
    doFirst {
        if (!parallelEnabled) {
            throw GradleException(
                "bootRunAll must be run with --parallel, e.g. './gradlew bootRunAll --parallel', " +
                        "otherwise the services block each other since bootRun never completes."
            )
        }
    }
}

subprojects {
    // Default all services started via bootRunAll to the 'local' profile, and wire them into bootRunAll.
    // Deferred with afterEvaluate since the docker-java convention plugin (and its tasks) is only applied
    // once each subproject's own build script has run.
    afterEvaluate {
        if (tasks.findByName("prepareJavaDockerContext") != null) {
            val servicePath =
                path // capture now: inside bootRunAllTask.configure{}, "project" would resolve to the root project instead
            bootRunAllTask.configure {
                dependsOn("$servicePath:bootRun")
            }
            tasks.matching { it.name == "bootRun" }.configureEach {
                (this as JavaExec).args("--spring.profiles.active=local")
            }
        }
    }

    sonar {
        properties {
            property("sonar.projectKey", "ch.sbb.atlas:atlas")
            property("sonar.projectVersion", project.version)
            property("sonar.dynamicAnalysis", "reuseReports")
            property("sonar.java.coveragePlugin", "jacoco")
            property(
                "sonar.exclusions",
                "**/node_modules/**,**/*.spec.ts,**/*.module.ts,**/*.routes.ts,**/karma.conf.js," +
                        "**/instana.js,**/polyfills.ts,**/cypress/**,**/db/migration/**/*,**/*.kts,**/*Config.java"
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
                    "**/node_modules/**,**/*.spec.ts,**/*.module.ts,**/*.routes.ts,**/karma.conf.js,**/*.kts,**/src/main.ts, " +
                            "**/eslint.config.js, **/tick-async.ts"
                )
                property("sonar.sources", "./")
                property("sonar.language", "ts")
                property("sonar.profile", "TsLint")
                property("sonar.verbose", "true")
                property("sonar.test.inclusion", "**/*.spec.ts")
                property("sonar.ts.tslint.configPath", "tslint.json")
                property(
                    "sonar.typescript.lcov.reportPaths",
                    "${project.projectDir}/coverage/form/lcov.info,${project.projectDir}/coverage/atlas-frontend/lcov.info"
                )
                property("sonar.coverage.exclusions", "**/*.spec.ts,**/cypress/**,/**/*.module.ts,**/src/main.ts")
            }
        }
    }
}
