package com.vibeeco.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object VibeColors {
    val Green = Color(0xFF05803F)        // botão principal (Figma)
    val GreenDark = Color(0xFF0A5C2E)
    val GreenBright = Color(0xFF1ED760)  // "Eco" do logo
    val GreenSoft = Color(0xFFE4F4EA)
    val Success = Color(0xFF00A651)
    val Title = Color(0xFF282A3C)
    val Label = Color(0xFF2E2E3A)
    val Muted = Color(0xFF9B9BA3)
    val Border = Color(0xFFBDBDC4)
    val Error = Color(0xFFD93025)
    val Warning = Color(0xFFF2A100)
    val Medium = Color(0xFFC6B200)
    val Gold = Color(0xFFF5B301)
    val Background = Color(0xFFF3F8F4)

    val HeaderBrush = Brush.verticalGradient(listOf(Color(0xFF0C7A42), Color(0xFF05562B)))
}

private val VibeScheme = lightColorScheme(
    primary = VibeColors.Green,
    onPrimary = Color.White,
    error = VibeColors.Error,
    surface = Color.White,
    background = VibeColors.Background,
)

// Dica: coloque a fonte do Figma em res/font e defina o FontFamily aqui.
private val VibeTypography = Typography(
    headlineMedium = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = VibeColors.Title),
    bodyMedium = TextStyle(fontSize = 14.sp, color = VibeColors.Muted),
    labelLarge = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = VibeColors.Label),
)

@Composable
fun VibeEcoTheme(content: @Composable () -> Unit) =
    MaterialTheme(colorScheme = VibeScheme, typography = VibeTypography, content = content)
