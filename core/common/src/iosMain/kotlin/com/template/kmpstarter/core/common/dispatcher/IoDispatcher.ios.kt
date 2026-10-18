package com.template.kmpstarter.core.common.dispatcher

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

/** iOS: the shared thread pool for blocking IO. */
internal actual val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
