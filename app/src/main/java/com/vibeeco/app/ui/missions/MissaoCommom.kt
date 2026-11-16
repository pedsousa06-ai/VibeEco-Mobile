package com.vibeeco.app.ui.missions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibeeco.app.ui.theme.TextMuted

@Composable
internal fun BarraProgresso(progresso: Int, altura: Int = 7) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Progresso", color = TextMuted, fontSize = 12.sp)
            Text("$progresso%", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
        val prog = (progresso / 100f).coerceIn(0f, 1f)
        Box(Modifier.fillMaxWidth().height(altura.dp).clip(CircleShape).background(Color(0xFFD6EADB))) {
            Box(
                Modifier.fillMaxHeight().fillMaxWidth(prog).clip(CircleShape)
                    .background(Brush.horizontalGradient(listOf(Color(0xFF16A34A), Color(0xFF22C55E)))),
            )
        }
    }
}

internal data class DificuldadeUi(val fundo: Color, val texto: Color)

internal fun dificuldadeUi(dificuldade: String): DificuldadeUi =
    when (normalizar(dificuldade)) {
        "facil" -> DificuldadeUi(Color(0xFFDCFCE7), Color(0xFF0F6B34))
        "medio" -> DificuldadeUi(Color(0xFFFFF1DC), Color(0xFFC26A00))
        else -> DificuldadeUi(Color(0xFFFEE2E2), Color(0xFFC62828))
    }

private fun normalizar(s: String): String {
    val nfd = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD)
    return nfd.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "").trim().lowercase()
}

internal fun rotuloPrazo(progresso: Int): String {
    val dias = (100 - progresso) / 10
    return when {
        dias <= 0 -> "Encerra hoje"
        dias == 1 -> "Falta 1 dia"
        else -> "Faltam $dias dias"
    }
}

@Composable
internal fun Tag(texto: String, fundo: Color, cor: Color) {
    Text(
        texto,
        modifier = Modifier.clip(RoundedCornerShape(50)).background(fundo)
            .padding(horizontal = 10.dp, vertical = 3.dp),
        color = cor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 1,
    )
}