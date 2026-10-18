import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import starter.JAVA_VERSION
import starter.intVersion
import starter.libs
import starter.library

/**
 * Configures the `:androidApp` module: Android application plugin (with AGP's built-in Kotlin),
 * the Compose compiler, SDK levels, Java version, BuildConfig, and the libraries the Android entry
 * point needs (Activity Compose, Koin for Android, Kermit).
 */
class AndroidApplicationConventionPlugin : Plugin<Project> {

    /** Applies the Android application plugin and the shared configuration to [target]. */
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.android.application")
        pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

        extensions.configure<ApplicationExtension> {
            compileSdk = libs.intVersion("compileSdk")
            defaultConfig {
                minSdk = libs.intVersion("minSdk")
                targetSdk = libs.intVersion("targetSdk")
            }
            compileOptions {
                sourceCompatibility = JAVA_VERSION
                targetCompatibility = JAVA_VERSION
            }
            buildFeatures {
                buildConfig = true
                compose = true
            }
        }

        dependencies {
            add("implementation", platform(libs.library("koin-bom")))
            add("implementation", libs.library("koin-android"))
            add("implementation", libs.library("kermit"))
            add("implementation", libs.library("androidx-activity-compose"))
        }
    }
}
