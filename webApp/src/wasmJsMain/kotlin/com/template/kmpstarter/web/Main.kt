package com.template.kmpstarter.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import co.touchlab.kermit.Logger
import com.template.kmpstarter.app.App
import com.template.kmpstarter.app.di.initKoin
import com.template.kmpstarter.core.common.config.AppConfig
import kotlinx.browser.window

/**
 * Web entry point. Starts the shared app and renders App() into the `composeTarget` element of
 * index.html. Logging is enabled when the page is served from localhost (the development server).
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val isDebug = window.location.hostname == "localhost" || window.location.hostname == "127.0.0.1"
    initKoin(AppConfig(isDebug = isDebug))
    Logger.withTag("Main").d { "Starting web app: host=${window.location.hostname}" }

    ComposeViewport(viewportContainerId = "composeTarget") {
        App()
    }
}
