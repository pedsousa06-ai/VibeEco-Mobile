package com.vibeeco.app.ui.missions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Star
import com.composables.icons.lucide.X
import com.vibeeco.app.ui.theme.TextMain
import com.vibeeco.app.ui.theme.TextMuted

@Composable
fun QuizModal(onFechar: () -> Unit, onEnviar: () -> Unit) {
    Dialog(onDismissRequest = onFechar) {
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cabeçalho
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column {
                    Text("Descarte Consciente - Etapa 4 de 4", color = TextMuted, fontSize = 12.sp)
                    Text("Responder quiz sobre reciclagem", color = TextMain, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Icon(Lucide.X, "Fechar", tint = TextMuted, modifier = Modifier.size(24.dp).clickable { onFechar() })
            }

            // XP Badge
            Row(
                Modifier.clip(CircleShape).background(Color(0xFFFFF7DE)).padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Lucide.Star, null, tint = Color(0xFFF5B301), modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("+40 XP ao concluir", color = Color(0xFF8A5A00), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Text("Responda todas as perguntas. É preciso acertar pelo menos 2 de 3 para concluir.", color = TextMuted, fontSize = 14.sp)

            // Perguntas
            PerguntaQuiz("1. Qual cor de lixeira é usada para descartar plástico?", listOf("Azul", "Vermelho", "Verde", "Amarelo"))
            PerguntaQuiz("2. Qual material deve ser colocado na lixeira azul?", listOf("Vidro", "Papel e papelão", "Metal", "Resíduo orgânico"))
            PerguntaQuiz("3. Onde descartar restos de comida?", listOf("Lixeira azul", "Lixeira marrom", "Lixeira verde", "Lixeira amarela"))

            Spacer(Modifier.height(8.dp))

            // Rodapé
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text("Cancelar", color = TextMain, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable { onFechar() }.padding(12.dp))
                Spacer(Modifier.width(8.dp))
                Box(
                    Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFF16A34A)).clickable { onEnviar() }.padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text("Enviar respostas", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun PerguntaQuiz(pergunta: String, opcoes: List<String>) {
    var selecionada by remember { mutableStateOf<String?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(pergunta, color = TextMain, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        opcoes.forEach { opcao ->
            val ativa = selecionada == opcao
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                    .border(1.dp, if (ativa) Color(0xFF16A34A) else Color(0xFFE5E7EB), RoundedCornerShape(8.dp))
                    .selectable(selected = ativa, onClick = { selecionada = opcao })
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier.size(20.dp).clip(CircleShape)
                        .border(2.dp, if (ativa) Color(0xFF16A34A) else Color(0xFFD1D5DB), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (ativa) Box(Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF16A34A)))
                }
                Spacer(Modifier.width(12.dp))
                Text(opcao, color = TextMain, fontSize = 14.sp)
            }
        }
    }
}