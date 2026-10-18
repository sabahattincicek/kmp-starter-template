// PLACEHOLDER — delete this file once the first feature module exists and AppNavDisplay starts
// with that feature's key.
package com.template.kmpstarter.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import com.template.kmpstarter.app.resources.Res
import com.template.kmpstarter.app.resources.app_name
import com.template.kmpstarter.presentation.ui.theme.AppTheme
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

/**
 * Navigation key of the placeholder start screen, used until the project has its first feature.
 */
@Serializable
data object StartKey : NavKey

/**
 * Placeholder start screen showing the app name, so a fresh project launches on every platform
 * before any feature module is added.
 */
@Composable
fun StartPlaceholder(modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(Res.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
            )
        }
    }
}

/** Preview of [StartPlaceholder]. */
@Preview(showBackground = true)
@Composable
private fun StartPlaceholderPreview() {
    AppTheme {
        StartPlaceholder()
    }
}
