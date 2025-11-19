import org.gradle.api.tasks.Copy
import docker.registerBuildDockerTask
import docker.registerPublishDockerTask

val dockerContextDir: Provider<Directory> = layout.buildDirectory.dir("docker")
val projectName = project.name
val baseImageName: String = "atlas.docker.bin.sbb.ch/atlas/atlas-${projectName}"
val groupDescription: String = "docker-nginx"

tasks.register<Copy>("copyDockerDir") {
    val dockerContextDir: Provider<Directory> = layout.buildDirectory.dir("docker/docker")
    group = groupDescription
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
    group = groupDescription
    description = "Copy frontent distribution directory."

    dependsOn("execNpmBuild")
        .dependsOn("copyDockerDir")

    into(dockerContextDir)

    from(layout.projectDirectory.dir("dist/atlas-frontend")) {
        include("**/*")
    }

    outputs.dir(dockerContextDir)
}

val prepareNginxDockerContext = tasks.register<Copy>("prepareNginxDockerContext") {
    val dockerContextDir: Provider<Directory> = layout.buildDirectory.dir("docker")
    group = groupDescription
    description = "Copy Dockerfile."

    dependsOn("copyFrontendDist")
    into(dockerContextDir)

    from(layout.projectDirectory.dir("docker")) {
        include("Dockerfile")
    }

    outputs.dir(dockerContextDir)

}

val buildDocker = registerBuildDockerTask(
    groupDescription = groupDescription,
    project = project,
    taskName = "buildDockerNginx",
    dockerContextDir = dockerContextDir,
    baseImageName = baseImageName,
    prepareDockerContext = prepareNginxDockerContext
)

registerPublishDockerTask(
    groupDescription = groupDescription,
    project = project,
    taskName = "publishDockerNginx",
    baseImageName = baseImageName,
    buildDocker = buildDocker,
)
