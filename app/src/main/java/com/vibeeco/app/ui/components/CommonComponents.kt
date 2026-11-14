package com.vibeeco.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibeeco.app.data.Category
import com.vibeeco.app.ui.theme.VibeColors

/** Cabeçalho verde com cantos arredondados embaixo; estende-se sob a barra de status. */
@Composable
fun GradientHeader(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                VibeColors.HeaderBrush,
                RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
            )
            .statusBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp),
        content = content,
    )
}

@Composable
fun VibeCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    containerColor: Color = Color.White,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(20.dp)
    val cardColors = CardDefaults.cardColors(containerColor = containerColor)
    val elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    if (onClick != null) {
        Card(onClick = onClick, modifier = modifier, shape = shape, colors = cardColors,
            elevation = elevation, content = content)
    } else {
        Card(modifier = modifier, shape = shape, colors = cardColors,
            elevation = elevation, content = content)
    }
}

@Composable
fun VibeProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
    color: Color = VibeColors.GreenBright,
    track: Color = Color.White.copy(alpha = 0.25f),
) {
    val animated by animateFloatAsState(progress.coerceIn(0f, 1f), tween(600), label = "progress")
    Box(
        modifier.fillMaxWidth().height(height).clip(RoundedCornerShape(50)).background(track)
    ) {
        Box(Modifier.fillMaxHeight().fillMaxWidth(animated).clip(RoundedCornerShape(50)).background(color))
    }
}

fun initialsOf(name: String): String =
    name.trim().split(" ").filter { it.isNotEmpty() }.take(2).joinToString("") { it.first().uppercase() }

@Composable
fun Avatar(
    name: String,
    size: Dp = 44.dp,
    background: Color = VibeColors.GreenSoft,
    textColor: Color = VibeColors.Green,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier.size(size).clip(CircleShape).background(background),
        contentAlignment = Alignment.Center,
    ) {
        Text(initialsOf(name), color = textColor, fontWeight = FontWeight.Bold, fontSize = (size.value * 0.38f).sp)
    }
}

@Composable
fun StatTile(emoji: String, value: String, label: String, modifier: Modifier = Modifier) {
    VibeCard(modifier) {
        Column(
            Modifier.fillMaxWidth().padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(emoji, fontSize = 22.sp)
            Spacer(Modifier.height(4.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = VibeColors.Title)
            Text(label, fontSize = 11.sp, color = VibeColors.Muted)
        }
    }
}

@Composable
fun SectionTitle(title: String, modifier: Modifier = Modifier, action: String? = null, onAction: () -> Unit = {}) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = VibeColors.Title, modifier = Modifier.weight(1f))
        if (action != null) {
            TextButton(onClick = onAction) {
                Text(action, color = VibeColors.Green, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun CategoryChips(selected: Category?, onSelect: (Category?) -> Unit) {
    val chipColors = FilterChipDefaults.filterChipColors(
        containerColor = Color.White,
        labelColor = VibeColors.Label,
        selectedContainerColor = VibeColors.Green,
        selectedLabelColor = Color.White,
    )
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            FilterChip(
                selected = selected == null, onClick = { onSelect(null) },
                label = { Text("Todos") }, colors = chipColors, shape = RoundedCornerShape(50),
            )
        }
        items(Category.entries) { c ->
            FilterChip(
                selected = selected == c, onClick = { onSelect(c) },
                label = { Text("${c.emoji} ${c.label}") }, colors = chipColors, shape = RoundedCornerShape(50),
            )
        }
    }
}

/** Círculo de marcação (check) dos desafios. */
@Composable
fun CheckCircle(checked: Boolean, modifier: Modifier = Modifier) {
    val bg by animateColorAsState(if (checked) VibeColors.Success else Color.White, label = "check")
    Box(
        modifier.size(28.dp).clip(CircleShape).background(bg)
            .border(2.dp, if (checked) VibeColors.Success else VibeColors.Border, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) Text("✓", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}