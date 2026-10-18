package com.template.kmpstarter.core.testing

import com.template.kmpstarter.core.common.dispatcher.DispatcherProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher

/**
 * [DispatcherProvider] for unit tests: every dispatcher is the same [testDispatcher], so coroutines
 * started by the class under test run on the test scheduler.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TestDispatcherProvider(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : DispatcherProvider {
    override val main = testDispatcher
    override val io = testDispatcher
    override val default = testDispatcher
    override val unconfined = testDispatcher
}
