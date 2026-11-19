package com.vibeeco.app.ui.challenges

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.vibeeco.app.ui.components.VibeCard
import com.vibeeco.app.ui.theme.BackgroundApp
import com.vibeeco.app.ui.theme.TextMain
import com.vibeeco.app.ui.theme.TextMuted

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DesafioDetalheScreen(
    desafio: Desafio,
    onBack: () -> Unit,
    onParticipar: () -> Unit,
    onAbandonar: () -> Unit,
    onConcluir: () -> Unit,
) {
    val d = desafio

    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(BackgroundApp)
            .statusBarsPadding(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Row(
                Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onBack)
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Lucide.ArrowLeft, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Voltar para desafios", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextMuted)
            }
        }

        item {
            VibeCard(Modifier.fillMaxWidth()) {
                Column(Modifier.clip(RoundedCornerShape(20.dp))) {
                    Box(Modifier.fillMaxWidth().height(6.dp).background(d.category.cor()))

                    Column(
                        Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            AnelProgresso(d.concluido)
                        }

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            // Correção aqui: usando .name em vez de .rotulo
                            TagGenerica(
                                texto = d.category.name.lowercase().replaceFirstChar { it.uppercase() },
                                corFundo = d.category.cor().copy(alpha = 0.15f),
                                corTexto = d.category.cor()
                            )
                            TagGenerica(texto = d.escopo.rotulo, corFundo = Color(0xFFF1F5F9), corTexto = Color(0xFF475569))
                            TagGenerica(texto = d.dificuldade.rotulo, corFundo = d.dificuldade.fundo, corTexto = d.dificuldade.texto)
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

        item {
            VibeCard(Modifier.fillMaxWidth()) {
                Column(
                    Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Você cumpriu este desafio?",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = TextMain,
                        textAlign = TextAlign.Center
                    )

                    if (!d.participando) {
                        Text(
                            text = "Participe do desafio para poder marcá-lo como concluído.",
                            fontSize = 14.sp,
                            color = Color(0xFF92400E),
                            textAlign = TextAlign.Center,
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
                                .padding(horizontal = 16.dp, vertical = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(Color(0xFFDCF8E6), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Lucide.Check, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(32.dp))
                            }

                            Text("Desafio concluído!", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF92400E))
                            Text("Você ganhou ${d.xp} XP e ${d.mc} MC.", fontSize = 14.sp, color = Color(0xFF92400E), textAlign = TextAlign.Center)
                        }
                    } else if (d.participando) {
                        BotaoDesafio(
                            texto = "Sim, cumpri o desafio",
                            suave = false,
                            onClick = onConcluir
                        )
                    }
                }
            }
        }

        item {
            if (d.participando) {
                BotaoDesafio("Abandonar desafio", suave = true, onClick = onAbandonar)
            } else {
                BotaoDesafio("Participar do desafio", suave = false, onClick = onParticipar)
            }
        }
    }
}

@Composable
private fun Fato(icone: ImageVector, texto: String, corTexto: Color, corIcone: Color, negrito: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icone, contentDescription = null, tint = corIcone, modifier = Modifier.size(15.dp))
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
private fun AnelProgresso(concluido: Boolean) {
    val animado by animateFloatAsState(if (concluido) 1f else 0f, tween(800), label = "anel")
    val corValor = if (concluido) Color(0xFFF59E0B) else Color(0xFF16A34A)

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
            Text(
                if (concluido) "100%" else "0%",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextMain
            )
            Text(
                if (concluido) "Concluído" else "Pendente",
                fontSize = 12.sp,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun TagGenerica(texto: String, corFundo: Color, corTexto: Color) {
    Box(
        Modifier
            .background(corFundo, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(texto, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = corTexto)
    }
}

@Composable
private fun Medidor(rotulo: String, valor: String, progresso: Float) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(rotulo, fontSize = 13.sp, color = TextMuted)
            Text(valor, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextMain)
        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFFE3ECE6))
        ) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progresso.coerceIn(0f, 1f))
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF16A34A))
            )
        }
    }
}

@Composable
private fun BotaoDesafio(texto: String, suave: Boolean, onClick: () -> Unit) {
    val bgColor = if (suave) Color.White else Color(0xFF16A34A)
    val borderColor = if (suave) Color(0xFFDDE7E0) else Color.Transparent
    val textColor = if (suave) Color(0xFFB91C1C) else Color.White

    Box(
        Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}