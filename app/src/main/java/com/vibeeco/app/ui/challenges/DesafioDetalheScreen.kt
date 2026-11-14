package com.vibeeco.app.ui.challenges

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Coins
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Star
import com.composables.icons.lucide.User
import com.composables.icons.lucide.Users
import com.vibeeco.app.data.Desafio
import com.vibeeco.app.data.Escopo
import com.vibeeco.app.data.Tarefa
import com.vibeeco.app.ui.components.VibeCard
import com.vibeeco.app.ui.theme.BackgroundApp
import com.vibeeco.app.ui.theme.BorderLight
import com.vibeeco.app.ui.theme.TextMain
import com.vibeeco.app.ui.theme.TextMuted

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DesafioDetalheScreen(
    desafio: Desafio,
    onBack: () -> Unit,
    onParticipar: () -> Unit,
    onAbandonar: () -> Unit,
    onToggleTarefa: (Tarefa) -> Unit,
) {
    val d = desafio

    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(BackgroundApp)
            .statusBarsPadding(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Voltar
        item {
            Row(
                Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onBack)
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Lucide.ArrowLeft, null, tint = TextMuted, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Voltar para desafios", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextMuted)
            }
        }

        // Cabeçalho
        item {
            VibeCard(Modifier.fillMaxWidth()) {
                Column(Modifier.clip(RoundedCornerShape(20.dp))) {
                    Box(Modifier.fillMaxWidth().height(6.dp).background(d.category.cor()))

                    Column(
                        Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            AnelProgresso(d.progresso, d.tarefasFeitas, d.tarefas.size, d.concluido)
                        }

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            TagCategoria(d.category)
                            TagEscopo(d.escopo.rotulo)
                            TagChip(d.dificuldade.rotulo, d.dificuldade.fundo, d.dificuldade.texto)
                        }

                        Text(
                            d.titulo,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                            lineHeight = 29.sp,
                            color = TextMain,
                        )
                        Text(d.descricao, fontSize = 15.sp, lineHeight = 22.sp, color = Color(0xFF4B5D52))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(18.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Fato(Lucide.Star, "${d.xp} XP", Color(0xFF92400E), Color(0xFFF59E0B), negrito = true)
                            Fato(Lucide.Coins, "${d.mc} MC", Color(0xFF15803D), Color(0xFF64748B), negrito = true)
                            Fato(Lucide.Clock, "${d.rotuloDias} restantes", TextMuted, TextMuted)
                            Fato(
                                if (d.escopo == Escopo.COLETIVO) Lucide.Users else Lucide.User,
                                "${d.participantes} de ${d.meta} participantes",
                                TextMuted, TextMuted,
                            )
                        }

                        Medidor("Participação", "${d.participacao}%", d.participacao / 100f)
                    }
                }
            }
        }

        // Tarefas
        item {
            VibeCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("Tarefas", fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = TextMain, modifier = Modifier.weight(1f))
                        if (d.participando) {
                            Text("${d.tarefasFeitas} de ${d.tarefas.size} concluídas", fontSize = 13.sp, color = TextMuted)
                        }
                    }

                    if (!d.participando) {
                        Text(
                            "Participe do desafio para começar a marcar as tarefas.",
                            fontSize = 14.sp,
                            color = Color(0xFF92400E),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFF7E6), RoundedCornerShape(10.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                        )
                    }

                    if (d.concluido) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFFBEB), RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFFCD34D), RoundedCornerShape(12.dp))
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            Text("Desafio concluído!", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF92400E))
                            Text("Você ganhou ${d.xp} XP e ${d.mc} MC.", fontSize = 14.sp, color = Color(0xFF92400E))
                        }
                    }

                    d.tarefas.forEach { t ->
                        TarefaItem(t, habilitada = d.participando, onClick = { onToggleTarefa(t) })
                    }
                }
            }
        }

        // Ação
        item {
            if (d.participando) BotaoDesafio("Abandonar desafio", suave = true, onClick = onAbandonar)
            else BotaoDesafio("Participar do desafio", suave = false, onClick = onParticipar)
        }
    }
}

@Composable
private fun Fato(icone: ImageVector, texto: String, corTexto: Color, corIcone: Color, negrito: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icone, null, tint = corIcone, modifier = Modifier.size(15.dp))
        Spacer(Modifier.width(6.dp))
        Text(
            texto,
            fontSize = 13.sp,
            color = corTexto,
            fontWeight = if (negrito) FontWeight.Bold else FontWeight.Normal,
        )
    }
}

@Composable
private fun AnelProgresso(progresso: Int, feitas: Int, total: Int, completo: Boolean) {
    val animado by animateFloatAsState(progresso / 100f, tween(800), label = "anel")
    val corValor = if (completo) Color(0xFFF59E0B) else Color(0xFF16A34A)

    Box(Modifier.size(132.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val traco = 10.dp.toPx()
            val topLeft = Offset(traco / 2, traco / 2)
            val tamanho = Size(size.width - traco, size.height - traco)
            drawArc(
                color = Color(0xFFE3ECE6), startAngle = 0f, sweepAngle = 360f, useCenter = false,
                topLeft = topLeft, size = tamanho, style = Stroke(traco),
            )
            drawArc(
                color = corValor, startAngle = -90f, sweepAngle = 360f * animado, useCenter = false,
                topLeft = topLeft, size = tamanho, style = Stroke(traco, cap = StrokeCap.Round),
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$progresso%", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = TextMain)
            Text("$feitas/$total tarefas", fontSize = 12.sp, color = TextMuted)
        }
    }
}

@Composable
private fun TarefaItem(t: Tarefa, habilitada: Boolean, onClick: () -> Unit) {
    val forma = RoundedCornerShape(14.dp)
    Row(
        Modifier
            .fillMaxWidth()
            .alpha(if (habilitada) 1f else 0.7f)
            .clip(forma)
            .background(if (t.feita) Color(0xFFDCF8E6) else Color.White)
            .border(1.dp, if (t.feita) Color(0xFF22C55E) else BorderLight, forma)
            .clickable(enabled = habilitada, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        val formaCaixa = RoundedCornerShape(8.dp)
        Box(
            Modifier
                .padding(top = 1.dp)
                .size(26.dp)
                .clip(formaCaixa)
                .background(if (t.feita) Color(0xFF16A34A) else Color.White)
                .border(2.dp, if (t.feita) Color(0xFF16A34A) else Color(0xFFB6CABD), formaCaixa),
            contentAlignment = Alignment.Center,
        ) {
            if (t.feita) Icon(Lucide.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
        }

        Spacer(Modifier.width(14.dp))

        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                t.titulo,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (t.feita) Color(0xFF0F5A2C) else TextMain,
                textDecoration = if (t.feita) TextDecoration.LineThrough else null,
            )
            Text(t.descricao, fontSize = 14.sp, lineHeight = 19.sp, color = Color(0xFF4B5D52))
        }
    }
}
