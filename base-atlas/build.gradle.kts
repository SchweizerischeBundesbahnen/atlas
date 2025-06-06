plugins {
    id("org.openapi.generator") version "7.13.0"
    id("buildlogic.java-conventions")
}

group = "ch.sbb.atlas"
version = "2.656.0"

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
    implementation("org.locationtech.proj4j:proj4j:${property("proj4jVersion")}") //optional
    implementation("org.locationtech.proj4j:proj4j-epsg:${property("proj4jVersion")}") //optional
    implementation("org.locationtech.jts:jts-core:${property("jtsVersion")}") //optional

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.security:spring-security-oauth2-client")
// API
    implementation("io.swagger.core.v3:swagger-core:${property("swaggerCoreVersion")}")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv")
    implementation("software.amazon.awssdk:s3:${property("awsS3Version")}")
    implementation("javax.xml.bind:jaxb-api:2.4.0-b180830.0359")
    implementation("org.apache.pdfbox:pdfbox:3.0.5")

    implementation("org.springframework.kafka:spring-kafka")//get this dependency from :kafka use as api does not work
    implementation(project(":kafka"))

    annotationProcessor("org.hibernate.orm:hibernate-jpamodelgen")

    testImplementation(project(":auto-rest-doc"))
    testImplementation("org.springframework.kafka:spring-kafka-test")//get this dependency from :kafka use as api does not work
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")

    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:postgresql")

    testRuntimeOnly("org.postgresql:postgresql")

}

//used to create the base-atlas-test jar
tasks.getByName("assemble").dependsOn("testJar")

//used to create the base-atlas-test jar
tasks.register<Jar>("testJar") {
    archiveFileName.set("base-atlas-$version-tests.jar")//use submodule name
    from(project.the<SourceSetContainer>()["test"].output)
}

//used to create the base-atlas-test jar
artifacts {
    add("test", tasks["testJar"])
}

tasks.bootJar {
    enabled = false
}
