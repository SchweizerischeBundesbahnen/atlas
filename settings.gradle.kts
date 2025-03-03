pluginManagement {
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
include(":business-organisation-directory")
include(":service-point-directory")
include(":prm-directory")
include(":export-service")
include(":bulk-import-service")
include(":user-administration")
include(":workflow")
include(":location")

include(":apim-configuration")
include(":api-auth-gateway")
include(":gateway")
include(":frontend")