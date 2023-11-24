pluginManagement {
  repositories {
    mavenLocal()
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

includeBuild("../") {

  dependencySubstitution {
    val group = "io.github.windedge.viform"
    substitute(module("$group:viform-core")).using(project(":modules:viform-core"))
    substitute(module("$group:viform-compose")).using(project(":modules:viform-compose"))
    substitute(module("$group:viform-orbitmvi")).using(project(":modules:viform-orbitmvi"))
    substitute(module("$group:test-utils")).using(project(":tests:test-utils"))
  }
}
includeBuild("../build-conventions")
