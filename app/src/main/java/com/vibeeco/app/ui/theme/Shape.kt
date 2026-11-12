package com.vibeeco.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Shapes do VibeEco — cantos arredondados estilo Duolingo.
 *
 * Mapeamento Material 3:
 *  - extraSmall → chips pequenos, badges
 *  - small      → inputs, botões secundários
 *  - medium     → PADRÃO: cards e botões principais
 *  - large      → cards de destaque, bottom sheets
 *  - extraLarge → dialogs, modais
 */
val VibeShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small      = RoundedCornerShape(12.dp),
    medium     = RoundedCornerShape(16.dp),
    large      = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

// ============================================================
// Shapes específicas (fora da escala M3, para usos pontuais)
// ============================================================

/** Pílula / cápsula (50%) — chips, StatPills, progress bars */
val PillShape = RoundedCornerShape(50)

/** Botão 3D do Duolingo — raio 16dp */
val VibeButtonShape = RoundedCornerShape(16.dp)

/** Card de missão / desafio — raio 16dp */
val VibeCardShape = RoundedCornerShape(16.dp)

/** Ícone dentro de quadrado arredondado (ex: 52dp) */
val VibeIconBoxShape = RoundedCornerShape(16.dp)

/** Avatar circular */
val VibeAvatarShape = RoundedCornerShape(50)

/** Bottom sheet / dialog grande */
val VibeSheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)