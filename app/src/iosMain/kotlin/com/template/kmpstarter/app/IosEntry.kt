package com.template.kmpstarter.app

import androidx.compose.ui.window.ComposeUIViewController
import com.template.kmpstarter.app.di.initKoin
import com.template.kmpstarter.core.common.config.AppConfig
import platform.UIKit.UIViewController

/*
 * iOS entry points, called from Swift in iosApp/ (the framework is imported as `ComposeApp`).
 */

/**
 * Starts the shared app once, from the SwiftUI `App` initializer:
 * `IosEntryKt.startApp(isDebug: ...)`.
 */
fun startApp(isDebug: Boolean) {
    initKoin(AppConfig(isDebug = isDebug))
}

/** The view controller that hosts the shared Compose UI: `IosEntryKt.MainViewController()`. */
@Suppress("FunctionName")
fun MainViewController(): UIViewController = ComposeUIViewController { App() }
