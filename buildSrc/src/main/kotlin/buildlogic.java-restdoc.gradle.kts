import org.asciidoctor.gradle.jvm.AsciidoctorTask
import java.util.*
import java.text.SimpleDateFormat

plugins {
    id("org.asciidoctor.jvm.convert")
}

tasks.named<AsciidoctorTask>("asciidoctor") {
    dependsOn(tasks.getByName("test")) // tests are required to run before generating the report

    options(
        mapOf(
            "doctype" to "book",
            "backend" to "html"
        )
    )

    attributes(
        mapOf(
            "buildTime" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
            "name" to project.name,
            "version" to project.version,
            "snippets" to "${layout.buildDirectory.get()}/generated-snippets"
        )
    )

    setSourceDir("${project.projectDir}/src/docs/asciidocs")
    setOutputDir("${layout.buildDirectory.get()}/classes/static")

    // For .adoc files to be able to use relative includes we need to set the baseDir to the sourceFile
    baseDirFollowsSourceFile()
    doLast {
        copy {
            println("Coping RestDoc atlas layout...")
            from("${tasks.asciidoctor.get().outputDir}")
            from("${project.rootProject.projectDir}/auto-rest-doc/src/main/resources/layout/images/logo-atlas.svg")
            into("${layout.buildDirectory.get()}/resources/main/static")
            println("RestDoc atlas layout copied...")
        }
    }
}

tasks.named<Jar>("jar") {
    enabled = false
}

if(hasProperty("generateAsciidoc")) {
    println("Generate Asciidoc on bootJar enabled....")
    tasks.named("bootJar") {
        println("Generating Asciidoc....")
        dependsOn("asciidoctor")
    }
}

// to run application with --configuration-cache you need to comment the following snippet
// (e.g. ./gradlew :line-directory:bootRun --args='--spring.profiles.active=local' --configuration-cache )
if(hasProperty("generateAsciidoc")) {
    println("Generate Asciidoc on bootRun enabled....")
    tasks.named("bootRun") {
        println("Generating Asciidoc....")
        dependsOn("asciidoctor")
    }
}

if(!hasProperty("generateAsciidoc")){
    println("Generate Asciidoc disabled...")
}