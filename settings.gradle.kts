pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "VorteXOS"

// Core unique features
include(":apps:launcher")
include(":apps:weather-sync")
include(":apps:emotion-engine")
include(":apps:assistant")

// System apps
include(":apps:calculator")
include(":apps:notepad")
include(":apps:filemanager")
include(":apps:settings")
include(":apps:security")

// Shared library
include(":apps:common")
