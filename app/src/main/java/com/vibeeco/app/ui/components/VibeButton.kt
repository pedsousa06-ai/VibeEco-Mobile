package com.vibeeco.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibeeco.app.ui.theme.GreenPrimary
import com.vibeeco.app.ui.theme.GreenPrimaryDark
import com.vibeeco.app.ui.theme.TextPrimary
import com.vibeeco.app.ui.theme.TextOnPrimary
import com.vibeeco.app.ui.theme.BorderNeutral
import com.vibeeco.app.ui.theme.BorderStrong
import com.vibeeco.app.ui.theme.RedDanger
import com.vibeeco.app.ui.theme.RedDangerDark

enum class VibeButtonStyle { Primary, Secondary, Danger }

/**
 * Botão estilo Duolingo: face colorida + "sombra" sólida embaixo.
 * Quando pressionado, "afunda" 4dp e a sombra encolhe → feedback tátil visual.
 */
@Composable
fun VibeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: VibeButtonStyle = VibeButtonStyle.Primary,
    enabled: Boolean = true,
    loading: Boolean = false,
    uppercase: Boolean = true,
    fillWidth: Boolean = true
) {
    val (faceColor, shadowColor, textColor) = when (style) {
        VibeButtonStyle.Primary   -> Triple(GreenPrimary, GreenPrimaryDark, TextOnPrimary)
        VibeButtonStyle.Secondary -> Triple(Color.White, BorderStrong, TextPrimary)
        VibeButtonStyle.Danger    -> Triple(RedDanger, RedDangerDark, TextOnPrimary)
    }

    val faceColorFinal   = if (enabled) faceColor   else faceColor.copy(alpha = 0.5f)
    val shadowColorFinal = if (enabled) shadowColor else shadowColor.copy(alpha = 0.5f)

    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val offsetY = if (pressed) 4.dp else 0.dp
    val shadowHeight = if (pressed) 0.dp else 4.dp

    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .then(if (fillWidth) Modifier.fillMaxWidth() else Modifier)
    ) {
        // sombra sólida embaixo (visual)
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = 4.dp)
                .clip(shape)
                .background(shadowColorFinal)
        )

        // sombra "extra" que reduz quando pressionado (efeito de afundar)
        if (shadowHeight > 0.dp) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(y = 4.dp)
                    .clip(shape)
                    .background(Color.Transparent)
            )
        }

        // face do botão
        Button(
            onClick = onClick,
            enabled = enabled && !loading,
            interactionSource = interaction,
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = faceColorFinal,
                contentColor   = textColor,
                disabledContainerColor = faceColorFinal,
                disabledContentColor   = textColor.copy(alpha = 0.7f)
            ),
            elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
            contentPadding = PaddingValues(vertical = 14.dp, horizontal = 20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = offsetY)
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = textColor,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = if (uppercase) text.uppercase() else text,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    letterSpacing = 0.8.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}