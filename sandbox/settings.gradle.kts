pluginManagement {
  repositories {
    mavenLocal()
    gradlePluginPortal()
    google()
    mavenCentral()
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
  }
}

dependencyResolutionManagement {
  versionCatalogs {
    create("libs") {
      from(files("../gradle/libs.versions.toml"))
    }
  }
}


includeBuild("../") {

  dependencySubstitution {
    val group = "io.github.windedge.viform"
    substitute(module("$group:viform-core")).using(project(":modules:viform-core"))
    substitute(module("$group:viform-compose")).using(project(":modules:viform-compose"))
    substitute(module("$group:viform-orbitmvi")).using(project(":modules:viform-orbitmvi"))
  }
}

include(
  ":signupapp"
)