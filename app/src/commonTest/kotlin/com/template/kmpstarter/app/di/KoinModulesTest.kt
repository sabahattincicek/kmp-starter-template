package com.template.kmpstarter.app.di

import com.template.kmpstarter.core.common.config.AppConfig
import com.template.kmpstarter.core.common.dispatcher.DispatcherProvider
import org.koin.core.context.stopKoin
import org.koin.dsl.koinApplication
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Checks that the app's Koin modules load together and resolve the shared definitions.
 */
class KoinModulesTest {

    /** Stops any Koin instance a test started, so tests stay independent. */
    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    /** The modules load without conflicts and provide the config and the dispatchers. */
    @Test
    fun appModules_resolveSharedDefinitions() {
        val config = AppConfig(isDebug = true)
        val koin = koinApplication { modules(appModules(config)) }.koin

        assertEquals(config, koin.get<AppConfig>())
        assertNotNull(koin.get<DispatcherProvider>())
    }
}
