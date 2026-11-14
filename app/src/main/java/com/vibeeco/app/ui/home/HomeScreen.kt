package com.vibeeco.app.ui.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
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
import com.vibeeco.app.ui.theme.*
import java.time.LocalTime

@Composable
fun GoalRing(progress: Float, label: String, modifier: Modifier = Modifier) {
    val animated by animateFloatAsState(progress.coerceIn(0f, 1f), tween(700), label = "ring")
    Box(modifier.size(76.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = 8.dp.toPx()
            val arcSize = Size(size.width - stroke, size.height - stroke)
            val topLeft = Offset(stroke / 2, stroke / 2)
            drawArc(
                color = Color(0xFFD6EADB), startAngle = 0f, sweepAngle = 360f, useCenter = false,
                topLeft = topLeft, size = arcSize, style = Stroke(stroke, cap = StrokeCap.Round),
            )
            drawArc(
                brush = Brush.sweepGradient(listOf(Color(0xFF16A34A), Color(0xFF4ADE80), Color(0xFF16A34A))),
                startAngle = -90f, sweepAngle = 360f * animated, useCenter = false,
                topLeft = topLeft, size = arcSize, style = Stroke(stroke, cap = StrokeCap.Round),
            )
        }
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = TextMain
        )
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
        Modifier
            .fillMaxSize()
            .background(BackgroundApp),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // BANNER DE BOAS-VINDAS (Degradê Floresta)
        item {
            GradientHeader {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "$greeting, $firstName! 👋",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            letterSpacing = (-0.3).sp
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "Você está em uma sequência de $streak dias!",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 13.5.sp
                        )
                    }

                    Avatar(
                        name = userName,
                        size = 46.dp,
                        modifier = Modifier
                            .clickable(onClick = onOpenProfile)
                            .border(2.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                    )
                }

                Spacer(Modifier.height(18.dp))

                // Cartão translúcido de Nível e XP
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(Levels.emoji(points), fontSize = 26.sp)
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = "Nível ${Levels.level(points)} · ${Levels.name(points)}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "$points XP acumulados",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 12.5.sp
                                )
                            }

                            // Chip XP em Destaque
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = GoldXPContainer,
                                modifier = Modifier.padding(start = 4.dp)
                            ) {
                                Row(
                                    Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = GoldXP,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = "$points XP",
                                        color = GoldXPText,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Barra clara de progresso
                        val prog = Levels.progress(points).coerceIn(0f, 1f)
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(7.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color.White.copy(alpha = 0.25f))
                        ) {
                            Box(
                                Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(prog)
                                    .clip(RoundedCornerShape(50))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFF86EFAC), Color.White)
                                        )
                                    )
                            )
                        }

                        Spacer(Modifier.height(6.dp))

                        Text(
                            text = "Faltam ${Levels.remaining(points)} XP para ${Levels.name(points + Levels.remaining(points))}",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.5.sp,
                        )
                    }
                }
            }
        }

        // CARDS DE RESUMO (3 MÉTRICAS)
        item {
            Row(
                Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatTile(
                    emoji = "🔥",
                    value = if (streak == 1) "1 dia" else "$streak dias",
                    label = "Ofensiva",
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    emoji = "🌍",
                    value = formatCo2(co2Grams),
                    label = "CO₂ evitado",
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    emoji = "🎯",
                    value = "$doneToday/$dailyGoal",
                    label = "Meta do dia",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // CARD DA META DIÁRIA
        item {
            VibeCard(Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
                Row(
                    Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GoalRing(doneToday / dailyGoal.toFloat(), "$doneToday/$dailyGoal")
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Meta Diária de Sustentabilidade",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextMain
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = if (goalReached)
                                "Parabéns! Meta batida hoje! 🎉 Continue gerando impacto positivo."
                            else
                                "Complete mais ${dailyGoal - doneToday} ação(ões) para bater a meta de hoje.",
                            fontSize = 13.sp,
                            color = TextMuted,
                            lineHeight = 18.sp,
                        )
                    }
                }
            }
        }

        // CABEÇALHO DA SEÇÃO DE DESAFIOS
        item {
            SectionTitle(
                title = "Desafios em Destaque",
                modifier = Modifier.padding(horizontal = 16.dp),
                action = "Ver todos",
                onAction = onSeeAll
            )
        }

        // LISTA DE DESAFIOS
        items(featured, key = { it.id }) { c ->
            ChallengeCard(
                challenge = c,
                done = c.id in doneIds,
                onToggle = { onToggle(c) },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // CARD DE DICA DO DIA / CONTEÚDOS
        item {
            VibeCard(
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                onClick = onOpenLearn,
                containerColor = GreenContainer,
                borderColor = Color(0xFFC7EBD2)
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                    Text("💡", fontSize = 26.sp)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Dica Sustentável",
                            fontWeight = FontWeight.Bold,
                            color = GreenOnContainer,
                            fontSize = 14.5.sp
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = tip,
                            fontSize = 13.sp,
                            color = Color(0xFF344638),
                            lineHeight = 19.sp
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Aprender mais conteúdos →",
                            fontSize = 12.5.sp,
                            color = GreenBrand,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
