pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        jcenter()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://oss.jfrog.org/artifactory/oss-snapshot-local") }

        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
}

rootProject.name = "New Tiktok"
include(":app")
include(":catloadinglibrary")
include(":silicompressor")
include(":toro-core")
include(":toro-exoplayero")
include(":toro-mopub")
include(":videocompressor")
include(":videotrimmer")
