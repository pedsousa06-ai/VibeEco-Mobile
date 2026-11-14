package com.vibeeco.app.ui.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Bell
import com.composables.icons.lucide.CalendarDays
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Coins
import com.composables.icons.lucide.Eye
import com.composables.icons.lucide.GraduationCap
import com.composables.icons.lucide.History
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.KeyRound
import com.composables.icons.lucide.Lock
import com.composables.icons.lucide.LogOut
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MapPin
import com.composables.icons.lucide.Settings
import com.composables.icons.lucide.ShieldCheck
import com.composables.icons.lucide.Sparkles
import com.composables.icons.lucide.Star
import com.composables.icons.lucide.Target
import com.composables.icons.lucide.TrendingUp
import com.composables.icons.lucide.User
import com.vibeeco.app.data.Progress
import com.vibeeco.app.data.User
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

/* ------------------------------------------------------------------ */
/*  CONSTANTES                                                         */
/* ------------------------------------------------------------------ */
private val ABAS = listOf(
    "todas" to "Todas",
    "disponivel" to "Disponíveis",
    "andamento" to "Em andamento",
    "concluida" to "Concluídas",
)
private val ROTULO_BOTAO = mapOf(
    "andamento" to "Continuar",
    "disponivel" to "Começar",
    "concluida" to "Ver detalhes",
)

private fun percentualCompleto(p: PerfilUsuario): Int {
    val campos = listOf(p.nome, p.nomePublico, p.email, p.matricula, p.curso, p.localizacao, p.bio, p.foto)
    return (campos.count { it.trim().isNotEmpty() } * 100f / campos.size).roundToInt()
}

/* ------------------------------------------------------------------ */
/*  TELA                                                               */
/* ------------------------------------------------------------------ */
@Composable
fun ProfileScreen(
    // Compatibilidade com AuthPreviews (ignorados)
    @Suppress("UNUSED_PARAMETER") user: User? = null,
    @Suppress("UNUSED_PARAMETER") progress: Progress? = null,
    @Suppress("UNUSED_PARAMETER") notifications: Boolean = true,
    @Suppress("UNUSED_PARAMETER") onNotificationsChange: (Boolean) -> Unit = {},
    onChangePassword: (() -> Unit)? = null,
    onLogout: (() -> Unit)? = null,
    // Novos parâmetros
    onAlterarSenha: () -> Unit = {},
    onSair: () -> Unit = {},
    onVoltar: (() -> Unit)? = null,
    onAbrirMissao: (String) -> Unit = {},
    // Atalhos do topo
    onAbrirConfiguracoes: () -> Unit = {},
    onAbrirHistorico: () -> Unit = {},
    onAbrirNotificacoes: () -> Unit = {},
) {
    val acaoSenha = onChangePassword ?: onAlterarSenha
    val acaoSair = onLogout ?: onSair
    val perfil = PerfilStore.perfil

    var form by remember(perfil) { mutableStateOf(FormPerfil.de(perfil)) }
    var salvo by remember { mutableStateOf(false) }
    var periodo by remember { mutableStateOf("30") }
    var aba by remember { mutableStateOf("todas") }

    val alterado = form != FormPerfil.de(perfil)
    val valido = form.nome.isNotBlank()

    LaunchedEffect(salvo) {
        if (salvo) {
            delay(3000)
            salvo = false
        }
    }

    Column(Modifier.fillMaxSize().background(PC.Pagina)) {
        // ---------- TOPO COM ATALHOS (engrenagem, histórico, notificações) ----------
        BarraTopo(
            titulo = "Meu perfil",
            onVoltar = onVoltar,
            acoes = {
                BotaoIcone(
                    icone = Lucide.Settings,
                    descricao = "Configurações",
                    onClick = onAbrirConfiguracoes,
                    contorno = true,
                )
                Spacer(Modifier.width(6.dp))
                BotaoIcone(
                    icone = Lucide.History,
                    descricao = "Histórico",
                    onClick = onAbrirHistorico,
                    contorno = true,
                )
                Spacer(Modifier.width(6.dp))
                BotaoIcone(
                    icone = Lucide.Bell,
                    descricao = "Notificações",
                    onClick = onAbrirNotificacoes,
                    contorno = true,
                    badge = NotificacoesStore.naoLidas,
                )
            },
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 16.dp)
                .padding(top = 4.dp, bottom = 24.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Revelar(0) { CabecalhoPagina() }

            Revelar(80) { CartaoIdentidade(perfil) }

            Revelar(0) {
                CartaoEdicao(
                    form = form,
                    perfil = perfil,
                    alterado = alterado,
                    valido = valido,
                    salvo = salvo,
                    onForm = { form = it },
                    onCancelar = { form = FormPerfil.de(perfil) },
                    onSalvar = {
                        if (valido) {
                            PerfilStore.atualizar(form)
                            salvo = true
                        }
                    },
                )
            }

            Revelar(0) { CartaoVisibilidade() }

            Revelar(0) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    TituloSecao("Desempenho e conquistas", "Sua jornada sustentável em números")
                    Segmentos(PERIODOS, periodo, { periodo = it })
                }
            }

            Crossfade(targetState = periodo, animationSpec = tween(350), label = "periodo") { p ->
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    val rotulo = PERIODOS.firstOrNull { it.first == p }?.second.orEmpty()
                    obterDesempenho(p).chunked(2).forEach { par ->
                        Row(
                            Modifier.fillMaxWidth().height(IntrinsicSize.Max),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            par.forEach { c ->
                                CartaoDesempenhoView(c, rotulo, Modifier.weight(1f).fillMaxHeight())
                            }
                        }
                    }
                }
            }

            Revelar(0) { ativo -> GraficoXp(ativo) }

            CartaoConquistas()

            Revelar(0) {
                SecaoMissoes(aba, { aba = it }, onAbrirMissao)
            }

            Revelar(0) { CartaoConta(acaoSenha, acaoSair) }
        }
    }
}

/* ------------------------------------------------------------------ */
/*  ANIMAÇÃO DE REVELAÇÃO                                              */
/* ------------------------------------------------------------------ */
@Composable
private fun Revelar(
    atraso: Int = 0,
    modifier: Modifier = Modifier,
    conteudo: @Composable (Boolean) -> Unit,
) {
    var visivel by remember { mutableStateOf(false) }
    val alturaTela = LocalConfiguration.current.screenHeightDp * LocalDensity.current.density
    val t by animateFloatAsState(
        targetValue = if (visivel) 1f else 0f,
        animationSpec = tween(550, delayMillis = atraso, easing = FastOutSlowInEasing),
        label = "revelar",
    )
    Box(
        modifier
            .onGloballyPositioned {
                if (!visivel && it.positionInWindow().y < alturaTela * 0.92f) visivel = true
            }
            .graphicsLayer {
                alpha = t
                translationY = (1f - t) * 28.dp.toPx()
            },
    ) { conteudo(visivel) }
}

@Composable
private fun TituloSecao(titulo: String, subtitulo: String) {
    Column {
        Text(titulo, color = PC.Texto, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        Text(subtitulo, color = PC.TextoSuave, fontSize = 13.sp, modifier = Modifier.padding(top = 2.dp))
    }
}

/* ------------------------------------------------------------------ */
/*  CABEÇALHO DA PÁGINA                                                */
/* ------------------------------------------------------------------ */
@Composable
private fun CabecalhoPagina() {
    val pulso = rememberInfiniteTransition(label = "pulso")
    val alfa by pulso.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1100), RepeatMode.Reverse),
        label = "pulsoAlfa",
    )
    Column(Modifier.fillMaxWidth()) {
        Text("Dados e progresso do perfil", color = PC.Texto, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        Text(
            "Gerencie suas informações, acompanhe sua evolução e celebre cada impacto positivo.",
            color = PC.TextoSuave, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp),
        )
        Row(Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(8.dp).graphicsLayer { this.alpha = alfa }
                    .clip(CircleShape).background(PC.Verde600),
            )
            Spacer(Modifier.width(8.dp))
            Text("Dados atualizados agora", color = PC.TextoSuave, fontSize = 12.sp)
        }
    }
}

/* ------------------------------------------------------------------ */
/*  IDENTIDADE                                                         */
/* ------------------------------------------------------------------ */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CartaoIdentidade(perfil: PerfilUsuario) {
    val completo = percentualCompleto(perfil)

    var avatarOk by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { avatarOk = true }
    val escala by animateFloatAsState(
        targetValue = if (avatarOk) 1f else 0.5f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "avatar",
    )

    val progresso = remember { Animatable(0f) }
    LaunchedEffect(completo) {
        progresso.animateTo(completo / 100f, tween(1300, delayMillis = 350, easing = FastOutSlowInEasing))
    }

    Cartao {
        Box(Modifier.fillMaxWidth()) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(88.dp)
                    .background(Brush.linearGradient(PC.Gradiente))
                    .drawBehind {
                        drawCircle(Color.White.copy(alpha = 0.12f), 70.dp.toPx(), Offset(size.width * 0.9f, 4.dp.toPx()))
                        drawCircle(Color.White.copy(alpha = 0.10f), 46.dp.toPx(), Offset(size.width * 0.12f, 80.dp.toPx()))
                    },
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 40.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    Modifier.size(100.dp).scale(escala).clip(CircleShape).background(Color.White),
                    contentAlignment = Alignment.Center,
                ) { AvatarPerfil(perfil.iniciais, 90.dp, 30) }

                Spacer(Modifier.height(12.dp))
                Text(
                    perfil.nome.ifBlank { "—" }, color = PC.Texto, fontSize = 21.sp,
                    fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center,
                )
                Row(Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Lucide.GraduationCap, null, tint = PC.TextoSuave, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "${perfil.curso} • ${perfil.instituicao}", color = PC.TextoSuave, fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                    )
                }

                FlowRow(
                    modifier = Modifier.padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Selo(Lucide.Sparkles, perfil.nivel)
                    Selo(Lucide.MapPin, perfil.localizacao)
                    Selo(Lucide.CalendarDays, "Desde ${perfil.desde}")
                }

                Spacer(Modifier.height(18.dp))
                DivisorLinha()
                Spacer(Modifier.height(18.dp))

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Box(Modifier.size(80.dp), contentAlignment = Alignment.Center) {
                        Canvas(Modifier.size(80.dp)) {
                            val w = 6.dp.toPx()
                            val topo = Offset(w / 2, w / 2)
                            val tam = Size(size.width - w, size.height - w)
                            drawArc(
                                color = PC.Verde100, startAngle = 0f, sweepAngle = 360f, useCenter = false,
                                topLeft = topo, size = tam, style = Stroke(w),
                            )
                            drawArc(
                                color = PC.Verde700, startAngle = -90f, sweepAngle = 360f * progresso.value,
                                useCenter = false, topLeft = topo, size = tam,
                                style = Stroke(w, cap = StrokeCap.Round),
                            )
                        }
                        Text(
                            "${(progresso.value * 100).roundToInt()}%",
                            color = PC.Texto, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold,
                        )
                    }
                    Column(Modifier.weight(1f, fill = false)) {
                        if (completo < 100) {
                            Text("Seu perfil está quase pronto", color = PC.Texto, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(
                                "Adicione uma foto para chegar a 100% e ganhar 50 XP.",
                                color = PC.TextoSuave, fontSize = 12.5.sp, modifier = Modifier.padding(top = 2.dp),
                            )
                        } else {
                            Text("Perfil completo!", color = PC.Texto, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(
                                "Você preencheu todas as informações",
                                color = PC.TextoSuave, fontSize = 12.5.sp, modifier = Modifier.padding(top = 2.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Selo(icone: ImageVector, texto: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(PC.Verde100)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(icone, null, tint = PC.Verde700, modifier = Modifier.size(13.dp))
        Text(texto, color = PC.Verde900, fontSize = 12.5.sp, fontWeight = FontWeight.Medium)
    }
}

/* ------------------------------------------------------------------ */
/*  EDIÇÃO DO PERFIL                                                   */
/* ------------------------------------------------------------------ */
@Composable
private fun CartaoEdicao(
    form: FormPerfil,
    perfil: PerfilUsuario,
    alterado: Boolean,
    valido: Boolean,
    salvo: Boolean,
    onForm: (FormPerfil) -> Unit,
    onCancelar: () -> Unit,
    onSalvar: () -> Unit,
) {
    Cartao {
        CabecalhoCartao(Lucide.User, "Edição do perfil", "Atualize as informações exibidas para a comunidade")
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            CampoTexto("Nome completo", form.nome, { onForm(form.copy(nome = it)) })
            CampoTexto(
                "Nome público", form.nomePublico, { onForm(form.copy(nomePublico = it)) },
                nota = "Usado no ranking e nas interações.",
            )
            CampoTexto(
                "E-mail institucional", perfil.email, {},
                habilitado = false, etiqueta = "Não editável", teclado = KeyboardType.Email,
            )
            CampoTexto("Matrícula", perfil.matricula, {}, habilitado = false, etiqueta = "Não editável")
            CampoTexto("Curso", form.curso, { onForm(form.copy(curso = it)) })
            CampoTexto("Localização", form.localizacao, { onForm(form.copy(localizacao = it)) })
            CampoTexto(
                "Bio", form.bio, { onForm(form.copy(bio = it.take(160))) },
                nota = "${form.bio.length} de 160 caracteres",
            )

            AnimatedVisibility(
                visible = salvo,
                enter = fadeIn(tween(250)) + expandVertically(),
                exit = fadeOut(tween(250)) + shrinkVertically(),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        Modifier.size(20.dp).clip(CircleShape).background(PC.Verde600),
                        contentAlignment = Alignment.Center,
                    ) { Icon(Lucide.Check, null, tint = Color.White, modifier = Modifier.size(13.dp)) }
                    Text("Perfil atualizado", color = PC.Verde700, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BotaoGradiente("Cancelar", Modifier.weight(1f), suave = true, habilitado = alterado, onClick = onCancelar)
                BotaoGradiente("Salvar alterações", Modifier.weight(1f), habilitado = alterado && valido, onClick = onSalvar)
            }
        }
    }
}

@Composable
private fun CampoTexto(
    rotulo: String,
    valor: String,
    onChange: (String) -> Unit,
    habilitado: Boolean = true,
    etiqueta: String? = null,
    nota: String? = null,
    teclado: KeyboardType = KeyboardType.Text,
) {
    var foco by remember { mutableStateOf(false) }
    val corBorda by animateColorAsState(if (foco) PC.Verde600 else PC.Borda, tween(200), label = "borda")
    val forma = RoundedCornerShape(10.dp)

    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(rotulo, color = PC.Texto, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            if (etiqueta != null) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Lucide.Lock, null, tint = PC.TextoSuave, modifier = Modifier.size(11.dp))
                    Text(etiqueta, color = PC.TextoSuave, fontSize = 11.5.sp)
                }
            }
        }
        BasicTextField(
            value = valor,
            onValueChange = onChange,
            enabled = habilitado,
            singleLine = true,
            textStyle = TextStyle(fontSize = 14.sp, color = if (habilitado) PC.Texto else PC.TextoSuave),
            cursorBrush = SolidColor(PC.Verde600),
            keyboardOptions = KeyboardOptions(keyboardType = teclado),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .onFocusChanged { foco = it.isFocused },
            decorationBox = { campo ->
                Box(
                    Modifier
                        .fillMaxSize()
                        .clip(forma)
                        .background(if (habilitado) Color.White else PC.Fundo)
                        .border(if (foco) 2.dp else 1.dp, corBorda, forma)
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart,
                ) { campo() }
            },
        )
        if (nota != null) {
            Text(nota, color = PC.TextoSuave, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

/* ------------------------------------------------------------------ */
/*  VISIBILIDADE                                                       */
/* ------------------------------------------------------------------ */
private data class LinhaVis(
    val titulo: String,
    val descricao: String,
    val valor: Boolean,
    val aoMudar: (Boolean) -> Unit,
)

@Composable
private fun CartaoVisibilidade() {
    val linhas = listOf(
        LinhaVis("Perfil público", "Qualquer pessoa pode ver seu perfil.", configuracoes.perfilPublico) { configuracoes.perfilPublico = it },
        LinhaVis("Aparecer no ranking", "Mostre seu nome no ranking global.", configuracoes.aparecerNoRanking) { configuracoes.aparecerNoRanking = it },
        LinhaVis("Atividades visíveis", "Permita que vejam suas ações recentes.", configuracoes.atividadesVisiveis) { configuracoes.atividadesVisiveis = it },
    )
    Cartao {
        CabecalhoCartao(Lucide.Eye, "Visibilidade", "Controle como seu perfil aparece")
        linhas.forEach { l ->
            DivisorLinha()
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Column(Modifier.weight(1f)) {
                    Text(l.titulo, color = PC.Texto, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(l.descricao, color = PC.TextoSuave, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                }
                Interruptor(l.valor, l.aoMudar)
            }
        }
        Row(
            Modifier.fillMaxWidth().background(Color(0x6184ABFF)).padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(Lucide.ShieldCheck, null, tint = Color(0xFF1E40AF), modifier = Modifier.size(18.dp))
            Text(
                "Seu e-mail e sua matrícula nunca ficam visíveis para outros usuários.",
                color = Color(0xFF1E40AF), fontSize = 12.5.sp,
            )
        }
    }
}

/* ------------------------------------------------------------------ */
/*  DESEMPENHO                                                         */
/* ------------------------------------------------------------------ */
@Composable
private fun CartaoDesempenhoView(c: CartaoDesempenho, periodoRotulo: String, modifier: Modifier = Modifier) {
    Cartao(modifier) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 14.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(
                Modifier.fillMaxWidth().padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(PC.Verde100),
                    contentAlignment = Alignment.Center,
                ) { Icon(c.icone, null, tint = PC.Verde700, modifier = Modifier.size(18.dp)) }
                Text(
                    periodoRotulo, color = PC.Verde900, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1,
                    modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(PC.Verde100)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                )
            }
            Text(c.valor, color = PC.Texto, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1)
            Text(c.rotulo, color = PC.Texto, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(c.nota, color = PC.TextoSuave, fontSize = 12.sp)
        }
    }
}

@Composable
private fun GraficoXp(ativo: Boolean) {
    var selecionada by remember { mutableStateOf<Int?>(null) }
    val maximo = remember { EVOLUCAO_XP.maxOf { it.xp } }
    val ultima = EVOLUCAO_XP.lastIndex

    Cartao {
        Column(Modifier.padding(20.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Evolução de XP", color = PC.Texto, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "Pontuação conquistada nas últimas 12 semanas",
                        color = PC.TextoSuave, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp),
                    )
                }
                Spacer(Modifier.width(12.dp))
                Row(
                    Modifier.clip(RoundedCornerShape(8.dp)).background(PC.Verde100)
                        .border(1.dp, PC.Borda, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(Lucide.TrendingUp, null, tint = PC.Verde700, modifier = Modifier.size(13.dp))
                    Text(EVOLUCAO_VARIACAO, color = PC.Verde900, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Text(
                text = selecionada?.let { "Semana ${EVOLUCAO_XP[it].semana} · ${EVOLUCAO_XP[it].xp} XP" }
                    ?: "Toque em uma barra para ver o XP",
                color = if (selecionada != null) PC.Verde700 else PC.TextoSuave,
                fontSize = 12.sp,
                fontWeight = if (selecionada != null) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.padding(top = 14.dp, bottom = 8.dp),
            )

            Row(
                Modifier.fillMaxWidth().height(150.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                EVOLUCAO_XP.forEachIndexed { i, s ->
                    val fracao by animateFloatAsState(
                        targetValue = if (ativo) s.altura(maximo) else 0f,
                        animationSpec = tween(750, delayMillis = 60 * i, easing = FastOutSlowInEasing),
                        label = "barra$i",
                    )
                    val cor by animateColorAsState(
                        targetValue = when {
                            selecionada == i -> PC.Verde600
                            i == ultima -> PC.Verde700
                            else -> Color(0xFFBBF7D0)
                        },
                        animationSpec = tween(250),
                        label = "corBarra$i",
                    )
                    Box(
                        Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { selecionada = if (selecionada == i) null else i },
                        contentAlignment = Alignment.BottomCenter,
                    ) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(fracao.coerceIn(0f, 1f))
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(cor),
                        )
                    }
                }
            }

            Row(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                EVOLUCAO_XP.forEach { s ->
                    Text(
                        s.semana, Modifier.weight(1f), color = PC.TextoSuave, fontSize = 9.sp,
                        textAlign = TextAlign.Center, maxLines = 1, softWrap = false, overflow = TextOverflow.Clip,
                    )
                }
            }
        }
    }
}

@Composable
private fun CartaoConquistas() {
    val desbloqueadas = CONQUISTAS.count { it.desbloqueada }
    Cartao(fundo = PC.Fundo) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(Modifier.padding(bottom = 6.dp)) {
                Text("Conquistas recentes", color = PC.Texto, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(
                    "$desbloqueadas de $TOTAL_EMBLEMAS emblemas desbloqueados",
                    color = PC.TextoSuave, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp),
                )
            }
            CONQUISTAS.forEachIndexed { i, c ->
                Revelar(atraso = 40 * (i % 3)) { LinhaConquista(c) }
            }
        }
    }
}

@Composable
private fun LinhaConquista(c: Conquista) {
    val forma = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { alpha = if (c.desbloqueada) 1f else 0.7f }
            .clip(forma)
            .background(Color.White)
            .border(1.dp, PC.Borda, forma)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(c.cor.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center,
        ) { Icon(c.icone, null, tint = c.cor, modifier = Modifier.size(19.dp)) }

        Column(Modifier.weight(1f)) {
            Text(c.titulo, color = PC.Texto, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
            Text(c.descricao, color = PC.TextoSuave, fontSize = 11.5.sp)
        }

        if (c.desbloqueada) {
            Box(
                Modifier.size(22.dp).clip(CircleShape).background(PC.Verde100)
                    .border(1.5.dp, PC.Verde700, CircleShape),
                contentAlignment = Alignment.Center,
            ) { Icon(Lucide.Check, "Desbloqueada", tint = PC.Verde700, modifier = Modifier.size(13.dp)) }
        } else {
            Icon(Lucide.Lock, "Bloqueada", tint = PC.TextoSuave, modifier = Modifier.size(16.dp))
        }
    }
}

/* ------------------------------------------------------------------ */
/*  CONTA                                                              */
/* ------------------------------------------------------------------ */
@Composable
private fun CartaoConta(onAlterarSenha: () -> Unit, onSair: () -> Unit) {
    var confirmar by remember { mutableStateOf(false) }

    Cartao {
        CabecalhoCartao(Lucide.ShieldCheck, "Conta", "Segurança e acesso")
        LinhaConta(Lucide.KeyRound, "Alterar senha", PC.Texto, PC.Verde700, onAlterarSenha)
        DivisorLinha()
        LinhaConta(Lucide.LogOut, "Sair da conta", PC.Perigo, PC.Perigo) { confirmar = true }
    }

    if (confirmar) {
        AlertDialog(
            onDismissRequest = { confirmar = false },
            containerColor = Color.White,
            title = { Text("Sair da conta?", color = PC.Texto, fontWeight = FontWeight.Bold) },
            text = { Text("Você precisará entrar novamente para continuar sua jornada.", color = PC.TextoSuave) },
            confirmButton = {
                TextButton(onClick = { confirmar = false; onSair() }) {
                    Text("Sair", color = PC.Perigo, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmar = false }) { Text("Cancelar", color = PC.TextoSuave) }
            },
        )
    }
}

@Composable
private fun LinhaConta(icone: ImageVector, titulo: String, corTexto: Color, corIcone: Color, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Icon(icone, null, tint = corIcone, modifier = Modifier.size(20.dp))
        Text(titulo, Modifier.weight(1f), color = corTexto, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        Icon(Lucide.ChevronRight, null, tint = PC.TextoSuave, modifier = Modifier.size(18.dp))
    }
}

/* ------------------------------------------------------------------ */
/*  MISSÕES                                                            */
/* ------------------------------------------------------------------ */
@Composable
private fun SecaoMissoes(aba: String, onAba: (String) -> Unit, onAbrirMissao: (String) -> Unit) {
    val contagens = remember {
        ABAS.associate { (valor, _) ->
            valor to if (valor == "todas") MISSOES.size else MISSOES.count { it.status == valor }
        }
    }
    Cartao {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            TituloSecao(
                "Missões que impulsionam o seu progresso",
                "Continue suas missões ativas para avançar de nível e desbloquear novas conquistas",
            )
            Segmentos(ABAS, aba, onAba, contagens = contagens)

            Crossfade(targetState = aba, animationSpec = tween(300), label = "abaMissoes") { atual ->
                val lista = if (atual == "todas") MISSOES else MISSOES.filter { it.status == atual }
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    if (lista.isEmpty()) {
                        EstadoVazio(Lucide.Target, "Nenhuma missão nesta aba", "Quando houver missões aqui, elas aparecem nesta lista.")
                    } else {
                        lista.forEachIndexed { i, m ->
                            Revelar(atraso = 50 * i) { ativo -> CartaoMissao(m, ativo, onAbrirMissao) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CartaoMissao(m: Missao, ativo: Boolean, onAbrir: (String) -> Unit) {
    val (fundoDif, corDif) = when (m.dificuldade) {
        "Fácil" -> PC.Verde100 to PC.Verde700
        "Médio" -> Color(0x34EBB43E) to Color(0xFFB45309)
        else -> Color(0x1FEE3333) to Color(0xFFDC2626)
    }
    val fracao by animateFloatAsState(
        targetValue = if (ativo) m.progresso / 100f else 0f,
        animationSpec = tween(950, delayMillis = 150, easing = FastOutSlowInEasing),
        label = "progMissao",
    )

    Cartao(fundo = PC.Pagina) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Etiqueta(m.categoria, PC.Verde100, PC.Verde700)
                Etiqueta(m.dificuldade, fundoDif, corDif)
            }
            Text(m.titulo, color = PC.Texto, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 4.dp))
            Text(m.resumo, color = PC.TextoSuave, fontSize = 12.5.sp)

            Row(
                Modifier.fillMaxWidth().padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Progresso", color = PC.TextoSuave, fontSize = 12.sp)
                Text("${m.progresso}%", color = PC.Texto, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .semantics { progressBarRangeInfo = ProgressBarRangeInfo(m.progresso.toFloat(), 0f..100f) }
                    .clip(CircleShape)
                    .background(PC.Verde100),
            ) {
                Box(
                    Modifier
                        .fillMaxWidth(fracao.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(Brush.horizontalGradient(listOf(PC.Verde600, Color(0xFF22C55E)))),
                )
            }

            Row(
                Modifier.fillMaxWidth().padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Lucide.Star, null, tint = Color(0xFFEAB308), modifier = Modifier.size(14.dp))
                        Text("${m.xp} XP", color = Color(0xFFCA8A04), fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Lucide.Coins, null, tint = PC.Verde700, modifier = Modifier.size(14.dp))
                        Text("${m.mc} MC", color = PC.Verde700, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                if (m.prazoDias != null) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Lucide.Clock, null, tint = PC.TextoSuave, modifier = Modifier.size(13.dp))
                        Text(rotuloPrazo(m.prazoDias), color = PC.TextoSuave, fontSize = 12.sp)
                    }
                }
            }

            BotaoGradiente(
                texto = ROTULO_BOTAO[m.status].orEmpty(),
                modifier = Modifier.fillMaxWidth(),
                suave = m.status == "concluida",
                onClick = { onAbrir(m.id) },
            )
        }
    }
}

@Composable
private fun Etiqueta(texto: String, fundo: Color, cor: Color) {
    Text(
        texto, color = cor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
        modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(fundo).padding(horizontal = 8.dp, vertical = 2.dp),
    )
}