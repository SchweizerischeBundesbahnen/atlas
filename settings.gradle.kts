pluginManagement {
    includeBuild("atlas-gradle-ci-plugin")
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
include(":user-administration-security")
include(":location")
include(":mail")
include(":api-auth-gateway")
include(":scheduling")
include(":scheduling")
include(":workflow")
include(":frontend")
