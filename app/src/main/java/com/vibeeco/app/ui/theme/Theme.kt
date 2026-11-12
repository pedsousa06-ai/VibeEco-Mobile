package com.vibeeco.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val VibeLightColorScheme = lightColorScheme(
    // Primary (verde CTA)
    primary            = GreenPrimary,
    onPrimary          = TextOnPrimary,
    primaryContainer   = GreenContainer,
    onPrimaryContainer = GreenOnContainer,

    // Secondary (azul info)
    secondary          = BlueAccent,
    onSecondary        = TextOnPrimary,
    secondaryContainer = BlueContainer,
    onSecondaryContainer = BlueAccentDark,

    // Tertiary (roxo ranking)
    tertiary           = PurpleAccent,
    onTertiary         = TextOnPrimary,
    tertiaryContainer  = PurpleContainer,
    onTertiaryContainer = PurpleAccentDark,

    // Fundo e superfícies (modo CLARO — branco, deixa o verde brilhar)
    background         = BackgroundNeutral,
    onBackground       = TextPrimary,
    surface            = SurfaceNeutral,
    onSurface          = TextPrimary,
    surfaceVariant     = SurfaceVariant,
    onSurfaceVariant   = TextSecondary,

    // Bordas
    outline            = BorderNeutral,
    outlineVariant     = BorderStrong,

    // Erro
    error              = RedDanger,
    onError            = TextOnPrimary,
    errorContainer     = RedContainer,
    onErrorContainer   = RedDangerDark
)

// Já preparado — só será usado na Etapa 7 (dark mode)
private val VibeDarkColorScheme = darkColorScheme(
    primary            = GreenPrimary,
    onPrimary          = Color(0xFF0B1A00),
    primaryContainer   = Color(0xFF2E6B00),
    onPrimaryContainer = GreenContainer,

    secondary          = BlueAccent,
    onSecondary        = Color(0xFF001E2E),
    secondaryContainer = Color(0xFF0A4F6E),
    onSecondaryContainer = BlueContainer,

    tertiary           = PurpleAccent,
    onTertiary         = Color(0xFF24003D),
    tertiaryContainer  = Color(0xFF5B2B87),
    onTertiaryContainer = PurpleContainer,

    background         = DarkBackground,
    onBackground       = DarkTextPrimary,
    surface            = DarkSurface,
    onSurface          = DarkTextPrimary,
    surfaceVariant     = DarkSurfaceVariant,
    onSurfaceVariant   = DarkTextSecondary,

    outline            = DarkBorder,
    outlineVariant     = DarkBorder,

    error              = RedDanger,
    onError            = Color(0xFF2A0000),
    errorContainer     = Color(0xFF5A1A1A),
    onErrorContainer   = RedContainer
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
        typography  = VibeTypography,
        shapes      = VibeShapes,
        content     = content
    )
}