package com.vibeeco.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// =========================================================================
// PALETA OFICIAL VIBEECO (Inspirada no Front-End Web / Desktop)
// =========================================================================

// Verdes Floresta / Identidade Primária
val ForestDarkest      = Color(0xFF0B2A16) // Fundo Auth / Telas Escuras
val ForestDark         = Color(0xFF06361B)
val ForestMedium       = Color(0xFF0A4A26)
val ForestLight        = Color(0xFF0F7A3B)

// Verdes Vivos / Acentos de Ação
val GreenBrand         = Color(0xFF16A34A) // Verde principal do web (Tailwind Green-600)
val GreenBrandLight    = Color(0xFF22C55E) // Verde brilhante (Tailwind Green-500)
val GreenAction        = Color(0xFF20C765) // Botões e destaques
val GreenActionHover   = Color(0xFF19AD55)
val GreenContainer     = Color(0xFFDCF5E5) // Fundo de tags, chips e badges leves
val GreenOnContainer   = Color(0xFF0F6B34)

// Cores Neutras e de Superfície
val BackgroundApp      = Color(0xFFF2F7F3) // Fundo geral suave do app (off-white menta)
val SurfaceApp         = Color(0xFFFFFFFF) // Branco puro para cards e inputs
val SurfaceVariantApp  = Color(0xFFEBF3ED)
val BorderLight        = Color(0xFFE0EAE2) // Borda sutil de cards
val BorderInput        = Color(0xFFDCE6DC) // Borda de inputs do login
val BorderInputFocused = Color(0xFF22C55E)

// Textos
val TextMain           = Color(0xFF12261A) // Preto-esverdeado de alto contraste (Títulos e labels)
val TextMuted          = Color(0xFF64756A) // Texto secundário e legendas
val TextSubtle         = Color(0xFF829084) // Placeholder e texto atenuado
val TextWhite          = Color(0xFFFFFFFF)

// Acentos de Gamificação / Status (idênticos à web)
val GoldXP             = Color(0xFFF5B301)
val GoldXPContainer    = Color(0xFFFFF8DF)
val GoldXPText         = Color(0xFF8A5A00)
val GoldXPBorder       = Color(0xFFF3DD9A)

val CoinMCContainer    = Color(0xFFEEF9F1)
val CoinMCText         = Color(0xFF0F5B2D)
val CoinMCBorder       = Color(0xFFC9E8D2)

val FlameOrange        = Color(0xFFEA580C)
val FlameOrangeContainer = Color(0xFFFFEDD5)

val PurpleChallenge    = Color(0xFF7C3AED)
val PurpleChallengeContainer = Color(0xFFEDE9FE)

val RedDanger          = Color(0xFFEF4444)
val RedDangerDark      = Color(0xFFC62828)
val RedContainer       = Color(0xFFFDECEC)

// Aliases para compatibilidade com o código existente
val GreenPrimary       = GreenBrand
val GreenPrimaryDark   = ForestMedium
val GreenPrimaryLight  = GreenBrandLight
val BackgroundNeutral  = BackgroundApp
val SurfaceNeutral     = SurfaceApp
val SurfaceVariant     = SurfaceVariantApp
val BorderNeutral      = BorderLight
val BorderStrong       = BorderInput
val TextPrimary        = TextMain
val TextSecondary      = TextMuted
val TextTertiary       = TextSubtle
val TextOnPrimary      = TextWhite

val BlueAccent         = Color(0xFF0284C7)
val BlueAccentDark     = Color(0xFF0369A1)
val BlueContainer      = Color(0xFFE0F2FE)

val YellowAccent       = GoldXP
val YellowAccentDark   = GoldXPText
val YellowContainer    = GoldXPContainer

val PurpleAccent       = PurpleChallenge
val PurpleAccentDark   = Color(0xFF6D28D9)

val OrangeAccent       = FlameOrange
val OrangeAccentDark   = Color(0xFFC2410C)
val OrangeContainer    = FlameOrangeContainer

val DarkBackground     = Color(0xFF081C10)
val DarkSurface        = Color(0xFF0D2817)
val DarkSurfaceVariant = Color(0xFF143822)
val DarkBorder         = Color(0xFF1F4E31)
val DarkTextPrimary    = Color(0xFFF1F8F3)
val DarkTextSecondary  = Color(0xFFA5C2AE)

object VibeColors {
    // Gradientes Principais
    val HeaderBrush = Brush.verticalGradient(
        listOf(Color(0xFF0B6A35), Color(0xFF07502A), Color(0xFF06411F))
    )
    val ButtonGradient = Brush.horizontalGradient(
        listOf(Color(0xFF1A8F4E), Color(0xFF22C55E))
    )
    val SplashGradient = Brush.linearGradient(
        listOf(Color(0xFF1D4D32), Color(0xFF39865B))
    )
    val AvatarGradient = Brush.linearGradient(
        listOf(GreenBrandLight, ForestLight)
    )

    val GreenBright     = GreenBrandLight
    val Green           = GreenBrand
    val GreenDark       = ForestMedium
    val GreenLight      = GreenBrandLight
    val GreenSoft       = Color(0xFFDCF5E5)
    val GreenContainer  = Color(0xFFDCF5E5)

    val Background      = BackgroundApp
    val Surface         = SurfaceApp
    val SurfaceVariant  = SurfaceVariantApp
    val Card            = SurfaceApp

    val Text            = TextMain
    val Title           = TextMain
    val TitleSoft       = TextMain
    val TextSoft        = TextMuted
    val Muted           = TextMuted
    val Subtitle        = TextSubtle
    val Body            = TextMain
    val BodySoft        = TextMuted
    val Caption         = TextSubtle
    val Label           = Color(0xFF344638)
    val Placeholder     = TextSubtle
    val Hint            = TextSubtle
    val OnGreen         = TextWhite
    val OnPrimary       = TextWhite

    val Border          = BorderLight
    val BorderInput     = Color(0xFFDCE6DC)
    val BorderStrong    = Color(0xFFDCE6DC)
    val Divider         = BorderLight

    val Danger          = RedDanger
    val Error           = RedDangerDark
    val ErrorContainer  = RedContainer
    val Warning         = GoldXP
    val Medium          = FlameOrange
    val Info            = BlueAccent
    val Success         = GreenBrand
    val SuccessContainer = Color(0xFFE3F4E8)
    val SuccessText     = Color(0xFF1B7A34)

    val Yellow          = GoldXP
    val YellowSoft      = GoldXPContainer
    val Blue            = BlueAccent
    val BlueSoft        = BlueContainer
    val Purple          = PurpleChallenge
    val PurpleSoft      = PurpleChallengeContainer
    val Orange          = FlameOrange
    val OrangeSoft      = FlameOrangeContainer
    val Red             = RedDanger
    val RedSoft         = RedContainer
}
