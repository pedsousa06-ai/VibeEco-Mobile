package com.vibeeco.app.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibeeco.app.data.Challenge
import com.vibeeco.app.data.Levels
import com.vibeeco.app.data.formatCo2
import com.vibeeco.app.ui.challenges.ChallengeCard
import com.vibeeco.app.ui.components.*
import com.vibeeco.app.ui.theme.VibeColors
import java.time.LocalTime

@Composable
fun GoalRing(progress: Float, label: String, modifier: Modifier = Modifier) {
    val animated by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessVeryLow
        ),
        label = "ring"
    )
    val isComplete = progress >= 1f

    Box(modifier.size(88.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = 10.dp.toPx()
            val arcSize = Size(size.width - stroke, size.height - stroke)
            val topLeft = Offset(stroke / 2f, stroke / 2f)

            // Trilha de fundo
            drawArc(
                color = VibeColors.GreenSoft,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(stroke, cap = StrokeCap.Round),
            )

            // Arco ativo com gradiente rotacionado para evitar emenda visual no topo
            if (animated > 0.01f) {
                rotate(degrees = -90f) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = if (isComplete) {
                                listOf(VibeColors.Green, VibeColors.GreenBright, VibeColors.Gold, VibeColors.Green)
                            } else {
                                listOf(VibeColors.Green, VibeColors.GreenBright, VibeColors.Green)
                            }
                        ),
                        startAngle = 0f,
                        sweepAngle = 360f * animated,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(stroke, cap = StrokeCap.Round),
                    )
                }
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = if (isComplete) VibeColors.Green else VibeColors.Title
            )
            Text(
                text = if (isComplete) "100%" else "hoje",
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
                color = VibeColors.Muted
            )
        }
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
    val greeting = when {
        hour < 12 -> "Bom dia"
        hour < 18 -> "Boa tarde"
        else -> "Boa noite"
    }
    val firstName = userName.trim().split(" ").firstOrNull().orEmpty()
    val doneToday = doneIds.size
    val goalReached = doneToday >= dailyGoal

    // Gatilho suave de entrada ao carregar a tela
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(VibeColors.Background),
        contentPadding = PaddingValues(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // 1. Cabeçalho Imersivo com Cartão de Nível Glassmorphic
        item {
            GradientHeader {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "$greeting, $firstName! 👋",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 23.sp,
                            letterSpacing = (-0.3).sp
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "Cada pequena escolha transforma o planeta",
                            color = Color.White.copy(alpha = 0.82f),
                            fontSize = 13.sp
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Avatar(
                        name = userName,
                        size = 48.dp,
                        background = Color.White.copy(alpha = 0.22f),
                        textColor = Color.White,
                        showRing = true,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable(onClick = onOpenProfile),
                    )
                }

                Spacer(Modifier.height(20.dp))

                // Cartão Glassmorphic do Nível
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.18f),
                                    Color.White.copy(alpha = 0.10f)
                                )
                            )
                        )
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.28f),
                            shape = RoundedCornerShape(22.dp)
                        )
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(Levels.emoji(points), fontSize = 26.sp)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = "NÍVEL ${Levels.level(points)}",
                                color = VibeColors.GreenBright,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp,
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = Levels.name(points),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                        }
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.22f)
                        ) {
                            Text(
                                text = "✨ $points pts",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    VibeProgressBar(Levels.progress(points), height = 10.dp)
                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Próximo: ${Levels.name(points + Levels.remaining(points))}",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Faltam ${Levels.remaining(points)} pts",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 2. Indicadores Rápidos (Métricas)
        item {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { 30 }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatTile(
                        emoji = "🔥",
                        value = if (streak == 1) "1 dia" else "$streak dias",
                        label = "Sequência",
                        modifier = Modifier.weight(1f)
                    )
                    StatTile(
                        emoji = "🌍",
                        value = formatCo2(co2Grams),
                        label = "CO₂ evitado",
                        modifier = Modifier.weight(1f)
                    )
                    StatTile(
                        emoji = "✅",
                        value = "$doneToday/$dailyGoal",
                        label = "Meta de hoje",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 3. Card Interativo da Meta Diária
        item {
            val cardBorder by animateColorAsState(
                targetValue = if (goalReached) VibeColors.Green.copy(alpha = 0.45f) else VibeColors.Border.copy(alpha = 0.55f),
                label = "goalBorder"
            )
            val cardBg by animateColorAsState(
                targetValue = if (goalReached) VibeColors.GreenSoft.copy(alpha = 0.45f) else Color.White,
                label = "goalBg"
            )

            VibeCard(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                containerColor = cardBg,
                borderColor = cardBorder
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GoalRing(
                        progress = doneToday / dailyGoal.toFloat(),
                        label = "$doneToday/$dailyGoal"
                    )
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Meta diária",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = VibeColors.Title
                            )
                            if (goalReached) {
                                Spacer(Modifier.width(8.dp))
                                Surface(
                                    shape = CircleShape,
                                    color = VibeColors.Success
                                ) {
                                    Text(
                                        text = "CONCLUÍDA 🎉",
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = if (goalReached) {
                                "Incrível! Você cumpriu sua meta ecológica de hoje e manteve o ritmo."
                            } else {
                                val remaining = (dailyGoal - doneToday).coerceAtLeast(1)
                                "Complete mais $remaining ${if (remaining == 1) "desafio" else "desafios"} para bater sua meta de hoje."
                            },
                            fontSize = 13.sp,
                            color = VibeColors.Label,
                            lineHeight = 18.sp,
                        )
                    }
                }
            }
        }

        // 4. Seção de Desafios em Destaque
        item {
            SectionTitle(
                title = "Desafios de hoje",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
                action = "Ver todos",
                onAction = onSeeAll
            )
        }

        items(featured, key = { it.id }) { c ->
            ChallengeCard(
                challenge = c,
                done = c.id in doneIds,
                onToggle = { onToggle(c) },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // 5. Card Editorial "Dica do Dia"
        item {
            VibeCard(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                onClick = onOpenLearn,
                containerColor = VibeColors.GreenSoft,
                borderColor = VibeColors.GreenMint
            ) {
                Column(Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = VibeColors.Green.copy(alpha = 0.12f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.AutoAwesome,
                                    contentDescription = null,
                                    tint = VibeColors.Green,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "DICA ECO DO DIA",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = VibeColors.GreenDark,
                                    letterSpacing = 0.6.sp
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("💡", fontSize = 24.sp)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = tip,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = VibeColors.Title,
                                lineHeight = 20.sp
                            )
                            Spacer(Modifier.height(10.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Explorar trilhas de aprendizado",
                                    fontSize = 13.sp,
                                    color = VibeColors.Green,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Rounded.ArrowForward,
                                    contentDescription = null,
                                    tint = VibeColors.Green,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}