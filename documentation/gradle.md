# Atlas - Gradle

atlas uses [Gradle](https://gradle.org/) as a **Build Automation Tool**.

## atlas Gradle multi module project

1. [settings.gradle.kts](../settings.gradle.kts)
   see [Gradle Settings script](https://docs.gradle.org/current/userguide/settings_file_basics.html#sec:settings_file_script)
1. [build.gradle.kts](../build.gradle.kts): defines a part of the common build configuration shared between the subprojects
1. [gradle.properties](../gradle.properties):
   see [Gradle Configuration Properties](https://docs.gradle.org/current/userguide/build_environment.html#sec:gradle_configuration_properties)

## Parallel Execution

With gradle is possible to build a project
with [parallel execution](https://docs.gradle.org/current/userguide/performance.html#parallel_execution) mode.

The parallel execution is only on tekton enabled. To run the build with parallel execution mode locally add the property 
```org.gradle.parallel``` to  [gradle.properties](../gradle.properties)

## How to
1. Build atlas: ```./gradlew build```
2. Clean atlas ( delete build directories): ```./gradlew clean```
3. Run subproject build, e.g. : ```./gradlew :apim-configuration:build```
4. Lists all project tasks: ```./gradlew tasks```. Each listed task can be executed ```./gradlew ${myTask}``` or for 
   subproject ```./gradlew :${mySubProject}:${myTask}```
