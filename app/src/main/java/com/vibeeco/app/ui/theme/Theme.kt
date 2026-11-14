package com.vibeeco.app.ui.theme

import android.app.Activity
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

// Mantido no arquivo para caso você queira usar em previews/testes.
// O app NÃO usa esse esquema por padrão.
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

/**
 * Tema oficial do VibeEco.
 *
 * IMPORTANTE: o app é SEMPRE claro. O [darkTheme] está aqui apenas para uso
 * explícito em previews/testes. O padrão é `false` e o `isSystemInDarkTheme()`
 * do sistema é IGNORADO de propósito, para manter a identidade visual
 * branca/verde do design em qualquer aparelho.
 */
@Composable
fun VibeTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) VibeDarkColorScheme else VibeLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            // Força ícones escuros na status bar e navigation bar (modo claro)
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = VibeTypography,
        shapes = VibeShapes,
        content = content
    )
}