plugins {
  id("convention.kotlin-mpp-tier0")
  id("convention.library-android")
  alias(libs.plugins.kopybuilder)
//  alias(libs.plugins.compose)
}

kotlin {
  sourceSets {
    commonTest {
      dependencies {
        implementation(project(":tests:test-utils"))
        implementation(project(":modules:viform-compose"))

//        implementation(compose.desktop.uiTestJUnit4)
//        implementation(compose.desktop.currentOs)
      }
    }
  }
}
