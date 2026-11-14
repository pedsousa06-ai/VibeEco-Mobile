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
    primary = GreenBrand,
    onPrimary = TextWhite,
    primaryContainer = GreenContainer,
    onPrimaryContainer = GreenOnContainer,
    secondary = ForestMedium,
    onSecondary = TextWhite,
    secondaryContainer = GreenContainer,
    background = BackgroundApp,
    onBackground = TextMain,
    surface = SurfaceApp,
    onSurface = TextMain,
    surfaceVariant = SurfaceVariantApp,
    onSurfaceVariant = TextMuted,
    outline = BorderLight,
    error = RedDanger,
    onError = TextWhite,
    errorContainer = RedContainer,
    onErrorContainer = RedDangerDark
)

private val VibeDarkColorScheme = darkColorScheme(
    primary = GreenBrandLight,
    onPrimary = DarkBackground,
    primaryContainer = ForestMedium,
    onPrimaryContainer = GreenContainer,
    secondary = GreenBrand,
    onSecondary = DarkBackground,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
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
        typography = VibeTypography,
        shapes = VibeShapes,
        content = content
    )
}
