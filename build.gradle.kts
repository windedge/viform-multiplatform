import com.android.build.api.dsl.LibraryExtension
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinBasePlugin

plugins {
    alias(libs.plugins.kotlin.kmp) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.maven.publish) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.kotlin.compose) apply false
}


repositories {
    mavenCentral()
}

subprojects {
    repositories {
        mavenCentral()
        google()
        mavenLocal()
    }

    plugins.withType<KotlinBasePlugin> {
        extensions.configure<KotlinMultiplatformExtension> {
            jvmToolchain(17)

            jvm()

            plugins.withId("com.android.library") {
                androidTarget {
                    publishLibraryVariants("release")
                }
            }

            iosArm64()
            iosX64()
            iosSimulatorArm64()
        }
    }

    afterEvaluate {
        plugins.withId("java") {
            extensions.configure<JavaPluginExtension> {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }
        }

        extensions.findByType<LibraryExtension>()?.apply {
            compileSdk = 35
            sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
            defaultConfig {
                minSdk = 21
            }
            compileOptions {
                this.sourceCompatibility = JavaVersion.VERSION_17
                this.targetCompatibility = JavaVersion.VERSION_17
            }
            namespace = "io.github.windedge.viform"
        }
    }
}
