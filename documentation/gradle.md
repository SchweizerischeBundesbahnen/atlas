# Atlas - Gradle

<!-- toc -->

- [atlas Gradle multi module project](#atlas-gradle-multi-module-project)
  * [Sharing build logic with convention plugin](#sharing-build-logic-with-convention-plugin)
- [How to](#how-to)
- [How to increase your Gradle Build Speed](#how-to-increase-your-gradle-build-speed)
  * [IntelliJ runner](#intellij-runner)
  * [Enable Gradle Offline Mode](#enable-gradle-offline-mode)
  * [Add GRADLE_OPTS env](#add-gradle_opts-env)
  * [Configuration Cache](#configuration-cache)
- [Troubleshooting](#troubleshooting)
  * [Parallel Execution](#parallel-execution)
- [Gradle Concepts in a Nutshell](#gradle-concepts-in-a-nutshell)
  * [Build Lifecycle](#build-lifecycle)
  * [Gradle Cache](#gradle-cache)
  * [Incremental Build](#incremental-build)

<!-- tocstop -->

atlas uses [Gradle](https://gradle.org/) as a **Build Automation Tool**.

## atlas Gradle multi module project

1. [settings.gradle.kts](../settings.gradle.kts)
   see [Gradle Settings script](https://docs.gradle.org/current/userguide/settings_file_basics.html#sec:settings_file_script)
1. [build.gradle.kts](../build.gradle.kts): defines a part of the common build configuration shared between the subprojects
1. [gradle.properties](../gradle.properties):
   see [Gradle Configuration Properties](https://docs.gradle.org/current/userguide/build_environment.html#sec:gradle_configuration_properties)

### Sharing build logic with convention plugin

The project [buildSrc](../buildSrc) defines two plugins:

1. [buildlogic.java-conventions](../buildSrc/src/main/kotlin/buildlogic.java-conventions.gradle.kts): util to
   share some java, dependencies and publication logic.
2. [buildlogic.java-restdoc](../buildSrc/src/main/kotlin/buildlogic.java-restdoc.gradle.kts): add the RestDoc
   configuration logic when applied to a project.

These are normal gradle plugins that can be applied to subprojects:

```kotlin
plugins {
    id("buildlogic.java-conventions")
    id("buildlogic.java-restdoc")
}
```

For more information
see [Sharing build logic with convention plugin](https://docs.gradle.org/current/samples/sample_convention_plugins.html):

## How to

1. Build atlas: ```./gradlew build```
2. Clean atlas ( delete build directories): ```./gradlew clean```
3. Run subproject build, e.g. : ```./gradlew :apim-configuration:build```
4. Lists all project tasks: ```./gradlew tasks```. Each listed task can be executed ```./gradlew ${myTask}``` or for
   subproject ```./gradlew :${mySubProject}:${myTask}```

## How to increase your Gradle Build Speed

### IntelliJ runner

To start a SpringBoot App and to get full benefit of gradle we need to use the Gradle Runner instead of the SpringBoot Runner.
the atlas gradle runners are stored in **.idea/runConfigurations**.

### Enable Gradle Offline Mode

Enable gradle Offline Work from **Preferences-> Build, Execution, Deployment-> Build Tools-> Gradle**. This will not allow the
gradle
to access the network during build and force it to resolve the dependencies from the cache itself. _**Note**: This only works if
all
the dependencies are downloaded and stored in the cache once. If you need to modify or add a new dependency you’ll have to disable
this option else the build would fail._

### Add GRADLE_OPTS env

Add **GRADLE_OPTS=-Xmx2048m** to your environment variables.

### Configuration Cache

"_The configuration cache is a feature that significantly improves build performance by caching the result of the
configuration phase and reusing this for subsequent builds. Using the configuration cache, Gradle can skip the configuration
phase entirely when nothing that affects the build configuration, such as build scripts, has changed. Gradle also applies
performance improvements to task execution as well._" See
[Official Gradle Configuration cache documentation](https://docs.gradle.org/current/userguide/configuration_cache.html).

:warning: Unfortunately at the moment this feature cannot be used with the command execution ```./gradlew build 
--configuration-cache``` due to incompatibility with the asciidoctor plugin
(see [GitHub issue](https://github.com/asciidoctor/asciidoctor-gradle-plugin/pull/730)).

To get the benefit of the gradle configuration cache feature we built a small workaround until the Asciidoctor plugin resolve
the issue:

1. As default the **configuration cache is enabled** and the **Asciidoctor generation is disabled**, by executing ```.
/gradlew build``` no RestDoc will be generated.
2. If you want to generate on your local machine the RestDoc you have to execute the following command: ```./gradlew build --no-configuration-cache 
   -PgenerateAsciidoc=true```.
3. On Tekton the RestDoc generation ist only enabled on the release Job

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

## Gradle Concepts in a Nutshell

### Build Lifecycle

Gradle has 3 major phases, see [Build Lifecycle](https://docs.gradle.org/current/userguide/build_lifecycle.html) for more details:
1. Initialization: Builds a graphical representation of tasks (chunks of work) for each project.
   1. Detects the settings.gradle(.kts) file.
   2. Creates a Settings instance.
   3. Evaluates the settings file to determine which projects (and included builds) make up the build.
   4. Creates a Project instance for every project.
2. Configuration: Downloads dependencies, applies plugins, and creates model representations of each modules’ task set. This produces a graph of tasks for phase 3 to consume.
   1. Evaluates the build scripts, build.gradle(.kts), of every project participating in the build.
   2. Creates a task graph for requested tasks.
3. Execution: Runs the subset of tasks based on the input from the graph built in phase 2.
   1. Schedules and executes the selected tasks.
   2. Dependencies between tasks determine execution order.
   3. Execution of tasks can occur in parallel.

### Gradle Cache

To improve the performance Gradle offers multiple cache features:

1. [Build Cache](https://docs.gradle.org/current/userguide/build_cache.html): caches the output result on certain tasks execution
2. [Configuration cache](https://docs.gradle.org/current/userguide/configuration_cache.html): caches the project configuration
3. [Parallel configuration caching](https://docs.gradle.org/current/userguide/configuration_cache.html#config_cache:usage:parallel):
   the configuration cache is parallel generated
4. [Configuration On Demand](https://docs.gradle.org/current/userguide/multi_project_configuration_and_execution.html):
   attempts to configure only the relevant projects for the requested tasks, i.e., it only evaluates the
   build script file of projects participating in the build

When you run a task and the task is marked with **FROM-CACHE** in the console output, this means build cache is at work.

### Incremental Build

The incremental build is a build that avoids running tasks whose inputs did not change since the previous build, making the 
execution of such tasks unnecessary. 

When you run a task and the task is marked with **UP-TO-DATE** in the console output, this means incremental build is at work.

