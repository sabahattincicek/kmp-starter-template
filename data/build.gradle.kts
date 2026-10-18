// Data layer. Implements the repository interfaces of :domain and maps data types to domain models.
//
//   local/database/   Room: AppDatabase, entity/, dao/                       (optional)
//   local/settings/   key-value settings data sources (multiplatform-settings) (optional)
//   remote/api/       Ktor API classes                                      (optional)
//   remote/dto/       network request and response models (DTOs)            (optional)
//   repository/       repository implementations
//   di/               Koin modules: repositoryModule, networkModule, databaseModule, settingsModule
//
// Platform code (database file location, SQLite driver, settings storage) lives in androidMain,
// iosMain, and wasmJsMain. The web database runs SQLite in a web worker from sqlite-web-worker/.
//
// Network, Room, and settings are optional: the lines that belong to each are marked below. See
// "Removing an optional part" in docsagent/SETUP.md.
plugins {
    alias(libs.plugins.starter.kmp.library)
    alias(libs.plugins.kotlin.serialization) // Network: DTOs are @Serializable
    alias(libs.plugins.ksp) // Room
    alias(libs.plugins.room) // Room
}

kotlin {
    android {
        namespace = "com.template.kmpstarter.data"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.domain)

            // Network
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.kotlinx.serialization.json)

            // Room
            implementation(libs.room.runtime)

            // Settings
            implementation(libs.multiplatform.settings)
        }
        androidMain.dependencies {
            implementation(libs.koin.android)
            implementation(libs.ktor.client.okhttp) // Network
            implementation(libs.sqlite.bundled) // Room
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin) // Network
            implementation(libs.sqlite.bundled) // Room
        }
        wasmJsMain.dependencies {
            implementation(libs.ktor.client.js) // Network
            // Room: the SQLite driver for the web and the worker that runs SQLite in the browser
            implementation(libs.sqlite.web)
            implementation(libs.kotlinx.browser)
            implementation(npm("sqlite-web-worker", layout.projectDirectory.dir("sqlite-web-worker").asFile))
        }
        commonTest.dependencies {
            implementation(projects.core.testing)
        }
    }
}

// Room: the annotation processor runs once per target.
dependencies {
    listOf("kspAndroid", "kspIosArm64", "kspIosSimulatorArm64", "kspWasmJs").forEach { configuration ->
        add(configuration, libs.room.compiler)
    }
}

// Room: exported schemas are committed, so migrations can be written and tested against them.
room3 {
    schemaDirectory(layout.projectDirectory.dir("schemas"))
}
