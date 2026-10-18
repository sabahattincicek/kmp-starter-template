package com.template.kmpstarter.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.template.kmpstarter.presentation.ui.theme.AppTheme

/**
 * Full-screen loading state: a progress indicator centered in the available space.
 */
@Composable
fun LoadingView(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

/** Preview of [LoadingView]. */
@Preview(showBackground = true)
@Composable
private fun LoadingViewPreview() {
    AppTheme {
        LoadingView()
    }
}
