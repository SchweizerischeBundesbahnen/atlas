plugins {
    id("buildlogic.java-conventions")
}

group = "ch.sbb.atlas"
version = "2.1565.0"

configurations {
    create("test") //used to create the atlas-s3-test jar
}

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-webmvc")

    // Libraries
    implementation(libs.aws.s3)

    // Project dependencies
    implementation(project(":base-atlas"))

    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-kafka")
    testImplementation(project(":base-atlas", "test"))
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.testcontainers:testcontainers-postgresql")

    testRuntimeOnly("org.postgresql:postgresql")
}

// used to create the atlas-s3-test jar
tasks.getByName("assemble").dependsOn("testJar")

tasks.register<Jar>("testJar") {
    description = "Create the atlas-s3-test jar"
    group = "verification"
    archiveFileName.set("atlas-s3-$version-tests.jar")//use submodule name
    from(project.the<SourceSetContainer>()["test"].output)
}

tasks.bootJar {
    enabled = false
}

// used to create the atlas-s3-test jar
artifacts {
    add("test", tasks["testJar"])
}
