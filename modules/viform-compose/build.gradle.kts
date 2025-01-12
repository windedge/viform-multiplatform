plugins {
  id("convention.kotlin-mpp-tier0")
  id("convention.kotlin-mpp-js")
  id("convention.kotlin-mpp-wasm")
  id("convention.library-android")
  id("convention.library-mpp")
  id("convention.publishing-mpp")
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.compose)
}

kotlin {

  sourceSets {
    commonMain {
      dependencies {
        api(project(":modules:viform-core"))
        implementation(libs.kotlinx.coroutines.core)
        implementation(compose.runtime)
      }
    }
  }
}