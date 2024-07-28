pluginManagement {
    repositories {
        mavenCentral()
        /*google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }*/
        google()
        gradlePluginPortal()
        maven { url = uri("https://maven.google.com") }
        maven {
            url = uri("https://jitpack.io")
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven { url = uri("https://maven.google.com") }
        maven {
            url = uri("https://jitpack.io")
        }
    }
}

rootProject.name = "My Spends"
include(":app")
include(":ExpandableRecyclerview")
