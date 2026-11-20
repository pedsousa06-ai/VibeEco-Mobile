package com.vibeeco.app.ui.missions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.ArrowRight
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Star
import com.vibeeco.app.data.MissaoUi
import com.vibeeco.app.data.StatusMissao
import com.vibeeco.app.ui.components.VibeCard
import com.vibeeco.app.ui.theme.TextMain
import com.vibeeco.app.ui.theme.TextMuted

private enum class AbaMissao(val rotulo: String, val status: StatusMissao?) {
    TODAS("Todas", null),
    DISPONIVEL("Disponíveis", StatusMissao.Disponivel),
    ANDAMENTO("Em Andamento", StatusMissao.Andamento),
    CONCLUIDA("Concluídas", StatusMissao.Concluida),
}

@Composable
fun MissoesScreen(
    cartoes: List<MissaoUi>,
    onVoltar: () -> Unit,
    onAbrirMissao: (String) -> Unit,
    onParticipar: (String) -> Unit,
) {
    var abaIdx by rememberSaveable { mutableIntStateOf(0) }
    val aba = AbaMissao.entries[abaIdx]

    val contagem = AbaMissao.entries.associateWith { a ->
        if (a.status == null) cartoes.size else cartoes.count { it.status == a.status }
    }
    val visiveis = if (aba.status == null) cartoes else cartoes.filter { it.status == aba.status }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF7FAF8)).statusBarsPadding(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Row(
                Modifier.clip(RoundedCornerShape(8.dp)).clickable(onClick = onVoltar).padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Lucide.ArrowLeft, null, tint = TextMuted, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Voltar", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextMuted)
            }
        }
        item {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Missões", color = TextMain, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.3).sp)
                Text("Complete missões para ganhar XP, Moedas Verdes e desbloquear conquistas.", color = TextMuted, fontSize = 15.sp, lineHeight = 21.sp)
            }
        }
        item {
            Abas(aba, contagem, { abaIdx = it.ordinal }, Modifier.padding(vertical = 6.dp))
        }

        if (visiveis.isEmpty()) {
            item {
                Text(
                    "Nenhuma missão nesta categoria por enquanto.",
                    modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                    color = TextMuted, fontSize = 14.sp, textAlign = TextAlign.Center,
                )
            }
        } else {
            items(visiveis, key = { it.missao.id }) { ui ->
                CartaoMissaoView(
                    ui = ui,
                    onClick = { onAbrirMissao(ui.missao.id) },
                    onAcao = {
                        if (ui.status == StatusMissao.Disponivel) onParticipar(ui.missao.id)
                        onAbrirMissao(ui.missao.id)
                    },
                )
            }
        }
    }
}

@Composable
private fun Abas(selecionada: AbaMissao, contagem: Map<AbaMissao, Int>, onSelecionar: (AbaMissao) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFFE6EEE8)).padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        AbaMissao.entries.forEach { aba ->
            val ativa = aba == selecionada
            Row(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(9.dp))
                    .then(if (ativa) Modifier.background(Color.White) else Modifier)
                    .clickable { onSelecionar(aba) }.padding(horizontal = 8.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    aba.rotulo, fontSize = 12.5.sp, maxLines = 1,
                    fontWeight = if (ativa) FontWeight.Bold else FontWeight.Medium,
                    color = if (ativa) TextMain else TextMuted,
                )
                Spacer(Modifier.width(6.dp))
                Box(
                    modifier = Modifier.defaultMinSize(minWidth = 20.dp, minHeight = 20.dp).clip(CircleShape)
                        .background(if (ativa) Color(0xFFDCFCE7) else Color(0x120F2B1B)).padding(horizontal = 6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("${contagem[aba] ?: 0}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (ativa) Color(0xFF0F6B34) else TextMuted)
                }
            }
        }
    }
}

@Composable
private fun CartaoMissaoView(ui: MissaoUi, onClick: () -> Unit, onAcao: () -> Unit) {
    val m = ui.missao
    val dif = dificuldadeUi(m.difficulty)
    val concluida = ui.status == StatusMissao.Concluida

    val (rotulo, corBotao, corTexto) = when (ui.status) {
        StatusMissao.Andamento -> Triple("Continuar", Color(0xFF14603A), Color.White)
        StatusMissao.Disponivel -> Triple("Participar", Color(0xFF22C55E), Color.White)
        StatusMissao.Concluida -> Triple("Ver detalhes", Color(0xFFEAF0EB), TextMuted)
    }

    VibeCard(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // ✅ CORRIGIDO: m.category.label ➔ m.category.nomeCategoria
                Tag(m.category.nomeCategoria ?: "Geral", Color(0xFFDCFCE7), Color(0xFF0F6B34))
                Tag(m.difficulty, dif.fundo, dif.texto)
                Spacer(Modifier.weight(1f))
                if (concluida) Tag("Concluída", Color(0xFFDCF5E5), Color(0xFF15803D))
            }

            Text(m.title, color = TextMain, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 23.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)

            BarraProgresso(ui.progresso)

            Row(Modifier.fillMaxWidth().padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Lucide.Star, null, tint = Color(0xFFF5B301), modifier = Modifier.size(15.dp))
                        Spacer(Modifier.width(5.dp))
                        Text("${m.xp} XP", color = Color(0xFF8A5A00), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
                if (!concluida) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Lucide.Clock, null, tint = TextMuted, modifier = Modifier.size(13.dp))
                        Spacer(Modifier.width(5.dp))
                        Text(rotuloPrazo(ui.progresso), color = TextMuted, fontSize = 12.sp)
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth().height(42.dp).clip(RoundedCornerShape(10.dp)).background(corBotao).clickable(onClick = onAcao),
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(rotulo, color = corTexto, fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold)
                    Icon(if (concluida) Lucide.Check else Lucide.ArrowRight, null, tint = corTexto, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}