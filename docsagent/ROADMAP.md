# Roadmap

Everything that is still to be done, organized in phases. Each phase is broken into small tasks.

- `- [ ]` open task
- `- [x]` finished task

Mark tasks as done when they are finished. When every task of a phase is done, move the phase to
`ROADMAP_ARCHIVE.md` (newest at the top) and leave one line here:
`Phase N — done, see ROADMAP_ARCHIVE.md`. Add new tasks to the phase they belong to, or add a new
phase.

---

## Phase 0 — Project Setup

- [ ] Run `renameProject` with the new package and project name
- [ ] Verify the rename and search for leftover `kmpstarter` references
- [ ] Remove the optional parts of `:data` the project does not need (Network, Room, Settings)
- [ ] Add the first feature module and remove the placeholder start screen
- [ ] Replace `ExampleEntity` and `ExampleDao` with the first real table (if Room is used)
- [ ] Replace the app icons (Android, iOS, web favicon)
- [ ] Decide how the iOS job runs in CI (see "CI and the iOS Build" in `docsagent/SETUP.md`)
- [ ] Review `AGENTS.md` and adjust the rules to the project
- [ ] Answer the AI's first-session questions (AI tool, who makes git commits and pushes)
- [ ] Build, test, lint, and run the app on Android and the web (and iOS on a Mac)
- [ ] Delete `docsagent/SETUP.md`
- [ ] Project discovery (`docsagent/PROJECT_DISCOVERY.md`): write and approve
      `docsagent/APP_PLAN.md`
- [ ] Project discovery: write and approve `docsagent/DESIGN.md` and `docsagent/design.html`
- [ ] Replace the phases below with the plan from `docsagent/APP_PLAN.md`

## Phase 1 — Foundation

<!-- Replace the tasks below with the real plan for this project. -->

- [ ] Apply `docsagent/DESIGN.md` to `presentation/ui/theme/` (colors, typography, shapes, spacing)
- [ ] Define the app's screens and navigation graph
- [ ] Define the domain models, repository interfaces, and use cases
- [ ] Set the real `BASE_URL` in `data/.../di/NetworkModule.kt` (if networking is used)

## Phase 2 — Features

- [ ] ...

## Later

- [ ] Consider adding a code style check (ktlint or detekt) to CI
- [ ] Consider deploying the web app from CI (GitHub Pages, Firebase Hosting, Netlify, ...)
