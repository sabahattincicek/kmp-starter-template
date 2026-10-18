// Web entry point (Kotlin/Wasm): main() starts the shared app and shows App() from :app in the
// browser. Also holds the web-only parts: index.html, styles.css, and the webpack dev server setup.
//
//   ./gradlew :webApp:wasmJsBrowserDevelopmentRun   run in the browser with live reload
//   ./gradlew :webApp:wasmJsBrowserDistribution     production build in webApp/build/dist/wasmJs/productionExecutable
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            commonWebpackConfig {
                outputFileName = "webApp.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        wasmJsMain.dependencies {
            implementation(projects.app)
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.kotlinx.browser)
            implementation(libs.kermit)
        }
    }
}
