package com.template.kmpstarter.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/*
 * App theme: the light and dark color schemes and the AppTheme composable that applies them.
 */
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
)

/**
 * Applies the app theme to [content]. Uses wallpaper-based dynamic colors on Android 12+ when
 * [dynamicColor] is true, otherwise (and on iOS and the web) the app's own light or dark color
 * scheme.
 */
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = (if (dynamicColor) dynamicColorScheme(darkTheme) else null)
        ?: if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}

/** The platform's dynamic color scheme, or null where there is none (Android below 12, iOS, web). */
@Composable
internal expect fun dynamicColorScheme(darkTheme: Boolean): ColorScheme?
