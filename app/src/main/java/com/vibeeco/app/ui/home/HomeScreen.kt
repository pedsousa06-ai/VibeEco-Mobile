package com.vibeeco.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vibeeco.app.data.Challenge
import com.vibeeco.app.ui.components.*
import com.vibeeco.app.ui.theme.*

@Composable
fun HomeScreen(
    userName: String,
    points: Int,
    streak: Int,
    co2Grams: Int,
    doneIds: Set<String>,
    dailyGoal: Int,
    featured: List<Challenge>,
    tip: String,
    onToggle: (Challenge) -> Unit,
    onSeeAll: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenLearn: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundNeutral)
            .verticalScroll(rememberScrollState())
    ) {
        // 1. Header Verde (Figma)
        VibeHeader(
            title = "Bom dia, $userName",
            subtitle = "Vamos salvar o planeta hoje?"
        ) {
            // Avatar do usuário (igual ao Figma)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
                    .clickable { onOpenProfile() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = "Perfil", tint = Color.White)
            }
        }

        // 2. Conteúdo que sobe por cima do header
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .offset(y = (-20).dp)
        ) {
            // Card de Progresso (Duolingo 3D style)
            FigmaCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Seu progresso", style = MaterialTheme.typography.titleMedium, color = TextSecondary)
                        Text("$points XP", style = MaterialTheme.typography.headlineMedium, color = GreenPrimaryDark, fontWeight = FontWeight.Black)
                    }
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(GreenContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = GreenPrimary)
                    }
                }
                Spacer(Modifier.height(16.dp))
                VibeProgressBar(progress = (points % 1000) / 1000f)
                Spacer(Modifier.height(8.dp))
                Text("Faltam ${1000 - (points % 1000)} XP para o próximo nível", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }

            Spacer(Modifier.height(16.dp))

            // Grid de Estatísticas (igual ao Figma)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCardFigma(Modifier.weight(1f), "Moedas", "$points", YellowAccent, YellowContainer, Icons.Default.ThumbUp)
                // ✅ AQUI ESTÁ A TROCA: De "Streak" para "Ofensiva"
                StatCardFigma(Modifier.weight(1f), "Ofensiva", "$streak d", OrangeAccent, OrangeContainer, Icons.Default.Whatshot)
            }

            Spacer(Modifier.height(24.dp))

            // Desafios de Hoje (Figma style)
            Text("Desafios de hoje", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
            Spacer(Modifier.height(12.dp))

            featured.take(2).forEach { challenge ->
                val done = challenge.id in doneIds
                FigmaCard(modifier = Modifier.padding(bottom = 12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(BlueContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Eco, contentDescription = null, tint = BlueAccent)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(challenge.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("+${challenge.points} XP", style = MaterialTheme.typography.bodySmall, color = GreenPrimary)
                        }
                        StatusChip(if (done) "Feito" else "Ativo", if (done) StatusType.Concluido else StatusType.Ativo)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            VibeButton("Ver todos os desafios", onSeeAll, style = VibeButtonStyle.Primary)
            Spacer(Modifier.height(32.dp))
        }
    }
}

// Helper para os cards pequenos do Figma
@Composable
private fun StatCardFigma(modifier: Modifier, title: String, value: String, color: Color, bg: Color, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    FigmaCard(modifier = modifier) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(bg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(title, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = TextPrimary)
    }
}