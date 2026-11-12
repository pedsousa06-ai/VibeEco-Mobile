package com.vibeeco.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibeeco.app.data.Category
import com.vibeeco.app.ui.theme.VibeColors

/** Cabeçalho orgânico com iluminação decorativa sutil e cantos inferiores arredondados. */
@Composable
fun GradientHeader(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val headerShape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = headerShape,
                ambientColor = VibeColors.GreenDark.copy(alpha = 0.25f),
                spotColor = VibeColors.GreenDark.copy(alpha = 0.25f)
            )
            .clip(headerShape)
            .background(VibeColors.HeaderBrush)
    ) {
        // Detalhe gráfico orgânico (círculos translúcidos de profundidade)
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                radius = size.width * 0.45f,
                center = Offset(size.width * 0.92f, size.height * 0.1f)
            )
            drawCircle(
                color = VibeColors.GreenBright.copy(alpha = 0.06f),
                radius = size.width * 0.30f,
                center = Offset(size.width * 0.1f, size.height * 0.95f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 26.dp),
            content = content,
        )
    }
}

/** Card elevado com borda sutil e física de toque (spring bounce) quando clicável. */
@Composable
fun VibeCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    containerColor: Color = Color.White,
    borderColor: Color = VibeColors.Border.copy(alpha = 0.55f),
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(22.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.975f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "cardScale"
    )

    val cardModifier = modifier
        .scale(scale)
        .shadow(
            elevation = if (isPressed) 2.dp else 6.dp,
            shape = shape,
            ambientColor = VibeColors.GreenDark.copy(alpha = 0.08f),
            spotColor = VibeColors.GreenDark.copy(alpha = 0.08f)
        )

    val colors = CardDefaults.cardColors(containerColor = containerColor)
    val border = BorderStroke(1.dp, borderColor)

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = cardModifier,
            shape = shape,
            colors = colors,
            border = border,
            interactionSource = interactionSource,
            content = content
        )
    } else {
        Card(
            modifier = cardModifier,
            shape = shape,
            colors = colors,
            border = border,
            content = content
        )
    }
}

/** Barra de progresso com animação elástica, gradiente e ponto luminoso na extremidade. */
@Composable
fun VibeProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 10.dp,
    color: Color = VibeColors.GreenBright,
    track: Color = Color.White.copy(alpha = 0.22f),
) {
    val animated by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessVeryLow
        ),
        label = "progress"
    )

    val fillBrush = if (color == VibeColors.GreenBright) {
        VibeColors.ProgressBrush
    } else {
        Brush.horizontalGradient(listOf(color, color.copy(alpha = 0.8f)))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(CircleShape)
            .background(track)
    ) {
        if (animated > 0.01f) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animated)
                    .clip(CircleShape)
                    .background(fillBrush),
                contentAlignment = Alignment.CenterEnd
            ) {
                // Brilho na ponta da barra quando há progresso visível
                if (animated > 0.08f) {
                    Box(
                        modifier = Modifier
                            .padding(end = 2.dp)
                            .size((height.value * 0.55f).dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.85f))
                    )
                }
            }
        }
    }
}

fun initialsOf(name: String): String =
    name.trim().split(" ").filter { it.isNotEmpty() }.take(2).joinToString("") { it.first().uppercase() }

/** Avatar circular com anel de gradiente e fundo suave. */
@Composable
fun Avatar(
    name: String,
    size: Dp = 44.dp,
    background: Color = VibeColors.GreenSoft,
    textColor: Color = VibeColors.Green,
    modifier: Modifier = Modifier,
    showRing: Boolean = true,
) {
    Box(
        modifier = modifier
            .size(size)
            .then(
                if (showRing) {
                    Modifier
                        .border(2.dp, VibeColors.AvatarRingBrush, CircleShape)
                        .padding(3.dp)
                } else {
                    Modifier
                }
            )
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initialsOf(name),
            color = textColor,
            fontWeight = FontWeight.ExtraBold,
            fontSize = (size.value * 0.36f).sp,
            letterSpacing = 0.5.sp
        )
    }
}

/** Tile de estatística com selo circular para o emoji e tipografia hierárquica. */
@Composable
fun StatTile(emoji: String, value: String, label: String, modifier: Modifier = Modifier) {
    VibeCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(VibeColors.GreenSoft),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 20.sp)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = value,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = VibeColors.Title
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = VibeColors.Muted
            )
        }
    }
}

@Composable
fun SectionTitle(
    title: String,
    modifier: Modifier = Modifier,
    action: String? = null,
    onAction: () -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
            letterSpacing = (-0.2).sp,
            color = VibeColors.Title,
            modifier = Modifier.weight(1f)
        )
        if (action != null) {
            Surface(
                onClick = onAction,
                shape = CircleShape,
                color = VibeColors.GreenSoft,
            ) {
                Text(
                    text = action,
                    color = VibeColors.Green,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun CategoryChips(selected: Category?, onSelect: (Category?) -> Unit) {
    val haptic = LocalHapticFeedback.current
    val colors = FilterChipDefaults.filterChipColors(
        containerColor = Color.White,
        labelColor = VibeColors.Label,
        selectedContainerColor = VibeColors.Green,
        selectedLabelColor = Color.White,
    )
    val border = FilterChipDefaults.filterChipBorder(
        enabled = true,
        selected = false,
        borderColor = VibeColors.Border,
        selectedBorderColor = VibeColors.Green,
        borderWidth = 1.dp
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            FilterChip(
                selected = selected == null,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onSelect(null)
                },
                label = {
                    Text(
                        "✨ Todos",
                        fontWeight = if (selected == null) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = colors,
                border = border,
                shape = CircleShape,
            )
        }
        items(Category.entries) { c ->
            val isSelected = selected == c
            FilterChip(
                selected = isSelected,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onSelect(c)
                },
                label = {
                    Text(
                        "${c.emoji} ${c.label}",
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = colors,
                border = border,
                shape = CircleShape,
            )
        }
    }
}

/** Círculo de marcação (check) gamificado com ícone vetorial e animação de mola (spring). */
@Composable
fun CheckCircle(checked: Boolean, modifier: Modifier = Modifier) {
    val bg by animateColorAsState(
        targetValue = if (checked) VibeColors.Success else Color.White,
        animationSpec = tween(250),
        label = "checkBg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (checked) VibeColors.Success else VibeColors.Border,
        animationSpec = tween(250),
        label = "checkBorder"
    )
    val checkScale by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "checkScale"
    )

    Box(
        modifier = modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(bg)
            .border(2.dp, borderColor, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Rounded.Check,
            contentDescription = if (checked) "Concluído" else "Pendente",
            tint = Color.White,
            modifier = Modifier
                .size(18.dp)
                .scale(checkScale)
        )
    }
}