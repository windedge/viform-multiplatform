plugins {
  id("convention.kotlin-mpp-tier0")
  id("convention.library-android")
  alias(libs.plugins.kopybuilder)
}

kotlin {
  sourceSets {
    commonTest {
      dependencies {
        implementation(project(":tests:test-utils"))
        implementation(project(":modules:viform-core"))
      }
    }
  }
}
