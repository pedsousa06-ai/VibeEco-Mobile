package com.vibeeco.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val GreenPrimary       = Color(0xFF58CC02)
val GreenPrimaryDark   = Color(0xFF46A302)
val GreenPrimaryLight  = Color(0xFF89E219)
val GreenContainer     = Color(0xFFE5F8D4)
val GreenOnContainer   = Color(0xFF2E6B00)

val BlueAccent         = Color(0xFF1CB0F6)
val BlueAccentDark     = Color(0xFF1899D6)
val BlueContainer      = Color(0xFFDDF4FF)

val YellowAccent       = Color(0xFFFFC800)
val YellowAccentDark   = Color(0xFFE6A800)
val YellowContainer    = Color(0xFFFFF4D1)

val PurpleAccent       = Color(0xFFCE82FF)
val PurpleAccentDark   = Color(0xFFA85CD9)
val PurpleContainer    = Color(0xFFF3E1FF)

val OrangeAccent       = Color(0xFFFF9600)
val OrangeAccentDark   = Color(0xFFE07A00)
val OrangeContainer    = Color(0xFFFFE9CC)

val RedDanger          = Color(0xFFFF4B4B)
val RedDangerDark      = Color(0xFFD63A3A)
val RedContainer       = Color(0xFFFFE0E0)

val BackgroundNeutral  = Color(0xFFFFFFFF)
val SurfaceNeutral     = Color(0xFFFFFFFF)
val SurfaceVariant     = Color(0xFFF7F7F7)
val BorderNeutral      = Color(0xFFE5E5E5)
val BorderStrong       = Color(0xFFD4D4D4)

val TextPrimary        = Color(0xFF3C3C3C)
val TextSecondary      = Color(0xFF777777)
val TextTertiary       = Color(0xFFAFAFAF)
val TextOnPrimary      = Color(0xFFFFFFFF)

val DarkBackground     = Color(0xFF131F24)
val DarkSurface        = Color(0xFF1C2A31)
val DarkSurfaceVariant = Color(0xFF243B44)
val DarkBorder         = Color(0xFF37464F)
val DarkTextPrimary    = Color(0xFFF1F7FB)
val DarkTextSecondary  = Color(0xFFAFC1C9)

object VibeColors {
    val HeaderBrush = Brush.verticalGradient(listOf(GreenPrimaryLight, GreenPrimary))
    val GreenBright = GreenPrimaryLight

    val Background      = BackgroundNeutral
    val Surface         = SurfaceNeutral
    val SurfaceVariant  = com.vibeeco.app.ui.theme.SurfaceVariant
    val Card            = SurfaceNeutral

    val Green           = GreenPrimary
    val GreenDark       = GreenPrimaryDark
    val GreenLight      = GreenPrimaryLight
    val GreenSoft       = com.vibeeco.app.ui.theme.GreenContainer
    val GreenContainer  = com.vibeeco.app.ui.theme.GreenContainer

    val Text            = TextPrimary
    val Title           = TextPrimary
    val TitleSoft       = TextPrimary
    val TextSoft        = TextSecondary
    val Muted           = TextSecondary
    val Subtitle        = TextSecondary
    val Body            = TextPrimary
    val BodySoft        = TextSecondary
    val Caption         = TextSecondary
    val Label           = TextSecondary
    val Placeholder     = TextTertiary
    val Hint            = TextSecondary
    val OnGreen         = TextOnPrimary
    val OnPrimary       = TextOnPrimary

    val Border          = BorderNeutral
    val BorderStrong    = com.vibeeco.app.ui.theme.BorderStrong
    val Divider         = BorderNeutral

    val Danger          = RedDanger
    val Error           = RedDanger
    val Warning         = YellowAccent
    val Medium          = OrangeAccent
    val Info            = BlueAccent
    val Success         = GreenPrimary

    val Yellow          = YellowAccent
    val YellowSoft      = YellowContainer
    val Blue            = BlueAccent
    val BlueSoft        = BlueContainer
    val Purple          = PurpleAccent
    val PurpleSoft      = PurpleContainer
    val Orange          = OrangeAccent
    val OrangeSoft      = OrangeContainer
    val Red             = RedDanger
    val RedSoft         = RedContainer
}