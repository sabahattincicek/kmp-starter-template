# Architecture

The folder layout, the parts of a feature module, and how to add one. The rules between the layers
are in the Architecture section of `AGENTS.md`.

This project is a **Kotlin Multiplatform** app for Android, iOS, and the web (Wasm), with one
shared Compose Multiplatform UI. It follows **Clean Architecture** with **MVVM** and
unidirectional data flow. Each layer is a Gradle module, and the folder tree shows the layers.
Respect the structure:

```text
androidApp/                   :androidApp — Android entry point: App (starts Koin), MainActivity
iosApp/                       Xcode project — iOS entry point (Swift), imports the ComposeApp framework
webApp/                       :webApp — web entry point: main(), index.html
app/                          :app — shared application shell, builds the iOS framework
  App.kt                      App(): theme + navigation graph
  navigation/                 AppNavDisplay (the Navigation 3 graph) and the registered NavKeys
  di/                         initKoin and the list of every Koin module
core/                         shared building blocks, no business logic
  common/                     :core:common
    config/                   AppConfig (debug flag passed in by the platforms)
    logging/                  Kermit setup
    result/                   Resource
    dispatcher/               DispatcherProvider
    util/                     extensions and small helpers
    di/                       commonModule
  testing/                    :core:testing — unit test helpers
domain/                       :domain — business rules, pure Kotlin
  model/                      business models
  repository/                 repository interfaces
  usecase/                    use cases
  di/                         domainModule (use cases)
data/                         :data — implements the domain
  local/database/             Room: AppDatabase, entity/, dao/        (optional)
  local/settings/             key-value settings data sources         (optional)
  remote/api/                 Ktor API classes                        (optional)
  remote/dto/                 network models (DTOs)                   (optional)
  repository/                 repository implementations
  di/                         repositoryModule, networkModule, databaseModule, settingsModule
  sqlite-web-worker/          SQLite web worker for Room on the web   (optional)
presentation/                 UI layer
  ui/                         :presentation:ui — shared UI
    theme/                    colors, typography, AppTheme
    components/               shared composables (ErrorView, LoadingView)
    text/                     UiText
    base/                     base classes for ViewModels or screens, if wanted
  features/<name>/            :presentation:features:<name> — one module per screen or flow
build-logic/                  convention plugins (see build-logic/README.md)
docsagent/                    AI working documents: DEVLOG, ROADMAP (and its archive), SETUP,
                              first session, architecture, features, services, project discovery,
                              app plan, design
scripts/                      check.ps1 and check.sh: build, test, and lint with a short summary
```

Every shared module has the source sets `commonMain` (most code), `androidMain`, `iosMain`, and
`wasmJsMain` (platform parts), and `commonTest`. Strings, images, and fonts live in
`src/commonMain/composeResources/`.

A feature module contains `<Name>Screen` (stateless), `<Name>Route` (stateful, gets the ViewModel),
`<Name>ViewModel`, `<Name>UiState` and `<Name>Event`, `navigation/` with its `NavKey` and
`xxxEntry()` function, and `di/` with its Koin module.

## Adding a feature module

1. Create `presentation/features/<name>/build.gradle.kts`:

   ```kotlin
   // <What the feature does, in one line.>
   plugins {
       alias(libs.plugins.starter.kmp.feature)
   }

   kotlin {
       android {
           namespace = "<base package>.presentation.features.<name>"
       }
   }

   compose.resources {
       packageOfResClass = "<base package>.presentation.features.<name>.resources"
   }
   ```

2. Put the code in `src/commonMain/kotlin/<base package path>/presentation/features/<name>/`, its
   strings in `src/commonMain/composeResources/values/strings.xml`, and its tests in
   `src/commonTest/kotlin/...`.
3. Add a Koin module for its ViewModels in `di/` (`val profileModule = module { viewModelOf(::ProfileViewModel) }`).
4. Add `include(":presentation:features:<name>")` to `settings.gradle.kts`.
5. In `:app`: add `implementation(projects.presentation.features.<name>)` to `app/build.gradle.kts`,
   add its Koin module to `featureModules` in `di/Koin.kt`, register its `NavKey` in `navKeys` and
   call its `xxxEntry()` in `navigation/AppNavDisplay.kt`. For the first feature, start the back
   stack with its key and delete `StartKey` and `StartPlaceholder.kt`.
6. Add a row for the feature to [`FEATURES.md`](FEATURES.md).

### Feature module generator

**Status:** `NOT OFFERED` <!-- "NOT OFFERED", "DECLINED", or "BUILT" -->

The steps above write the same boilerplate for every feature. Once the project has **four or more
feature modules** and the status is `NOT OFFERED`, tell the developer once, when the next feature is
being added:

> The feature modules now follow a settled pattern. I can add a `newFeature` Gradle task (like
> `renameProject`) that generates a new feature module from this project's own conventions and
> registers it. Every later feature then costs fewer tokens and starts out consistent. Shall I
> build it?

If they agree, build the task from the project's existing features (not from this template's
placeholders), test it by generating and building a throwaway feature, and point the steps above to
it. Set the status to `BUILT` or `DECLINED` and do not offer it again.
