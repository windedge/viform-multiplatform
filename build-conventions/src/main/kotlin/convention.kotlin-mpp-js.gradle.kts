import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
  id("convention.kotlin-mpp")
}

kotlin {
  js {
    useCommonJs()
    browser {
      commonWebpackConfig {
        cssSupport { enabled.set(true) }
        scssSupport { enabled.set(true) }
      }
      testTask {
//        useKarma()
        enabled = false
      }
    }
  }
}
