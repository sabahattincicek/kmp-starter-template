package com.template.kmpstarter.core.common.dispatcher

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Injectable coroutine dispatchers. Classes receive this instead of using [Dispatchers] directly,
 * so unit tests can supply test dispatchers (see `TestDispatcherProvider` in `:core:testing`).
 */
interface DispatcherProvider {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
}

/**
 * Production [DispatcherProvider] backed by the standard [Dispatchers]. [io] comes from
 * [ioDispatcher], because the web has no IO dispatcher.
 */
class DefaultDispatcherProvider : DispatcherProvider {
    override val main: CoroutineDispatcher get() = Dispatchers.Main
    override val io: CoroutineDispatcher get() = ioDispatcher
    override val default: CoroutineDispatcher get() = Dispatchers.Default
    override val unconfined: CoroutineDispatcher get() = Dispatchers.Unconfined
}

/** Dispatcher for blocking IO work: `Dispatchers.IO` on Android and iOS, `Dispatchers.Default` on the web. */
internal expect val ioDispatcher: CoroutineDispatcher
