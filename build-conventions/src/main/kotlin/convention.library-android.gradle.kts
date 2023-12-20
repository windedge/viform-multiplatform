plugins {
  id("convention.library")
  id("com.android.library")
}

android {
  namespace = "$group.${name.replace(Regex("[_-]"), ".")}"
  compileSdk = 33
  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
  sourceSets["main"].res.srcDirs("src/androidMain/res")
  sourceSets["main"].resources.srcDirs("src/androidMain/resources")
  defaultConfig {
    minSdk = 24
    aarMetadata {
      minCompileSdk = minSdk
    }
  }
}
