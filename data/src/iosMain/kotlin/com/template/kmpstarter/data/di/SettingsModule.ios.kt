// OPTIONAL (Settings) — see SettingsModule.kt in commonMain.
package com.template.kmpstarter.data.di

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

/** iOS: settings are stored in the app's standard NSUserDefaults. */
internal actual val platformSettingsModule: Module = module {
    single<Settings> { NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults) }
}
