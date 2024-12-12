# Atlas - Gradle

<!-- toc -->

- [atlas Gradle multi module project](#atlas-gradle-multi-module-project)
  * [Sharing build logic with convention plugin](#sharing-build-logic-with-convention-plugin)
- [How to](#how-to)
- [Troubleshooting](#troubleshooting)
  * [Parallel Execution](#parallel-execution)

<!-- tocstop -->

atlas uses [Gradle](https://gradle.org/) as a **Build Automation Tool**.

## atlas Gradle multi module project

1. [settings.gradle.kts](../settings.gradle.kts)
   see [Gradle Settings script](https://docs.gradle.org/current/userguide/settings_file_basics.html#sec:settings_file_script)
1. [build.gradle.kts](../build.gradle.kts): defines a part of the common build configuration shared between the subprojects
1. [gradle.properties](../gradle.properties):
   see [Gradle Configuration Properties](https://docs.gradle.org/current/userguide/build_environment.html#sec:gradle_configuration_properties)

### Sharing build logic with convention plugin

The project [atlas-gradle-ci-plugin](../atlas-gradle-ci-plugin) defines two plugins:
1. [buildlogic.java-conventions](../atlas-gradle-ci-plugin/src/main/kotlin/buildlogic.java-conventions.gradle.kts): util to 
   share some java, dependencies and publication logic.  
2. [buildlogic.java-restdoc](../atlas-gradle-ci-plugin/src/main/kotlin/buildlogic.java-restdoc.gradle.kts): add the RestDoc 
   configuration logic when applied to a project.

These are normal gradle plugins that can be applied to subprojects: 

```kotlin
plugins {
    id("buildlogic.java-conventions")
    id("buildlogic.java-restdoc")
}
```
For more information see [Sharing build logic with convention plugin](https://docs.gradle.org/current/samples/sample_convention_plugins.html): 

## How to
1. Build atlas: ```./gradlew build```
2. Clean atlas ( delete build directories): ```./gradlew clean```
3. Run subproject build, e.g. : ```./gradlew :apim-configuration:build```
4. Lists all project tasks: ```./gradlew tasks```. Each listed task can be executed ```./gradlew ${myTask}``` or for 
   subproject ```./gradlew :${mySubProject}:${myTask}```

## Troubleshooting

### Parallel Execution

With gradle is possible to build a project
with [parallel execution](https://docs.gradle.org/current/userguide/performance.html#parallel_execution) mode.

_atlas_ is built by gradle in **parallel execution mode** with the additional parameter ```--parallel```.   

The **parallel execution mode** is a heavy process that can overload your machine and is not enabled locally.
If your machine is fit just run gradle task with ```--parallel```, e.g:

```shell
./gradlew build --parallel
```