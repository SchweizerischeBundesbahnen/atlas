# atlas - Gradle Plugin

<!-- toc -->

- [Java Convention plugin](#java-convention-plugin)
- [RestDoc Convention plugin](#restdoc-convention-plugin)

<!-- tocstop -->

This project is responsible for sharing build logic between subprojects in atlas. For more information see [Sharing build 
logic between subprojects Sample](https://docs.gradle.org/current/samples/sample_convention_plugins.html) 

## Java Convention plugin

The file [buildlogic.java-conventions.gradle.kts](src/main/kotlin/buildlogic.java-conventions.gradle.kts) defines the common 
basic configuration for an atlas java module, like commons plugins, spring boot and spring cloud dependency management, test 
configuration and so on.

This custom plugin-in can be applied to a module like a normal gradle plugin:
```kotlin
plugins {
    id("buildlogic.java-conventions")
}
```

## RestDoc Convention plugin

The file [buildlogic.java-restdoc.gradle.kts](src/main/kotlin/buildlogic.java-restdoc.gradle.kts) defines the **restdoc** 
configuration.

This custom plugin can be applied to a module like a normal gradle plugin:
```kotlin
plugins {
    id("buildlogic.java-restdoc")
}
```
