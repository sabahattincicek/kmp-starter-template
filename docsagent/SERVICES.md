# Third-Party Services

Every outside service the project uses: what it is for, which features depend on it, and where it
is wired into the code. Read this file instead of searching the code when you need to know which
service backs a feature, or what removing or replacing a service would touch.

Libraries that run only inside the app (Koin, Ktor, Room, Kermit) are not listed here; they are in
`gradle/libs.versions.toml`. A library that is the client of an outside service (a Firebase SDK,
the Sentry SDK) is listed here with its service.

## Rules

- Update this file in the same piece of work that adds, removes, or replaces a service, or changes
  what a service is used for. Remove the entry of a service that is no longer used, and note the
  removal in `DEVLOG.md`.
- Never write secrets here: no API keys, tokens, passwords, or private URLs. Say where each secret
  is kept instead (`local.properties`, a CI secret, an uncommitted key file).
- The services planned during project discovery are in `APP_PLAN.md` ("Integrations"). This file
  lists only the services that are actually in the project.

---

## Overview

| Service | Purpose | Used by |
|---|---|---|
| GitHub Actions | CI: build, tests, and lint on every push and pull request | Development only |

---

## GitHub Actions

- **Purpose:** continuous integration. Builds the Android and web apps, runs the unit tests and
  lint on Linux, and builds the iOS app on macOS.
- **Used by:** development only; the app does not talk to it.
- **In the code:** `.github/workflows/ci.yml`.
- **Platforms:** Linux job for Android and web; macOS job for iOS.
- **Configuration and secrets:** none.
- **Account / console:** the repository's Actions tab on GitHub.
- **Cost:** free on public repositories. On private repositories it uses the account's monthly
  minute quota, and macOS minutes count several times over, so the iOS job runs there only when
  started by hand.

<!--
Copy this block for each new service and add a row to the overview table.

## <Service name>

- **Purpose:** what it does for the app, in one sentence.
- **Used by:** the features and modules that depend on it (`:presentation:features:profile`, `:data`).
- **In the code:** where it is wired in: Gradle plugin and dependency, Koin module, initialization
  code, configuration files (`google-services.json`, `Info.plist` keys).
- **Platforms:** Android, iOS, web, and what differs between them.
- **Configuration and secrets:** which keys it needs and where they are kept, never the values.
- **Environments:** separate debug and release (or dev and prod) projects, if any.
- **Account / console:** dashboard URL and which account owns it.
- **Cost:** plan and limits.
- **Notes:** anything a future developer must know: quotas, user data stored there, privacy or
  consent requirements.
-->
