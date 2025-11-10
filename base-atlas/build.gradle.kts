plugins {
    alias(libs.plugins.openapi.generator)
    id("buildlogic.java-conventions")
}

group = "ch.sbb.atlas"
version = "2.944.0"

configurations {
    create("test") //used to create the base-atlas-test jar
}

dependencies {
// For BaseVersion
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.hibernate.orm:hibernate-jpamodelgen")
// For UserService
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
// For correlation id
    implementation("io.micrometer:micrometer-tracing")
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
// Feign Client Specific Micrometer
    implementation("io.github.openfeign:feign-micrometer")
// Service Point and ExportService
    implementation(libs.bundles.geo.data) //optional

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.security:spring-security-oauth2-client")
// API
    implementation(libs.swagger.core)
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv")
    implementation(libs.aws.s3)
    implementation(libs.jaxb.api)
    implementation(libs.pdfbox)

    implementation("org.springframework.kafka:spring-kafka")//get this dependency from :kafka use as api does not work
    implementation(project(":kafka"))

    annotationProcessor("org.hibernate.orm:hibernate-jpamodelgen")

    testImplementation(project(":auto-rest-doc"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")

    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:postgresql")

    testRuntimeOnly("org.postgresql:postgresql")

}

// used to create the base-atlas-test jar
tasks.getByName("assemble").dependsOn("testJar")

tasks.register<Jar>("testJar") {
    description = "Create the base-atlas-test jar"
    group = "verification"
    archiveFileName.set("base-atlas-$version-tests.jar")//use submodule name
    from(project.the<SourceSetContainer>()["test"].output)
}

// used to create the base-atlas-test jar
artifacts {
    add("test", tasks["testJar"])
}

tasks.bootJar {
    enabled = false
}
