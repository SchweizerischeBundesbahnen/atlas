plugins {
    id("buildlogic.java-conventions")
}

group = "ch.sbb.atlas"
version = "2.1553.0"

dependencies {
    // Libraries
    implementation(libs.pdfbox)

    // Test dependencies
    testImplementation("commons-codec:commons-codec")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
}

tasks.bootJar {
    enabled = false
}
