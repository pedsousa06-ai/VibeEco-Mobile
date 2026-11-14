package com.vibeeco.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Shapes modernos do VibeEco — alinhados com o design do sistema Web.
 */
val VibeShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small      = RoundedCornerShape(10.dp),
    medium     = RoundedCornerShape(14.dp),
    large      = RoundedCornerShape(18.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

/** Pílula / cápsula (chips, StatPills, badges) */
val PillShape = RoundedCornerShape(50)

/** Botão moderno suave */
val VibeButtonShape = RoundedCornerShape(10.dp)

/** Card padrão (conteúdo, desafios, métricas) */
val VibeCardShape = RoundedCornerShape(16.dp)

/** Card de autenticação / modal flutuante */
val VibeAuthCardShape = RoundedCornerShape(28.dp)

/** Ícone em caixa arredondada */
val VibeIconBoxShape = RoundedCornerShape(12.dp)

/** Avatar circular */
val VibeAvatarShape = RoundedCornerShape(50)

/** Bottom sheet / modais */
val VibeSheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
