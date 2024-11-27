pluginManagement {
    includeBuild("build-logic")
    repositories {
        maven(url = "https://bin.sbb.ch/artifactory/mvn")
        maven(url = "https://bin.sbb.ch/artifactory/esta.mvn")
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
}

rootProject.name = "atlas"
include(":auto-rest-doc")
include(":kafka")
include(":base-atlas")
include(":location")
