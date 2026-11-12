package com.vibeeco.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val VibeLightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = TextOnPrimary,
    primaryContainer = GreenContainer,
    onPrimaryContainer = GreenOnContainer,
    secondary = BlueAccent,
    onSecondary = TextOnPrimary,
    secondaryContainer = BlueContainer,
    background = BackgroundNeutral,
    onBackground = TextPrimary,
    surface = SurfaceNeutral,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    error = RedDanger,
    onError = TextOnPrimary
)

private val VibeDarkColorScheme = darkColorScheme(
    primary = GreenPrimary,
    onPrimary = DarkBackground,
    primaryContainer = GreenOnContainer,
    onPrimaryContainer = GreenContainer,
    secondary = BlueAccent,
    onSecondary = DarkBackground,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    error = RedDanger,
    onError = DarkBackground
)

@Composable
fun VibeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) VibeDarkColorScheme else VibeLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = VibeTypography, // Certifique-se que essa variável existe no Type.kt
        content = content
    )
}