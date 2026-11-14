package com.vibeeco.app.ui.challenges

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibeeco.app.data.Category
import com.vibeeco.app.ui.theme.TextMuted

/** Cor de destaque de cada categoria (faixa do card, tag e ícones). */
fun Category.cor(): Color = when (this) {
    Category.AGUA -> Color(0xFF2563EB)
    Category.ENERGIA -> Color(0xFFD97706)
    Category.RESIDUOS -> Color(0xFF16A34A)
    Category.TRANSPORTE -> Color(0xFF7C3AED)
    Category.ALIMENTACAO -> Color(0xFFEA580C)
}

@Composable
fun TagChip(texto: String, fundo: Color, cor: Color, modifier: Modifier = Modifier) {
    Surface(shape = RoundedCornerShape(50), color = fundo, modifier = modifier) {
        Text(
            text = texto,
            color = cor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
        )
    }
}

@Composable
fun TagCategoria(category: Category) {
    val cor = category.cor()
    TagChip(category.label, cor.copy(alpha = 0.14f), lerp(cor, Color.Black, 0.3f))
}

@Composable
fun TagEscopo(texto: String) = TagChip(texto, Color(0xFFEEF2EF), Color(0xFF52665A))

/** Barra de progresso com rótulo e valor, igual ao medidor do desktop. */
@Composable
fun Medidor(
    rotulo: String,
    valor: String,
    fracao: Float,
    modifier: Modifier = Modifier,
    cores: List<Color> = listOf(Color(0xFF16A34A), Color(0xFF22C55E)),
) {
    val animada by animateFloatAsState(fracao.coerceIn(0f, 1f), tween(600), label = "medidor")
    Column(modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(rotulo, fontSize = 12.sp, color = TextMuted)
            Text(valor, fontSize = 12.sp, color = TextMuted)
        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFFE3ECE6))
        ) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animada)
                    .clip(RoundedCornerShape(50))
                    .background(Brush.horizontalGradient(cores))
            )
        }
    }
}

/** Botão principal (verde) ou suave (cinza), como "Participar" e "Abandonar". */
@Composable
fun BotaoDesafio(
    texto: String,
    suave: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val fundo: Brush = if (suave) SolidColor(Color(0xFFEEF2EF))
    else Brush.horizontalGradient(listOf(Color(0xFF0F6B34), Color(0xFF1FA856)))
    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(fundo)
            .clickable(onClick = onClick)
            .padding(vertical = 13.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = texto,
            color = if (suave) Color(0xFF52665A) else Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
        )
    }
}
