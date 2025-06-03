plugins {
  id(libs.plugins.kotlin.kmp.get().pluginId)
  id(libs.plugins.android.library.get().pluginId)
  id(libs.plugins.maven.publish.get().pluginId)
}

android {
    namespace = "io.github.windedge.viform.orbitmvi"
}

kotlin {
  sourceSets {
    val commonMain by getting {
      dependencies {
        api(project(":modules:viform-core"))
        implementation(libs.kotlinx.coroutines.core)
        api(libs.orbitmvi.core)
      }
    }
  }
}
