@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)

plugins {
    alias(libs.plugins.kotlin.kmp)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kopybuilder)
}

kotlin {
  jvmToolchain(17)

  androidTarget("android") {
    compilations.all {
      kotlinOptions {
        jvmTarget = "17"
      }
    }
  }

  jvm("desktop")

  wasmJs {
    browser()
    binaries.executable()
  }

  sourceSets {
    all {
      languageSettings {
        optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
      }
    }
    
    val commonMain by getting {
      dependencies {
        implementation(libs.kotlinx.coroutines.core)

        implementation(compose.runtime)
        implementation(compose.foundation)
        implementation(compose.material)
        implementation(compose.ui)
        @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
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
}

android {
  namespace = "local.sandbox.signupapp"
  compileSdk = 35
  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
  sourceSets["main"].res.srcDirs("src/androidMain/res")
  sourceSets["main"].resources.srcDirs("src/androidMain/resources")

  defaultConfig {
    applicationId = "local.sandbox.signupapp"
    minSdk = 24
    targetSdk = 35
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
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  composeOptions {
    kotlinCompilerExtensionVersion = libs.versions.compose.get()
  }
}