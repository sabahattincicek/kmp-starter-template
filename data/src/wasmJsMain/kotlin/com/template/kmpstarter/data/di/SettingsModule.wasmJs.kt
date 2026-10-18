// OPTIONAL (Settings) — see SettingsModule.kt in commonMain.
package com.template.kmpstarter.data.di

import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings
import org.koin.core.module.Module
import org.koin.dsl.module

/** Web: settings are stored in the browser's localStorage. */
internal actual val platformSettingsModule: Module = module {
    single<Settings> { StorageSettings() }
}
