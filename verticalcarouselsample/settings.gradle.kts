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

rootProject.name = "VerticalCarouselSample"
include(":app")
// Include your local VerticalCarousel library module
include(":verticalcarousel")
project(":verticalcarousel").projectDir = file("../VerticalCarousel") // Adjust path if needed