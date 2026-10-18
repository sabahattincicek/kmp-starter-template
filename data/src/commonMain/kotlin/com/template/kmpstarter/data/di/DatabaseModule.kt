// OPTIONAL (Room) — delete this file and its platform files (DatabaseModule.<platform>.kt) if the
// project does not use a local database, together with local/database/ and the Room lines in
// data/build.gradle.kts and DataModules.kt (see "Removing an optional part" in docsagent/SETUP.md).
package com.template.kmpstarter.data.di

import androidx.room3.RoomDatabase
import co.touchlab.kermit.Logger
import com.template.kmpstarter.data.local.database.AppDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

private val log = Logger.withTag("DatabaseModule")

/**
 * Koin module providing the [AppDatabase] and its DAOs. The platform part
 * ([platformDatabaseModule]) supplies the builder with the database location and SQLite driver.
 * Add one line per DAO.
 */
val databaseModule = module {
    includes(platformDatabaseModule)
    single {
        log.d { "Creating AppDatabase: name=${AppDatabase.NAME}" }
        get<RoomDatabase.Builder<AppDatabase>>().build()
    }
    // Example DAO; remove it together with ExampleEntity.
    single { get<AppDatabase>().exampleDao() }
}

/** Provides the platform's `RoomDatabase.Builder<AppDatabase>`: file location and SQLite driver. */
internal expect val platformDatabaseModule: Module
