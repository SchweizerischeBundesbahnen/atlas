plugins {
    id("buildlogic.java-conventions")
}

group = "ch.sbb.atlas"
version = "2.1439.0"

dependencies {
    implementation(project(":base-atlas"))

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.bootJar {
    enabled = false
}
