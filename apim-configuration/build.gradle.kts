plugins {
    id("buildlogic.java-conventions")
}

group = "ch.sbb.atlas"
version = "2.497.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springdoc:springdoc-openapi-starter-common:${property("openapiStarterCommonVersion")}")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
}

configurations {
    create("uploadZipDev")
    create("uploadZipTest")
    create("uploadZipInt")
    create("uploadZipProd")
}

val zipDirectoryDev = layout.buildDirectory.dir("libs/apim-configuration-dev-${project.version}.zip")
val zipArtifactDev = artifacts.add("uploadZipDev", zipDirectoryDev.get().asFile) {
    type = "zip"
    builtBy("createZipApimDev")
    name = "apim-configuration-dev-${project.version}.zip"
}

val zipDirectoryTest = layout.buildDirectory.dir("libs/apim-configuration-test-${project.version}.zip")
val zipArtifactTest = artifacts.add("uploadZipTest", zipDirectoryTest.get().asFile) {
    type = "zip"
    builtBy("createZipApimTest")
    name = "apim-configuration-test-${project.version}.zip"
}

val zipDirectoryInt = layout.buildDirectory.dir("libs/apim-configuration-int-${project.version}.zip")
val zipArtifactInt = artifacts.add("uploadZipInt", zipDirectoryInt.get().asFile) {
    type = "zip"
    builtBy("createZipApimInt")
    name = "apim-configuration-int-${project.version}.zip"
}

val zipDirectoryProd = layout.buildDirectory.dir("libs/apim-configuration-${project.version}.zip")
val zipArtifactProd = artifacts.add("uploadZipProd", zipDirectoryProd.get().asFile) {
    type = "zip"
    builtBy("createZipApimProd")
    name = "apim-configuration-${project.version}.zip"
}


tasks.register<JavaExec>("generateApiSpec", fun JavaExec.() {
    mainClass = "ch.sbb.atlas.apim.configuration.ApimConfigurationGeneratorApplication"
    classpath = sourceSets["main"].runtimeClasspath
    args(listOf(project.version))
    mustRunAfter(tasks.getByName("processResources"))
})

tasks.register<Zip>("createZipApimProd", fun Zip.() {
    from("src/main/resources/api-prod/")
    destinationDirectory.set(layout.buildDirectory.dir("libs"))
    mustRunAfter(tasks.getByName("generateApiSpec"))
})

tasks.register<Zip>("createZipApimDev", fun Zip.() {
    archiveAppendix = "dev"
    from("src/main/resources/api-dev/")
    destinationDirectory.set(layout.buildDirectory.dir("libs"))
    mustRunAfter(tasks.getByName("generateApiSpec"))
})

tasks.register<Zip>("createZipApimTest", fun Zip.() {
    archiveAppendix = "test"
    from("src/main/resources/api-test/")
    destinationDirectory.set(layout.buildDirectory.dir("libs"))
    mustRunAfter(tasks.getByName("generateApiSpec"))
})

tasks.register<Zip>("createZipApimInt", fun Zip.() {
    archiveAppendix = "int"
    from("src/main/resources/api-int/")
    destinationDirectory.set(layout.buildDirectory.dir("libs"))
    mustRunAfter(tasks.getByName("generateApiSpec"))
})

tasks.register("generateSpec") {
    dependsOn(tasks.getByName("generateApiSpec"))
        .dependsOn(tasks.getByName("createZipApimProd"))
        .dependsOn(tasks.getByName("createZipApimInt"))
        .dependsOn(tasks.getByName("createZipApimTest"))
        .dependsOn(tasks.getByName("createZipApimDev"))
}

publishing {
    publications {
        create<MavenPublication>("publishApimZips") {
            artifact(zipArtifactDev) {
                extension = "zip"
                classifier = "dev"
            }
            artifact(zipArtifactTest) {
                extension = "zip"
                classifier = "test"
            }
            artifact(zipArtifactInt) {
                extension = "zip"
                classifier = "int"
            }
            artifact(zipArtifactProd) {
                extension = "zip"
                classifier = ""
            }
        }
    }
}