import org.gradle.api.tasks.Copy

val dockerContextDir: Provider<Directory> = layout.buildDirectory.dir("docker")
val projectName = project.name
val baseImageName: String = "atlas.docker.bin.sbb.ch/atlas/${projectName}"

tasks.register<Copy>("prepareDockerContext") {
    group = "docker"
    description = "Prepare Docker build context (copies JAR and Dockerfile into build/docker)."

    // Ensure the jar task runs before copying
    dependsOn(tasks.named("bootJar"))
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
    val dockerTag = "${project.version}"
    val imageName = "$baseImageName:$dockerTag"
    dependsOn(tasks.named("prepareDockerContext"))

    // set working dir to the prepared context
    workingDir = dockerContextDir.get().asFile

    if (!project.hasProperty("buildOnTekton")) {
        println("Runnig docker build from local machine whit docker cli for project ${projectName}...")
        commandLine = listOf("docker", "build", "-t", imageName, ".")
        isIgnoreExitValue = false
    } else {
        println("Runnig build docker from tekton buildah for project ${projectName}...")
        commandLine = listOf(
            "buildah",
            "bud",
            "--cert-dir",
            "--tls-verify=false",
            "-t", imageName,
            "--no-cache",
            "."
        )
    }
}

tasks.register<Exec>("publishDocker") {
    group = "docker"
    description = "Build Docker image from build/docker context."

    val dockerTag = "${project.version}"
    val imageName = "$baseImageName:$dockerTag"

    dependsOn(tasks.named("buildDocker"))

    if (project.hasProperty("buildOnTekton")) {
        println("Runnig publish docker from tekton buildah for project ${projectName}...")
        commandLine = listOf(
            "buildah",
            "push",
            "--format",
            "v2s2",
            "--tls-verify=false",
            imageName
        )
        isIgnoreExitValue = false
    } else {
        commandLine = listOf("echo", "Publishing Docker images is only allowed on Tekton!")
    }
}
