// Shared application shell, used by every platform: App() (theme + navigation graph), the
// Navigation 3 graph that ties the feature modules together (navigation/), and the Koin setup that
// loads every layer's modules (di/). Also builds the iOS framework "ComposeApp" that iosApp/
// imports. Keep it thin; screens live in :presentation:features:*, everything else in its own
// layer module.
plugins {
    alias(libs.plugins.starter.kmp.library)
    alias(libs.plugins.starter.kmp.compose)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    android {
        namespace = "com.template.kmpstarter.app"
    }

    listOf(iosArm64(), iosSimulatorArm64()).forEach { target ->
        target.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.core.common)
            implementation(projects.domain)
            implementation(projects.presentation.ui)
            // Puts the repository, network, database, and settings Koin modules into the app.
            implementation(projects.data)

            // Feature modules: add one line per feature, e.g.
            // implementation(projects.presentation.features.profile)

            implementation(libs.lifecycle.runtime.compose)
            implementation(libs.lifecycle.viewmodel.navigation3)
            implementation(libs.navigation3.ui)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}

compose.resources {
    packageOfResClass = "com.template.kmpstarter.app.resources"
}
