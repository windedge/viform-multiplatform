plugins {
  id("convention.jvm")
  kotlin("jvm") version libs.versions.kotlin.get()
  alias(libs.plugins.kopybuilder)
  application
}

description = "Local consumer sandbox"

application {
  mainClass.set("local.sandbox.MainKt")
}

dependencies {
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.viform.core)
  testImplementation(libs.viform.test.utils)
}
