# build-logic

Convention plugins shared by every module. A module applies one or more of them in its `plugins {}`
block instead of repeating the same Kotlin Multiplatform, Android, and Compose setup in each
`build.gradle.kts`.

| Plugin id | Applies | Used by |
|---|---|---|
| `starter.kmp.library` | Kotlin Multiplatform with the Android, iOS (arm64, simulator arm64), and Wasm targets; SDK levels, JVM target, Kermit, Koin, coroutines, and the `commonTest` libraries; Android host tests | every shared module |
| `starter.kmp.compose` | Compose Multiplatform, the Compose compiler, Material 3, Compose resources, preview tooling | shared modules with composables |
| `starter.kmp.feature` | library + compose + serialization, and `:core:common`, `:domain`, `:presentation:ui`, lifecycle, Koin ViewModel, Navigation 3 | every `:presentation:features:*` module |
| `starter.android.application` | Android application with AGP's built-in Kotlin, Compose compiler, SDK levels, Java version, BuildConfig, Koin for Android, Kermit, Activity Compose | `:androidApp` |
| `starter.rename` | the `renameProject` task (see `docsagent/SETUP.md`) | root project |

SDK levels (`compileSdk`, `targetSdk`, `minSdk`) are read from `gradle/libs.versions.toml`.
`:webApp` applies the Kotlin Multiplatform and Compose plugins directly, because it only has the
Wasm target and builds the browser app.
