# First Session

Steps the AI follows while a value in the Project Settings of `AGENTS.md` is still `NOT SET`, or
while "Project discovery" is `NOT DONE`.

If a value in Project Settings is still `NOT SET`, ask the developer and write the answer into
Project Settings. Do not ask again once it is set. Project discovery (question 3) has its own
rule below.

**1. Which AI tool is being used?**

> Which AI coding tool are you using for this project?

Then rename `AGENTS.md` to the name that tool loads automatically, so the rules apply in every
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

Once the setup in [`SETUP.md`](SETUP.md) is finished, and while "Project
discovery" is `NOT DONE`, run the interview in
[`PROJECT_DISCOVERY.md`](PROJECT_DISCOVERY.md):

> The setup is done. Before we build anything, I would like to plan the app with you: first what it
> does and how it will grow, then how it should look. Shall we start?

It produces `docsagent/APP_PLAN.md`, `docsagent/DESIGN.md`, and `docsagent/design.html`. Follow the
rules in that file, and set "Project discovery" to `DONE` when the developer has approved all
three. If the developer wants to postpone it, offer it again at the next session start, and before
building the first feature.
