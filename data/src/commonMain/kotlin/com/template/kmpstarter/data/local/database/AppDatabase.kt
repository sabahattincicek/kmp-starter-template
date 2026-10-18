// OPTIONAL (Room) — delete the whole local/database/ folder if the project does not use a local
// database, together with di/DatabaseModule*.kt and the Room lines in data/build.gradle.kts.
package com.template.kmpstarter.data.local.database

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import com.template.kmpstarter.data.local.database.dao.ExampleDao
import com.template.kmpstarter.data.local.database.entity.ExampleEntity

/**
 * The app's Room database, shared by all platforms. Register every entity in `entities` and expose
 * one DAO per entity. Bump [version] and add a migration whenever the schema changes.
 */
@Database(
    entities = [ExampleEntity::class],
    version = 1,
    exportSchema = true,
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {

    /** DAO of the example table; remove it together with [ExampleEntity]. */
    abstract fun exampleDao(): ExampleDao

    companion object {
        const val NAME = "app.db"
    }
}

/** Creates the generated [AppDatabase] implementation on every platform; Room generates the actuals. */
@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
