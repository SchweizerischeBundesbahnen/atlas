plugins {
    id("io.spring.dependency-management") version "1.1.6"
    id("org.springframework.boot") version "3.3.4"
    id("org.sonarqube") version "6.0.1.5171"
}



subprojects {
    apply(plugin = "org.sonarqube")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    extra["springCloudVersion"] = "2023.0.3"

    dependencyManagement {
        imports {
            mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
        }
    }
}

allprojects {
    sonar {
        properties {
            property("sonar.projectName", "ATLAS - SKI Business Application")
            property("sonar.projectKey", "ch.sbb.atlas:atlas")
            property("sonar.sources", "src/main/java, src/app")
            property("sonar.dynamicAnalysis", "reuseReports")
            property("sonar.java.coveragePlugin", "jacoco")
            property("sonar.exclusions", "**/node_modules/**,**/src/app/api/**,**/*.spec.ts,**/*.module.ts,**/*.routes.ts,**/karma.conf.js,**/instana.js,**/polyfills.ts,**/cypress/**,**/db/migration/**/*")
            property("sonar.test.inclusion", "**/*.spec.ts")
            property("sonar.ts.tslint.configPath", "tslint.json")
            property("sonar.ts.coverage.lcovReportPath", "coverage/atlas-frontend/lcov.info")
            property("sonar.typescript.lcov.reportPaths", "${project.projectDir}/coverage/atlas-frontend/lcov.info")
        }
    }
}