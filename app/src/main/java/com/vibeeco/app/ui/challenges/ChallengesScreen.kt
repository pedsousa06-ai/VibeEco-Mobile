package com.vibeeco.app.ui.challenges

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.vibeeco.app.ui.theme.Colors

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
                    if (done) Colors.GreenSoft else Color(0xFFF1F3F2), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center,
            ) { Text(challenge.category.emoji, fontSize = 22.sp) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    challenge.title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp,
                    color = if (done) Colors.Muted else Colors.Title,
                    textDecoration = if (done) TextDecoration.LineThrough else null,
                )
                Text(challenge.description, fontSize = 12.sp, color = Colors.Muted,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Text("+${challenge.points} pts · ${formatCo2(challenge.co2Grams)} de CO₂",
                    fontSize = 12.sp, color = Colors.Green, fontWeight = FontWeight.Medium)
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
