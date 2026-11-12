package com.vibeeco.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vibeeco.app.ui.theme.GreenPrimary
import com.vibeeco.app.ui.theme.GreenPrimaryLight
import com.vibeeco.app.ui.theme.BorderNeutral

@Composable
fun VibeProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 12.dp,
    trackColor: Color = BorderNeutral,
    fillColor: Color = GreenPrimary,
    highlightColor: Color = GreenPrimaryLight,
    animated: Boolean = true
) {
    val clamped = progress.coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = clamped,
        animationSpec = tween(durationMillis = if (animated) 500 else 0),
        label = "progress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(50))
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .height(height)
                .clip(RoundedCornerShape(50))
                .background(fillColor)
        ) {
            // "Brilho" no topo — deixa com cara de cápsula (Duolingo)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height / 3)
                    .clip(RoundedCornerShape(50))
                    .background(highlightColor.copy(alpha = 0.4f))
            )
        }
    }
}