// OPTIONAL (Room) — see DatabaseModule.kt in commonMain.
package com.template.kmpstarter.data.di

import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.template.kmpstarter.data.local.database.AppDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

/** iOS: the database lives in the app's Documents folder and uses the bundled SQLite. */
internal actual val platformDatabaseModule: Module = module {
    single {
        Room.databaseBuilder<AppDatabase>(name = "${documentsPath()}/${AppDatabase.NAME}")
            .setDriver(BundledSQLiteDriver())
    }
}

/** Returns the path of the app's Documents folder, creating it if needed. */
@OptIn(ExperimentalForeignApi::class)
private fun documentsPath(): String {
    val url = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null,
    )
    return requireNotNull(url?.path) { "Documents folder is not available" }
}
