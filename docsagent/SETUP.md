# Starter Template Setup Guide

Follow these steps immediately after creating a new project from this template.

---

## 1. Rename the Project

The template ships with the package `com.template.kmpstarter`, the project name `KmpStarter`, and
the app name `KMP Starter`. One Gradle task renames all of them, on every platform:

```bash
./gradlew renameProject -PnewPackage=com.mycompany.mynewapp -PnewProjectName=MyNewApp
```

In PowerShell, quote the arguments:

```powershell
.\gradlew renameProject "-PnewPackage=com.mycompany.mynewapp" "-PnewProjectName=MyNewApp"
```

Optional: `-PnewAppName="My New App"` sets the name shown under the app icon and in the browser
tab. Without it, the project name is split into words (`MyNewApp` → `My New App`).

The task:

* replaces the package in every Kotlin, Gradle, XML, and TOML file (`namespace`, `applicationId`,
  `packageOfResClass`, `package` and `import` lines in all modules) and in the iOS bundle id
  (`iosApp/Configuration/Config.xcconfig`);
* moves the source folders of every module and every source set (`commonMain`, `androidMain`,
  `iosMain`, `wasmJsMain`, tests) to the new package path, and renames the Room schema folder in
  `data/schemas`;
* sets `rootProject.name` in `settings.gradle.kts`;
* replaces the app name in the string resources (`app_name` in `:androidApp` and `:app`), the iOS
  app name (`APP_NAME` in `Config.xcconfig`), and the web page title (`webApp/.../index.html`);
* deletes the `build` folders of the modules, because outputs built with the old package would
  break the next build.

Package rules: lowercase, at least two segments, no underscores (`com.mycompany.mynewapp`).

The `App` class, `App()` composable, `AppTheme`, `Theme.App`, the `iosApp` Xcode project, and the
`ComposeApp` iOS framework are intentionally generic and do not need to be renamed.

---

## 2. Verify the Project

Close and reopen the project in Android Studio (or run **File → Sync Project with Gradle Files**),
then:

* [ ] Search the project for `kmpstarter`, `KmpStarter`, and `KMP Starter`. The only expected hits
      are in `build-logic/` (the rename task itself), the docs in `docsagent/`, and the title of
      `README.md` (replace it with your project's name and description).
* [ ] Check `applicationId` in `androidApp/build.gradle.kts` and `BUNDLE_ID` in
      `iosApp/Configuration/Config.xcconfig`.
* [ ] Run `./gradlew :androidApp:assembleDebug :webApp:wasmJsBrowserDistribution allTests lint`.
* [ ] Run the Android app and make sure it launches.
* [ ] Run the web app with `./gradlew :webApp:wasmJsBrowserDevelopmentRun`; it opens at
      `http://localhost:8080`.
* [ ] iOS (only on a Mac with Xcode): open `iosApp/iosApp.xcodeproj` and run it on a simulator. To
      run on a real device, set your Apple team id as `TEAM_ID` in
      `iosApp/Configuration/Config.xcconfig`. Without a Mac, the iOS build runs in CI (section 5).

The browser tests (`allTests`) need Google Chrome installed; CI has it.

---

## 3. Remove the Optional Parts You Do Not Need

The `:data` module comes with three ready-to-use parts, each working on Android, iOS, and the web:
**Network** (Ktor), **Room** (local database), and **Settings** (key-value storage with
multiplatform-settings). Not every app needs them. Their files start with an `OPTIONAL` note, and
their lines in `data/build.gradle.kts` and `data/.../di/DataModules.kt` are marked with comments.

### Removing an optional part

| Step | Network | Room | Settings |
|---|---|---|---|
| 1. Delete files | `data/.../di/NetworkModule.kt` | `data/.../di/DatabaseModule*.kt` (common and the three platform files), the `data/.../local/database/` folder, `data/schemas/`, `data/sqlite-web-worker/` | `data/.../di/SettingsModule*.kt` (common and the three platform files) |
| 2. In `data/build.gradle.kts` remove | the `// Network` lines and the serialization plugin | the `// Room` lines, the ksp and room plugins, the `dependencies { }` block with the `ksp...` lines, and the `room3 { }` block | the `// Settings` line |
| 3. In `DataModules.kt` remove | `networkModule` | `databaseModule` | `settingsModule` |
| 4. Extra cleanup | `INTERNET` permission in `androidApp/src/main/AndroidManifest.xml` | `alias(libs.plugins.ksp)` and `alias(libs.plugins.room)` in the root `build.gradle.kts` | — |

Keep the empty `remote/` or `local/` folders if you like; their `.gitkeep` files are harmless.
Optionally, remove the now unused entries from `gradle/libs.versions.toml` (`ktor`, `room`,
`sqlite`, `multiplatformSettings`). Keep `kotlinx-serialization`: Navigation 3 keys use it.

After removing Room, run `./gradlew kotlinWasmUpgradeYarnLock` once: the web build no longer needs
the SQLite worker's npm packages, and the lock file in `kotlin-js-store/` must be updated.

Then sync Gradle and build again.

---

## 4. Replace the Placeholders

* **Start screen:** the app opens a placeholder screen that shows the app name
  (`app/.../navigation/StartPlaceholder.kt`). Add your first feature (see "Adding a feature
  module" in `docsagent/ARCHITECTURE.md`), start the back stack in `AppNavDisplay` with its key, and
  delete the placeholder.
* **`ExampleEntity` / `ExampleDao`** in `data/.../local/database/`: Room needs at least one entity,
  so replace them with your first real table instead of just deleting them. Delete `data/schemas/`
  as well so the schema starts fresh at version 1.
* **App icons:** the Android launcher icon is in `androidApp/src/main/res/` (drawable and
  mipmap), the iOS icon is `iosApp/iosApp/Assets.xcassets/AppIcon.appiconset/app-icon-1024.png`
  (1024 × 1024, no transparency). Add a `favicon.ico` to `webApp/src/wasmJsMain/resources/` for the
  browser tab.
* **Backend URL:** `BASE_URL` in `data/.../di/NetworkModule.kt` (if networking is used).
* **Empty folders** contain a `.gitkeep` file so that git keeps them. Delete the `.gitkeep` once a
  folder has real files, or delete the folder if the project does not need it.

---

## 5. CI and the iOS Build

`.github/workflows/ci.yml` has two jobs:

| Job | Runner | Runs | When |
|---|---|---|---|
| Android and Web | Linux | Android and web build, unit tests (Android host and browser), lint | every push to `main` / `develop` and every pull request |
| iOS | macOS | iOS simulator unit tests and the Xcode build of `iosApp` | **public repository:** same as above. **Private repository:** only when started by hand |

GitHub-hosted runners are free for public repositories. For private repositories every job uses
the account's monthly Actions minutes, and macOS minutes cost about ten times as much as Linux
minutes. When the minutes run out (with the default spending limit of $0), all jobs stop until
the next month, not only the iOS one. That is why the iOS job does not run automatically in a
private repository.

* **Private repository, run the iOS job:** GitHub → **Actions** → **CI** → **Run workflow**.
* **Always run it, even when private:** in the `ios` job, delete the `if:` line.
* **No iOS build in CI at all:** delete the `ios` job.

---

## 6. Review the AI Assistant Files

The template ships with files that guide AI coding assistants. Open them and adjust them to your
project:

| File | Purpose |
|---|---|
| `AGENTS.md` | Rules the AI follows: scope, comments, logging, unit tests, architecture, and git workflow. Stays in the root. |
| `docsagent/DEVLOG.md` | Timestamped log of what has been done, newest entry first, so the AI does not have to re-read the whole codebase every session. |
| `docsagent/ROADMAP.md` | Planned work, split into phases and checkbox tasks (`[ ]` open, `[x]` done). |
| `docsagent/PROJECT_DISCOVERY.md` | The questions the AI asks to write the app plan and the design criteria. |
| `docsagent/FIRST_SESSION.md` | What the AI asks and does in the first session (AI tool, git, project discovery). |
| `docsagent/ARCHITECTURE.md` | Full folder tree, parts of a feature module, and how to add one. |
| `docsagent/ROADMAP_ARCHIVE.md` | Finished roadmap phases, moved out of `ROADMAP.md`. |

Things to do:

* [ ] Read `AGENTS.md` and change, add, or remove rules as you see fit.
* [ ] Replace the placeholder phases in `docsagent/ROADMAP.md` with your own plan.
* [ ] Leave the first `docsagent/DEVLOG.md` entry as it is: it describes what the template gave
      you, and its mention of `KmpStarter` is intentional.

In your first session, tell the AI to read `AGENTS.md`. It will then ask you two things and record
your answers in the file:

* **Which AI tool you are using.** It renames `AGENTS.md` to the file name your tool loads
  automatically (for example `CLAUDE.md` for Claude Code), so later sessions pick the rules up on
  their own.
* **Who makes git commits and pushes:** the AI or you.

When the setup is finished, the AI runs the **project discovery** interview from
`docsagent/PROJECT_DISCOVERY.md`. It asks you, a few questions at a time, about the app (purpose,
users, scope, platforms, backend and hosting, integrations, monetization, release plan) and then
about its design (personality, colors, typography, shapes, components, layout, motion,
accessibility, tone). From your answers it writes:

* `docsagent/APP_PLAN.md`: the app plan, from the first release to the long-term vision.
* `docsagent/DESIGN.md`: the design system the Compose theme is built from.
* `docsagent/design.html`: a single page showing the palette, components, and screen mockups.
  Open it in a browser.

You can answer any question with "you decide" or "not sure yet". The AI then makes a choice and
explains it, or records the question as open. It also rebuilds `docsagent/ROADMAP.md` from the
plan.

---

## 7. Remove the Setup Files

After completing the setup successfully:

* Delete this file, `docsagent/SETUP.md`, and the "First-Time Setup" section of `README.md`.
* Keep `docsagent/PROJECT_DISCOVERY.md`: the AI uses it again when you want to revisit the plan or
  the design.
* Optionally remove the rename task: delete `alias(libs.plugins.starter.rename)` from the root
  `build.gradle.kts`, the `starter-rename` line in `gradle/libs.versions.toml`, the `rename` entry in
  `build-logic/convention/build.gradle.kts`, and
  `build-logic/convention/src/main/kotlin/RenameProjectConventionPlugin.kt`.

The project is now ready for development.
