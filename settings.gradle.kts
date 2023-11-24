pluginManagement {
  repositories {
    mavenLocal()
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
//  ":modules:viform-ksp",
  ":modules:viform-core",
  ":modules:viform-compose",
  ":modules:viform-orbitmvi",
)

include(
  ":tests:viform-core-tests",
  ":tests:viform-compose-tests",
)
