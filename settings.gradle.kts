pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
  }
}

plugins {
  id("com.gradle.enterprise") version "3.13"
}

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

rootProject.name = "viform-multiplatform"
includeBuild("./build-conventions/")
include(":tests:test-utils")

include(
  ":modules:viform-core",
  ":tests:viform-core-tests"
)

include(
  ":modules:viform-compose",
  ":tests:viform-compose-tests"
)
