import org.gradle.api.tasks.Copy
import docker.registerBuildDockerTask
import docker.registerPublishDockerTask

val dockerContextDir: Provider<Directory> = layout.buildDirectory.dir("docker")
val projectName = project.name
val baseImageName: String = "atlas.docker.bin.sbb.ch/atlas/atlas-${projectName}"

tasks.register<Copy>("copyDockerDir") {
    val dockerContextDir: Provider<Directory> = layout.buildDirectory.dir("docker/docker")
    group = "docker"
    description = "Copy docker directory."

    into(dockerContextDir)

    // Copy other files needed in the context, e.g., application.conf, scripts, etc.
    from(layout.projectDirectory.dir("docker")) {
        include("**/*")
        exclude("Dockerfile")
    }

    outputs.dir(dockerContextDir)
}

tasks.register<Copy>("copyFrontendDist") {
    val dockerContextDir: Provider<Directory> = layout.buildDirectory.dir("docker/dist/atlas-frontend")
    group = "docker"
    description = "Copy frontent distribution directory."
    dependsOn("copyDockerDir")
    into(dockerContextDir)
    //TODO: depends on execNpmBuild ?

    from(layout.projectDirectory.dir("dist/atlas-frontend")) {
        include("**/*")
    }

    outputs.dir(dockerContextDir)
}

val prepareNginxDockerContext = tasks.register<Copy>("prepareNginxDockerContext") {

    val dockerContextDir: Provider<Directory> = layout.buildDirectory.dir("docker")
    group = "docker"
    description = "Copy Dockerfile."

    dependsOn("copyFrontendDist")
    into(dockerContextDir)

    from(layout.projectDirectory.dir("docker")) {
        include("Dockerfile")
    }

    outputs.dir(dockerContextDir)

}

val buildDocker = registerBuildDockerTask(
    project = project,
    taskName = "buildDockerNginx",
    dockerContextDir = dockerContextDir,
    baseImageName = baseImageName,
    prepareDockerContext = prepareNginxDockerContext
)

registerPublishDockerTask(
    project = project,
    taskName = "publishDockerNginx",
    baseImageName = baseImageName,
    buildDocker = buildDocker,
)
