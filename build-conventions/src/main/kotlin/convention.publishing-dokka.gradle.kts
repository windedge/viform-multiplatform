plugins {
  id("convention.publishing")
  id("org.jetbrains.dokka")
}

tasks {
  register<Jar>("javadocJar") {
    dependsOn(dokkaHtml)
    archiveClassifier by "javadoc"
    from(dokkaHtml)
  }
}

publishing {
  if (!project.version.toString().contains("SNAPSHOT")) {
    publications {
      withType<MavenPublication> {
        artifact(tasks["javadocJar"])
      }
    }
  }
}
