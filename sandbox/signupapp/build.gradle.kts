@file:Suppress("UnstableApiUsage")

import org.jetbrains.compose.ComposeBuildConfig.composeVersion
import org.jetbrains.compose.ExperimentalComposeLibrary

import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
  alias(libs.plugins.kotlin.kmp)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.android.application)
  alias(libs.plugins.compose)
  alias(libs.plugins.kopybuilder)
}

kotlin {
  jvmToolchain(11)

  androidTarget("android") {
    compilations.all {
      kotlinOptions {
        jvmTarget = "11"
      }
    }
  }

  jvm("desktop")

  @OptIn(ExperimentalWasmDsl::class)
  wasmJs {
    moduleName = "signupapp"
    browser {
      commonWebpackConfig {
        outputFileName = "signupapp.js"
      }
    }
    binaries.executable()
    applyBinaryen()
  }

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(libs.kotlinx.coroutines.core)

        implementation(compose.runtime)
        implementation(compose.foundation)
        implementation(compose.material)
        implementation(compose.ui)
        @OptIn(ExperimentalComposeLibrary::class)
        api(compose.components.resources)

        implementation(libs.viform.core)
        implementation(libs.viform.compose)
      }
    }
    val androidMain by getting {
      dependsOn(commonMain)

      dependencies {
        implementation(compose.uiTooling)
        implementation(libs.androidx.activity.compose)
      }
    }
    val desktopMain by getting {
      dependsOn(commonMain)

      dependencies {
        implementation(compose.desktop.currentOs)
      }
    }
    val wasmJsMain by getting {
      dependsOn(commonMain)
    }
  }
}

compose {
  desktop {
    application {
      mainClass = "local.sandbox.signupapp.MainKt"
    }
  }
  experimental {
    web.application {}
  }
}

android {
  compileSdk = 34
  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
  sourceSets["main"].res.srcDirs("src/androidMain/res")
  sourceSets["main"].resources.srcDirs("src/androidMain/resources")

  defaultConfig {
    applicationId = "local.sandbox.signupapp"
    minSdk = 24
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }
  buildTypes {
    getByName("release") {
      isMinifyEnabled = true
      signingConfig = signingConfigs.getByName("debug")
    }
    getByName("debug") {
      isMinifyEnabled = false
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  composeOptions {
    kotlinCompilerExtensionVersion = composeVersion
  }
}