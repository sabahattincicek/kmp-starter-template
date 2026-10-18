import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import starter.libs
import starter.library

/**
 * Adds Compose Multiplatform to a module that already applies `starter.kmp.library`: the Compose
 * compiler, the Compose libraries with Material 3, Compose resources (`composeResources/`), and the
 * preview tooling. Each module sets the package of its generated `Res` class in
 * `compose.resources { packageOfResClass = ... }`.
 */
class KmpComposeConventionPlugin : Plugin<Project> {

    /** Applies the Compose plugins and the Compose libraries to [target]. */
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jetbrains.compose")
        pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

        extensions.configure<KotlinMultiplatformExtension> {
            extensions.configure<KotlinMultiplatformAndroidLibraryTarget> {
                // Packs the Compose resources (strings, images, fonts) into the Android build.
                androidResources { enable = true }
            }

            // Compose tests on the web need a webpack bundle to load the Skiko runtime.
            @OptIn(ExperimentalWasmDsl::class)
            wasmJs { binaries.executable() }

            sourceSets.getByName("commonMain").dependencies {
                implementation(libs.library("compose-runtime"))
                implementation(libs.library("compose-foundation"))
                implementation(libs.library("compose-ui"))
                implementation(libs.library("compose-material3"))
                implementation(libs.library("compose-components-resources"))
                implementation(libs.library("compose-ui-tooling-preview"))
            }
        }

        dependencies {
            // Renders @Preview composables in Android Studio.
            add("androidRuntimeClasspath", libs.library("compose-ui-tooling"))
        }
    }
}
