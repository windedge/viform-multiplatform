import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
  id("convention.kotlin-mpp")
}

kotlin {
  @OptIn(ExperimentalWasmDsl::class)
  wasmJs {
    browser {
      commonWebpackConfig {
        cssSupport { enabled.set(true) }
        scssSupport { enabled.set(true) }
      }
      testTask { useKarma() }
    }
  }
}
