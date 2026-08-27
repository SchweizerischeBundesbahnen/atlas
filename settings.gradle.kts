pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
}

rootProject.name = "atlas-github-playground"
include(":auto-rest-doc")
include(":kafka")
include(":base-atlas")
include(":user-administration-security")
include(":wheelchair-accessibility")

include(":mail")
include(":scheduling")
include(":line-directory")
include(":timetable-hearing")
include(":business-organisation-directory")
include(":service-point-directory")
include(":prm-directory")
include(":export-service")
include(":bulk-import-service")
include(":user-administration")
include(":workflow")
include(":location")

include(":api-auth-gateway")
include(":gateway")
include(":frontend")
