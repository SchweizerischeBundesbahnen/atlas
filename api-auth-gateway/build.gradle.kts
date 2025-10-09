import java.util.*

plugins {
    id("buildlogic.java-conventions")
}

group = "ch.sbb.atlas"
version = "2.889.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.cloud:spring-cloud-starter-gateway-server-webflux")
    implementation("org.springframework.security:spring-security-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-configuration-processor")//optional

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.squareup.okhttp3:mockwebserver:${property("okhttpVersion")}")
    testImplementation("com.squareup.okhttp3:okhttp:${property("okhttpVersion")}")

}

tasks.named<Jar>("jar") {
    enabled = false
}

springBoot {
    buildInfo {
        properties {
            additional.set(
                mapOf(
                    "time" to "${Date()}"
                )
            )
        }
    }
}