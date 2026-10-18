// OPTIONAL (Settings) — see SettingsModule.kt in commonMain.
package com.template.kmpstarter.data.di

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

private const val PREFERENCES_FILE = "app_preferences"

/** Android: settings are stored in the private SharedPreferences file `app_preferences`. */
internal actual val platformSettingsModule: Module = module {
    single<Settings> {
        SharedPreferencesSettings(androidContext().getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE))
    }
}
