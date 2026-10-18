// EXAMPLE — replace together with ExampleEntity.
package com.template.kmpstarter.data.local.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import com.template.kmpstarter.data.local.database.entity.ExampleEntity
import kotlinx.coroutines.flow.Flow

/**
 * Example DAO showing an observable query and a write operation. On every platform, DAO functions
 * are either `suspend` or return a `Flow`.
 */
@Dao
interface ExampleDao {

    /** Emits all rows, and again whenever the table changes. */
    @Query("SELECT * FROM example")
    fun observeAll(): Flow<List<ExampleEntity>>

    /** Inserts the rows, or updates the ones whose id already exists. */
    @Upsert
    suspend fun upsert(entities: List<ExampleEntity>)
}
