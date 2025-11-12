import org.gradle.api.tasks.Copy

val dockerContextDir: Provider<Directory> = layout.buildDirectory.dir("docker")
val baseImageName: String = "atlas.docker.bin.sbb.ch/atlas/"

tasks.register<Copy>("prepareDockerContext") {
    group = "docker"
    description = "Prepare Docker build context (copies JAR and Dockerfile into build/docker)."

    // Ensure the jar task runs before copying
    dependsOn(tasks.named("jar"))

    // Where to put files
    into(dockerContextDir)

    // Copy the Dockerfile from project root
    from("Dockerfile") {
        rename { "Dockerfile" }
    }

    // Copy the JAR produced by the jar task
    from(layout.buildDirectory.dir("libs")) {
        include("*.jar")
    }

    // Copy other files needed in the context, e.g., application.conf, scripts, etc.
    from("docker") {
        into(".")
        include("**/*")
    }

    outputs.dir(dockerContextDir)
}

tasks.register<Exec>("buildDocker") {
    group = "docker"
    description = "Build Docker image from build/docker context."
    val dockerImageName = project.name
    val dockerTag = "${project.version}"

    dependsOn(tasks.named("prepareDockerContext"))

    // set working dir to the prepared context
    workingDir = dockerContextDir.get().asFile
    if (!project.hasProperty("buildOnTekton")) {
        println("Runnig docker build from local machine wiht docker cli...")
        commandLine = listOf("docker", "build", "-t", "$baseImageName$dockerImageName:$dockerTag", ".")
        isIgnoreExitValue = false
    } else {
        println("Runnig docker build from tekton buildah...")
        commandLine = listOf(
            "buildah",
            "bud",
            "--cert-dir",
            "--tls-verify=false",
            "-t", "$baseImageName$dockerImageName:$dockerTag",
            "--no-cache",
            "."
        )
    }
}

tasks.register<Exec>("publishDocker") {
    group = "docker"
    description = "Build Docker image from build/docker context."
    val dockerImageName = project.name
    val dockerTag = "${project.version}"

    dependsOn(tasks.named("buildDocker"))

    if (project.hasProperty("buildOnTekton")) {
        println("Runnig docker build from tekton buildah...")
        commandLine = listOf(
            "buildah",
            "push",
            "--format",
            "v2s2",
            "--tls-verify=false",
            "$baseImageName$dockerImageName:$dockerTag"
        )
    } else {
        commandLine = listOf("echo","Publishing Docker images is only allowed on Tekton!")
    }
}
