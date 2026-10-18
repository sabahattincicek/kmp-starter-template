package com.template.kmpstarter.core.common.config

/**
 * Values that only the platform entry points know and pass into the shared code when the app
 * starts (`initKoin` in `:app`). Available everywhere through Koin: `get<AppConfig>()`.
 *
 * @property isDebug true for debug builds: enables logging and HTTP body logging.
 */
data class AppConfig(
    val isDebug: Boolean,
)
