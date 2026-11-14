package com.vibeeco.app.ui.learn

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Dumbbell
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.PartyPopper
import com.composables.icons.lucide.Star
import com.composables.icons.lucide.X
import com.vibeeco.app.data.Article
import com.vibeeco.app.ui.components.GradientHeader
import com.vibeeco.app.ui.components.PrimaryButton
import com.vibeeco.app.ui.theme.GoldXP
import com.vibeeco.app.ui.theme.GoldXPText
import com.vibeeco.app.ui.theme.TextMain
import com.vibeeco.app.ui.theme.TextMuted
import kotlin.math.roundToInt
import kotlinx.coroutines.delay

@Composable
fun ArticleDetailScreen(
    article: Article,
    isRead: Boolean,
    onBack: () -> Unit,
    onMarkRead: () -> Unit,
) {
    var etapa by remember { mutableStateOf(Etapa.Revisao) }
    var indice by remember { mutableIntStateOf(0) }
    var selecionada by remember { mutableStateOf<Int?>(null) }
    var acertos by remember { mutableIntStateOf(0) }
    var respostas by remember { mutableStateOf<List<Int>>(emptyList()) }

    val total = article.quiz.size
    val questaoAtual = article.quiz.getOrNull(indice)
    val respondida = selecionada != null

    if (etapa == Etapa.Revisao) {
        Revisao(
            article = article,
            isRead = isRead,
            onBack = onBack,
            onMarkRead = onMarkRead,
            onIniciarQuiz = if (total > 0) {
                {
                    indice = 0
                    selecionada = null
                    acertos = 0
                    respostas = emptyList()
                    etapa = Etapa.Quiz
                }
            } else null,
        )
        return
    }

    if (etapa == Etapa.Quiz && questaoAtual != null) {
        val porcentagem = if (total == 0) 0 else ((indice + if (respondida) 1 else 0) * 100) / total

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            TextButton(onClick = { etapa = Etapa.Revisao }) {
                Text("← Voltar à revisão", color = TextMuted)
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Questão ${indice + 1} de $total", fontSize = 13.sp, color = TextMuted)
                Text("$porcentagem% concluído", fontSize = 13.sp, color = TextMuted)
            }
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFD6EADB)),
            ) {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(porcentagem / 100f)
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF16A34A), Color(0xFF4ADE80)),
                            ),
                        ),
                )
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        questaoAtual.pergunta,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TextMain,
                    )

                    questaoAtual.opcoes.forEachIndexed { i, opcao ->
                        val estado = when {
                            !respondida -> OpcaoEstado.Neutra
                            i == questaoAtual.correta -> OpcaoEstado.Correta
                            i == selecionada -> OpcaoEstado.Errada
                            else -> OpcaoEstado.Apagar
                        }
                        Opcao(
                            texto = opcao,
                            estado = estado,
                            habilitado = !respondida,
                            onClick = {
                                if (!respondida) {
                                    selecionada = i
                                    respostas = respostas + i
                                    if (i == questaoAtual.correta) acertos++
                                }
                            },
                        )
                    }

                    if (respondida) {
                        val acertou = selecionada == questaoAtual.correta
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (acertou) Color(0xFFECF9F0) else Color(0xFFFEF1F1),
                            border = BorderStroke(
                                1.dp,
                                if (acertou) Color(0xFFBFE3C9) else Color(0xFFF7CACA),
                            ),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(
                                    if (acertou) "Resposta correta" else "Resposta incorreta",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp,
                                    color = if (acertou) Color(0xFF0F6B34) else Color(0xFFB91C1C),
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    questaoAtual.explicacao,
                                    fontSize = 13.sp,
                                    color = if (acertou) Color(0xFF0F6B34) else Color(0xFFB91C1C),
                                )
                            }
                        }

                        PrimaryButton(
                            text = if (indice == total - 1) "Ver resultado" else "Próxima questão",
                            onClick = {
                                if (indice < total - 1) {
                                    indice++
                                    selecionada = null
                                } else {
                                    etapa = Etapa.Resultado
                                }
                            },
                        )
                    }
                }
            }
        }
        return
    }

    // ===== RESULTADO =====
    ResultadoQuiz(
        article = article,
        isRead = isRead,
        acertos = acertos,
        total = total,
        respostas = respostas,
        onVoltar = onBack,
        onRever = { etapa = Etapa.Revisao },
        onRefazer = {
            indice = 0
            selecionada = null
            acertos = 0
            respostas = emptyList()
            etapa = Etapa.Quiz
        },
    )
}

private enum class Etapa { Revisao, Quiz, Resultado }

private enum class OpcaoEstado { Neutra, Correta, Errada, Apagar }

@Composable
private fun Revisao(
    article: Article,
    isRead: Boolean,
    onBack: () -> Unit,
    onMarkRead: () -> Unit,
    onIniciarQuiz: (() -> Unit)?,
) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        GradientHeader {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
            }
            Spacer(Modifier.height(8.dp))
            Text(article.category.emoji, fontSize = 44.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                article.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                lineHeight = 30.sp,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "${article.category.label} · ${article.minutes} min de leitura",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp,
            )
        }

        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            article.body.split("\n\n").forEach {
                Text(it, fontSize = 16.sp, lineHeight = 25.sp, color = TextMain)
            }

            if (onIniciarQuiz != null) {
                PrimaryButton(text = "Começar quiz", onClick = onIniciarQuiz)
            }

            PrimaryButton(
                text = if (isRead) "Lido ✓ — pontos já recebidos" else "Marcar como lido (+${article.points} pts)",
                onClick = onMarkRead,
                enabled = !isRead,
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun Opcao(
    texto: String,
    estado: OpcaoEstado,
    habilitado: Boolean,
    onClick: () -> Unit,
) {
    val estilo: EstiloOpcao = when (estado) {
        OpcaoEstado.Correta -> EstiloOpcao(
            bg = Color(0xFFECF9F0),
            borda = Color(0xFF16A34A),
            cor = Color(0xFF0F6B34),
            marcadorBg = Color(0xFF16A34A),
            icone = Lucide.Check,
        )
        OpcaoEstado.Errada -> EstiloOpcao(
            bg = Color(0xFFFEF1F1),
            borda = Color(0xFFEF4444),
            cor = Color(0xFFB91C1C),
            marcadorBg = Color(0xFFEF4444),
            icone = Lucide.X,
        )
        OpcaoEstado.Apagar -> EstiloOpcao(
            bg = Color(0xFFF5F5F5),
            borda = null,
            cor = Color(0xFF9E9E9E),
            marcadorBg = Color.Transparent,
            icone = null,
        )
        OpcaoEstado.Neutra -> EstiloOpcao(
            bg = Color(0xFFF7F9F7),
            borda = null,
            cor = TextMain,
            marcadorBg = Color.White,
            icone = null,
        )
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = estilo.bg,
        border = estilo.borda?.let { BorderStroke(1.dp, it) },
        modifier = Modifier
            .fillMaxWidth()
            .then(if (habilitado) Modifier.clickable { onClick() } else Modifier),
    ) {
        Row(
            Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(50))
                    .background(estilo.marcadorBg),
                contentAlignment = Alignment.Center,
            ) {
                if (estilo.icone != null) {
                    Icon(
                        estilo.icone,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Text(texto, fontSize = 14.5.sp, fontWeight = FontWeight.Medium, color = estilo.cor)
        }
    }
}

private data class EstiloOpcao(
    val bg: Color,
    val borda: Color?,
    val cor: Color,
    val marcadorBg: Color,
    val icone: ImageVector?,
)

// ============================================================
// TELA DE RESULTADO COM ANIMAÇÃO (SEM EMOJI, SÓ LUCIDE)
// ============================================================

@Composable
private fun ResultadoQuiz(
    article: Article,
    isRead: Boolean,
    acertos: Int,
    total: Int,
    respostas: List<Int>,
    onVoltar: () -> Unit,
    onRever: () -> Unit,
    onRefazer: () -> Unit,
) {
    val aprovado = total > 0 && acertos >= (total * 0.6).toInt()
    val porcentagem = if (total == 0) 0 else (acertos * 100) / total
    val corPrincipal = if (aprovado) Color(0xFF16A34A) else Color(0xFFEA580C)
    val corFundo = if (aprovado) Color(0xFFECF9F0) else Color(0xFFFFF7ED)
    val titulo = if (aprovado) "Conteúdo concluído!" else "Quase lá!"
    val subtitulo = if (aprovado) {
        if (isRead) "Você já tinha concluído este conteúdo antes."
        else "Você ganhou ${article.points} XP por este conteúdo."
    } else {
        "Você precisa de ${(total * 0.6).toInt()} acertos para concluir. Tente de novo!"
    }
    val iconeResultado: ImageVector = if (aprovado) Lucide.PartyPopper else Lucide.Dumbbell
    val corIcone = if (aprovado) Color(0xFF16A34A) else Color(0xFFEA580C)

    // ===== animações de entrada =====
    val iconeScale = remember { Animatable(0.3f) }
    val iconeAlpha = remember { Animatable(0f) }
    val arcoProgresso = remember { Animatable(0f) }
    val contadorPct = remember { Animatable(0f) }
    var mostrarCards by remember { mutableStateOf(false) }
    var mostrarBotoes by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        iconeScale.animateTo(1.15f, tween(280, easing = LinearOutSlowInEasing))
        iconeScale.animateTo(1f, tween(160))
        iconeAlpha.animateTo(1f, tween(240))
        arcoProgresso.animateTo(
            porcentagem / 100f,
            tween(900, easing = LinearOutSlowInEasing),
        )
        contadorPct.animateTo(porcentagem.toFloat(), tween(900))
        mostrarCards = true
        delay(200)
        mostrarBotoes = true
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Spacer(Modifier.height(12.dp))

        // ===== ícone Lucide com pop (em vez de emoji) =====
        Icon(
            imageVector = iconeResultado,
            contentDescription = null,
            tint = corIcone,
            modifier = Modifier
                .size(72.dp)
                .scale(iconeScale.value)
                .graphicsLayer { alpha = iconeAlpha.value },
        )

        // título e subtítulo (fade junto do ícone)
        Text(
            titulo,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            color = TextMain,
            textAlign = TextAlign.Center,
            modifier = Modifier.graphicsLayer { alpha = iconeAlpha.value },
        )
        Text(
            subtitulo,
            fontSize = 14.sp,
            color = TextMuted,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.graphicsLayer { alpha = iconeAlpha.value },
        )

        Spacer(Modifier.height(4.dp))

        // ===== anel animado =====
        Box(
            modifier = Modifier.size(180.dp),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(Modifier.size(180.dp)) {
                val stroke = 14.dp.toPx()
                val raio = (size.minDimension - stroke) / 2f
                val centro = center

                drawCircle(
                    color = corFundo,
                    radius = raio,
                    center = centro,
                    style = Stroke(width = stroke),
                )

                drawArc(
                    color = corPrincipal,
                    startAngle = -90f,
                    sweepAngle = 360f * arcoProgresso.value,
                    useCenter = false,
                    topLeft = Offset(centro.x - raio, centro.y - raio),
                    size = Size(raio * 2, raio * 2),
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "${contadorPct.value.roundToInt()}%",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = corPrincipal,
                    lineHeight = 44.sp,
                )
                Text(
                    "de acerto",
                    fontSize = 12.sp,
                    color = TextMuted,
                )
            }
        }

        // ===== card do placar =====
        AnimatedVisibility(
            visible = mostrarCards,
            enter = fadeIn(tween(350)) + slideInVertically(
                initialOffsetY = { it / 4 },
                animationSpec = tween(350),
            ),
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("Acertos", fontSize = 13.sp, color = TextMuted)
                        Text(
                            "$acertos de $total",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMain,
                        )
                    }

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(50)),
                    ) {
                        val fracAcerto = if (total == 0) 0f else acertos / total.toFloat()
                        Box(
                            Modifier
                                .weight(fracAcerto.coerceAtLeast(0.0001f))
                                .fillMaxHeight()
                                .background(Color(0xFF16A34A)),
                        )
                        val fracErro = if (total == 0) 1f else (total - acertos) / total.toFloat()
                        Box(
                            Modifier
                                .weight(fracErro.coerceAtLeast(0.0001f))
                                .fillMaxHeight()
                                .background(Color(0xFFEF4444)),
                        )
                    }

                    if (aprovado && !isRead) {
                        Spacer(Modifier.height(4.dp))
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                Lucide.Star,
                                contentDescription = null,
                                tint = GoldXP,
                                modifier = Modifier.size(22.dp),
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "+${article.points} XP",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                color = GoldXPText,
                            )
                        }
                    }
                }
            }
        }

        // ===== revisão das questões =====
        if (total > 0) {
            AnimatedVisibility(
                visible = mostrarCards,
                enter = fadeIn(tween(450)) + slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(450),
                ),
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(
                            "Revisão das questões",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextMain,
                        )

                        article.quiz.forEachIndexed { i, q ->
                            val minha = respostas.getOrNull(i)
                            val acertou = minha == q.correta
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top,
                            ) {
                                Box(
                                    Modifier
                                        .size(22.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(
                                            if (acertou) Color(0xFF16A34A) else Color(0xFFEF4444)
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        if (acertou) Lucide.Check else Lucide.X,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(13.dp),
                                    )
                                }
                                Spacer(Modifier.width(10.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        "${i + 1}. ${q.pergunta}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextMain,
                                        lineHeight = 18.sp,
                                    )
                                    if (!acertou) {
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            "Resposta correta: ${q.opcoes[q.correta]}",
                                            fontSize = 12.sp,
                                            color = Color(0xFF0F6B34),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // ===== botões =====
        AnimatedVisibility(
            visible = mostrarBotoes,
            enter = fadeIn(tween(300)) + slideInVertically(
                initialOffsetY = { it / 6 },
                animationSpec = tween(300),
            ),
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .widthIn(max = 360.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                PrimaryButton(text = "Voltar aos conteúdos", onClick = onVoltar)

                OutlinedButton(
                    onClick = onRefazer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFF16A34A)),
                ) {
                    Text("Refazer quiz", color = Color(0xFF16A34A), fontWeight = FontWeight.SemiBold)
                }

                TextButton(
                    onClick = onRever,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Rever conteúdo", color = TextMuted)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}