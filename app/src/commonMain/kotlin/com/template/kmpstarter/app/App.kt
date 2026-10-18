package com.template.kmpstarter.app

import androidx.compose.runtime.Composable
import com.template.kmpstarter.app.navigation.AppNavDisplay
import com.template.kmpstarter.presentation.ui.theme.AppTheme

/**
 * Root composable of the app, shown by every platform entry point (MainActivity on Android,
 * MainViewController on iOS, main() on the web): the app theme around the navigation graph.
 * Koin must already be started with `initKoin`.
 */
@Composable
fun App() {
    AppTheme {
        AppNavDisplay()
    }
}
