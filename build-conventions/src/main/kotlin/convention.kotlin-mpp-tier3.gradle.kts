import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
  id("convention.kotlin-mpp-tier2")
  id("convention.kotlin-mpp-wasm")
}

// https://kotlinlang.org/docs/native-target-support.html#tier-3
kotlin {
  androidNativeArm32()
  androidNativeArm64()
  androidNativeX86()
  androidNativeX64()
  mingwX64()
  watchosDeviceArm64()
}
