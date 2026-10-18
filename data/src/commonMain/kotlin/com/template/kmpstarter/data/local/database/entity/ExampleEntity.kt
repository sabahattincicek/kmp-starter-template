// EXAMPLE — replace with the project's first real entity. Room needs at least one entity, so delete
// this file only after adding your own (and remove ExampleDao and its lines in AppDatabase and
// DatabaseModule).
package com.template.kmpstarter.data.local.database.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

/**
 * Example table showing how an entity is declared. Entities stay in this module and are mapped to
 * domain models in the repositories.
 */
@Entity(tableName = "example")
data class ExampleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
)
