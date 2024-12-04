plugins {
    id("buildlogic.java-conventions")
}
dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springdoc:springdoc-openapi-starter-common:${property("openapiStarterCommonVersion")}")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")

}

task<JavaExec>("generateApiSpec") {
    doFirst {
        println("Run ApimConfigurationGeneratorApplication")
    }
    mainClass = "ch.sbb.atlas.apim.configuration.ApimConfigurationGeneratorApplication"
    classpath = sourceSets["main"].runtimeClasspath
    args(listOf(project.version))
    mustRunAfter(tasks.getByName("processResources"))
}

task<Zip>("createZipApimProd") {
    from("src/main/resources/api-prod/")
    destinationDirectory.set(layout.buildDirectory.dir("libs"))
}

task<Zip>("createZipApimDev") {
    archiveAppendix = "dev"
    from("src/main/resources/api-dev/")
    destinationDirectory.set(layout.buildDirectory.dir("libs"))
}

task<Zip>("createZipApimTest") {
    archiveAppendix = "test"
    from("src/main/resources/api-test/")
    destinationDirectory.set(layout.buildDirectory.dir("libs"))
}

task<Zip>("createZipApimInt") {
    archiveAppendix = "int"
    from("src/main/resources/api-int/")
    destinationDirectory.set(layout.buildDirectory.dir("libs"))
}

//tasks.check.get()
//    .dependsOn(tasks.getByName("generateApiSpec"))
//    .dependsOn(tasks.getByName("createZipApimProd"))
//    .dependsOn(tasks.getByName("createZipApimInt"))
//    .dependsOn(tasks.getByName("createZipApimTest"))
//    .dependsOn(tasks.getByName("createZipApimDev"))
