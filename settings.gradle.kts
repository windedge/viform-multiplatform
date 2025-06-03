pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
    mavenLocal()
  }
}


rootProject.name = "viform-multiplatform"

include(
//  ":modules:viform-ksp",
  ":modules:viform-core",
  ":modules:viform-compose",
  ":modules:viform-orbitmvi",
)
