package com.vibeeco.app.ui.auth

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibeeco.app.R
import com.vibeeco.app.ui.theme.GreenBrandLight
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val alphaAnim = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(0.88f) }

    LaunchedEffect(Unit) {
        launch { alphaAnim.animateTo(1f, tween(650)) }
        launch { scaleAnim.animateTo(1f, tween(650)) }
        delay(1800)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(listOf(Color(0xFF1D4D32), Color(0xFF39865B)))
            )
    ) {
        Column(
            Modifier
                .align(Alignment.Center)
                .graphicsLayer {
                    this.alpha = alphaAnim.value
                    scaleX = scaleAnim.value
                    scaleY = scaleAnim.value
                }
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.logo_principal),
                contentDescription = "VibeEco",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(220.dp)
                    .height(70.dp)
            )

            Spacer(Modifier.height(18.dp))

            Text(
                text = buildAnnotatedString {
                    append("Sustentabilidade que\n")
                    withStyle(SpanStyle(color = Color(0xFF22C55E), fontWeight = FontWeight.SemiBold)) {
                        append("engaja e transforma")
                    }
                },
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 15.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center,
            )
        }

        CircularProgressIndicator(
            Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 54.dp)
                .size(26.dp),
            color = Color.White,
            strokeWidth = 2.dp,
        )
    }
}
