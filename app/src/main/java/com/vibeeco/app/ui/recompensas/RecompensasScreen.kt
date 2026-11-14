package com.vibeeco.app.ui.recompensas

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Coins
import com.composables.icons.lucide.Copy
import com.composables.icons.lucide.Gift
import com.composables.icons.lucide.Lock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ShoppingBag
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/* ------------------------------------------------------------------ */
/*  CORES (equivalentes ao CSS do desktop)                             */
/* ------------------------------------------------------------------ */
private object RC {
    val Texto = Color(0xFF12261A)
    val TextoSuave = Color(0xFF5B6E62)
    val Borda = Color(0xFFDDE7E0)
    val Verde600 = Color(0xFF16A34A)
    val Preco = Color(0xFF0F5A2C)
    val Moeda = Color(0xFFD97706)

    val SaldoGradiente = listOf(Color(0xFF0C4A25), Color(0xFF117A3D), Color(0xFF1A9A4E))
    val SaldoUnidade = Color(0xFF86EFAC)
    val SaldoMoeda = Color(0xFFFDE68A)

    val AbasFundo = Color(0xFFE8F1EA)
    val BotaoGradiente = listOf(Color(0xFF0F6B34), Color(0xFF1FA856))
    val BotaoDesabilitado = Color(0xFFE8F1EA)
    val BotaoDesabilitadoTexto = Color(0xFF8AA393)
    val BotaoSuave = Color(0xFFEEF6F0)
    val BotaoSuaveTexto = Color(0xFF1F5D37)

    val CodigoFundo = Color(0xFFF1F8F3)
    val CodigoBorda = Color(0xFF9BCAA9)
    val Copiado = Color(0xFF16A34A)

    val NivelFaltando = Color(0xFFB45309)
    val Estoque = Color(0xFFDC2626)
    val Erro = Color(0xFFB91C1C)
    val ErroFundo = Color(0xFFFEE2E2)
    val ModalIcone = Color(0xFFE3F5EA)
    val Tipo = Color(0xE60C4A25)
}

/* ------------------------------------------------------------------ */
/*  MODELOS                                                            */
/*  Se já existirem equivalentes em data/Models.kt, use os seus.       */
/* ------------------------------------------------------------------ */
enum class TemaRecompensa(val cores: List<Color>) {
    Verde(listOf(Color(0xFFBBF7D0), Color(0xFF4ADE80))),
    Laranja(listOf(Color(0xFFFED7AA), Color(0xFFFB923C))),
    Terra(listOf(Color(0xFFD6C4A8), Color(0xFF8B6F4E))),
    Azul(listOf(Color(0xFFBFDBFE), Color(0xFF60A5FA))),
}

data class Recompensa(
    val id: String,
    val titulo: String,
    val tipo: String,
    val preco: Int,
    val nivelMinimo: Int,
    val estoque: Int? = null, // null = ilimitado
    val emoji: String = "🎁",
    val tema: TemaRecompensa = TemaRecompensa.Verde,
)

data class Resgate(
    val id: String,
    val nome: String,
    val tipo: String,
    val dataIso: String, // ex.: "2026-11-14"
    val preco: Int,
    val codigo: String,
    val emoji: String = "🎁",
    val tema: TemaRecompensa = TemaRecompensa.Verde,
)

sealed interface ResultadoResgate {
    data class Ok(val resgate: Resgate) : ResultadoResgate
    data class Erro(val motivo: String) : ResultadoResgate
}

private enum class EstadoRecompensa { Disponivel, Saldo, Nivel, Esgotado }
private enum class AbaRecompensas { Disponiveis, Resgatadas }
private enum class FaseModal { Confirmar, Sucesso }

private fun estadoDe(r: Recompensa, saldo: Int, nivelUsuario: Int): EstadoRecompensa = when {
    r.estoque != null && r.estoque <= 0 -> EstadoRecompensa.Esgotado
    nivelUsuario < r.nivelMinimo -> EstadoRecompensa.Nivel
    saldo < r.preco -> EstadoRecompensa.Saldo
    else -> EstadoRecompensa.Disponivel
}

private fun formatarData(iso: String): String = runCatching {
    LocalDate.parse(iso.take(10)).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
}.getOrDefault(iso)

/* ------------------------------------------------------------------ */
/*  TELA                                                               */
/* ------------------------------------------------------------------ */
@Composable
fun RecompensasScreen(
    saldo: Int,
    totalResgatado: Int,
    nivelUsuario: Int,
    recompensas: List<Recompensa>,
    resgates: List<Resgate>,
    onResgatar: (idRecompensa: String) -> ResultadoResgate,
    onVoltar: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var aba by rememberSaveable { mutableStateOf(AbaRecompensas.Disponiveis) }

    // Modal
    var selecionada by remember { mutableStateOf<Recompensa?>(null) }
    var fase by remember { mutableStateOf(FaseModal.Confirmar) }
    var ultimoResgate by remember { mutableStateOf<Resgate?>(null) }
    var erro by remember { mutableStateOf("") }

    // Copiar código
    val clipboard = LocalClipboardManager.current
    var copiado by remember { mutableStateOf("") }
    LaunchedEffect(copiado) {
        if (copiado.isNotEmpty()) {
            delay(1800)
            copiado = ""
        }
    }
    val copiar: (String) -> Unit = { codigo ->
        clipboard.setText(AnnotatedString(codigo))
        copiado = codigo
    }

    val saldoAnimado by animateIntAsState(saldo, tween(900), label = "saldo")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7FAF8)),
    ) {
        BarraTopo(onVoltar = onVoltar)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                CardSaldo(saldoAnimado, totalResgatado)
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Abas(aba = aba, qtdResgates = resgates.size, onSelecionar = { aba = it })
            }

            if (aba == AbaRecompensas.Disponiveis) {
                items(recompensas, key = { it.id }) { r ->
                    val estado = estadoDe(r, saldo, nivelUsuario)
                    CardRecompensa(
                        r = r,
                        estado = estado,
                        faltam = (r.preco - saldo).coerceAtLeast(0),
                        nivelInsuficiente = nivelUsuario < r.nivelMinimo,
                        onResgatar = {
                            selecionada = r
                            fase = FaseModal.Confirmar
                            erro = ""
                        },
                    )
                }
            } else {
                if (resgates.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        EstadoVazio(onVerRecompensas = { aba = AbaRecompensas.Disponiveis })
                    }
                } else {
                    items(resgates, key = { it.id }, span = { GridItemSpan(maxLineSpan) }) { r ->
                        CardResgatada(r, copiado == r.codigo, copiar)
                    }
                }
            }
        }
    } // fim Column

    // MODAL
    selecionada?.let { rec ->
        ModalResgate(
            fase = fase,
            recompensa = rec,
            saldo = saldo,
            ultimoResgate = ultimoResgate,
            erro = erro,
            copiado = copiado,
            onCopiar = copiar,
            onFechar = { selecionada = null },
            onConfirmar = {
                when (val res = onResgatar(rec.id)) {
                    is ResultadoResgate.Ok -> {
                        ultimoResgate = res.resgate
                        fase = FaseModal.Sucesso
                    }
                    is ResultadoResgate.Erro -> erro = res.motivo
                }
            },
            onVerResgatadas = {
                selecionada = null
                aba = AbaRecompensas.Resgatadas
            },
        )
    }
}

/* ---------- BARRA DE TOPO ---------- */
@Composable
private fun BarraTopo(onVoltar: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .clickable(role = Role.Button, onClickLabel = "Voltar para o ranking", onClick = onVoltar),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Lucide.ArrowLeft, contentDescription = "Voltar para o ranking", tint = RC.Preco, modifier = Modifier.size(24.dp))
        }
        Text("Recompensas", color = RC.Texto, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
    }
}

/* ---------- SALDO ---------- */
@Composable
private fun CardSaldo(saldoExibido: Int, totalResgatado: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp), ambientColor = Color(0xFF0D5F31), spotColor = Color(0xFF0D5F31))
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(RC.SaldoGradiente))
            .padding(horizontal = 20.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column {
            Text("Seu saldo atual", color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp)
            Row(
                modifier = Modifier.padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text("$saldoExibido", color = Color.White, fontSize = 38.sp, fontWeight = FontWeight.ExtraBold)
                Text("Moedas Verdes", color = RC.SaldoUnidade, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Icon(Lucide.Coins, null, tint = RC.SaldoMoeda, modifier = Modifier.size(30.dp))
            }
            Text(
                "Acumuladas por missões, desafios e conteúdos",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White.copy(alpha = 0.1f))
                .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(14.dp))
                .padding(horizontal = 22.dp, vertical = 12.dp),
        ) {
            Text("Total resgatado", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            Text("$totalResgatado MC", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

/* ---------- ABAS ---------- */
@Composable
private fun Abas(aba: AbaRecompensas, qtdResgates: Int, onSelecionar: (AbaRecompensas) -> Unit) {
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(RC.AbasFundo)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            AbaItem("Disponíveis", aba == AbaRecompensas.Disponiveis, null) { onSelecionar(AbaRecompensas.Disponiveis) }
            AbaItem("Resgatadas", aba == AbaRecompensas.Resgatadas, qtdResgates.takeIf { it > 0 }) {
                onSelecionar(AbaRecompensas.Resgatadas)
            }
        }
    }
}

@Composable
private fun AbaItem(texto: String, ativa: Boolean, contador: Int?, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(9.dp))
            .then(if (ativa) Modifier.shadow(1.dp, RoundedCornerShape(9.dp)).background(Color.White) else Modifier)
            .selectable(selected = ativa, role = Role.Tab, onClick = onClick)
            .padding(horizontal = 22.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            texto,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (ativa) RC.Preco else RC.TextoSuave,
        )
        if (contador != null) {
            Box(
                modifier = Modifier
                    .defaultMinSize(minWidth = 20.dp, minHeight = 20.dp)
                    .clip(CircleShape)
                    .background(RC.Verde600)
                    .padding(horizontal = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("$contador", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/* ---------- BOTÕES ---------- */
@Composable
private fun BotaoPrimario(
    texto: String,
    habilitado: Boolean = true,
    suave: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val formato = RoundedCornerShape(12.dp)
    val fundo = when {
        !habilitado -> Modifier.background(RC.BotaoDesabilitado)
        suave -> Modifier.background(RC.BotaoSuave)
        else -> Modifier.background(Brush.horizontalGradient(RC.BotaoGradiente))
    }
    val corTexto = when {
        !habilitado -> RC.BotaoDesabilitadoTexto
        suave -> RC.BotaoSuaveTexto
        else -> Color.White
    }
    Box(
        modifier = modifier
            .clip(formato)
            .then(fundo)
            .clickable(enabled = habilitado, role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(texto, color = corTexto, fontSize = 14.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, maxLines = 1)
    }
}

/* ---------- CARD DE RECOMPENSA ---------- */
@Composable
private fun CardRecompensa(
    r: Recompensa,
    estado: EstadoRecompensa,
    faltam: Int,
    nivelInsuficiente: Boolean,
    onResgatar: () -> Unit,
) {
    val opaco = estado == EstadoRecompensa.Nivel || estado == EstadoRecompensa.Esgotado

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, RC.Borda, RoundedCornerShape(16.dp)),
    ) {
        // Mídia
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(112.dp)
                .background(Brush.linearGradient(r.tema.cores)),
            contentAlignment = Alignment.Center,
        ) {
            Text(r.emoji, fontSize = 48.sp)
            Text(
                text = r.tipo,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
                    .clip(CircleShape)
                    .background(RC.Tipo)
                    .padding(horizontal = 10.dp, vertical = 3.dp),
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
            )
            when (estado) {
                EstadoRecompensa.Nivel -> Box(
                    Modifier.matchParentSize().background(Color(0x73141E18)),
                    contentAlignment = Alignment.Center,
                ) { Icon(Lucide.Lock, "Bloqueado por nível", tint = Color.White, modifier = Modifier.size(26.dp)) }

                EstadoRecompensa.Esgotado -> Box(
                    Modifier.matchParentSize().background(Color(0x80141E18)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("ESGOTADO", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                }

                else -> Unit
            }
        }

        // Corpo
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                r.titulo,
                color = RC.Texto,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.alpha(if (opaco) 0.6f else 1f),
            )
            Row(
                modifier = Modifier.alpha(if (opaco) 0.6f else 1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(Lucide.Coins, null, tint = RC.Moeda, modifier = Modifier.size(18.dp))
                Text("${r.preco}", color = RC.Preco, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            }
            Text(
                "Nível ${r.nivelMinimo}+ necessário",
                color = if (nivelInsuficiente) RC.NivelFaltando else RC.TextoSuave,
                fontSize = 12.sp,
                fontWeight = if (nivelInsuficiente) FontWeight.SemiBold else FontWeight.Normal,
            )
            if (r.estoque != null && r.estoque in 1..5) {
                Text("Restam só ${r.estoque}", color = RC.Estoque, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            BotaoPrimario(
                texto = when (estado) {
                    EstadoRecompensa.Disponivel -> "Resgatar"
                    EstadoRecompensa.Saldo -> "Faltam $faltam MC"
                    EstadoRecompensa.Esgotado -> "Esgotado"
                    EstadoRecompensa.Nivel -> "Bloqueado"
                },
                habilitado = estado == EstadoRecompensa.Disponivel,
                modifier = Modifier.fillMaxWidth(),
                onClick = onResgatar,
            )
        }
    }
}

/* ---------- RESGATADAS ---------- */
@Composable
private fun CardResgatada(r: Resgate, copiado: Boolean, onCopiar: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, RC.Borda, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(r.tema.cores)),
                contentAlignment = Alignment.Center,
            ) { Text(r.emoji, fontSize = 26.sp) }

            Column(Modifier.weight(1f)) {
                Text(r.nome, color = RC.Texto, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    "${r.tipo} · Resgatado em ${formatarData(r.dataIso)}",
                    color = RC.TextoSuave,
                    fontSize = 12.sp,
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Icon(Lucide.Coins, null, tint = RC.Moeda, modifier = Modifier.size(18.dp))
                Text("${r.preco}", color = RC.Preco, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
        CodigoBotao(codigo = r.codigo, copiado = copiado, grande = false, onCopiar = onCopiar)
    }
}

@Composable
private fun CodigoBotao(codigo: String, copiado: Boolean, grande: Boolean, onCopiar: (String) -> Unit) {
    val formato = RoundedCornerShape(10.dp)
    Row(
        modifier = Modifier
            .clip(formato)
            .background(RC.CodigoFundo)
            .border(1.dp, RC.CodigoBorda, formato)
            .clickable(role = Role.Button, onClickLabel = "Copiar código $codigo") { onCopiar(codigo) }
            .padding(horizontal = if (grande) 18.dp else 12.dp, vertical = if (grande) 12.dp else 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            codigo,
            color = RC.Preco,
            fontFamily = FontFamily.Monospace,
            fontSize = if (grande) 18.sp else 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
        )
        if (copiado) {
            Icon(Lucide.Check, null, tint = RC.Copiado, modifier = Modifier.size(14.dp))
            Text("Copiado", color = RC.Copiado, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        } else {
            Icon(Lucide.Copy, "Copiar", tint = RC.Preco, modifier = Modifier.size(14.dp))
        }
    }
}

@Composable
private fun EstadoVazio(onVerRecompensas: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, RC.Borda, RoundedCornerShape(16.dp))
            .padding(horizontal = 20.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(Lucide.Gift, null, tint = RC.TextoSuave, modifier = Modifier.size(40.dp))
        Text("Você ainda não resgatou nada", color = RC.Preco, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text("Use suas Moedas Verdes para trocar por recompensas.", color = RC.TextoSuave, fontSize = 14.sp, textAlign = TextAlign.Center)
        Spacer(Modifier.height(6.dp))
        BotaoPrimario("Ver recompensas", onClick = onVerRecompensas)
    }
}

/* ---------- MODAL ---------- */
@Composable
private fun ModalResgate(
    fase: FaseModal,
    recompensa: Recompensa,
    saldo: Int,
    ultimoResgate: Resgate?,
    erro: String,
    copiado: String,
    onCopiar: (String) -> Unit,
    onFechar: () -> Unit,
    onConfirmar: () -> Unit,
    onVerResgatadas: () -> Unit,
) {
    Dialog(onDismissRequest = onFechar) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(start = 24.dp, end = 24.dp, top = 28.dp, bottom = 24.dp)
                .animateContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (fase == FaseModal.Confirmar) {
                IconeModal(sucesso = false)
                Text("Confirmar Resgate", color = RC.Texto, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                Text(
                    text = androidx.compose.ui.text.buildAnnotatedString {
                        append("Você deseja resgatar ")
                        pushStyle(androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold))
                        append(recompensa.titulo)
                        pop()
                        append(" por ")
                        pushStyle(androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold))
                        append("${recompensa.preco} Moedas Verdes")
                        pop()
                        append("?")
                    },
                    color = Color(0xFF3D4F44),
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    textAlign = TextAlign.Center,
                )
                Text(
                    "Saldo depois do resgate: ${saldo - recompensa.preco} MC",
                    color = RC.TextoSuave,
                    fontSize = 13.sp,
                )
                if (erro.isNotEmpty()) {
                    Text(
                        erro,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(RC.ErroFundo)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        color = RC.Erro,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Spacer(Modifier.height(4.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    BotaoPrimario("Cancelar", suave = true, modifier = Modifier.weight(1f), onClick = onFechar)
                    BotaoPrimario("Confirmar", modifier = Modifier.weight(1f), onClick = onConfirmar)
                }
            } else if (ultimoResgate != null) {
                IconeModal(sucesso = true)
                Text("Resgate realizado!", color = RC.Texto, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                Text(
                    text = androidx.compose.ui.text.buildAnnotatedString {
                        pushStyle(androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold))
                        append(ultimoResgate.nome)
                        pop()
                        append(" é seu. Guarde o código abaixo:")
                    },
                    color = Color(0xFF3D4F44),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                )
                CodigoBotao(ultimoResgate.codigo, copiado == ultimoResgate.codigo, grande = true, onCopiar = onCopiar)
                Text("Saldo atual: $saldo MC", color = RC.TextoSuave, fontSize = 13.sp)
                Spacer(Modifier.height(4.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    BotaoPrimario("Fechar", suave = true, modifier = Modifier.weight(1f), onClick = onFechar)
                    BotaoPrimario("Ver resgatadas", modifier = Modifier.weight(1f), onClick = onVerResgatadas)
                }
            }
        }
    }
}

@Composable
private fun IconeModal(sucesso: Boolean) {
    // "pop" ao aparecer, como a animação do desktop
    var visivel by remember { mutableStateOf(false) }
    LaunchedEffect(sucesso) { visivel = true }
    val escala by animateFloatAsState(
        targetValue = if (visivel) 1f else 0.3f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "pop",
    )
    Box(
        modifier = Modifier
            .size(64.dp)
            .scale(escala)
            .clip(CircleShape)
            .background(if (sucesso) RC.Verde600 else RC.ModalIcone),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = if (sucesso) Lucide.Check else Lucide.ShoppingBag,
            contentDescription = null,
            tint = if (sucesso) Color.White else Color(0xFF0F6B34),
            modifier = Modifier.size(34.dp),
        )
    }
}

/* ------------------------------------------------------------------ */
/*  PREVIEW COM DADOS FICTÍCIOS                                        */
/* ------------------------------------------------------------------ */
@Preview(showBackground = true, heightDp = 1100)
@Composable
private fun RecompensasPreview() {
    val recompensas = listOf(
        Recompensa("1", "Muda de árvore nativa", "Físico", 120, 1, emoji = "🌱"),
        Recompensa("2", "Ecobag VibeEco", "Físico", 300, 3, estoque = 3, emoji = "👜", tema = TemaRecompensa.Laranja),
        Recompensa("3", "Curso de compostagem", "Digital", 500, 8, emoji = "📚", tema = TemaRecompensa.Azul),
        Recompensa("4", "Garrafa reutilizável", "Físico", 250, 2, estoque = 0, emoji = "🥤", tema = TemaRecompensa.Terra),
    )
    RecompensasScreen(
        saldo = 280,
        totalResgatado = 120,
        nivelUsuario = 4,
        recompensas = recompensas,
        resgates = emptyList(),
        onResgatar = { ResultadoResgate.Erro("Prévia: sem backend") },
    )
}