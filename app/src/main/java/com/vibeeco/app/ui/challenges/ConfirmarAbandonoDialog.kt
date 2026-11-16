package com.vibeeco.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.composables.icons.lucide.LogOut
import com.composables.icons.lucide.Lucide

/**
 * Pergunta se a pessoa realmente quer abandonar o desafio.
 * Tocar fora do card ou no botão voltar equivale a "Cancelar".
 */
@Composable
fun ConfirmarAbandonoDialog(
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit,
    titulo: String = "Abandonar desafio?",
    mensagem: String = "Tem certeza que deseja abandonar este desafio? Seu progresso nele poderá ser perdido.",
) {
    Dialog(
        onDismissRequest = onCancelar,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .widthIn(max = 420.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(start = 24.dp, end = 24.dp, top = 28.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFDECEC)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Lucide.LogOut, contentDescription = null, tint = Color(0xFFC0392B), modifier = Modifier.size(30.dp))
            }

            Text(
                text = titulo,
                color = Color(0xFF12261A),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = mensagem,
                color = Color(0xFF3D4F44),
                fontSize = 14.sp,
                lineHeight = 21.sp,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(6.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BotaoDialogo(
                    texto = "Cancelar",
                    fundo = Color(0xFFEEF6F0),
                    corTexto = Color(0xFF1F5D37),
                    modifier = Modifier.weight(1f),
                    onClick = onCancelar,
                )
                BotaoDialogo(
                    texto = "Abandonar",
                    fundo = Color(0xFFC0392B),
                    corTexto = Color.White,
                    modifier = Modifier.weight(1f),
                    onClick = onConfirmar,
                )
            }
        }
    }
}

@Composable
private fun BotaoDialogo(
    texto: String,
    fundo: Color,
    corTexto: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(fundo)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = 13.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(texto, color = corTexto, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}
