package com.template.kmpstarter.core.common.dispatcher

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/** Android: the shared thread pool for blocking IO. */
internal actual val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
