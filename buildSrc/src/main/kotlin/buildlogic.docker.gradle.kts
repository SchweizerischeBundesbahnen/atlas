import org.gradle.api.tasks.Copy
import docker.registerBuildDockerTask
import docker.registerPublishDockerTask

val dockerContextDir: Provider<Directory> = layout.buildDirectory.dir("docker")
val projectName = project.name
val baseImageName: String = "atlas.docker.bin.sbb.ch/atlas/${projectName}"

val prepareDockerContext = tasks.register<Copy>("prepareDockerContext") {
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

val buildDocker = registerBuildDockerTask(
    project = project,
    taskName = "buildDocker",
    dockerContextDir = dockerContextDir,
    baseImageName = baseImageName,
    prepareDockerContext = prepareDockerContext
)

registerPublishDockerTask(
    project = project,
    taskName = "publishDocker",
    baseImageName = baseImageName,
    buildDocker = buildDocker,
)
