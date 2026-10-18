package com.template.kmpstarter.core.testing

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Checks that [MainDispatcherTest] installs the test dispatcher as [Dispatchers.Main] on every
 * platform, so ViewModel tests can rely on it.
 */
class MainDispatcherTestTest : MainDispatcherTest() {

    /** Work launched on Dispatchers.Main runs immediately on the unconfined test dispatcher. */
    @Test
    fun mainDispatcher_isReplacedByTestDispatcher() {
        var ran = false
        CoroutineScope(Dispatchers.Main).launch { ran = true }
        assertTrue(ran)
    }
}
