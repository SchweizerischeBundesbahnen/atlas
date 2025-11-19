package docker

import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.register

fun registerBuildDockerTask(
    groupDescription: String,
    project: Project,
    taskName: String = "buildDocker",
    dockerContextDir: Provider<Directory>,
    baseImageName: String,
    prepareDockerContext: TaskProvider<Copy>
): TaskProvider<Exec> {
    return project.tasks.register<Exec>(taskName) {
        group = groupDescription
        description = "Build Docker image from build/docker context."
        val dockerTag = "${project.parent?.version}"
        val imageName = "$baseImageName:$dockerTag"
        dependsOn(prepareDockerContext)

        // Set working dir to the prepared context
        workingDir = dockerContextDir.get().asFile

        if (!project.hasProperty("buildOnTekton")) {
            println("Running docker build locally for project ${project.name} ...")
            // Use the List-based setter (property assignment) to avoid vararg issues
            commandLine = listOf("docker", "build", "-t", imageName, ".")
            isIgnoreExitValue = false
        } else {
            println("Running docker build via Tekton/Buildah for project ${project.name} ...")
            commandLine = listOf(
                "buildah", "bud",
                "--cert-dir",
                "--tls-verify=false",
                "-t", imageName,
                "--no-cache",
                "."
            )
        }
    }
}
