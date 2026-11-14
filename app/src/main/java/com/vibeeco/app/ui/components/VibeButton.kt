package com.vibeeco.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibeeco.app.ui.theme.*

enum class VibeButtonStyle { Primary, Secondary, Danger, Outline }

/**
 * Botão moderno do VibeEco: degradê suave, cantos refinados e feedback tátil fluido.
 */
@Composable
fun VibeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: VibeButtonStyle = VibeButtonStyle.Primary,
    enabled: Boolean = true,
    loading: Boolean = false,
    uppercase: Boolean = false,
    fillWidth: Boolean = true
) {
    val shape = RoundedCornerShape(10.dp)

    val (bgBrush, textColor, borderStroke) = when (style) {
        VibeButtonStyle.Primary -> Triple(
            if (enabled) Brush.horizontalGradient(listOf(Color(0xFF1A8F4E), Color(0xFF22C55E)))
            else SolidColor(GreenBrand.copy(alpha = 0.45f)),
            Color.White,
            null
        )
        VibeButtonStyle.Secondary -> Triple(
            SolidColor(Color.White),
            TextMain,
            BorderStroke(1.dp, BorderInput)
        )
        VibeButtonStyle.Outline -> Triple(
            SolidColor(Color.Transparent),
            GreenBrand,
            BorderStroke(1.5.dp, GreenBrand)
        )
        VibeButtonStyle.Danger -> Triple(
            SolidColor(RedDanger),
            Color.White,
            null
        )
    }

    val finalModifier = modifier
        .then(if (fillWidth) Modifier.fillMaxWidth() else Modifier)
        .height(48.dp)

    if (borderStroke != null) {
        OutlinedButton(
            onClick = onClick,
            enabled = enabled && !loading,
            shape = shape,
            border = borderStroke,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = textColor,
                containerColor = Color.White
            ),
            modifier = finalModifier
        ) {
            ButtonContent(text, uppercase, textColor, loading)
        }
    } else {
        Button(
            onClick = onClick,
            enabled = enabled && !loading,
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            ),
            contentPadding = PaddingValues(0.dp),
            modifier = finalModifier
                .shadow(
                    elevation = if (enabled && !loading && style == VibeButtonStyle.Primary) 3.dp else 0.dp,
                    shape = shape,
                    ambientColor = GreenBrand.copy(alpha = 0.3f),
                    spotColor = GreenBrand.copy(alpha = 0.3f)
                )
                .clip(shape)
                .background(bgBrush)
        ) {
            ButtonContent(text, uppercase, textColor, loading)
        }
    }
}

@Composable
private fun ButtonContent(
    text: String,
    uppercase: Boolean,
    textColor: Color,
    loading: Boolean
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
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            letterSpacing = if (uppercase) 0.8.sp else 0.2.sp,
            textAlign = TextAlign.Center,
            color = textColor
        )
    }
}
