package com.template.kmpstarter.core.common.di

import com.template.kmpstarter.core.common.dispatcher.DefaultDispatcherProvider
import com.template.kmpstarter.core.common.dispatcher.DispatcherProvider
import org.koin.dsl.module

/**
 * Koin module with the shared building blocks: the coroutine dispatchers used across the app.
 * Unit tests pass a `TestDispatcherProvider` instead.
 */
val commonModule = module {
    single<DispatcherProvider> { DefaultDispatcherProvider() }
}
