package com.vibeeco.app.ui.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibeeco.app.data.Challenge
import com.vibeeco.app.data.Levels
import com.vibeeco.app.data.formatCo2
import com.vibeeco.app.ui.challenges.ChallengeCard
import com.vibeeco.app.ui.components.*
import com.vibeeco.app.ui.theme.Colors
import java.time.LocalTime

@Composable
fun GoalRing(progress: Float, label: String, modifier: Modifier = Modifier) {
    val animated by animateFloatAsState(progress.coerceIn(0f, 1f), tween(700), label = "ring")
    Box(modifier.size(84.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = 10.dp.toPx()
            val arcSize = Size(size.width - stroke, size.height - stroke)
            val topLeft = Offset(stroke / 2, stroke / 2)
            drawArc(
                color = Colors.GreenSoft, startAngle = 0f, sweepAngle = 360f, useCenter = false,
                topLeft = topLeft, size = arcSize, style = Stroke(stroke, cap = StrokeCap.Round),
            )
            drawArc(
                color = Colors.Green, startAngle = -90f, sweepAngle = 360f * animated, useCenter = false,
                topLeft = topLeft, size = arcSize, style = Stroke(stroke, cap = StrokeCap.Round),
            )
        }
        Text(label, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Colors.Title)
    }
}

@Composable
fun HomeScreen(
    userName: String,
    points: Int,
    streak: Int,
    co2Grams: Int,
    doneIds: Set<String>,
    dailyGoal: Int,
    featured: List<Challenge>,
    tip: String,
    onToggle: (Challenge) -> Unit,
    onSeeAll: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenLearn: () -> Unit,
) {
    val hour = LocalTime.now().hour
    val greeting = when { hour < 12 -> "Bom dia"; hour < 18 -> "Boa tarde"; else -> "Boa noite" }
    val firstName = userName.trim().split(" ").firstOrNull().orEmpty()
    val doneToday = doneIds.size
    val goalReached = doneToday >= dailyGoal

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            GradientHeader {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("$greeting, $firstName! 👋", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        Text("Vamos cuidar do planeta hoje?", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                    }
                    Avatar(
                        userName, 46.dp, background = Color.White.copy(alpha = 0.2f), textColor = Color.White,
                        modifier = Modifier.clickable(onClick = onOpenProfile),
                    )
                }
                Spacer(Modifier.height(20.dp))
                Column(
                    Modifier.fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.14f), RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(Levels.emoji(points), fontSize = 30.sp)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Nível ${Levels.level(points)} · ${Levels.name(points)}",
                                color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("$points pontos", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    VibeProgressBar(Levels.progress(points), height = 10.dp)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Faltam ${Levels.remaining(points)} pts para ${Levels.name(points + Levels.remaining(points))}",
                        color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp,
                    )
                }
            }
        }
        item {
            Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatTile("🔥", if (streak == 1) "1 dia" else "$streak dias", "Sequência", Modifier.weight(1f))
                StatTile("🌍", formatCo2(co2Grams), "CO₂ evitado", Modifier.weight(1f))
                StatTile("✅", "$doneToday/$dailyGoal", "Meta de hoje", Modifier.weight(1f))
            }
        }
        item {
            VibeCard(Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    GoalRing(doneToday / dailyGoal.toFloat(), "$doneToday/$dailyGoal")
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("Meta diária", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Colors.Title)
                        Text(
                            if (goalReached) "Meta batida! 🎉 Você está arrasando."
                            else "Complete mais ${dailyGoal - doneToday} desafio(s) para bater a meta de hoje.",
                            fontSize = 13.sp, color = Colors.Muted, lineHeight = 18.sp,
                        )
                    }
                }
            }
        }
        item {
            SectionTitle("Desafios de hoje", Modifier.padding(horizontal = 16.dp), "Ver todos", onSeeAll)
        }
        items(featured, key = { it.id }) { c ->
            ChallengeCard(c, c.id in doneIds, { onToggle(c) }, Modifier.padding(horizontal = 16.dp))
        }
        item {
            VibeCard(
                Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                onClick = onOpenLearn,
                containerColor = Colors.GreenSoft,
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                    Text("💡", fontSize = 28.sp)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Dica do dia", fontWeight = FontWeight.Bold, color = Colors.GreenDark, fontSize = 15.sp)
                        Spacer(Modifier.height(2.dp))
                        Text(tip, fontSize = 13.sp, color = Colors.Label, lineHeight = 19.sp)
                        Spacer(Modifier.height(6.dp))
                        Text("Ver mais conteúdos →", fontSize = 12.sp, color = Colors.Green, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
