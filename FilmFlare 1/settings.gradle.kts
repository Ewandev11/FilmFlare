pluginManagement {
    repositories {
        google() // 🔄 Moved to top (preferred)
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        id("androidx.navigation.safeargs.kotlin") version "2.7.7"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS) // ✅ Fix here
    repositories {
        google()
        mavenCentral()
    }

}

rootProject.name = "FilmFlare"
include(":app")
