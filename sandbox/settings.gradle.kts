pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
  }
}

dependencyResolutionManagement {
  versionCatalogs {
    create("libs") {
      from(files("../gradle/libs.versions.toml"))
    }
  }
}


plugins {
  id("com.gradle.enterprise") version "3.13"
}

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

includeBuild("../")
includeBuild("../build-conventions")
