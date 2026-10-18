package com.template.kmpstarter

import android.app.Application
import co.touchlab.kermit.Logger
import com.template.kmpstarter.app.di.initKoin
import com.template.kmpstarter.core.common.config.AppConfig
import org.koin.android.ext.koin.androidContext

/**
 * Android application entry point. Starts the shared app (logging and Koin) with the Android
 * context, which the data layer needs for the database and the settings.
 */
class App : Application() {

    /** Starts Koin before any activity is created; logging is enabled in debug builds only. */
    override fun onCreate() {
        super.onCreate()
        initKoin(AppConfig(isDebug = BuildConfig.DEBUG)) {
            androidContext(this@App)
        }
        Logger.withTag("App").d { "Application created" }
    }
}
