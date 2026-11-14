package com.vibeeco.app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Lucide

/* ------------------------------------------------------------------ */
/*  CORES (equivalentes às variáveis CSS do desktop)                   */
/* ------------------------------------------------------------------ */
internal object PC {
    val Pagina = Color(0xFFF7FAF8)
    val Fundo = Color(0xFFF4F8F5)          // var(--fundo)
    val Texto = Color(0xFF12261A)          // var(--texto)
    val TextoSuave = Color(0xFF5B6E62)     // var(--texto-suave)
    val Borda = Color(0xFFDDE7E0)          // var(--borda)
    val Verde100 = Color(0xFFDCFCE7)
    val Verde600 = Color(0xFF16A34A)
    val Verde700 = Color(0xFF15803D)
    val Verde900 = Color(0xFF14603A)
    val AbasFundo = Color(0xFFE8F1EA)
    val Gradiente = listOf(Color(0xFF22C55E), Color(0xFF0F7A3B))
    val Perigo = Color(0xFFC0392B)
    val PerigoFundo = Color(0xFFFDF0F0)
    val PerigoBorda = Color(0xFFF3C9C9)
}

/* ------------------------------------------------------------------ */
/*  BLOCOS BÁSICOS                                                     */
/* ------------------------------------------------------------------ */
@Composable
internal fun Cartao(
    modifier: Modifier = Modifier,
    fundo: Color = Color.White,
    conteudo: @Composable ColumnScope.() -> Unit,
) {
    val forma = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(forma)
            .background(fundo)
            .border(1.dp, PC.Borda, forma),
        content = conteudo,
    )
}

@Composable
internal fun CabecalhoCartao(icone: ImageVector?, titulo: String, subtitulo: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PC.Fundo)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (icone != null) Icon(icone, null, tint = PC.Verde900, modifier = Modifier.size(18.dp))
        Column {
            Text(titulo, color = PC.Texto, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            if (subtitulo != null) Text(subtitulo, color = PC.TextoSuave, fontSize = 12.sp)
        }
    }
}

@Composable
internal fun DivisorLinha() {
    Box(Modifier.fillMaxWidth().height(1.dp).background(PC.Borda))
}

@Composable
internal fun AvatarPerfil(iniciais: String, tamanho: Dp, fonte: Int = 13) {
    Box(
        modifier = Modifier
            .size(tamanho)
            .clip(CircleShape)
            .background(Brush.linearGradient(PC.Gradiente)),
        contentAlignment = Alignment.Center,
    ) {
        Text(iniciais, color = Color.White, fontSize = fonte.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
internal fun BotaoIcone(
    icone: ImageVector,
    descricao: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = PC.Verde900,
    contorno: Boolean = false,
    badge: Int = 0,
) {
    val forma = if (contorno) RoundedCornerShape(12.dp) else CircleShape
    Box(
        modifier = modifier
            .size(44.dp)
            .clip(forma)
            .then(if (contorno) Modifier.background(Color.White).border(1.dp, PC.Borda, forma) else Modifier)
            .clickable(role = Role.Button, onClickLabel = descricao, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icone, contentDescription = descricao, tint = tint, modifier = Modifier.size(22.dp))
        if (badge > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 4.dp, end = 4.dp)
                    .defaultMinSize(minWidth = 16.dp, minHeight = 16.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFDC2626))
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(if (badge > 9) "9+" else "$badge", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
internal fun BarraTopo(titulo: String, onVoltar: (() -> Unit)?, acoes: @Composable RowScope.() -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onVoltar != null) {
            BotaoIcone(Lucide.ArrowLeft, "Voltar", onVoltar)
        } else {
            Spacer(Modifier.width(12.dp))
        }
        Text(
            titulo,
            modifier = Modifier.weight(1f).padding(start = 4.dp),
            color = PC.Texto,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        acoes()
    }
}

/* ------------------------------------------------------------------ */
/*  CONTROLES                                                          */
/* ------------------------------------------------------------------ */
/** Abas / segmentos (período, filtro de missões, abas de notificações). */
@Composable
internal fun Segmentos(
    opcoes: List<Pair<String, String>>, // valor -> rótulo
    selecionado: String,
    onSelecionar: (String) -> Unit,
    modifier: Modifier = Modifier,
    contagens: Map<String, Int>? = null,
    contagemDestaque: Boolean = false,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(PC.AbasFundo)
            .padding(4.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        opcoes.forEach { (valor, rotulo) ->
            val ativo = valor == selecionado
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(9.dp))
                    .then(if (ativo) Modifier.background(Color.White) else Modifier)
                    .clickable(role = Role.Tab) { onSelecionar(valor) }
                    .padding(horizontal = 14.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    rotulo,
                    color = if (ativo) Color(0xFF0F5A2C) else PC.TextoSuave,
                    fontSize = 13.sp,
                    fontWeight = if (ativo) FontWeight.Bold else FontWeight.SemiBold,
                    maxLines = 1,
                )
                val c = contagens?.get(valor)
                if (c != null && (!contagemDestaque || c > 0)) {
                    Box(
                        modifier = Modifier
                            .defaultMinSize(minWidth = 20.dp, minHeight = 20.dp)
                            .clip(CircleShape)
                            .background(if (contagemDestaque) PC.Verde600 else PC.Verde100)
                            .padding(horizontal = 6.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            "$c",
                            color = if (contagemDestaque) Color.White else PC.Verde700,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun Interruptor(marcado: Boolean, onChange: (Boolean) -> Unit) {
    Switch(
        checked = marcado,
        onCheckedChange = onChange,
        colors = SwitchDefaults.colors(
            checkedTrackColor = PC.Verde600,
            checkedThumbColor = Color.White,
            uncheckedTrackColor = Color(0xFFCBD5CF),
            uncheckedThumbColor = Color.White,
            uncheckedBorderColor = Color.Transparent,
        ),
    )
}

@Composable
internal fun BotaoGradiente(
    texto: String,
    modifier: Modifier = Modifier,
    suave: Boolean = false,
    habilitado: Boolean = true,
    onClick: () -> Unit,
) {
    val fundo = if (suave) Modifier.background(Color(0xFFEEF6F0))
    else Modifier.background(Brush.horizontalGradient(listOf(Color(0xFF0F6B34), Color(0xFF1FA856))))
    Box(
        modifier = modifier
            .alpha(if (habilitado) 1f else 0.45f)
            .clip(RoundedCornerShape(12.dp))
            .then(fundo)
            .clickable(enabled = habilitado, role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            texto,
            color = if (suave) Color(0xFF1F5D37) else Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
    }
}

/* ------------------------------------------------------------------ */
/*  TIPO DE ATIVIDADE (IconeTipo.vue)                                  */
/* ------------------------------------------------------------------ */
@Composable
internal fun IconeTipo(tipo: TipoAtividade, tamanho: Dp) {
    Box(
        modifier = Modifier.size(tamanho).clip(CircleShape).background(tipo.fundo),
        contentAlignment = Alignment.Center,
    ) {
        Icon(tipo.icone, contentDescription = null, tint = tipo.cor, modifier = Modifier.size(tamanho * 0.5f))
    }
}

/* ------------------------------------------------------------------ */
/*  PAINEL DE DETALHES (DetalheLateral.vue -> bottom sheet)            */
/* ------------------------------------------------------------------ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DetalheSheet(
    tipo: TipoAtividade,
    rotulo: String,
    titulo: String,
    quando: String,
    onFechar: () -> Unit,
    acoes: @Composable RowScope.() -> Unit,
    conteudo: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onFechar,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconeTipo(tipo, 44.dp)
                Column {
                    Text(rotulo, color = tipo.cor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text(titulo, color = PC.Texto, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
                    Text(quando, color = PC.TextoSuave, fontSize = 12.sp)
                }
            }
            conteudo()
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), content = acoes)
        }
    }
}

@Composable
internal fun ListaDetalhes(itens: List<Pair<String, String>>) {
    if (itens.isEmpty()) return
    val forma = RoundedCornerShape(14.dp)
    Column(Modifier.fillMaxWidth().clip(forma).border(1.dp, PC.Borda, forma)) {
        itens.forEachIndexed { i, (rotulo, valor) ->
            if (i > 0) DivisorLinha()
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(rotulo, color = PC.TextoSuave, fontSize = 14.sp)
                Text(valor, color = PC.Texto, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.End)
            }
        }
    }
}

@Composable
internal fun EstadoVazio(icone: ImageVector, titulo: String, texto: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawRoundRect(
                    color = PC.Borda,
                    cornerRadius = CornerRadius(16.dp.toPx()),
                    style = Stroke(width = 1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f))),
                )
            }
            .padding(horizontal = 20.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(icone, null, tint = PC.TextoSuave, modifier = Modifier.size(40.dp))
        Text(titulo, color = Color(0xFF0F5A2C), fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(texto, color = PC.TextoSuave, fontSize = 14.sp, textAlign = TextAlign.Center)
    }
}
