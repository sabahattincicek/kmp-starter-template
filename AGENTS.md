# AI Agent Rules

Rules for any AI coding assistant working in this repository. They are tool-agnostic, and
developers are free to edit them to fit the project.

---

## Project Settings

Filled in once, on the first session (see [`docsagent/FIRST_SESSION.md`](docsagent/FIRST_SESSION.md)).

- **AI tool:** `NOT SET` <!-- e.g. "Claude Code", "Codex", "Gemini CLI", "Cursor" -->
- **Git commits and pushes are made by:** `NOT SET` <!-- "AI" or "Developer" -->
- **Language for code, comments, logs, and commit messages:** `English`
- **Project discovery (app plan and design):** `NOT DONE` <!-- "DONE" once the plan and design files in docsagent/ are approved -->

The AI working documents (development log, roadmap, setup guide, first session steps,
architecture, feature map, third-party services, project discovery, app plan, design) live together in [`docsagent/`](docsagent/).
Only this file and `README.md` stay in the root, because tools load this file from there.

If any Project Setting is `NOT SET`, or "Project discovery" is `NOT DONE`, follow
`docsagent/FIRST_SESSION.md` before anything else.

---

## 1. Session Start

At the start of every session, before writing any code:

1. Read only the first ~150 lines of [`docsagent/DEVLOG.md`](docsagent/DEVLOG.md) (newest entries
   are at the top) to learn what has been done recently. For anything older, search the file with
   grep instead of reading it from start to end. Do not re-read the whole codebase to rebuild that
   context; open only the files the current task needs.
2. Look only at the open tasks (`- [ ]`) in [`docsagent/ROADMAP.md`](docsagent/ROADMAP.md).
3. Check the Project Settings above.
4. If [`docsagent/APP_PLAN.md`](docsagent/APP_PLAN.md) and
   [`docsagent/DESIGN.md`](docsagent/DESIGN.md) exist, read the parts that matter for the current
   task: the plan for scope and features, the design for any UI work.

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
- **Third-party services.** [`docsagent/SERVICES.md`](docsagent/SERVICES.md) lists every
  outside service (backend, analytics, crash reporting, push, payments, and so on), the features
  that use it, and where it is wired in. Read it instead of searching the code when a task touches
  a service. Ask before adding a service, just like a library, and update the file in the same
  piece of work that adds, removes, or changes one. Never write secrets into it.
- **Follow the plan and the design.** Build what `docsagent/APP_PLAN.md` describes and style the UI
  with the tokens and components in `docsagent/DESIGN.md`; no colors, sizes, or fonts outside the
  theme. When a task changes the plan or the design, update those files (and
  `docsagent/design.html`) and their decision log in the same piece of work.
- **Search cheaply.** Find code with grep and glob, and open only the files and line ranges you
  need. For broad searches across many files, use the tool's built-in search subagent if it has one
  (Claude Code: `Explore`), so only its short answer enters the conversation. Search directly when
  one grep is enough. Files listed in `.ignore` (generated files such as Room schemas) are left out
  of search on purpose; read one by its path only when the task needs it.
- **Feature map.** [`docsagent/FEATURES.md`](docsagent/FEATURES.md) has one line per feature: what
  it does, its module, its use cases, and its services. When a task mentions a feature, look it up
  there first. Update the row in the same piece of work that adds, changes, or removes a feature.
- **Token usage log.** The Claude Code hooks write `docsagent/token-usage/` on their own. Never
  edit those files, and read them only through `python3 scripts/token_usage.py report` (`python` on
  Windows) when the developer asks about token usage.
- **Language.** Write code identifiers, comments, log messages, and commit messages in the language
  set in Project Settings. User-facing text follows the app's own localization.

### Definition of done

A task is finished only when all of these are true:

1. The build wrapper prints `BUILD OK`: `scripts/check.ps1` on Windows, `scripts/check.sh` on
   macOS and Linux. With no arguments it builds the Android and web apps, runs all unit tests
   (Android host and browser), and runs lint. On macOS, add `--ios` for the iOS simulator tests
   and the Xcode build.
2. Unit tests are updated for the change.
3. New and changed code follows the comment and logging rules below.
4. `docsagent/DEVLOG.md` has a new entry for the work. `docsagent/FEATURES.md` and
   `docsagent/SERVICES.md` are up to date if a feature or a service was added, changed, or removed.
5. The matching `docsagent/ROADMAP.md` tasks are marked `[x]`.

CI runs the same Gradle tasks on every push and pull request (`.github/workflows/ci.yml`), plus an
iOS build on macOS. iOS code cannot be built on Windows or Linux: Kotlin still compiles the iOS
source sets there and reports errors, but the app itself is built only on macOS (CI or a Mac).
Never report a task as done while any of these is missing; say what is still open.

When the task is done (after DEVLOG and ROADMAP are updated), end your last message to the
developer with this line: `Task done. Run /clear before the next task.` Do not start a new task
in the same session on your own.

### Running the build

- Build, test, and lint through the wrapper, not with raw Gradle. It writes the full output to
  `build/agent-logs/last-build.log` and prints only a summary: one line on success; the compile
  errors, failed tests, and lint errors on failure.
- While working, pass only the tasks you need, for example `scripts/check.ps1 :domain:allTests`.
  Run it without arguments before calling the task done.
- If the summary is not enough to understand a failure, search `last-build.log` with grep instead
  of reading it whole or running the build again. Run Gradle directly only when the log does not
  answer it either.

---

## 3. Documentation Comments

- **Every file or class** starts with a KDoc comment explaining what it is for and where it fits in
  the app. Every module's `build.gradle.kts` starts with a comment saying what the module holds.
- **Functions** that are public API, or whose purpose is not clear from the code, have a comment of
  one or two sentences above them saying what they do. Other functions need no comment.
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
) : ViewModel()
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
  placeholders. Do not use `println`, `android.util.Log`, or `NSLog`.
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
  future session needs to know. Keep an entry short (about ten bullet points at most): every
  session reads the newest entries.
- Never rewrite or delete older entries.

---

## 7. Roadmap — `docsagent/ROADMAP.md`

[`docsagent/ROADMAP.md`](docsagent/ROADMAP.md) holds everything still to be done.

- Work is organized in **phases**. Each phase is broken into small tasks.
- Every task starts with a checkbox: `- [ ]` for open, `- [x]` for done.
- Mark a task `[x]` as soon as it is finished.
- When every task of a phase is `[x]`, move the phase to
  [`docsagent/ROADMAP_ARCHIVE.md`](docsagent/ROADMAP_ARCHIVE.md) (newest at the top) and leave one
  line in `ROADMAP.md`: `Phase N — done, see ROADMAP_ARCHIVE.md`.
- When new work comes up, add it to the right phase, or add a new phase.
- Before starting work, confirm the task exists in the roadmap; add it if it does not.

---

## 8. Architecture

A **Kotlin Multiplatform** app for Android, iOS, and the web (Wasm) with one shared Compose
Multiplatform UI, built with **Clean Architecture**, **MVVM**, and unidirectional data flow; each
layer is a Gradle module. Read [`docsagent/ARCHITECTURE.md`](docsagent/ARCHITECTURE.md) before
adding a module or when the folder layout matters.

Rules:

- **Dependency direction:** platform entry points → `:app` → `:presentation:features:*` →
  `:domain` ← `:data`. Features depend only on `:domain`, `:presentation:ui`, and `:core:common`
  (never on `:data` or each other), so they reach data only through use cases. `:domain` depends
  only on `:core:common`; only `:app` depends on `:data`, to load its Koin modules.
- **Pure domain:** no platform APIs, Ktor, or Room types in `:domain`; Kermit and Koin module
  declarations are the only exceptions.
- **Repositories:** interface in `domain/repository/`, implementation in `data/repository/`, bound
  in `data/di/RepositoryModule`.
- **Models:** DTOs (`data/remote/dto/`) and entities (`data/local/database/entity/`) are mapped to
  `domain/model/` in the repositories and never reach the UI.
- **Use cases:** one job each, exposed through `operator fun invoke`, registered in
  `domain/di/DomainModule`.
- **ViewModels:** one immutable UI state as `StateFlow`, user actions as events through one
  `onEvent`, and calls to use cases, not repositories. Registered in the feature's Koin module
  (`viewModelOf(::ProfileViewModel)`) and obtained in the route with `koinViewModel()`.
- **Composables:** stateless where possible, state hoisted to the ViewModel, no business logic.
  Every screen composable has a `@Preview`.
- **Navigation:** Navigation 3. Each feature defines a `@Serializable` `NavKey` and an
  `EntryProviderScope<NavKey>.xxxEntry()`; `:app` registers the key in `navKeys` and the entry in
  `AppNavDisplay`. Features navigate only through lambda parameters (`onOpenDetail: (id) -> Unit`),
  never by touching the back stack.
- **Results and text:** results wrapped in `Resource`; user-facing text passed as `UiText`.
- **No hardcoded user-facing text:** strings live in
  `src/commonMain/composeResources/values/strings.xml` of the module that uses them, prefixed with
  the module name (`profile_title`, `ui_retry`), used through `stringResource(Res.string.x)` or
  `UiText.StringResource`. Translations go in `values-<language>/strings.xml`.
- **Dependency injection:** everything comes from Koin modules through the constructor; never
  create dependencies by hand inside classes.
- **Platform code:** only in `androidMain`, `iosMain`, and `wasmJsMain`, as `expect`/`actual` or a
  platform Koin module (see `data/di/DatabaseModule*.kt`). No platform checks
  (`if (platform == ...)`) in shared code.
- **Build configuration:** shared setup lives in the `build-logic/` convention plugins; a module's
  `build.gradle.kts` only applies plugins, sets its namespace, and lists its own dependencies. All
  library versions go in `gradle/libs.versions.toml`.
- **Small files:** keep a file under about 300 lines, because an AI reads whole files and pays for
  every line. Split a growing screen into smaller composables and a growing ViewModel into use
  cases.

---

## 9. Git

Follow the choice recorded in Project Settings:

- **`AI`:** create branches, commit, and push as described below.
- **`Developer`:** do not commit or push. When a piece of work is finished, suggest a branch name
  and a commit message for the developer to use.

### Branches

- `main` is always stable and releasable; never commit to it directly. `develop` is the
  integration branch.
- Work on short-lived branches cut from `develop`: `feature/`, `bugfix/`, `refactor/`, `chore/`,
  or `docs/` followed by a short description.
- `hotfix/<short-description>` is cut from `main` for urgent production fixes;
  `release/<version>` is cut from `develop` to prepare a release.

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
