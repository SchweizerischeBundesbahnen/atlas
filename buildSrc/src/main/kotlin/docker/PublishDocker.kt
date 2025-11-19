package docker

import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.register

fun registerPublishDockerTask(
    groupDescription: String,
    project: Project,
    taskName: String = "publishDocker",
    baseImageName: String,
    buildDocker: TaskProvider<Exec>
): TaskProvider<Exec> {
    return project.tasks.register<Exec>(taskName) {
        group = groupDescription
        description = "Publish Docker image to registry"
        val dockerTag = "${project.parent?.version}"
        val imageName = "$baseImageName:$dockerTag"

        dependsOn(buildDocker)

        if (project.hasProperty("buildOnTekton")) {
            println("Runnig publish docker from tekton buildah for project ${project.name}...")
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
}
