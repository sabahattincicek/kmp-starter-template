# Development Log

A running record of what has been done in this project, so a new session (human or AI) can catch up
without reading the whole codebase.

- **Newest entries go at the top**, directly under this header. Read downward to go back in time.
- Entry heading format: `## YYYY-MM-DD HH:mm — Short title`
- Say what changed, why, and which files or areas were touched.
- Do not rewrite or delete older entries.

---

## 2026-10-18 12:00 — Project created from KmpStarter template

Starting point of the project. What the template provides:

- **Targets:** Android (minSdk 26, compileSdk/targetSdk 37), iOS (arm64 devices and arm64
  simulators, iOS 16+), and the web (Kotlin/Wasm in the browser). One shared Compose Multiplatform
  UI. No desktop target.
- **Build:** Gradle 9.8.1, AGP 9.4.1 (Android KMP library plugin), Kotlin 2.4.21, Compose
  Multiplatform 1.12.2, KSP, version catalog in `gradle/libs.versions.toml`, type-safe project
  accessors (`projects.core.common`), configuration cache on. iOS targets are skipped on Windows
  and Linux (`kotlin.native.ignoreDisabledTargets`).
- **Convention plugins** in `build-logic/`: `starter.kmp.library` (targets, SDK levels, Kermit,
  Koin, coroutines, test libraries), `starter.kmp.compose`, `starter.kmp.feature`,
  `starter.android.application`, and `starter.rename` (the one-time `renameProject` task, which
  also renames the iOS bundle id and the web page title).
- **Layout:** Clean Architecture, one Gradle module per layer, so the folder tree shows the
  architecture: platform entry points `:androidApp`, `iosApp/` (Xcode), `:webApp`; the shared
  shell `:app`; `:core:common`, `:core:testing`, `:domain`, `:data`, `:presentation:ui`, and
  feature modules under `presentation/features/` (none yet). Folder tree and rules are in the
  Architecture section of `AGENTS.md`. Intended but empty folders contain a `.gitkeep`.
- **App:** `App()` (theme + navigation graph) shown by `MainActivity` (Android),
  `MainViewController` (iOS, framework `ComposeApp`), and `main()` (web). `initKoin(AppConfig)`
  starts logging and Koin with the modules of every layer; each platform passes its debug flag.
  `AppNavDisplay` is a Navigation 3 `NavDisplay` with saveable state and per-entry ViewModel store
  decorators; NavKeys are registered in a `SavedStateConfiguration` because iOS and the web have
  no reflection. It starts at `StartKey`, a placeholder screen (`StartPlaceholder.kt`) that shows
  the app name until the first feature exists. Android release builds use R8.
- **Core:** `AppConfig`, `configureLogging` (Kermit, silent in release), `Resource` (Success /
  Error / Loading) with `Flow.asResource()`, `DispatcherProvider` (IO falls back to Default on the
  web) in `commonModule`; `:core:testing` with `MainDispatcherTest` and `TestDispatcherProvider`.
- **Domain:** empty `model/`, `repository/`, `usecase/`, and an empty `domainModule`.
- **Data:** empty `repository/`, `remote/api/`, `remote/dto/`, `local/settings/`, and an empty
  `repositoryModule`. Optional parts, ready to use on all three platforms: Network (`networkModule`:
  Json and a Ktor `HttpClient` with timeouts, `BASE_URL` = `https://example.com/`, debug-only body
  logging; engines OkHttp / Darwin / Fetch), Room 3 (`local/database/AppDatabase` with the
  placeholder `ExampleEntity`/`ExampleDao`, bundled SQLite on Android and iOS, a SQLite web worker
  in `data/sqlite-web-worker/` that stores the database in the browser's Origin Private File
  System, schemas exported to `data/schemas`), Settings (multiplatform-settings: SharedPreferences,
  NSUserDefaults, localStorage).
- **Presentation UI:** `AppTheme` (dynamic color on Android 12+), `ErrorView`, `LoadingView`,
  `UiText` (Compose resources), empty `base/`. Strings in `composeResources/values/strings.xml`.
- **Tests:** `kotlin.test`, kotlinx-coroutines-test, and Turbine in `commonTest` of every module,
  run as Android host tests and browser tests (and iOS simulator tests on macOS). `ResourceTest`,
  `MainDispatcherTestTest`, and `KoinModulesTest` included.
- **CI:** `.github/workflows/ci.yml`: Android and web build, unit tests, and lint on Linux for every
  push and pull request; iOS tests and the Xcode build on macOS (automatic for public
  repositories, started by hand for private ones).
- **Docs:** `AGENTS.md` in the root; `DEVLOG.md`, `ROADMAP.md`, `SETUP.md`, and
  `PROJECT_DISCOVERY.md` in `docsagent/`.
- **Project discovery:** `docsagent/PROJECT_DISCOVERY.md` holds the interview the AI runs after
  setup. From the answers it writes `docsagent/APP_PLAN.md`, `docsagent/DESIGN.md`, and
  `docsagent/design.html`. These files do not exist yet.
