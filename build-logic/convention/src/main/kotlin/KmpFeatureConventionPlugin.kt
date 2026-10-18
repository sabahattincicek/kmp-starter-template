import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import starter.libs
import starter.library

/**
 * Configures a `:presentation:features:*` module: a shared library with Compose, Koin ViewModels,
 * and serializable Navigation 3 keys, depending on the modules every screen needs.
 *
 * Features depend on `:domain` and never on `:data`, so screens can only reach data through use
 * cases.
 */
class KmpFeatureConventionPlugin : Plugin<Project> {

    /** Applies the library, Compose, and serialization plugins and the feature dependencies to [target]. */
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("starter.kmp.library")
        pluginManager.apply("starter.kmp.compose")
        pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

        extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.getByName("commonMain").dependencies {
                implementation(project(":core:common"))
                implementation(project(":domain"))
                implementation(project(":presentation:ui"))

                implementation(libs.library("lifecycle-runtime-compose"))
                implementation(libs.library("lifecycle-viewmodel-compose"))
                implementation(libs.library("koin-compose-viewmodel"))
                implementation(libs.library("navigation3-ui"))
                implementation(libs.library("kotlinx-serialization-json"))
            }
            sourceSets.getByName("commonTest").dependencies {
                implementation(project(":core:testing"))
            }
        }
    }
}
