package com.vibeeco.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibeeco.app.data.Category
import com.vibeeco.app.ui.theme.*

/** Cabeçalho com degradê floresta idêntico ao menu/banner do web desktop */
@Composable
fun GradientHeader(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0B6A35), Color(0xFF07502A), Color(0xFF06411F))
                ),
                RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
            )
            .statusBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 24.dp),
        content = content,
    )
}

/** Card padrão com cantos de 16dp e borda sutil idêntico à web */
@Composable
fun VibeCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    containerColor: Color = Color.White,
    borderColor: Color = BorderLight,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(16.dp)
    val cardColors = CardDefaults.cardColors(containerColor = containerColor)
    val border = BorderStroke(1.dp, borderColor)

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            colors = cardColors,
            border = border,
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            content = content
        )
    } else {
        Card(
            modifier = modifier,
            shape = shape,
            colors = cardColors,
            border = border,
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            content = content
        )
    }
}

/** Barra de progresso com degradê suave */
@Composable
fun VibeProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
    color: Color = GreenBrandLight,
    track: Color = Color(0xFFD6EADB),
) {
    val animated by animateFloatAsState(progress.coerceIn(0f, 1f), tween(650), label = "progress")
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(50))
            .background(track)
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(animated)
                .clip(RoundedCornerShape(50))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF16A34A), Color(0xFF4ADE80))
                    )
                )
        )
    }
}

fun initialsOf(name: String): String =
    name.trim().split(" ").filter { it.isNotEmpty() }.take(2).joinToString("") { it.first().uppercase() }

@Composable
fun Avatar(
    name: String,
    size: Dp = 42.dp,
    background: Color? = null,
    textColor: Color? = null,
    modifier: Modifier = Modifier,
) {
    val bgModifier = if (background != null) {
        Modifier.background(background)
    } else {
        Modifier.background(Brush.linearGradient(listOf(Color(0xFF22C55E), Color(0xFF0F7A3B))))
    }
    val finalTextColor = textColor ?: Color.White

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .then(bgModifier),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initialsOf(name),
            color = finalTextColor,
            fontWeight = FontWeight.Bold,
            fontSize = (size.value * 0.36f).sp
        )
    }
}

/** Card de métrica / resumo com ícone colorido em caixa suave */
@Composable
fun StatTile(
    emoji: String,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.White
) {
    VibeCard(modifier = modifier, containerColor = containerColor) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(emoji, fontSize = 22.sp)
            Spacer(Modifier.height(4.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextMain
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 11.5.sp,
                color = TextMuted
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
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = TextMain,
            modifier = Modifier.weight(1f)
        )
        if (action != null) {
            TextButton(
                onClick = onAction,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "$action →",
                    color = GreenBrand,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun CategoryChips(selected: Category?, onSelect: (Category?) -> Unit) {
    val chipColors = FilterChipDefaults.filterChipColors(
        containerColor = Color.White,
        labelColor = TextMuted,
        selectedContainerColor = GreenBrand,
        selectedLabelColor = Color.White,
    )
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            FilterChip(
                selected = selected == null,
                onClick = { onSelect(null) },
                label = { Text("Todos", fontWeight = if (selected == null) FontWeight.Bold else FontWeight.Normal) },
                colors = chipColors,
                shape = RoundedCornerShape(50),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selected == null,
                    borderColor = BorderLight,
                    selectedBorderColor = GreenBrand
                )
            )
        }
        items(Category.entries) { c ->
            val isSelected = selected == c
            FilterChip(
                selected = isSelected,
                onClick = { onSelect(c) },
                label = { Text("${c.emoji} ${c.label}", fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                colors = chipColors,
                shape = RoundedCornerShape(50),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = BorderLight,
                    selectedBorderColor = GreenBrand
                )
            )
        }
    }
}

/** Círculo de marcação (check) dos desafios */
@Composable
fun CheckCircle(checked: Boolean, modifier: Modifier = Modifier) {
    val bg by animateColorAsState(if (checked) GreenBrand else Color.White, label = "check")
    Box(
        modifier = modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(bg)
            .border(1.5.dp, if (checked) GreenBrand else BorderInput, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            Text("✓", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}
