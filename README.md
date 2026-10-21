# KMP Starter

A Kotlin Multiplatform starter template for **Android, iOS, and the web** with one shared Compose
Multiplatform UI: Material 3, Navigation 3, Koin, Ktor, Room, multiplatform-settings, and Kermit,
organized with Clean Architecture and MVVM.

## First-Time Setup

If you are using this template for the first time, **follow the instructions in
[`docsagent/SETUP.md`](docsagent/SETUP.md) before starting development**.

The setup guide covers renaming the package and project with one Gradle task (Android, iOS, and
web), removing the optional parts you do not need, replacing the placeholders, the iOS build in
CI, and the AI assistant files: `AGENTS.md` in the root, and the development log, roadmap, and
setup guide in `docsagent/`. After the setup, the AI interviews you about the app and its design
([`docsagent/PROJECT_DISCOVERY.md`](docsagent/PROJECT_DISCOVERY.md)) and writes the app plan and
the design criteria from your answers into `docsagent/`.

> **Do not skip the setup steps.**

## Architecture

Clean Architecture with MVVM. Each layer is a Gradle module shared by all platforms, so the folder
tree shows the layers:

```text
androidApp/                Android entry point: App, MainActivity
iosApp/                    iOS entry point: Xcode project (Swift)
webApp/                    web entry point: main(), index.html
app/                       shared shell: App(), navigation graph, Koin setup, iOS framework
core/
  common/                  AppConfig, logging, Resource, DispatcherProvider, utilities
  testing/                 unit test helpers
domain/                    models, repository interfaces, use cases (pure Kotlin)
data/                      repository implementations
  local/                   Room database, key-value settings      (optional)
  remote/                  Ktor APIs and DTOs                     (optional)
presentation/
  ui/                      theme, shared composables, UiText
  features/<name>/         one module per screen or flow
build-logic/               convention plugins
docsagent/                 AI working documents: dev log, roadmap, setup, architecture, features, services, app plan, design
scripts/                   build, test, and lint with a short summary (for AI agents)
```

```text
androidApp ─┐
iosApp ─────┼──▶  app  ──▶  presentation/features  ──▶  domain  ◀──  data
webApp ─────┘
```

Almost all code lives in `commonMain`. The platform source sets (`androidMain`, `iosMain`,
`wasmJsMain`) hold only what differs, such as where the database file lives. See
[`docsagent/ARCHITECTURE.md`](docsagent/ARCHITECTURE.md) for the full folder tree and how to add a
feature module, and the Architecture section of [`AGENTS.md`](AGENTS.md) for the rules between the
layers.

## Stack

| Area | Library |
|---|---|
| Build | Gradle 9.8.1, AGP 9.4.1, Kotlin 2.4.21, KSP, version catalog, convention plugins |
| Targets | Android (minSdk 26, compileSdk / targetSdk 37), iOS 16+ (arm64, simulator arm64), Web (Kotlin/Wasm) |
| UI | Compose Multiplatform 1.12.2, Material 3, Navigation 3, Compose resources |
| DI | Koin (with Compose ViewModel support) |
| Data | Room 3 (bundled SQLite; SQLite web worker on the web), multiplatform-settings, Ktor + kotlinx.serialization |
| Async | Kotlin Coroutines and Flow |
| Logging | Kermit |
| Tests | kotlin.test, Turbine, kotlinx-coroutines-test, run on the JVM, in the browser, and on the iOS simulator |
| CI | GitHub Actions: Android and web build, tests, and lint on Linux; iOS tests and Xcode build on macOS |

## Saving Tokens: Tips for the Developer

The AI rules already keep each session lean: short build output, reading only what a task needs,
and documents (`docsagent/FEATURES.md`, `docsagent/SERVICES.md`) that answer "where is this?"
without a code search. These habits on your side save more:

- **Start a new conversation after every task** (`/clear` in Claude Code). Every message resends
  the whole conversation, so a long session gets more expensive with each step. The AI reminds you
  at the end of each task; DEVLOG and ROADMAP carry the context to the next session.
- **Keep tasks small.** A ROADMAP task should fit in one session. Split anything bigger first.
- **Ask for a plan before large changes.** Fixing a wrong approach after the code is written costs
  far more than correcting a plan.
- **Pick the model for the job.** Routine work (a new screen, tests, small fixes) runs well on a
  smaller, cheaper model (in Claude Code: Sonnet). Keep the largest model for architecture
  decisions and hard bugs. Switch with `/model`.
- **Point to the place.** "Fix the retry button in `ProfileScreen.kt`" costs less than "the retry
  button is broken somewhere", because the AI does not have to search for it.
- **Paste only the relevant part of an error.** For a crash, the exception and the first lines of
  the stack trace are enough.

## Common Commands

```bash
scripts/check.sh                                 # build, test, and lint with a short summary (Windows: scripts\check.ps1)
./gradlew :androidApp:assembleDebug              # build the Android debug APK
./gradlew :webApp:wasmJsBrowserDevelopmentRun    # run the web app at http://localhost:8080
./gradlew :webApp:wasmJsBrowserDistribution      # production web build in webApp/build/dist/
./gradlew allTests                               # run all unit tests (Android host, browser, iOS on macOS)
./gradlew lint                                   # run Android lint
```

iOS: open `iosApp/iosApp.xcodeproj` in Xcode on a Mac and run it. The Xcode build runs the Gradle
task that builds the shared `ComposeApp` framework. Without a Mac, CI builds iOS on a macOS runner.
