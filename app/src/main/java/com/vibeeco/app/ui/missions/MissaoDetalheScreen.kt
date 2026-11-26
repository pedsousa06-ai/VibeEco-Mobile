package com.vibeeco.app.ui.missions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Lightbulb
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Play
import com.composables.icons.lucide.Star
import com.vibeeco.app.data.MissaoUi
import com.vibeeco.app.data.StatusMissao
import com.vibeeco.app.data.api.model.EtapaMissaoApiDto
import com.vibeeco.app.ui.components.VibeCard
import com.vibeeco.app.ui.theme.TextMain
import com.vibeeco.app.ui.theme.TextMuted

@Composable
fun MissaoDetalheScreen(
    ui: MissaoUi,
    onVoltar: () -> Unit,
    onParticipar: () -> Unit,
    onAvancar: (EtapaMissaoApiDto) -> Boolean
) {
    val m = ui.missao
    val categoriaStr = m.categoria?.firstOrNull()?.nomeCategoria ?: "Geral"
    val dificuldadeStr = m.nomeDificuldade ?: "Fácil"
    val dif = dificuldadeUi(dificuldadeStr)

    val etapas = m.etapas.orEmpty()
    val participando = ui.status != StatusMissao.Disponivel
    val concluidaTotal = ui.status == StatusMissao.Concluida
    // quantas etapas já foram feitas (o progresso da API é feito/total*100, arredondado)
    val feitas = (etapas.size * (ui.progresso / 100f)).roundToInt().coerceIn(0, etapas.size)
    val proximaEtapa = if (participando && !concluidaTotal) etapas.getOrNull(feitas) else null

    // evita toques repetidos enquanto a etapa está sendo enviada
    var enviando by remember { mutableStateOf(false) }
    LaunchedEffect(ui.progresso) { enviando = false }
    LaunchedEffect(enviando) { if (enviando) { delay(3000); enviando = false } }
    // como no front web: confirmação antes de participar e antes de marcar a etapa
    var confirmandoParticipar by remember { mutableStateOf(false) }
    var etapaEmConfirmacao by remember { mutableStateOf<EtapaMissaoApiDto?>(null) }
    val concluirEtapa: (EtapaMissaoApiDto) -> Unit = { etapa ->
        if (!enviando) etapaEmConfirmacao = etapa
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFFF7FAF8))
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            Modifier.clip(RoundedCornerShape(8.dp)).clickable(onClick = onVoltar).padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Lucide.ArrowLeft, null, tint = TextMuted, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Text("Voltar para Missões", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextMuted)
        }

        VibeCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Tag(categoriaStr, Color(0xFFDCFCE7), Color(0xFF0F6B34))
                    Tag(dificuldadeStr, dif.fundo, dif.texto)
                }

                Text(m.nomeMissao ?: "Missão", color = TextMain, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 34.sp)
                Text(m.descricaoMissao ?: "Sem descrição.", color = TextMuted, fontSize = 15.sp, lineHeight = 22.sp)

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Metrica(Lucide.Star, "${m.xpMissao ?: 0}", "XP", Color(0xFFFFF7DE), Color(0xFF8A5A00), Modifier.weight(1f))
                    Metrica(Lucide.Clock, "${m.prazoDias ?: 0} dias", "Prazo", Color(0xFFF0F4F1), TextMuted, Modifier.weight(1f))
                }

                Spacer(Modifier.height(4.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Progresso geral", color = TextMain, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("${ui.progresso}%", color = Color(0xFF15803D), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Box(Modifier.fillMaxWidth().height(10.dp).clip(CircleShape).background(Color(0xFFD6EADB))) {
                    Box(Modifier.fillMaxHeight().fillMaxWidth(ui.progresso / 100f).clip(CircleShape).background(Color(0xFF16A34A)))
                }
                Text("${m.etapas?.size ?: 0} etapas no total", color = TextMuted, fontSize = 12.sp)
            }
        }

        VibeCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Etapas da Missão", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextMain)

                etapas.forEachIndexed { index, etapa ->
                    val concluida = index < feitas || concluidaTotal
                    val andamento = participando && !concluidaTotal && index == feitas

                    EtapaItem(
                        titulo = etapa.nomeEtapa ?: "Etapa ${index + 1}",
                        xp = etapa.qtdeXp ?: 0,
                        concluida = concluida,
                        andamento = andamento
                    ) { if (andamento) concluirEtapa(etapa) }
                }

                Spacer(Modifier.height(4.dp))

                if (!participando) {
                    Box(
                        Modifier.fillMaxWidth().height(50.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFF16A34A))
                            .clickable { confirmandoParticipar = true },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("Participar da Missão", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (proximaEtapa != null) {
                    Box(
                        Modifier.fillMaxWidth().height(50.dp).clip(RoundedCornerShape(10.dp))
                            .background(if (enviando) Color(0xFF86C99A) else Color(0xFF16A34A))
                            .clickable(enabled = !enviando) { concluirEtapa(proximaEtapa) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            if (enviando) "Salvando conclusão..." else "Continuar Missão",
                            color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold,
                        )
                    }
                } else if (concluidaTotal) {
                    Text("Missão concluída! Veja outras missões para continuar ganhando XP.", color = Color(0xFF15803D), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        VibeCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Sobre a missão", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextMain)
                LinhaInfo("Categoria", categoriaStr)
                LinhaInfo("Dificuldade", dificuldadeStr)
                LinhaInfo("Prazo", "${m.prazoDias ?: 0} dias restantes")
                LinhaInfo("XP disponível", "${m.xpMissao ?: 0} XP")
            }
        }

        VibeCard(Modifier.fillMaxWidth()) {
            Column(
                Modifier.background(Color(0xFFE6F7EC)).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Lucide.Lightbulb, null, tint = Color(0xFF0F5B2D), modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Dica da missão", color = Color(0xFF0F5B2D), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Text(m.dicaMissao ?: "Nenhuma dica disponível.", color = Color(0xFF1F5A37), fontSize = 14.sp, lineHeight = 20.sp)
            }
        }

        Spacer(Modifier.height(24.dp))
    }

    if (confirmandoParticipar) {
        AlertDialog(
            onDismissRequest = { confirmandoParticipar = false },
            title = { Text("Participar desta missão") },
            text = { Text("Você tem certeza que deseja participar da missão “${m.nomeMissao ?: ""}”?") },
            confirmButton = {
                TextButton(onClick = { confirmandoParticipar = false; onParticipar() }) { Text("Sim, participar") }
            },
            dismissButton = { TextButton(onClick = { confirmandoParticipar = false }) { Text("Cancelar") } },
        )
    }

    etapaEmConfirmacao?.let { etapa ->
        AlertDialog(
            onDismissRequest = { etapaEmConfirmacao = null },
            title = { Text(etapa.nomeEtapa ?: "Etapa") },
            text = {
                Text("+${etapa.qtdeXp ?: 0} XP ao concluir.\n\nRealize esta etapa e, quando terminar, marque como concluída para receber o seu XP.")
            },
            confirmButton = {
                TextButton(onClick = { etapaEmConfirmacao = null; enviando = true; onAvancar(etapa) }) { Text("Marcar como concluída") }
            },
            dismissButton = { TextButton(onClick = { etapaEmConfirmacao = null }) { Text("Agora não") } },
        )
    }
}

@Composable
private fun Metrica(icone: androidx.compose.ui.graphics.vector.ImageVector, valor: String, rotulo: String, fundo: Color, cor: Color, modifier: Modifier = Modifier) {
    Column(
        modifier.clip(RoundedCornerShape(12.dp)).background(fundo).padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(icone, null, tint = cor, modifier = Modifier.size(20.dp))
        Spacer(Modifier.height(6.dp))
        Text(valor, color = cor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Text(rotulo, color = TextMuted, fontSize = 11.sp)
    }
}

@Composable
private fun EtapaItem(titulo: String, xp: Int, concluida: Boolean, andamento: Boolean, onClick: () -> Unit = {}) {
    val bg = if (andamento) Color.White else Color(0xFFF1F6F2)
    val border = if (andamento) Color(0xFF22C55E) else Color.Transparent

    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(bg)
            .border(2.dp, border, RoundedCornerShape(12.dp))
            .clickable(enabled = andamento, onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(32.dp).clip(CircleShape)
                .background(if (concluida || andamento) Color(0xFF16A34A) else Color.White)
                .border(1.5.dp, Color(0xFFC8D4CB), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (concluida) Icon(Lucide.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
            else if (andamento) Icon(Lucide.Play, null, tint = Color.White, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.width(14.dp))
        Text(titulo, color = if (concluida || andamento) TextMain else TextMuted, fontSize = 15.sp, modifier = Modifier.weight(1f))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Lucide.Star, null, tint = Color(0xFFF5B301), modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text("$xp XP", color = Color(0xFF8A5A00), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun LinhaInfo(rotulo: String, valor: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(rotulo, color = TextMuted, fontSize = 14.sp)
        Text(valor, color = TextMain, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

