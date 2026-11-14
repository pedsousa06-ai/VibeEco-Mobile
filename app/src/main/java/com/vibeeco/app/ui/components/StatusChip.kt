package com.vibeeco.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibeeco.app.ui.theme.*

enum class StatusType { Ativo, Pendente, Concluido, Expirado, Neutro }

@Composable
fun StatusChip(
    text: String,
    status: StatusType = StatusType.Neutro,
    modifier: Modifier = Modifier
) {
    val (bg, fg) = when (status) {
        StatusType.Ativo     -> GreenContainer  to GreenOnContainer
        StatusType.Pendente  -> GoldXPContainer to GoldXPText
        StatusType.Concluido -> BlueContainer   to BlueAccentDark
        StatusType.Expirado  -> RedContainer    to RedDangerDark
        StatusType.Neutro    -> SurfaceVariant  to TextSecondary
    }

    Box(
        modifier = modifier
            .background(bg, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = fg,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.5.sp,
            letterSpacing = 0.2.sp
        )
    }
}
