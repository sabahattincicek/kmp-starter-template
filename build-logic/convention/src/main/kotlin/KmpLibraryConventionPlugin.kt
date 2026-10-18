import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import starter.JVM_TARGET
import starter.intVersion
import starter.libs
import starter.library

/**
 * Configures a shared Kotlin Multiplatform module (every module except `:androidApp` and
 * `:webApp`): the Android, iOS, and Web (Wasm) targets, SDK levels, Kermit, Koin, coroutines, and
 * the test libraries of `commonTest`.
 *
 * The module still sets its own `namespace` in `kotlin { android { } }`.
 */
class KmpLibraryConventionPlugin : Plugin<Project> {

    /** Applies the Kotlin Multiplatform and Android KMP library plugins and the shared setup to [target]. */
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        pluginManager.apply("com.android.kotlin.multiplatform.library")

        extensions.configure<KotlinMultiplatformExtension> {
            extensions.configure<KotlinMultiplatformAndroidLibraryTarget> {
                compileSdk = libs.intVersion("compileSdk")
                minSdk = libs.intVersion("minSdk")
                compilerOptions { jvmTarget.set(JVM_TARGET) }
                lint { targetSdk = libs.intVersion("targetSdk") }
                // Runs the commonTest tests on the JVM as Android host tests (testAndroidHostTest).
                withHostTest {}
            }

            // expect/actual classes (used by Room's database constructor) are stable enough to use.
            compilerOptions { freeCompilerArgs.add("-Xexpect-actual-classes") }

            iosArm64()
            iosSimulatorArm64()

            @OptIn(ExperimentalWasmDsl::class)
            wasmJs {
                browser()
            }

            sourceSets.getByName("commonMain").dependencies {
                implementation(project.dependencies.platform(libs.library("koin-bom")))
                implementation(libs.library("koin-core"))
                implementation(libs.library("kermit"))
                implementation(libs.library("kotlinx-coroutines-core"))
            }
            sourceSets.getByName("commonTest").dependencies {
                implementation(libs.library("kotlin-test"))
                implementation(libs.library("kotlinx-coroutines-test"))
                implementation(libs.library("turbine"))
            }
        }
    }
}
