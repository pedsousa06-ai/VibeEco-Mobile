package com.vibeeco.app.ui.auth

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibeeco.app.ui.components.VibeButton
import com.vibeeco.app.ui.components.VibeBackground
import com.vibeeco.app.ui.components.VibeEcoLogo
import com.vibeeco.app.ui.theme.VibeColors
import kotlinx.coroutines.launch

private data class OnbPage(val emoji: String, val title: String, val text: String)

private val pages = listOf(
    OnbPage("🌱", "Pequenas ações, grande impacto",
        "Descubra como seus hábitos do dia a dia ajudam a cuidar do planeta e da sua comunidade."),
    OnbPage("🎯", "Complete desafios diários",
        "Receba missões de energia, água, resíduos e muito mais. Cada ação concluída vira pontos."),
    OnbPage("🏆", "Suba no ranking",
        "Acompanhe sua evolução, conquiste medalhas e inspire os colegas da sua instituição."),
)

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()
    val isLast = pagerState.currentPage == pages.lastIndex

    VibeBackground {
        Column(
            Modifier.fillMaxSize().systemBarsPadding().padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onFinish) { Text("Pular", color = Color.White, fontSize = 15.sp) }
            }
            Spacer(Modifier.height(24.dp))
            VibeEcoLogo()
            Spacer(Modifier.weight(1f))
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(vertical = 28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth()) { i ->
                        Column(
                            Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Box(
                                Modifier.size(104.dp).background(VibeColors.GreenSoft, CircleShape),
                                contentAlignment = Alignment.Center,
                            ) { Text(pages[i].emoji, fontSize = 52.sp) }
                            Spacer(Modifier.height(20.dp))

                            Text(pages[i].title, fontSize = 22.sp, fontWeight = FontWeight.Bold,
                                color = VibeColors.Title, textAlign = TextAlign.Center)

                            Spacer(Modifier.height(8.dp))

                            Text(pages[i].text, fontSize = 14.sp, color = VibeColors.Muted,
                                textAlign = TextAlign.Center, lineHeight = 21.sp,
                                modifier = Modifier.height(66.dp))
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        repeat(pages.size) { i ->
                            val selected = pagerState.currentPage == i
                            val width by animateDpAsState(if (selected) 24.dp else 8.dp, label = "dot")
                            Box(
                                Modifier.height(8.dp).width(width).background(
                                    if (selected) VibeColors.Green else VibeColors.Border.copy(alpha = 0.6f),
                                    CircleShape)
                            )
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                    Box(Modifier.padding(horizontal = 18.dp)) {
                        VibeButton(
                            text = if (isLast) "Começar" else "Próximo",
                            onClick = {
                                if (isLast) onFinish()
                                else scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                            },
                            style = com.vibeeco.app.ui.components.VibeButtonStyle.Primary
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}