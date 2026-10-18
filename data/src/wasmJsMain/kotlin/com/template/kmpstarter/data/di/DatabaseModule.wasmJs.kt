// OPTIONAL (Room) — see DatabaseModule.kt in commonMain.
package com.template.kmpstarter.data.di

import androidx.room3.Room
import androidx.sqlite.driver.web.WebWorkerSQLiteDriver
import com.template.kmpstarter.data.local.database.AppDatabase
import org.koin.core.module.Module
import org.koin.dsl.module
import org.w3c.dom.Worker

/**
 * Web: SQLite runs in a web worker (`data/sqlite-web-worker/`) and stores the database in the
 * browser's Origin Private File System.
 */
internal actual val platformDatabaseModule: Module = module {
    single {
        Room.databaseBuilder<AppDatabase>(name = AppDatabase.NAME)
            .setDriver(WebWorkerSQLiteDriver(createSqliteWorker()))
    }
}

/** Starts the SQLite web worker; webpack bundles `worker.js` from the `sqlite-web-worker` npm package. */
@OptIn(ExperimentalWasmJsInterop::class)
private fun createSqliteWorker(): Worker =
    js("""new Worker(new URL("sqlite-web-worker/worker.js", import.meta.url))""")
