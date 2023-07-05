pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "mobility_scooter_safety_assessment_system"
include(":Mobility_Scooter_Safety_Assessment_Android")
include(":shared")