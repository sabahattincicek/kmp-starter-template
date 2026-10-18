// OPTIONAL (Settings) — delete this file and its platform files (SettingsModule.<platform>.kt) if
// the project does not store key-value settings, together with the Settings lines in
// data/build.gradle.kts and DataModules.kt (see "Removing an optional part" in docsagent/SETUP.md).
package com.template.kmpstarter.data.di

import com.russhwolf.settings.Settings
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Koin module providing the app's key-value [Settings]: SharedPreferences on Android,
 * NSUserDefaults on iOS, and localStorage on the web. There must be only one instance, which is why
 * it is a singleton. Data sources in `local/settings/` receive it through their constructor.
 */
val settingsModule = module {
    includes(platformSettingsModule)
}

/** Provides the platform's [Settings] implementation. */
internal expect val platformSettingsModule: Module
