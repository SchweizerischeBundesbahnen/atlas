plugins {
    alias(libs.plugins.openapi.generator)
    id("buildlogic.java-conventions")
}

group = "ch.sbb.atlas"
version = "2.1108.0"

configurations {
    create("test") //used to create the base-atlas-test jar
}

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-kafka")

    // Spring Cloud
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

    // Micrometer & Tracing
    implementation("org.springframework.boot:spring-boot-micrometer-tracing-brave")
    implementation("io.micrometer:micrometer-tracing-bridge-brave")

    implementation("io.github.openfeign:feign-micrometer")

    // Hibernate
    implementation("org.hibernate.orm:hibernate-processor")

    // Libraries
    implementation(libs.bundles.geo.data)
    implementation(libs.swagger.core)
    implementation(libs.aws.s3)
    implementation(libs.jaxb.api)
    implementation(libs.pdfbox)
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv")

    // Project dependencies
    implementation(project(":kafka"))

    annotationProcessor("org.hibernate.orm:hibernate-processor")

    // Test dependencies
    testImplementation(project(":auto-rest-doc"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-micrometer-tracing-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.testcontainers:testcontainers-postgresql")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")

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

tasks.bootJar {
    enabled = false
}

// used to create the base-atlas-test jar
artifacts {
    add("test", tasks["testJar"])
}
