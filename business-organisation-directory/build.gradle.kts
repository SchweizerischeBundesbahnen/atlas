plugins {
    id("buildlogic.java-conventions")
    id("com.github.bjornvester.wsdl2java") version "1.2"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("org.springframework.ws:spring-ws-core")
    implementation("org.springframework.ws:spring-ws-support")
    implementation("org.springframework.ws:spring-ws-security")
    implementation("com.sun.xml.messaging.saaj:saaj-impl")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")

    implementation("org.hibernate.orm:hibernate-jpamodelgen")
    annotationProcessor("org.hibernate.orm:hibernate-jpamodelgen:6.6.3.Final")

    implementation("com.sun.xml.ws:jaxws-ri:4.0.1")

    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("software.amazon.awssdk:s3:2.29.1")

    implementation("org.springframework.kafka:spring-kafka")

    implementation(project(":base-atlas"))
    implementation(project(":kafka"))
    implementation(project(":user-administration-security"))

    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")

    testImplementation(project(":base-atlas", "test"))
    testImplementation(project(":auto-rest-doc"))
}

wsdl2java {
    includes.add("${projectDir}/src/main/resources/crd/crd.wsdl")
    generatedSourceDir.set(layout.buildDirectory.dir("generated-sources/wsdl/src/main/java"))
    packageName.set("ch.sbb.business.organisation.directory.service.crd")
}

tasks.compileJava.get().dependsOn(tasks.wsdl2java)

springBoot {
    buildInfo()
}
