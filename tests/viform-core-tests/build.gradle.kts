plugins {
  id("convention.kotlin-mpp-tier0")
  id("convention.library-android")
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
