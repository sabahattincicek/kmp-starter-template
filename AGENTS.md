# AI Agent Rules

Rules for any AI coding assistant working in this repository. They are tool-agnostic, and
developers are free to edit them to fit the project.

Read this file fully at the start of every session, then follow the session start steps below.

---

## Project Settings

Filled in once, on the first session (see "First session" below).

- **AI tool:** `NOT SET` <!-- e.g. "Claude Code", "Codex", "Gemini CLI", "Cursor" -->
- **Git commits and pushes are made by:** `NOT SET` <!-- "AI" or "Developer" -->
- **Language for code, comments, logs, and commit messages:** `English`
- **Project discovery (app plan and design):** `NOT DONE` <!-- "DONE" once the plan and design files in docsagent/ are approved -->

The AI working documents (development log, roadmap, setup guide, project discovery, app plan,
design) live together in [`docsagent/`](docsagent/). Only this file and `README.md` stay in the
root, because tools load this file from there.

---

## 1. Session Start

At the start of every session, before writing any code:

1. Read [`docsagent/DEVLOG.md`](docsagent/DEVLOG.md) to learn what has been done so far. Do not
   re-read the whole codebase to rebuild that context; open only the files the current task needs.
2. Read [`docsagent/ROADMAP.md`](docsagent/ROADMAP.md) to see what is planned and what is still
   open.
3. Check the Project Settings above.
4. If [`docsagent/APP_PLAN.md`](docsagent/APP_PLAN.md) and
   [`docsagent/DESIGN.md`](docsagent/DESIGN.md) exist, read the parts that matter for the current
   task: the plan for scope and features, the design for any UI work.

### First session

If a value in Project Settings is still `NOT SET`, ask the developer and write the answer into
Project Settings. Do not ask again once it is set. Project discovery (question 3) has its own
rule below.

**1. Which AI tool is being used?**

> Which AI coding tool are you using for this project?

Then rename this file to the name that tool loads automatically, so the rules apply in every
session without the developer pointing to them:

| Tool | File name |
|---|---|
| Claude Code | `CLAUDE.md` |
| Gemini CLI | `GEMINI.md` |
| GitHub Copilot | `.github/copilot-instructions.md` |
| Codex, Cursor, and other tools that read `AGENTS.md` | keep `AGENTS.md` |
| Any other tool | the instruction file name that tool documents |

Keep the renamed file in the root (or in `.github/` for Copilot); tools do not look for it in
`docsagent/`. After renaming, update every reference to `AGENTS.md` in `README.md`, the CI
workflow, and the files in `docsagent/`.

**2. Who makes git commits and pushes?**

> Should I make the git commits and pushes myself, or will you do them?

Record `AI` or `Developer`.

**3. Project discovery: the app plan and the design criteria**

Once the setup in [`docsagent/SETUP.md`](docsagent/SETUP.md) is finished, and while "Project
discovery" is `NOT DONE`, run the interview in
[`docsagent/PROJECT_DISCOVERY.md`](docsagent/PROJECT_DISCOVERY.md):

> The setup is done. Before we build anything, I would like to plan the app with you: first what it
> does and how it will grow, then how it should look. Shall we start?

It produces `docsagent/APP_PLAN.md`, `docsagent/DESIGN.md`, and `docsagent/design.html`. Follow the
rules in that file, and set "Project discovery" to `DONE` when the developer has approved all
three. If the developer wants to postpone it, offer it again at the next session start, and before
building the first feature.

---

## 2. Working Rules

- **Stay in scope.** Change only what the task needs. Do not refactor, reformat, or "improve"
  unrelated code along the way; mention it to the developer instead.
- **Ask before large or destructive changes:** deleting files or modules, rewriting a module,
  changing the architecture, or anything hard to undo.
- **Ask before adding a library.** Do not add, replace, or upgrade a dependency without the
  developer's approval. Say what it is for and what the alternatives are, and check that it
  supports every target of the project (Android, iOS, and Wasm).
- **Shared code first.** Write code in `commonMain`. Use a platform source set (`androidMain`,
  `iosMain`, `wasmJsMain`) only for what a platform does differently, through `expect`/`actual` or
  a Koin module per platform, and keep the platform part as small as possible.
- **Follow the plan and the design.** Build what `docsagent/APP_PLAN.md` describes and style the UI
  with the tokens and components in `docsagent/DESIGN.md`; no colors, sizes, or fonts outside the
  theme. When a task changes the plan or the design, update those files (and
  `docsagent/design.html`) and their decision log in the same piece of work.
- **Language.** Write code identifiers, comments, log messages, and commit messages in the language
  set in Project Settings. User-facing text follows the app's own localization.

### Definition of done

A task is finished only when all of these are true:

1. The Android and web apps build (`./gradlew :androidApp:assembleDebug
   :webApp:wasmJsBrowserDistribution`).
2. Unit tests are updated and pass (`./gradlew allTests`: Android host tests and browser tests;
   on macOS also the iOS simulator tests).
3. Lint reports no new issues (`./gradlew lint`).
4. New and changed code follows the comment and logging rules below.
5. `docsagent/DEVLOG.md` has a new entry for the work.
6. The matching `docsagent/ROADMAP.md` tasks are marked `[x]`.

The same commands run in CI on every push and pull request (`.github/workflows/ci.yml`), plus an
iOS build on macOS. iOS code cannot be built on Windows or Linux: Kotlin still compiles the iOS
source sets there and reports errors, but the app itself is built only on macOS (CI or a Mac).
Never report a task as done while any of these is missing; say what is still open.

---

## 3. Documentation Comments

- **Every file or class** starts with a KDoc comment explaining what it is for and where it fits in
  the app. Every module's `build.gradle.kts` starts with a comment saying what the module holds.
- **Every function** has a comment of one or two sentences above it saying what it does.
- `actual` declarations say what the platform does differently
  (`/** iOS: settings are stored in NSUserDefaults. */`).
- Keep comments in sync with the code. When behavior changes, update the comment in the same change.
- Explain purpose and intent, not the syntax.

```kotlin
/**
 * Loads the signed-in user's profile and exposes it to the profile screen as UI state.
 */
class ProfileViewModel(
    private val getProfile: GetProfileUseCase,
) : ViewModel() {

    /** Fetches the profile again and updates the UI state with the result. */
    fun refresh() { ... }
}
```

---

## 4. Logging

- Every class that has behavior logs, using **Kermit**. Each class creates a tagged logger and logs
  with lambdas, so the message is only built when it is printed:

  ```kotlin
  private val log = Logger.withTag("ProfileViewModel")

  log.d { "Profile loaded: id=${profile.id}" }
  ```

  Classes with nothing to log are exempt: plain data holders (models, DTOs, entities, UI state,
  events, wrappers such as `Resource` and `UiText`), constants, theme definitions, and empty
  placeholders. Do not use `println`, `android.util.Log`, or `NSLog`. Kermit writes to Logcat on
  Android, to the unified log (OSLog) on iOS, and to the browser console on the web; it is silent
  in release builds (`configureLogging` in `:core:common`, called from `initKoin`). Every shared
  module already has Kermit through the convention plugins.
- Place logs wherever they help debugging: function entry for significant operations, state
  changes, branch decisions, network and database calls with their results, and every caught
  exception.
- Use the right level: `log.d` for flow and state, `log.i` for notable events, `log.w` for
  recoverable problems, `log.e` for failures (always pass the exception first:
  `log.e(exception) { "Profile load failed" }`).
- Log messages state what happened and include the relevant values, so a log line is useful on its
  own.
- Never log passwords, tokens, API keys, or personal user data.

---

## 5. Unit Tests

- Keep unit tests up to date at all times. Any change to behavior comes with the matching test
  change in the same piece of work.
- New use cases, repositories, and ViewModels get unit tests when they are written, in the
  `src/commonTest` folder of the same module, so they run on every platform.
- Available in every module: `kotlin.test`, kotlinx-coroutines-test, and Turbine (for testing
  flows). `:core:testing` adds `MainDispatcherTest` (base class for ViewModel tests) and
  `TestDispatcherProvider`; feature modules get it automatically, `:domain` and `:data` already
  have it, other modules add `commonTest.dependencies { implementation(projects.core.testing) }`.
- Mocking libraries such as MockK do not work on iOS and the web. Write small fakes instead: a
  `FakeProfileRepository` that implements the interface and keeps its data in a list, in the
  `commonTest` folder of the module that needs it.
- Inject `DispatcherProvider` instead of using `Dispatchers` directly, so tests can supply test
  dispatchers.
- Run the tests before calling a task done. Never leave failing tests behind, and never delete or
  weaken a test just to make it pass.

---

## 6. Development Log — `docsagent/DEVLOG.md`

[`docsagent/DEVLOG.md`](docsagent/DEVLOG.md) is the project's memory. It exists so that a new
session can understand the project without reading all the code.

- After finishing a piece of work, add an entry describing what was done.
- Every entry is stamped with date and time: `## YYYY-MM-DD HH:mm — Short title`.
- **New entries go at the top of the file**, directly under the header. The file reads from newest
  to oldest.
- Write what changed, why, and which files or areas were touched. Note decisions and anything a
  future session needs to know.
- Never rewrite or delete older entries.

---

## 7. Roadmap — `docsagent/ROADMAP.md`

[`docsagent/ROADMAP.md`](docsagent/ROADMAP.md) holds everything still to be done.

- Work is organized in **phases**. Each phase is broken into small tasks.
- Every task starts with a checkbox: `- [ ]` for open, `- [x]` for done.
- Mark a task `[x]` as soon as it is finished. Do not remove finished tasks.
- When new work comes up, add it to the right phase, or add a new phase.
- Before starting work, confirm the task exists in the roadmap; add it if it does not.

---

## 8. Architecture

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
docsagent/                    AI working documents: DEVLOG, ROADMAP, SETUP, project discovery,
                              app plan, design
```

Every shared module has the source sets `commonMain` (most code), `androidMain`, `iosMain`, and
`wasmJsMain` (platform parts), and `commonTest`. Strings, images, and fonts live in
`src/commonMain/composeResources/`.

A feature module contains `<Name>Screen` (stateless), `<Name>Route` (stateful, gets the ViewModel),
`<Name>ViewModel`, `<Name>UiState` and `<Name>Event`, `navigation/` with its `NavKey` and
`xxxEntry()` function, and `di/` with its Koin module.

Rules:

- **Dependency direction:** platform entry points → `:app` → `:presentation:features:*` →
  `:domain` ← `:data`. Feature modules depend on `:domain`, `:presentation:ui`, and `:core:common`;
  never on `:data` or on each other, so they reach data only through use cases. `:domain` depends
  only on `:core:common`. Only `:app` depends on `:data`, to load its Koin modules.
- **Domain stays pure Kotlin:** no platform APIs, Ktor, or Room types in `:domain` (Kermit logging
  and Koin module declarations are the only exceptions).
- **Repositories:** interface in `domain/repository/`, implementation in `data/repository/`, bound
  in `data/di/RepositoryModule`.
- **Models:** DTOs stay in `data/remote/dto/` and entities in `data/local/database/entity/`;
  repositories map them to the domain models in `domain/model/`. They never reach the UI.
- **Use cases:** one use case does one thing and exposes it through `operator fun invoke`.
  Register it in `domain/di/DomainModule`.
- **ViewModels:** expose a single immutable UI state as `StateFlow`, receive user actions as
  events through one `onEvent` function, and call use cases, not repositories. Each feature
  registers its ViewModels in its own Koin module (`viewModelOf(::ProfileViewModel)`), and the
  route gets them with `koinViewModel()`.
- **Composables:** stateless where possible; state is hoisted to the ViewModel. No business logic
  in composables. Every screen composable has a `@Preview`.
- **Navigation:** Navigation 3. Each feature defines a `@Serializable` `NavKey` and an
  `EntryProviderScope<NavKey>.xxxEntry()` function; `:app` registers the key in `navKeys` and the
  entry in `AppNavDisplay`. Features navigate through lambda parameters
  (`onOpenDetail: (id) -> Unit`), never by touching the back stack themselves.
- **Results and text:** wrap operation results in `Resource`; pass user-facing text as `UiText`.
- **No hardcoded user-facing text:** every string the user sees lives in
  `src/commonMain/composeResources/values/strings.xml` of the module that uses it, prefixed with the
  module name (`profile_title`, `ui_retry`), and is referenced through `stringResource(Res.string.x)`
  or `UiText.StringResource`. Translations go in `values-<language>/strings.xml`.
- **Dependency injection:** everything is provided through Koin modules. Do not create dependencies
  by hand inside classes; receive them through the constructor.
- **Platform code:** what differs per platform goes in `androidMain`, `iosMain`, and `wasmJsMain`,
  either as `expect`/`actual` declarations or as a platform Koin module (see
  `data/di/DatabaseModule*.kt`). Never put platform checks (`if (platform == ...)`) in shared code.
- **Build configuration:** shared module setup lives in the convention plugins in `build-logic/`;
  a module's `build.gradle.kts` only applies plugins, sets its namespace, and lists its own
  dependencies. Declare all library versions in `gradle/libs.versions.toml` (and see "Ask before
  adding a library" above).

### Adding a feature module

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

---

## 9. Git

Follow the choice recorded in Project Settings:

- **`AI`:** create branches, commit, and push as described below.
- **`Developer`:** do not commit or push. When a piece of work is finished, suggest a branch name
  and a commit message for the developer to use.

### Branches

- `main` is always stable and releasable. Never commit to it directly.
- `develop` is the integration branch.
- Work happens on short-lived branches cut from `develop`:
  - `feature/<short-description>` for new functionality
  - `bugfix/<short-description>` for fixes
  - `refactor/<short-description>`, `chore/<short-description>`, `docs/<short-description>` as fitting
- `hotfix/<short-description>` is cut from `main` for urgent production fixes.
- `release/<version>` is cut from `develop` when preparing a release.

### Commits

Use [Conventional Commits](https://www.conventionalcommits.org):

```text
<type>(<scope>): <short summary in imperative mood>
```

Types: `feat`, `fix`, `refactor`, `test`, `docs`, `chore`, `style`, `perf`, `build`, `ci`.

Example: `feat(profile): add profile screen with pull to refresh`

- One logical change per commit. Keep commits small and buildable.
- Never commit secrets, keys, or `local.properties`.
- Never force-push shared branches or rewrite published history.
