package com.vibeeco.app.ui.profile

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Coins
import com.composables.icons.lucide.History
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Star
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val FILTROS = listOf(
    "todos" to "Tudo",
    "missao" to "Missões",
    "conteudo" to "Conteúdos",
    "desafio" to "Desafios",
    "conquista" to "Conquistas",
    "recompensa" to "Recompensas",
)

private val MESES = listOf("JAN", "FEV", "MAR", "ABR", "MAI", "JUN", "JUL", "AGO", "SET", "OUT", "NOV", "DEZ")
private val COR_XP = Color(0xFF8A5A00)
private val COR_ESTRELA = Color(0xFFF5B301)
private val COR_MOEDA = Color(0xFFD97706)

private data class GrupoDia(val dias: Int, val rotulo: String, val itens: List<Atividade>)

private fun dataDe(diasAtras: Int): LocalDate = LocalDate.now().minusDays(diasAtras.toLong())

private fun rotuloDia(diasAtras: Int): String {
    val d = dataDe(diasAtras)
    val base = "${d.dayOfMonth.toString().padStart(2, '0')} ${MESES[d.monthValue - 1]} ${d.year}"
    return when (diasAtras) { 0 -> "HOJE, $base"; 1 -> "ONTEM, $base"; else -> base }
}

private fun quandoDe(a: Atividade): String {
    val data = dataDe(a.diasAtras).format(DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale("pt", "BR")))
    val prefixo = when (a.diasAtras) { 0 -> "Hoje, "; 1 -> "Ontem, "; else -> "" }
    return "$prefixo$data às ${a.hora}"
}

private fun sinal(v: Int) = if (v > 0) "+$v" else "$v"

@Composable
fun HistoricoScreen(onVoltar: () -> Unit, onIrPara: (TipoAtividade) -> Unit, modifier: Modifier = Modifier) {
    var filtro by rememberSaveable { mutableStateOf("todos") }
    var aberta by remember { mutableStateOf<Atividade?>(null) }

    val grupos = remember(filtro) {
        ATIVIDADES
            .filter { filtro == "todos" || it.tipo.name.lowercase() == filtro }
            .groupBy { it.diasAtras }
            .toSortedMap()
            .map { (dias, itens) -> GrupoDia(dias, rotuloDia(dias), itens) }
    }

    Column(modifier.fillMaxSize().background(PC.Pagina)) {
        BarraTopo("Histórico", onVoltar)

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 110.dp),
        ) {
            item { Resumo(Modifier.padding(bottom = 16.dp)) }
            item {
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(bottom = 18.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FILTROS.forEach { (id, rotulo) ->
                        val ativo = filtro == id
                        Text(
                            rotulo,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (ativo) PC.Verde600 else Color.White)
                                .border(1.dp, if (ativo) PC.Verde600 else PC.Borda, CircleShape)
                                .clickable(role = Role.Button) { filtro = id }
                                .padding(horizontal = 14.dp, vertical = 7.dp),
                            color = if (ativo) Color.White else PC.TextoSuave,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                        )
                    }
                }
            }
            grupos.forEach { g ->
                item(key = "dia-${g.dias}") {
                    Text(g.rotulo, modifier = Modifier.padding(top = 6.dp, bottom = 10.dp), color = Color(0xFF4B5D52), fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                }
                itemsIndexed(g.itens, key = { _, a -> a.id }) { i, a ->
                    ItemLinha(a, ultimo = i == g.itens.lastIndex) { aberta = a }
                }
            }
            if (grupos.isEmpty()) {
                item { EstadoVazio(Lucide.History, "Nenhuma atividade encontrada", "Tente outro filtro para ver mais do seu histórico.") }
            }
        }
    }

    aberta?.let { a ->
        DetalheSheet(
            tipo = a.tipo,
            rotulo = a.rotulo,
            titulo = a.titulo,
            quando = quandoDe(a),
            onFechar = { aberta = null },
            acoes = {
                BotaoGradiente("Fechar", Modifier.weight(1f), suave = true) { aberta = null }
                BotaoGradiente(a.tipo.destinoRotulo, Modifier.weight(1f)) { aberta = null; onIrPara(a.tipo) }
            },
        ) {
            if (a.xp != 0 || a.mc != 0) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (a.xp != 0) Ganho(Lucide.Star, "+${a.xp}", "XP", Color(0xFFFEF3C7), COR_XP, COR_ESTRELA, Modifier.weight(1f))
                    if (a.mc != 0) {
                        val neg = a.mc < 0
                        Ganho(
                            Lucide.Coins, sinal(a.mc), "Moedas Verdes",
                            if (neg) Color(0xFFFEE2E2) else Color(0xFFDCF5E5),
                            if (neg) Color(0xFFB91C1C) else Color(0xFF0F6B34),
                            COR_MOEDA, Modifier.weight(1f),
                        )
                    }
                }
            }
            a.progresso?.let { p ->
                Column {
                    Row(Modifier.fillMaxWidth().padding(bottom = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Progresso", color = PC.TextoSuave, fontSize = 12.sp)
                        Text("$p%", color = PC.TextoSuave, fontSize = 12.sp)
                    }
                    Box(Modifier.fillMaxWidth().height(8.dp).clip(CircleShape).background(Color(0xFFD6EADB))) {
                        Box(Modifier.fillMaxHeight().fillMaxWidth(p.coerceIn(0, 100) / 100f).clip(CircleShape).background(PC.Verde600))
                    }
                }
            }
            Text(a.descricao, color = Color(0xFF3D4F44), fontSize = 15.sp, lineHeight = 23.sp)
            ListaDetalhes(a.detalhes)
        }
    }
}

@Composable
private fun Resumo(modifier: Modifier = Modifier) {
    val itens = listOf(
        Triple("Missões concluídas", TipoAtividade.Missao, Color(0xFFE8590C)),
        Triple("Conteúdos lidos", TipoAtividade.Conteudo, Color(0xFF3B6FE0)),
        Triple("Desafios", TipoAtividade.Desafio, Color(0xFF7C3AED)),
        Triple("Conquistas", TipoAtividade.Conquista, Color(0xFFD97706)),
    )
    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        itens.chunked(2).forEach { linha ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                linha.forEach { (rotulo, tipo, cor) ->
                    val total = ATIVIDADES.count { it.tipo == tipo }
                    val numero by animateIntAsState(total, tween(800), label = "resumo")
                    Cartao(Modifier.weight(1f)) {
                        Column(
                            Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text("$numero", color = cor, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
                            Text(rotulo, color = PC.TextoSuave, fontSize = 13.sp, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemLinha(a: Atividade, ultimo: Boolean, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(Modifier.width(40.dp).fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {
            IconeTipo(a.tipo, 40.dp)
            if (!ultimo) {
                Box(Modifier.padding(top = 4.dp).width(2.dp).weight(1f).clip(RoundedCornerShape(2.dp)).background(Color(0xFFCFE6D6)))
            }
        }
        Box(Modifier.weight(1f).padding(bottom = 12.dp)) {
            Cartao(Modifier.clickable(role = Role.Button, onClick = onClick)) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(a.rotulo, color = a.tipo.cor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text(a.titulo, color = PC.Texto, fontSize = 15.sp, fontWeight = FontWeight.Medium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                    if (a.xp != 0 || a.mc != 0) {
                        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            if (a.xp != 0) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Lucide.Star, null, tint = COR_ESTRELA, modifier = Modifier.size(13.dp))
                                    Text("+${a.xp} XP", color = COR_XP, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            if (a.mc != 0) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Lucide.Coins, null, tint = COR_MOEDA, modifier = Modifier.size(15.dp))
                                    Text(sinal(a.mc), color = if (a.mc < 0) Color(0xFFDC2626) else PC.Verde600, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Ganho(icone: androidx.compose.ui.graphics.vector.ImageVector, valor: String, legenda: String, fundo: Color, cor: Color, corIcone: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(14.dp)).background(fundo).padding(horizontal = 10.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Icon(icone, null, tint = corIcone, modifier = Modifier.size(20.dp))
        Text(valor, color = cor, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        Text(legenda, color = cor, fontSize = 12.sp)
    }
}