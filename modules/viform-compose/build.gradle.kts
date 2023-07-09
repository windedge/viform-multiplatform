plugins {
  id("convention.kotlin-mpp-tier0")
  id("convention.library-android")
  id("convention.library-mpp")
  id("convention.publishing-mpp")
  alias(libs.plugins.compose)
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(project(":modules:viform-core"))
        implementation(libs.kotlinx.coroutines.core)

        implementation(compose.runtime)
      }
    }
  }
}
