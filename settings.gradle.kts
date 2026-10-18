pluginManagement {
    // Convention plugins shared by all modules (see build-logic/README.md).
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
// No repositoriesMode: the Kotlin/Wasm plugin adds its own repositories to download Node.js and the
// npm tooling, which FAIL_ON_PROJECT_REPOS would reject.
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "KmpStarter"

// Lets modules depend on each other as projects.core.common instead of project(":core:common").
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// The folder tree follows the clean architecture layers; see the Architecture section of AGENTS.md.

// Platform entry points: thin launchers that start the shared app. iOS lives in iosApp/ (Xcode).
include(":androidApp")
include(":webApp")

// Shared application shell: App(), navigation graph, Koin setup, iOS framework
include(":app")

// Shared building blocks with no business logic
include(":core:common")
include(":core:testing")

// Domain layer: models, repository interfaces, use cases (pure Kotlin)
include(":domain")

// Data layer: repository implementations, local (Room, settings) and remote (Ktor) sources
include(":data")

// Presentation layer: shared UI, and one module per feature under presentation/features/
include(":presentation:ui")
// include(":presentation:features:<name>")
