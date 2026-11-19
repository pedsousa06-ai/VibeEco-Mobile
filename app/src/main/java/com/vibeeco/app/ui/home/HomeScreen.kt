package com.vibeeco.app.ui.home

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vibeeco.app.data.*
import com.vibeeco.app.data.api.model.ConquistaInicioDto
import com.vibeeco.app.data.api.model.ConteudoInicioDto
import com.vibeeco.app.data.api.model.UsuarioInicioDto
import com.vibeeco.app.ui.components.*
import com.vibeeco.app.ui.theme.*
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale

private val VerdeEscuro = Color(0xFF0C4A25)
private val VerdeClaro = Color(0xFF16A34A)
private val VerdeSuave = Color(0xFFDCF5E5)
private val Laranja = Color(0xFFEA580C)
private val LaranjaClaro = Color(0xFFFB923C)
private val Dourado = Color(0xFFD97706)
private val DouradoSuave = Color(0xFFFEF3C7)
private val Roxo = Color(0xFF7C3AED)
private val RoxoSuave = Color(0xFFEDE9FE)

private val formatoNumero: NumberFormat = NumberFormat.getIntegerInstance(Locale("pt", "BR"))

private fun formatarNumero(valor: Any?): String = when (valor) {
    is Number -> formatoNumero.format(valor)
    null -> "0"
    else -> valor.toString()
}

@Composable
fun HomeScreen(
    usuario: UsuarioInicioDto?,
    streak: Int,
    desafios: List<Challenge>,
    missoes: List<MissaoUi>,
    conteudos: List<ConteudoInicioDto>,
    conquistas: List<ConquistaInicioDto>,
    onSeeAll: () -> Unit,
    onSeeAllMissions: () -> Unit,
    onOpenMission: (String) -> Unit,
    onOpenProfile: () -> Unit,
    onOpenLearn: () -> Unit,
    onOpenNotifications: () -> Unit,
    unreadNotifications: Int = 0,
) {
    val hour = LocalTime.now().hour
    val saudacao = when {
        hour < 12 -> "Bom dia,"
        hour < 18 -> "Boa tarde,"
        else -> "Boa noite,"
    }

    val nome = usuario?.nomeExibicao.orEmpty()
    val primeiroNome = nome.trim().split(" ").firstOrNull().orEmpty()
    val xp = usuario?.xpExibicao ?: 0
    val moedas = usuario?.coinsExibicao ?: 0

    val emAndamento = missoes.filter { it.status == StatusMissao.Andamento }

    val percentual = usuario?.progressoPercentual
        ?.toString()
        ?.replace(",", ".")
        ?.toFloatOrNull()
        ?.coerceIn(0f, 100f) ?: 0f

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundApp),
        contentPadding = PaddingValues(bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        // ---------- CABEÇALHO ----------
        item(key = "header") {
            HomeHeader(
                saudacao = saudacao,
                nome = nome,
                primeiroNome = primeiroNome,
                streak = streak,
                xp = xp,
                moedas = moedas,
                nivel = usuario?.nivelAtual,
                nomeNivel = usuario?.nomeLevel.orEmpty(),
                proximoNivel = usuario?.nomeProximoLevel.orEmpty(),
                percentual = percentual,
                xpFaltante = usuario?.xpFaltante,
                unreadNotifications = unreadNotifications,
                onOpenNotifications = onOpenNotifications,
                onOpenProfile = onOpenProfile,
            )
        }

        // ---------- RESUMO ----------
        item(key = "resumo") {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ResumoCard(
                        Lucide.Target, VerdeClaro, VerdeSuave,
                        "${usuario?.qtdMissoesAndamento ?: emAndamento.size}", "Missões em andamento",
                        modifier = Modifier.weight(1f).fillMaxHeight().clickable { onSeeAllMissions() }
                    )
                    ResumoCard(
                        Lucide.Medal, Dourado, DouradoSuave,
                        "${usuario?.qtdMissoesConcluidas ?: 0}", "Missões concluídas",
                        modifier = Modifier.weight(1f).fillMaxHeight().clickable { onSeeAllMissions() }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ResumoCard(
                        Lucide.Zap, Roxo, RoxoSuave,
                        "${usuario?.qtdDesafiosAtivos ?: desafios.size}", "Desafios ativos",
                        modifier = Modifier.weight(1f).fillMaxHeight().clickable { onSeeAll() }
                    )
                    ResumoCard(
                        Lucide.Trophy, Laranja, Color(0xFFFFEDD5),
                        usuario?.posicaoRanking?.let { "#$it" } ?: "—",
                        "Posição no ranking",
                        detail = usuario?.rankingTopPercentual?.let { "Top $it%" },
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                }
            }
        }

        // ---------- OFENSIVA ----------
        item(key = "streak") {
            StreakCard(streak = streak, modifier = Modifier.padding(horizontal = 16.dp))
        }

        // ---------- MISSÕES ----------
        item(key = "missoes_titulo") {
            SectionTitle("Missões em andamento", Modifier.padding(horizontal = 16.dp), "Ver todas", onSeeAllMissions)
        }

        if (emAndamento.isEmpty()) {
            item(key = "missoes_vazio") {
                VibeCard(
                    modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                    onClick = onSeeAllMissions,
                ) {
                    Text(
                        "Nenhuma missão em andamento. Toque para escolher uma!",
                        modifier = Modifier.padding(18.dp), fontSize = 13.sp, color = TextMuted,
                    )
                }
            }
        } else {
            items(emAndamento.take(3), key = { "missao_${it.missao.id}" }) { ui ->
                MissionCard(
                    ui = ui,
                    onClick = { onOpenMission(ui.missao.id) },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }

        // ---------- DESAFIOS ----------
        item(key = "desafios_titulo") {
            SectionTitle("Desafios em destaque", Modifier.padding(horizontal = 16.dp), "Ver todos", onSeeAll)
        }

        if (desafios.isEmpty()) {
            item(key = "desafios_vazio") {
                VibeCard(
                    modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                    onClick = onSeeAll,
                ) {
                    Text(
                        "Nenhum desafio disponível agora. Volte mais tarde!",
                        modifier = Modifier.padding(18.dp), fontSize = 13.sp, color = TextMuted,
                    )
                }
            }
        } else {
            items(desafios.take(3), key = { "desafio_${it.id}" }) { desafio ->
                DesafioCard(
                    desafio = desafio,
                    onClick = onSeeAll,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }

        // ---------- CONTEÚDOS ----------
        item(key = "conteudos_titulo") {
            SectionTitle("Conteúdos recomendados", Modifier.padding(horizontal = 16.dp), "Ver todos", onOpenLearn)
        }

        if (conteudos.isEmpty()) {
            item(key = "conteudos_vazio") {
                VibeCard(
                    modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                    onClick = onOpenLearn,
                ) {
                    Text(
                        "Nenhum conteúdo recomendado ainda. Toque para explorar!",
                        modifier = Modifier.padding(18.dp), fontSize = 13.sp, color = TextMuted,
                    )
                }
            }
        } else {
            item(key = "conteudos_lista") {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(conteudos, key = { "conteudo_${it.id}" }) { conteudo ->
                        VibeCard(
                            modifier = Modifier
                                .width(244.dp)
                                .height(136.dp)
                                .clickable(onClick = onOpenLearn)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(14.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(shape = RoundedCornerShape(50), color = GreenContainer) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Lucide.BookOpen, null, tint = GreenOnContainer, modifier = Modifier.size(12.dp))
                                            Spacer(Modifier.width(4.dp))
                                            Text("Aprender", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GreenOnContainer)
                                        }
                                    }
                                    conteudo.qtdeXp?.let {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Lucide.Star, null, tint = GoldXP, modifier = Modifier.size(13.dp))
                                            Spacer(Modifier.width(3.dp))
                                            Text("$it XP", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = GoldXPText)
                                        }
                                    }
                                }

                                Text(
                                    conteudo.nome.orEmpty(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    lineHeight = 19.sp,
                                    color = TextMain,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    conteudo.duracaoLeitura?.let {
                                        Icon(Lucide.Clock, null, tint = TextMuted, modifier = Modifier.size(13.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("$it min de leitura", fontSize = 11.5.sp, color = TextMuted)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ---------- CONQUISTAS ----------
        item(key = "conquistas") {
            VibeCard(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
                Column(Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Lucide.Award, null, tint = Dourado, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Conquistas recentes", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextMain)
                    }
                    Spacer(Modifier.height(14.dp))

                    if (conquistas.isEmpty()) {
                        Text(
                            "Complete missões e desafios para desbloquear suas primeiras conquistas.",
                            fontSize = 13.sp, color = TextMuted, lineHeight = 18.sp
                        )
                    } else {
                        conquistas.take(6).chunked(2).forEachIndexed { linha, par ->
                            if (linha > 0) Spacer(Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                par.forEachIndexed { coluna, conquista ->
                                    AchievementItem(
                                        conquista = conquista,
                                        indice = linha * 2 + coluna,
                                        modifier = Modifier.weight(1f).fillMaxHeight()
                                    )
                                }
                                if (par.size == 1) Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

// =====================================================================
// Cabeçalho
// =====================================================================

@Composable
private fun HomeHeader(
    saudacao: String,
    nome: String,
    primeiroNome: String,
    streak: Int,
    xp: Any,
    moedas: Any,
    nivel: Any?,
    nomeNivel: String,
    proximoNivel: String,
    percentual: Float,
    xpFaltante: Any?,
    unreadNotifications: Int,
    onOpenNotifications: () -> Unit,
    onOpenProfile: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(listOf(VerdeEscuro, Color(0xFF117A3D), Color(0xFF1FA856))),
                RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp)
            )
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(saudacao, color = Color.White.copy(alpha = 0.85f), fontSize = 13.5.sp)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        if (primeiroNome.isNotBlank()) "$primeiroNome!" else "Visitante",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        letterSpacing = (-0.4).sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        if (streak > 0) "Você está em uma ofensiva de $streak ${if (streak == 1) "dia" else "dias"}. Continue assim!"
                        else "Continue sua jornada sustentável!",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp
                    )
                }

                Box {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.16f))
                            .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape)
                            .clickable(onClick = onOpenNotifications),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Lucide.Bell, "Notificações", tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                    if (unreadNotifications > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 4.dp, y = (-4).dp)
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFDC2626))
                                .border(1.5.dp, VerdeEscuro, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (unreadNotifications > 9) "9+" else "$unreadNotifications",
                                color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(Modifier.width(10.dp))
                Avatar(
                    name = nome,
                    size = 52.dp,
                    modifier = Modifier
                        .clickable(onClick = onOpenProfile)
                        .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BannerStatChip(Lucide.Star, Color(0xFFFDE68A), "${formatarNumero(xp)} XP", Modifier.weight(1f))
                BannerStatChip(Lucide.Coins, Color(0xFFA7F3D0), "${formatarNumero(moedas)} MC", Modifier.weight(1f))
                BannerStatChip(Lucide.Flame, Color(0xFFFDBA74), "$streak ${if (streak == 1) "dia" else "dias"}", Modifier.weight(1f))
            }

            Spacer(Modifier.height(14.dp))

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
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Lucide.Award, null, tint = Color.White, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                if (nivel != null && nomeNivel.isNotBlank()) "Nível $nivel - $nomeNivel" else "Nível não informado",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "${percentual.toInt()}%",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.White.copy(alpha = 0.25f))
                    ) {
                        Box(
                            Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(percentual / 100f)
                                .clip(RoundedCornerShape(50))
                                .background(Brush.horizontalGradient(listOf(Color(0xFF86EFAC), Color.White)))
                        )
                    }

                    if (proximoNivel.isNotBlank() || xpFaltante != null) {
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                if (proximoNivel.isNotBlank()) "Próximo: $proximoNivel" else "",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 11.5.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            xpFaltante?.let {
                                Text(
                                    "Faltam ${formatarNumero(it)} XP",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// =====================================================================
// Ofensiva
// =====================================================================

@Composable
private fun StreakCard(streak: Int, modifier: Modifier = Modifier) {
    val cinzaClaro = Color(0xFFF3F4F6)
    val dias = listOf("S", "T", "Q", "Q", "S", "S", "D")
    val hojeIdx = LocalDate.now().dayOfWeek.value - 1
    val diasAtivosNaSemana = streak.coerceAtMost(hojeIdx + 1)

    VibeCard(modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Brush.linearGradient(listOf(LaranjaClaro, Laranja)), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    ChamaOfensiva(tamanho = 26.dp, cor = Color.White, animada = true)
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text("Ofensiva diária", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextMain)
                    Text(
                        if (streak > 0) "Continue assim para manter a sequência"
                        else "Complete um desafio hoje para começar",
                        fontSize = 12.sp, color = TextMuted
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "$streak",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (streak > 0) Laranja else TextMuted,
                        lineHeight = 32.sp
                    )
                    Text(if (streak == 1) "dia" else "dias", fontSize = 11.5.sp, color = TextMuted)
                }
            }
            Spacer(Modifier.height(18.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                dias.forEachIndexed { index, letra ->
                    val ehHoje = index == hojeIdx
                    val ativo = index <= hojeIdx && index > hojeIdx - diasAtivosNaSemana

                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    if (ativo) Brush.linearGradient(listOf(LaranjaClaro, Laranja))
                                    else Brush.linearGradient(listOf(cinzaClaro, cinzaClaro))
                                )
                                .then(if (ehHoje) Modifier.border(2.dp, Laranja, CircleShape) else Modifier),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (ativo) ChamaOfensiva(tamanho = 18.dp, cor = Color.White, animada = ehHoje)
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            letra,
                            fontSize = 11.5.sp,
                            fontWeight = if (ehHoje) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (ehHoje) Laranja else TextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChamaOfensiva(
    tamanho: Dp,
    cor: Color,
    animada: Boolean = false,
    modifier: Modifier = Modifier,
) {
    if (!animada) {
        Icon(Lucide.Flame, contentDescription = null, tint = cor, modifier = modifier.size(tamanho))
        return
    }

    val transicao = rememberInfiniteTransition(label = "chama")

    val escalaY by transicao.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.10f,
        animationSpec = infiniteRepeatable(
            animation = tween(560, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "escalaY",
    )
    val escalaX by transicao.animateFloat(
        initialValue = 1.05f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(560, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "escalaX",
    )
    val giro by transicao.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "giro",
    )

    Icon(
        Lucide.Flame,
        contentDescription = null,
        tint = cor,
        modifier = modifier
            .size(tamanho)
            .graphicsLayer {
                scaleX = escalaX
                scaleY = escalaY
                rotationZ = giro
                transformOrigin = TransformOrigin(0.5f, 1f)
            },
    )
}

// =====================================================================
// Componentes
// =====================================================================

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
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.22f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, null, tint = iconColor, modifier = Modifier.size(15.dp))
            Spacer(Modifier.width(5.dp))
            Text(
                label,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.5.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(44.dp).background(iconBg, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconTint, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = TextMain, lineHeight = 22.sp, maxLines = 1)
                Text(label, fontSize = 11.5.sp, color = TextMuted, lineHeight = 14.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                if (detail != null) {
                    Text(detail, fontSize = 10.5.sp, color = VerdeClaro, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun DesafioCard(
    desafio: Challenge,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    VibeCard(modifier = modifier.fillMaxWidth(), onClick = onClick) {
        Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(44.dp).background(DouradoSuave, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Lucide.Trophy, null, tint = Dourado, modifier = Modifier.size(22.dp))
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    desafio.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.5.sp,
                    color = TextMain,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(3.dp))
                Text("Toque para ver detalhes", fontSize = 11.5.sp, color = TextMuted)
            }

            Spacer(Modifier.width(10.dp))

            Surface(shape = RoundedCornerShape(50), color = DouradoSuave) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Lucide.Coins, null, tint = Dourado, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("+${desafio.points}", color = GoldXPText, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun MissionCard(ui: MissaoUi, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val mission = ui.missao
    val difColor = when (mission.difficulty.lowercase()) {
        "fácil", "facil" -> VerdeClaro
        "médio", "medio" -> Dourado
        else -> Color(0xFFDC2626)
    }
    val progresso = ui.progresso.coerceIn(0, 100)

    VibeCard(modifier = modifier.fillMaxWidth(), onClick = onClick) {
        Column(Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(shape = RoundedCornerShape(50), color = GreenContainer) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Lucide.Target, null, tint = GreenOnContainer, modifier = Modifier.size(12.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Missão", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = GreenOnContainer)
                        }
                    }
                    Text(mission.difficulty, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = difColor)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Lucide.Star, null, tint = GoldXP, modifier = Modifier.size(13.dp))
                    Spacer(Modifier.width(3.dp))
                    Text("+${mission.xp} XP", color = GoldXPText, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                mission.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.5.sp,
                color = TextMain,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Progresso", fontSize = 11.5.sp, color = TextMuted)
                Text("$progresso%", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = TextMain)
            }

            Spacer(Modifier.height(5.dp))

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
                        .fillMaxWidth(progresso / 100f)
                        .clip(RoundedCornerShape(50))
                        .background(Brush.horizontalGradient(listOf(VerdeClaro, Color(0xFF4ADE80))))
                )
            }
        }
    }
}

@Composable
private fun AchievementItem(
    conquista: ConquistaInicioDto,
    indice: Int,
    modifier: Modifier = Modifier
) {
    val paletas = listOf(
        VerdeSuave to VerdeClaro,
        DouradoSuave to Dourado,
        Color(0xFFDBEAFE) to Color(0xFF2563EB),
        RoxoSuave to Roxo,
    )
    val icones = listOf(Lucide.Sprout, Lucide.Recycle, Lucide.Droplets, Lucide.Zap, Lucide.Award)
    val (bgColor, tint) = paletas[indice % paletas.size]
    val vetor = icones[indice % icones.size]
    val descricao = conquista.descricao

    Surface(shape = RoundedCornerShape(12.dp), color = BackgroundApp, modifier = modifier) {
        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(36.dp).background(bgColor, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(vetor, null, tint = tint, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    conquista.nome.orEmpty(),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = TextMain,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (!descricao.isNullOrBlank()) {
                    Text(
                        descricao,
                        fontSize = 10.5.sp,
                        lineHeight = 13.sp,
                        color = TextMuted,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}