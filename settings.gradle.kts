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

include(":mail")
include(":scheduling")
include(":line-directory")

include(":service-point-directory")
include(":prm-directory")
include(":export-service-point")
include(":import-service-point")
include(":user-administration")
include(":workflow")
include(":location")

include(":api-auth-gateway")
include(":gateway")
include(":frontend")
