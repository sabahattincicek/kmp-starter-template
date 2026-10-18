package com.template.kmpstarter.core.testing

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

/**
 * Base class for tests of code that uses [Dispatchers.Main], such as `viewModelScope` in
 * ViewModels. It replaces the main dispatcher with [testDispatcher] before each test and restores
 * it afterwards, on every platform.
 *
 * Usage: `class ProfileViewModelTest : MainDispatcherTest() { ... }`
 */
@OptIn(ExperimentalCoroutinesApi::class)
abstract class MainDispatcherTest(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) {

    /** Installs [testDispatcher] as the main dispatcher before each test. */
    @BeforeTest
    fun setMainDispatcher() {
        Dispatchers.setMain(testDispatcher)
    }

    /** Restores the real main dispatcher after each test. */
    @AfterTest
    fun resetMainDispatcher() {
        Dispatchers.resetMain()
    }
}
