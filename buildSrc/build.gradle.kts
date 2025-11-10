plugins {
    // Support convention plugins written in Kotlin. Convention plugins are build scripts in 'src/main' that automatically become available as plugins in the main build.
    `kotlin-dsl`
}

repositories {
    // Use the plugin portal to apply community plugins in convention plugins.
    gradlePluginPortal()
}

dependencies {
    implementation(libs.asciidoctor.gradle.plugin)
    implementation(libs.spring.dependency.management.gradle.plugin)
    implementation(libs.spring.gradle.plugin)
}