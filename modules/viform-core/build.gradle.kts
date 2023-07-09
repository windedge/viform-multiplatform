plugins {
  id("convention.kotlin-mpp-tier0")
  id("convention.kotlin-mpp-js")
  id("convention.library-android")
  id("convention.library-mpp")
  id("convention.publishing-mpp")
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(libs.kotlinx.coroutines.core)
      }
    }
  }
}
