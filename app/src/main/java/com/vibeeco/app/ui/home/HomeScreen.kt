package com.vibeeco.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Award
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Coins
import com.composables.icons.lucide.Droplets
import com.composables.icons.lucide.Flame
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Medal
import com.composables.icons.lucide.Recycle
import com.composables.icons.lucide.Sprout
import com.composables.icons.lucide.Star
import com.composables.icons.lucide.Target
import com.composables.icons.lucide.Trophy
import com.composables.icons.lucide.Zap
import com.vibeeco.app.data.*
import com.vibeeco.app.ui.challenges.ChallengeCard
import com.vibeeco.app.ui.components.*
import com.vibeeco.app.ui.theme.*
import java.time.LocalDate
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
                            icon = Lucide.Star,
                            iconColor = Color(0xFFFDE68A),
                            label = "$points XP"
                        )
                        BannerStatChip(
                            icon = Lucide.Coins,
                            iconColor = Color(0xFFA7F3D0),
                            label = "$mcMoedas MC"
                        )
                        BannerStatChip(
                            icon = Lucide.Flame,
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
                                    Icon(
                                        imageVector = Levels.icon(points),
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
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
                        icon = Lucide.Target,
                        iconTint = Color(0xFF16A34A),
                        iconBg = Color(0xFFDCF5E5),
                        value = "3",
                        label = "Missões em Andamento",
                        modifier = Modifier.weight(1f)
                    )
                    ResumoCard(
                        icon = Lucide.Medal,
                        iconTint = Color(0xFFD97706),
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
                        icon = Lucide.Zap,
                        iconTint = Color(0xFF7C3AED),
                        iconBg = Color(0xFFEDE9FE),
                        value = "${featured.size}",
                        label = "Desafios Ativos",
                        modifier = Modifier.weight(1f)
                    )
                    ResumoCard(
                        icon = Lucide.Trophy,
                        iconTint = Color(0xFFEA580C),
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
        // 3. OFENSIVA SEMANAL
        // =========================================================================
        item {
            StreakCard(
                streak = streak,
                doneToday = doneToday,
                dailyGoal = dailyGoal,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
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
                                    Icon(
                                        imageVector = Lucide.Star,
                                        contentDescription = null,
                                        tint = GoldXP,
                                        modifier = Modifier.size(13.dp)
                                    )
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

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Lucide.Clock,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "${article.minutes} min de leitura",
                                    fontSize = 11.5.sp,
                                    color = TextMuted
                                )
                            }
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
                        Icon(
                            imageVector = Lucide.Award,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(22.dp)
                        )
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
private fun StreakCard(
    streak: Int,
    doneToday: Int,
    dailyGoal: Int,
    modifier: Modifier = Modifier
) {
    val laranja = Color(0xFFEA580C)
    val laranjaClaro = Color(0xFFFFF1E6)
    val dias = listOf("S", "T", "Q", "Q", "S", "S", "D")
    val hojeIdx = LocalDate.now().dayOfWeek.value - 1 // segunda = 0
    val metaHoje = dailyGoal.coerceAtLeast(1)
    val progressoHoje = (doneToday.toFloat() / metaHoje).coerceIn(0f, 1f)
    val metaBatida = doneToday >= metaHoje

    VibeCard(modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp)) {
            // Cabeçalho: chama + título + contador
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(
                            Brush.linearGradient(listOf(Color(0xFFFB923C), Color(0xFFEA580C))),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.Flame,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(Modifier.width(14.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        text = "Ofensiva Diária",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextMain
                    )
                    Text(
                        text = if (streak > 0) "Continue assim para manter a sequência" else "Complete um desafio hoje para começar",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$streak",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = laranja,
                        lineHeight = 32.sp
                    )
                    Text(
                        text = if (streak == 1) "dia" else "dias",
                        fontSize = 11.5.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            // Semana: um círculo por dia, com a letra embaixo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                dias.forEachIndexed { index, letra ->
                    val ehHoje = index == hojeIdx
                    val ativo = index <= hojeIdx && index > hojeIdx - streak

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    if (ativo) Brush.linearGradient(
                                        listOf(Color(0xFFFB923C), Color(0xFFEA580C))
                                    ) else Brush.linearGradient(
                                        listOf(laranjaClaro, laranjaClaro)
                                    )
                                )
                                .then(
                                    if (ehHoje) Modifier.border(2.dp, laranja, CircleShape)
                                    else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (ativo) {
                                Icon(
                                    imageVector = Lucide.Flame,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(6.dp))

                        Text(
                            text = letra,
                            fontSize = 11.5.sp,
                            fontWeight = if (ehHoje) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (ehHoje) laranja else TextMuted
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Meta de hoje
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = laranjaClaro,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (metaBatida) Lucide.Check else Lucide.Target,
                            contentDescription = null,
                            tint = if (metaBatida) Color(0xFF16A34A) else laranja,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (metaBatida) "Meta de hoje concluída!" else "Meta de hoje",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = TextMain,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "$doneToday/$metaHoje desafios",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (metaBatida) Color(0xFF16A34A) else laranja
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.White)
                    ) {
                        Box(
                            Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(progressoHoje)
                                .clip(RoundedCornerShape(50))
                                .background(
                                    Brush.horizontalGradient(
                                        if (metaBatida) listOf(Color(0xFF16A34A), Color(0xFF4ADE80))
                                        else listOf(Color(0xFFFB923C), Color(0xFFEA580C))
                                    )
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BannerStatChip(
    icon: ImageVector,
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
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(15.dp)
            )
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
    icon: ImageVector,
    iconTint: Color,
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
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
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
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = mission.category.icon,
                                contentDescription = null,
                                tint = GreenOnContainer,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = mission.category.label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GreenOnContainer
                            )
                        }
                    }

                    Text(
                        text = mission.difficulty,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = difColor
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Lucide.Star,
                        contentDescription = null,
                        tint = GoldXP,
                        modifier = Modifier.size(13.dp)
                    )
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
    val tint = when (badge.color) {
        "verde" -> Color(0xFF16A34A)
        "amarelo" -> Color(0xFFD97706)
        "azul" -> Color(0xFF2563EB)
        else -> Color(0xFF7C3AED)
    }
    val vetor = when (badge.id) {
        "b1" -> Lucide.Sprout
        "b2" -> Lucide.Recycle
        "b3" -> Lucide.Droplets
        "b4" -> Lucide.Zap
        else -> Lucide.Award
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
                Icon(
                    imageVector = vetor,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(20.dp)
                )
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