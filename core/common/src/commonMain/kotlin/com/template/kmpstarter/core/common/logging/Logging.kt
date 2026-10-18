package com.template.kmpstarter.core.common.logging

import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.platformLogWriter

/*
 * Kermit setup for the whole app. Kermit writes to Logcat on Android, to the unified log (OSLog) on
 * iOS, and to the browser console on the web.
 */

/**
 * Configures the global Kermit logger once at app start: every level in debug builds, nothing in
 * release builds.
 */
fun configureLogging(isDebug: Boolean) {
    if (isDebug) {
        Logger.setLogWriters(platformLogWriter())
        Logger.setMinSeverity(Severity.Verbose)
    } else {
        Logger.setLogWriters(emptyList())
    }
    Logger.withTag("Logging").i { "Logging configured: isDebug=$isDebug" }
}
