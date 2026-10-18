package com.template.kmpstarter.core.common.dispatcher

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/** Web: the browser runs on a single thread and has no IO dispatcher, so Default is used. */
internal actual val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
