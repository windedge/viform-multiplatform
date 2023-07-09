import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
  id("convention.jvm")
  id("convention.kotlin-mpp")
}

plugins.withId("com.android.library") {
  configure<KotlinMultiplatformExtension> {
    android()
  }
}

kotlin {
  jvm()
}
