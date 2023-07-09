plugins {
  id("convention.jvm")
  kotlin("jvm") version libs.versions.kotlin.get()
  application
}

description = "Local consumer sandbox"

application {
  mainClass.set("local.sandbox.MainKt")
}

dependencies {
  implementation(libs.kotlinx.coroutines.core)
  implementation("io.github.windedge:vi-form-core")
  testImplementation("io.github.windedge:test-utils")
}
