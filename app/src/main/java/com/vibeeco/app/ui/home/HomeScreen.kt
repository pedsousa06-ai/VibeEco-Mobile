package com.vibeeco.app.ui.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibeeco.app.data.*
import com.vibeeco.app.ui.challenges.ChallengeCard
import com.vibeeco.app.ui.components.*
import com.vibeeco.app.ui.theme.*
import java.time.LocalTime

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
    val saudacao = when { hour < 12 -> "Bom dia," ; hour < 18 -> "Boa tarde," ; else -> "Boa noite," }
    val firstName = userName.trim().split(" ").firstOrNull().orEmpty()
    val doneToday = doneIds.size
    val mcMoedas = (points / 5).coerceAtLeast(12) // Moedas virtuais VibeEco (MC)
    val myRankPosition = 4 // Simulação ranking institucional

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundApp),
        contentPadding = PaddingValues(bottom = 36.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        // =========================================================================
        // 1. BANNER DE BOAS-VINDAS (Fidelidade ao .banner do InicioView.vue)
        // =========================================================================
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF0C4A25), Color(0xFF117A3D), Color(0xFF1FA856))
                        ),
                        RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp)
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = saudacao,
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 13.5.sp
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "$firstName!",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                letterSpacing = (-0.4).sp
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "Você está em uma ofensiva de $streak dias. Continue assim!",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 13.sp
                            )
                        }

                        Avatar(
                            name = userName,
                            size = 52.dp,
                            modifier = Modifier
                                .clickable(onClick = onOpenProfile)
                                .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    // Chips no Banner (XP, MC, Ofensiva)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BannerStatChip(
                            icon = "★",
                            iconColor = Color(0xFFFDE68A),
                            label = "$points XP"
                        )
                        BannerStatChip(
                            icon = "🪙",
                            iconColor = Color(0xFFA7F3D0),
                            label = "$mcMoedas MC"
                        )
                        BannerStatChip(
                            icon = "🔥",
                            iconColor = Color(0xFFFDBA74),
                            label = "$streak dias"
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    // Bloco do Nível
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = 0.14f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(Levels.emoji(points), fontSize = 22.sp)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = "Nível ${Levels.level(points)} - ${Levels.name(points)}",
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp
                                    )
                                }
                                Text(
                                    text = "$points / ${(Levels.level(points)) * 100} XP",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(Modifier.height(8.dp))

                            // Barra clara com degradê
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
                        }
                    }
                }
            }
        }

        // =========================================================================
        // 2. CARDS DE RESUMO (Grid 2x2 idêntico ao desktop .resumo)
        // =========================================================================
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ResumoCard(
                        icon = "🎯",
                        iconBg = Color(0xFFDCF5E5),
                        value = "3",
                        label = "Missões em Andamento",
                        modifier = Modifier.weight(1f)
                    )
                    ResumoCard(
                        icon = "🏅",
                        iconBg = Color(0xFFFEF3C7),
                        value = "14",
                        label = "Missões Concluídas",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ResumoCard(
                        icon = "⚡",
                        iconBg = Color(0xFFEDE9FE),
                        value = "${featured.size}",
                        label = "Desafios Ativos",
                        modifier = Modifier.weight(1f)
                    )
                    ResumoCard(
                        icon = "🏆",
                        iconBg = Color(0xFFFFEDD5),
                        value = "#$myRankPosition",
                        label = "Posição no Ranking",
                        detail = "Top 10% na instituição",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // =========================================================================
        // 3. OFENSIVA SEMANAL (7 dias S, T, Q, Q, S, S, D com fogo)
        // =========================================================================
        item {
            VibeCard(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            ) {
                Column(Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🔥", fontSize = 20.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Ofensiva Diária",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextMain
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "$streak",
                                fontSize = 38.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFEA580C),
                                lineHeight = 38.sp
                            )
                            Text(
                                text = "dias consecutivos",
                                fontSize = 12.5.sp,
                                color = TextMuted
                            )
                        }

                        // Os 7 dias da semana
                        val dias = listOf("S", "T", "Q", "Q", "S", "S", "D")
                        val diasCumpridos = streak.coerceAtMost(7)

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            dias.forEachIndexed { index, letra ->
                                val ativo = index < diasCumpridos
                                val hoje = index == (diasCumpridos - 1).coerceAtLeast(0)
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (ativo) Brush.linearGradient(
                                                listOf(Color(0xFFF97316), Color(0xFFEA580C))
                                            )
                                            else Brush.linearGradient(
                                                listOf(Color(0xFFFFEDD5), Color(0xFFFFEDD5))
                                            )
                                        )
                                        .then(
                                            if (hoje) Modifier.border(
                                                2.dp,
                                                Color(0xFFEA580C),
                                                CircleShape
                                            ) else Modifier
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = letra,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (ativo) Color.White else Color(0xFFC2410C)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // =========================================================================
        // 4. MISSÕES EM ANDAMENTO (Com barra de progresso, tags e XP)
        // =========================================================================
        item {
            SectionTitle(
                title = "Missões em Andamento",
                modifier = Modifier.padding(horizontal = 16.dp),
                action = "Ver todas",
                onAction = onSeeAll
            )
        }

        items(Content.missions, key = { it.id }) { mission ->
            MissionCard(
                mission = mission,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // =========================================================================
        // 5. DESAFIOS EM DESTAQUE (Com check-box interativo)
        // =========================================================================
        item {
            SectionTitle(
                title = "Desafios de Hoje",
                modifier = Modifier.padding(horizontal = 16.dp),
                action = "Ver todos",
                onAction = onSeeAll
            )
        }

        items(featured, key = { it.id }) { challenge ->
            ChallengeCard(
                challenge = challenge,
                done = challenge.id in doneIds,
                onToggle = { onToggle(challenge) },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // =========================================================================
        // 6. CONTEÚDOS RECOMENDADOS (Carrossel Horizontal)
        // =========================================================================
        item {
            SectionTitle(
                title = "Conteúdos Recomendados",
                modifier = Modifier.padding(horizontal = 16.dp),
                action = "Ver todos",
                onAction = onOpenLearn
            )
        }

        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(Content.articles.take(3), key = { it.id }) { article ->
                    VibeCard(
                        modifier = Modifier
                            .width(240.dp)
                            .clickable(onClick = onOpenLearn)
                    ) {
                        Column(
                            Modifier
                                .padding(14.dp)
                                .heightIn(min = 120.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = GreenContainer
                                ) {
                                    Text(
                                        text = article.category.label,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GreenOnContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("★", color = GoldXP, fontSize = 12.sp)
                                    Spacer(Modifier.width(3.dp))
                                    Text(
                                        text = "${article.points} XP",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldXPText
                                    )
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            Text(
                                text = article.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextMain,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(Modifier.height(8.dp))

                            Text(
                                text = "⏱ ${article.minutes} min de leitura",
                                fontSize = 11.5.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }

        // =========================================================================
        // 7. CONQUISTAS RECENTES (Grid 2x2 com badges oficiais)
        // =========================================================================
        item {
            VibeCard(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            ) {
                Column(Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🏅", fontSize = 19.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Conquistas Recentes",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextMain
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Content.achievements.take(2).forEach { badge ->
                            AchievementItem(badge = badge, modifier = Modifier.weight(1f))
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Content.achievements.drop(2).take(2).forEach { badge ->
                            AchievementItem(badge = badge, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// SUBCOMPONENTES AUXILIARES
// =========================================================================

@Composable
private fun BannerStatChip(
    icon: String,
    iconColor: Color,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color.White.copy(alpha = 0.16f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.22f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 13.sp, color = iconColor)
            Spacer(Modifier.width(5.dp))
            Text(
                text = label,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.5.sp
            )
        }
    }
}

@Composable
private fun ResumoCard(
    icon: String,
    iconBg: Color,
    value: String,
    label: String,
    detail: String? = null,
    modifier: Modifier = Modifier
) {
    VibeCard(modifier = modifier) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(iconBg, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 22.sp)
            }

            Spacer(Modifier.width(12.dp))

            Column {
                Text(
                    text = value,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = TextMain,
                    lineHeight = 22.sp
                )
                Text(
                    text = label,
                    fontSize = 11.5.sp,
                    color = TextMuted,
                    lineHeight = 14.sp
                )
                if (detail != null) {
                    Text(
                        text = detail,
                        fontSize = 10.sp,
                        color = GreenBrand,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun MissionCard(
    mission: Mission,
    modifier: Modifier = Modifier
) {
    val difColor = when (mission.difficulty.lowercase()) {
        "fácil", "facil" -> Color(0xFF16A34A)
        "médio", "medio" -> Color(0xFFD97706)
        else -> Color(0xFFDC2626)
    }

    VibeCard(modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = GreenContainer
                    ) {
                        Text(
                            text = "${mission.category.emoji} ${mission.category.label}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GreenOnContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Text(
                        text = mission.difficulty,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = difColor
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("★", color = GoldXP, fontSize = 13.sp)
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = "+${mission.xp} XP",
                        color = GoldXPText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = mission.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.5.sp,
                color = TextMain
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Progresso",
                    fontSize = 11.5.sp,
                    color = TextMuted
                )
                Text(
                    text = "${mission.progress}%",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextMain
                )
            }

            Spacer(Modifier.height(5.dp))

            // Barra de progresso verde
            val prog = (mission.progress / 100f).coerceIn(0f, 1f)
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFD6EADB))
            ) {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(prog)
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF16A34A), Color(0xFF4ADE80))
                            )
                        )
                )
            }
        }
    }
}

@Composable
private fun AchievementItem(
    badge: AchievementBadge,
    modifier: Modifier = Modifier
) {
    val bgColor = when (badge.color) {
        "verde" -> Color(0xFFDCF5E5)
        "amarelo" -> Color(0xFFFEF3C7)
        "azul" -> Color(0xFFDBEAFE)
        else -> Color(0xFFEDE9FE)
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = BackgroundApp,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(bgColor, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = badge.icon, fontSize = 18.sp)
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = badge.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = TextMain,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
