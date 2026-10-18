package com.template.kmpstarter.core.common.result

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * The outcome of a data operation (network request, database query, ...), passed from the data and
 * domain layers up to the ViewModels.
 *
 * - [Loading] while the operation runs.
 * - [Success] with the result when it completes.
 * - [Error] with an optional message and the cause when it fails.
 */
sealed interface Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>
    data class Error(val message: String? = null, val cause: Throwable? = null) : Resource<Nothing>
    data object Loading : Resource<Nothing>
}

private val log = Logger.withTag("Resource")

/**
 * Wraps every value of this flow in [Resource.Success], emits [Resource.Loading] first, and turns
 * an upstream exception into a final [Resource.Error] instead of crashing the collector.
 */
fun <T> Flow<T>.asResource(): Flow<Resource<T>> =
    map<T, Resource<T>> { Resource.Success(it) }
        .onStart { emit(Resource.Loading) }
        .catch { exception ->
            log.e(exception) { "Flow failed, emitting Resource.Error" }
            emit(Resource.Error(exception.message, exception))
        }
