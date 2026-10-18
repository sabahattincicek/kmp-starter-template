// OPTIONAL (Room) — see DatabaseModule.kt in commonMain.
package com.template.kmpstarter.data.di

import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.template.kmpstarter.data.local.database.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

/** Android: the database lives in the app's private database folder and uses the bundled SQLite. */
internal actual val platformDatabaseModule: Module = module {
    single {
        Room.databaseBuilder<AppDatabase>(context = androidContext(), name = AppDatabase.NAME)
            .setDriver(BundledSQLiteDriver())
    }
}
