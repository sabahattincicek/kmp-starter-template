package com.template.kmpstarter.core.common.result

import app.cash.turbine.test
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame

/**
 * Unit tests for [asResource].
 */
class ResourceTest {

    /** A successful flow emits Loading first, then one Success per value. */
    @Test
    fun asResource_emitsLoadingThenSuccess() = runTest {
        flowOf(1, 2).asResource().test {
            assertEquals(Resource.Loading, awaitItem())
            assertEquals(Resource.Success(1), awaitItem())
            assertEquals(Resource.Success(2), awaitItem())
            awaitComplete()
        }
    }

    /** An upstream exception ends the flow with an Error that carries the cause. */
    @Test
    fun asResource_emitsErrorOnException() = runTest {
        val exception = IllegalStateException("boom")
        flow<Int> { throw exception }.asResource().test {
            assertEquals(Resource.Loading, awaitItem())
            val error = assertIs<Resource.Error>(awaitItem())
            assertEquals("boom", error.message)
            assertSame(exception, error.cause)
            awaitComplete()
        }
    }
}
