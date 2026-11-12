package com.vibeeco.app.ui.auth

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibeeco.app.ui.components.VibeBackground
import com.vibeeco.app.ui.components.VibeEcoLogo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val alphaAnim = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(0.85f) }

    LaunchedEffect(Unit) {
        launch { alphaAnim.animateTo(1f, tween(700)) }
        launch { scaleAnim.animateTo(1f, tween(700)) }
        delay(1800)
        onFinished()
    }

    VibeBackground {
        Column(
            Modifier.align(Alignment.Center).graphicsLayer {
                this.alpha = alphaAnim.value
                scaleX = scaleAnim.value
                scaleY = scaleAnim.value
            },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            VibeEcoLogo(textSize = 44, iconSize = 58)
            Spacer(Modifier.height(12.dp))
            Text(
                "Plataforma de conscientização\nà sustentabilidade",
                color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp, textAlign = TextAlign.Center,
            )
        }
        CircularProgressIndicator(
            Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = 48.dp).size(28.dp),
            color = Color.White, strokeWidth = 2.5.dp,
        )
    }
}
