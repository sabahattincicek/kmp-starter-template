# Features

One line per feature of the app: what it does, the module that holds it, its main use cases, and
the third-party services it uses. When a task mentions a feature, look it up here first instead of
searching the code for it.

## Rules

- Add a row when a feature module is created. Update the row in the same piece of work that changes
  what the feature does, its use cases, or its services, and remove it when the feature is deleted.
- Keep each row to one line. Details belong in `APP_PLAN.md` and in the code.
- Name services exactly as in [`SERVICES.md`](SERVICES.md).

---

| Feature | What it does | Module | Use cases | Services |
|---|---|---|---|---|

<!--
The template has no feature yet. Example row:

| Profile | Shows and edits the signed-in user's profile | `:presentation:features:profile` | `GetProfileUseCase`, `UpdateProfileUseCase` | Firebase Authentication |
-->
