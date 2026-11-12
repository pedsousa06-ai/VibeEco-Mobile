package com.vibeeco.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object VibeColors {
    // Cores Principais & Marca
    val Green = Color(0xFF05803F)          // Verde principal
    val GreenDark = Color(0xFF064624)      // Verde profundo para contraste
    val GreenMid = Color(0xFF0B9E51)       // Verde intermediário vibrante
    val GreenBright = Color(0xFF1ED760)    // Verde neon "Eco"
    val GreenSoft = Color(0xFFE6F6EC)      // Superfície suave esverdeada
    val GreenMint = Color(0xFFD0F0DC)      // Destaque sutil / bordas ativas

    // Neutros & Superfícies
    val Title = Color(0xFF1B2420)          // Preto esverdeado mais orgânico e legível
    val Label = Color(0xFF333D38)          // Cinza escuro quente para textos secundários
    val Muted = Color(0xFF7D8A83)          // Cinza médio com melhor contraste WCAG
    val Border = Color(0xFFDCE5DF)         // Borda sutil orgânica
    val SurfaceElevated = Color(0xFFFFFFFF)
    val Background = Color(0xFFF4F9F6)     // Fundo off-white botânico

    // Semânticas & Gamificação
    val Success = Color(0xFF00B359)
    val Error = Color(0xFFE03131)
    val Warning = Color(0xFFF59F00)
    val Medium = Color(0xFFC6B200)
    val Gold = Color(0xFFFFB703)
    val GoldSoft = Color(0xFFFFF6DB)

    // Gradientes Signature
    val HeaderBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF0E8A4B),
            Color(0xFF066334),
            Color(0xFF044423)
        ),
        start = Offset(0f, 0f),
        end = Offset(800f, 1000f)
    )

    val ProgressBrush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF1ED760),
            Color(0xFF69F0AE)
        )
    )

    val CardHighlightBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFFFFF),
            Color(0xFFF9FCFA)
        )
    )

    val AvatarRingBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF1ED760),
            Color(0xFF05803F)
        )
    )
}

private val VibeScheme = lightColorScheme(
    primary = VibeColors.Green,
    onPrimary = Color.White,
    primaryContainer = VibeColors.GreenSoft,
    onPrimaryContainer = VibeColors.GreenDark,
    secondary = VibeColors.GreenMid,
    onSecondary = Color.White,
    tertiary = VibeColors.Gold,
    error = VibeColors.Error,
    surface = VibeColors.SurfaceElevated,
    onSurface = VibeColors.Title,
    surfaceVariant = VibeColors.GreenSoft,
    onSurfaceVariant = VibeColors.Label,
    background = VibeColors.Background,
    onBackground = VibeColors.Title,
    outline = VibeColors.Border,
)

private val VibeTypography = Typography(
    displaySmall = TextStyle(
        fontSize = 30.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.5).sp,
        color = VibeColors.Title
    ),
    headlineMedium = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.3).sp,
        color = VibeColors.Title
    ),
    titleMedium = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = VibeColors.Title
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = VibeColors.Muted
    ),
    labelLarge = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = VibeColors.Label
    ),
    labelSmall = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.3.sp,
        color = VibeColors.Muted
    )
)

@Composable
fun VibeEcoTheme(content: @Composable () -> Unit) =
    MaterialTheme(
        colorScheme = VibeScheme,
        typography = VibeTypography,
        content = content
    )