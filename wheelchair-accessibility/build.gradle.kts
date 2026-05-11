plugins {
    id("buildlogic.java-conventions")
}

group = "ch.sbb.atlas"
version = "1.0.0"

dependencies {
    // Wir brauchen base-atlas für die Enums (BooleanOptionalAttributeType etc.)
    implementation(project(":base-atlas"))

    // Lombok (wenn ihr Lombok nutzt)
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")

    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
}

tasks.bootJar {
    enabled = false
}
