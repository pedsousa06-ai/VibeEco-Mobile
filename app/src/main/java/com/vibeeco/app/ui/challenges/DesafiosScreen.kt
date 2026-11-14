package com.vibeeco.app.ui.challenges

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Coins
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Star
import com.vibeeco.app.data.Desafio
import com.vibeeco.app.data.Escopo
import com.vibeeco.app.ui.components.GradientHeader
import com.vibeeco.app.ui.components.VibeCard
import com.vibeeco.app.ui.theme.TextMain
import com.vibeeco.app.ui.theme.TextMuted

private enum class Aba(val rotulo: String, val escopo: Escopo?) {
    TODOS("Todos", null),
    INDIVIDUAL("Individuais", Escopo.INDIVIDUAL),
    COLETIVO("Coletivos", Escopo.COLETIVO),
}

@Composable
fun DesafiosScreen(
    desafios: List<Desafio>,
    onOpen: (Desafio) -> Unit,
    onParticipar: (Desafio) -> Unit,
    onAbandonar: (Desafio) -> Unit,
) {
    var aba by remember { mutableStateOf(Aba.TODOS) }

    fun daAba(a: Aba) = if (a.escopo == null) desafios else desafios.filter { it.escopo == a.escopo }
    val visiveis = daAba(aba)
    val participando = desafios.count { it.participando }

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            GradientHeader {
                Text("Desafios", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 26.sp)
                Text(
                    "Complete ações sustentáveis e ganhe pontos.",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                )
                Spacer(Modifier.height(14.dp))
                Text(
                    text = "$participando " + if (participando == 1) "desafio em andamento" else "desafios em andamento",
                    color = Color.White.copy(alpha = 0.92f),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                )
            }
        }

        item { Abas(aba, { daAba(it).size }, { aba = it }) }

        items(visiveis, key = { it.id }) { d ->
            DesafioCard(
                d = d,
                onOpen = { onOpen(d) },
                onParticipar = { onParticipar(d) },
                onAbandonar = { onAbandonar(d) },
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }

        if (visiveis.isEmpty()) {
            item {
                Column(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text("Nenhum desafio nesta categoria", fontWeight = FontWeight.Bold, color = Color(0xFF0F5A2C))
                    Text("Novos desafios aparecem aqui assim que forem publicados.", fontSize = 13.sp, color = TextMuted)
                }
            }
        }
    }
}

@Composable
private fun Abas(selecionada: Aba, contagem: (Aba) -> Int, onSelect: (Aba) -> Unit) {
    Row(
        Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .background(Color(0xFFE8F1EA), RoundedCornerShape(12.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Aba.entries.forEach { aba ->
            val ativa = aba == selecionada
            Row(
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(9.dp))
                    .background(if (ativa) Color.White else Color.Transparent)
                    .clickable { onSelect(aba) }
                    .padding(vertical = 9.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    aba.rotulo,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (ativa) Color(0xFF0F5A2C) else TextMuted,
                )
                Spacer(Modifier.width(6.dp))
                Box(
                    Modifier
                        .defaultMinSize(minWidth = 20.dp)
                        .height(20.dp)
                        .background(if (ativa) Color(0xFF16A34A) else Color(0xFFD3E6D9), CircleShape)
                        .padding(horizontal = 6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "${contagem(aba)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (ativa) Color.White else Color(0xFF0F5A2C),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DesafioCard(
    d: Desafio,
    onOpen: () -> Unit,
    onParticipar: () -> Unit,
    onAbandonar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    VibeCard(modifier.fillMaxWidth(), onClick = onOpen) {
        Column(Modifier.clip(RoundedCornerShape(20.dp))) {
            // faixa de cor da categoria
            Box(Modifier.fillMaxWidth().height(6.dp).background(d.category.cor()))

            Column(
                Modifier.padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    TagCategoria(d.category)
                    TagEscopo(d.escopo.rotulo)
                    TagChip(d.dificuldade.rotulo, d.dificuldade.fundo, d.dificuldade.texto)
                    if (d.participando) TagChip("Participando", Color(0xFFDCF8E6), Color(0xFF15803D))
                }

                Text(
                    d.titulo,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    lineHeight = 23.sp,
                    color = TextMain,
                )
                Text(d.descricao, fontSize = 14.sp, lineHeight = 20.sp, color = Color(0xFF4B5D52))

                Medidor("Participação", "${d.participantes}/${d.meta}", d.participacao / 100f)

                if (d.participando) {
                    Medidor(
                        "Seu progresso", "${d.progresso}%", d.progresso / 100f,
                        cores = listOf(Color(0xFF0F6B34), Color(0xFF1FA856)),
                    )
                }

                // recompensas
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Lucide.Star, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(5.dp))
                        Text("${d.xp} XP", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF92400E))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Lucide.Coins, null, tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(5.dp))
                        Text("${d.mc}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF15803D))
                    }
                    Spacer(Modifier.weight(1f))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Lucide.Clock, null, tint = TextMuted, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(5.dp))
                        Text(d.rotuloDias, fontSize = 12.sp, color = TextMuted)
                    }
                }

                if (d.participando) BotaoDesafio("Abandonar desafio", suave = true, onClick = onAbandonar)
                else BotaoDesafio("Participar do desafio", suave = false, onClick = onParticipar)
            }
        }
    }
}
