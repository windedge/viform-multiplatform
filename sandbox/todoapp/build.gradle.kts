plugins {
  id("convention.jvm")
  kotlin("jvm") version libs.versions.kotlin.get()
  alias(libs.plugins.kopybuilder)
  alias(libs.plugins.compose)
  application
}

application {
  mainClass.set("local.sandbox.todoapp.MainKt")
}

dependencies {
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.viform.core)
  implementation(libs.viform.compose)
  implementation(libs.viform.orbitmvi)

//  implementation(compose.runtime)
//  implementation(compose.foundation)
  implementation(compose.desktop.currentOs)
  implementation(compose.material3)

  testImplementation(libs.viform.test.utils)
}
