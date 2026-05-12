plugins {
    id("buildlogic.java-conventions")
}

group = "ch.sbb.atlas"
version = "1.0.0"

dependencies {
    implementation(project(":base-atlas"))

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
}

tasks.bootJar {
    enabled = false
}
