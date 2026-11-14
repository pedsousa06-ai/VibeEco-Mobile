package com.vibeeco.app.ui.auth

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
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
import com.vibeeco.app.ui.components.PrimaryButton
import com.vibeeco.app.ui.components.VibeBackground
import com.vibeeco.app.ui.components.VibeEcoLogo
import com.vibeeco.app.ui.theme.BorderInput
import com.vibeeco.app.ui.theme.GreenBrand
import com.vibeeco.app.ui.theme.GreenContainer
import kotlinx.coroutines.launch

private data class OnbPage(val emoji: String, val title: String, val text: String)

private val pages = listOf(
    OnbPage("🌱", "Pequenas ações, grande impacto",
        "Descubra como seus hábitos diários ajudam a cuidar do planeta e transformar a sua comunidade acadêmica."),
    OnbPage("🎯", "Complete desafios diários",
        "Participe de missões de energia, água, reciclagem e consumo consciente. Cada ação concluída gera pontos."),
    OnbPage("🏆", "Suba no ranking",
        "Acompanhe sua evolução na instituição, conquiste medalhas exclusivas e inspire seus colegas a engajar."),
)

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()
    val isLast = pagerState.currentPage == pages.lastIndex

    VibeBackground {
        Column(
            Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onFinish) {
                    Text("Pular", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(8.dp))

            VibeEcoLogo(modifier = Modifier.widthIn(max = 220.dp))

            Spacer(Modifier.weight(1f))

            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                shadowElevation = 12.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp),
            ) {
                Column(
                    Modifier.padding(vertical = 28.dp, horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth()) { i ->
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Box(
                                Modifier
                                    .size(92.dp)
                                    .background(GreenContainer, CircleShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(pages[i].emoji, fontSize = 44.sp)
                            }

                            Spacer(Modifier.height(20.dp))

                            Text(
                                text = pages[i].title,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF203B27),
                                textAlign = TextAlign.Center
                            )

                            Spacer(Modifier.height(8.dp))

                            Text(
                                text = pages[i].text,
                                fontSize = 13.5.sp,
                                color = Color(0xFF829084),
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp,
                                modifier = Modifier.height(64.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        repeat(pages.size) { i ->
                            val selected = pagerState.currentPage == i
                            val width by animateDpAsState(if (selected) 24.dp else 8.dp, label = "dot")
                            Box(
                                Modifier
                                    .height(8.dp)
                                    .width(width)
                                    .background(
                                        if (selected) GreenBrand else BorderInput,
                                        CircleShape
                                    )
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    PrimaryButton(
                        text = if (isLast) "Começar agora" else "Próximo",
                        onClick = {
                            if (isLast) onFinish()
                            else scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
