description = "Local consumer sandbox"

subprojects {
  repositories {
    mavenLocal()
    google()
    mavenCentral()
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
  }
}
