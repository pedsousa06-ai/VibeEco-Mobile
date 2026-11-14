package com.vibeeco.app.ui.challenges

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibeeco.app.data.Category
import com.vibeeco.app.data.Challenge
import com.vibeeco.app.data.formatCo2
import com.vibeeco.app.ui.components.*
import com.vibeeco.app.ui.theme.VibeColors

/** Cor do ícone de cada categoria. */
private fun Category.tint(): Color = when (this) {
    Category.AGUA -> Color(0xFF2563EB)
    Category.ENERGIA -> Color(0xFFD97706)
    Category.RESIDUOS -> Color(0xFF16A34A)
    Category.TRANSPORTE -> Color(0xFF7C3AED)
    Category.ALIMENTACAO -> Color(0xFFEA580C)
}

/** Fundo suave de cada categoria. */
private fun Category.softBackground(): Color = when (this) {
    Category.AGUA -> Color(0xFFDBEAFE)
    Category.ENERGIA -> Color(0xFFFEF3C7)
    Category.RESIDUOS -> Color(0xFFDCF5E5)
    Category.TRANSPORTE -> Color(0xFFEDE9FE)
    Category.ALIMENTACAO -> Color(0xFFFFEDD5)
}

@Composable
fun ChallengeCard(
    challenge: Challenge,
    done: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    VibeCard(modifier.fillMaxWidth(), onClick = onToggle) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(46.dp).background(
                    if (done) VibeColors.GreenSoft else challenge.category.softBackground(),
                    RoundedCornerShape(14.dp)
                ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = challenge.category.icon,
                    contentDescription = challenge.category.label,
                    tint = if (done) VibeColors.Green else challenge.category.tint(),
                    modifier = Modifier.size(24.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    challenge.title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp,
                    color = if (done) VibeColors.Muted else VibeColors.Title,
                    textDecoration = if (done) TextDecoration.LineThrough else null,
                )
                Text(challenge.description, fontSize = 12.sp, color = VibeColors.Muted,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Text("+${challenge.points} pts · ${formatCo2(challenge.co2Grams)} de CO₂",
                    fontSize = 12.sp, color = VibeColors.Green, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.width(8.dp))
            CheckCircle(done)
        }
    }
}

@Composable
fun ChallengesScreen(
    challenges: List<Challenge>,
    doneIds: Set<String>,
    onToggle: (Challenge) -> Unit,
) {
    var selected by remember { mutableStateOf<Category?>(null) }
    val filtered = challenges.filter { selected == null || it.category == selected }
    val doneCount = challenges.count { it.id in doneIds }

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            GradientHeader {
                Text("Desafios", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 26.sp)
                Text("Complete ações sustentáveis e ganhe pontos.", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                Spacer(Modifier.height(18.dp))
                Row(Modifier.fillMaxWidth()) {
                    Text("Hoje", color = Color.White, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    Text("$doneCount de ${challenges.size} concluídos", color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
                }
                Spacer(Modifier.height(8.dp))
                VibeProgressBar(doneCount / challenges.size.toFloat())
            }
        }
        item { CategoryChips(selected) { selected = it } }
        items(filtered, key = { it.id }) { c ->
            ChallengeCard(c, c.id in doneIds, { onToggle(c) }, Modifier.padding(horizontal = 16.dp))
        }
    }
}