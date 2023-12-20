plugins {
  id("convention.kotlin-mpp-tier0")
  id("convention.kotlin-mpp-js")
  id("convention.kotlin-mpp-wasm")
  id("convention.library-android")
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(kotlin("test"))
        api(libs.kotlinx.coroutines.test)
//        api(libs.bundles.kotest.assertions)
      }
    }
    jvmMain {
      dependencies {
        api(kotlin("test-junit5"))
      }
    }
    androidMain {
      dependencies {
        api(kotlin("test-junit5"))
      }
    }
    jsMain {
      dependencies {
        api(kotlin("test-js"))
      }
    }
  }
}
