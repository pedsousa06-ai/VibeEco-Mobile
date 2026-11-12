package com.vibeeco.app.ui.ranking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibeeco.app.data.RankEntry
import com.vibeeco.app.ui.components.*
import com.vibeeco.app.ui.theme.Colors

@Composable
private fun PodiumSpot(entry: RankEntry, position: Int, barHeight: Dp, modifier: Modifier = Modifier) {
    val medal = when (position) { 1 -> "🥇"; 2 -> "🥈"; else -> "🥉" }
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(medal, fontSize = 26.sp)
        Spacer(Modifier.height(4.dp))
        Avatar(
            entry.name, if (position == 1) 64.dp else 54.dp,
            background = if (entry.isMe) Colors.GreenBright else Color.White,
            textColor = Colors.GreenDark,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            if (entry.isMe) "Você" else entry.name.split(" ").first(),
            color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp,
        )
        Text("${entry.points} pts", color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))
        Box(
            Modifier.fillMaxWidth().height(barHeight).background(
                Color.White.copy(alpha = 0.18f),
                RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)),
            contentAlignment = Alignment.Center,
        ) { Text("$position", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp) }
    }
}

@Composable
fun RankingScreen(entries: List<RankEntry>) {
    val sorted = entries.sortedByDescending { it.points }
    val myPosition = sorted.indexOfFirst { it.isMe } + 1
    val top = sorted.take(3)

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            GradientHeader {
                Text("Ranking", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 26.sp)
                Text("Veja quem mais está cuidando do planeta.", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                Spacer(Modifier.height(20.dp))
                if (top.size == 3) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Bottom) {
                        PodiumSpot(top[1], 2, 70.dp, Modifier.weight(1f))
                        PodiumSpot(top[0], 1, 100.dp, Modifier.weight(1f))
                        PodiumSpot(top[2], 3, 52.dp, Modifier.weight(1f))
                    }
                }
            }
        }
        if (myPosition > 0) {
            item {
                VibeCard(Modifier.padding(horizontal = 16.dp).fillMaxWidth(), containerColor = Colors.GreenSoft) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("📍", fontSize = 22.sp)
                        Spacer(Modifier.width(10.dp))
                        Text("Sua posição atual: ${myPosition}º lugar", fontWeight = FontWeight.SemiBold, color = Colors.GreenDark)
                    }
                }
            }
        }
        itemsIndexed(sorted.drop(3)) { index, entry ->
            val pos = index + 4
            VibeCard(
                Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                containerColor = if (entry.isMe) Colors.GreenSoft else Color.White,
            ) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("$pos", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Colors.Muted,
                        modifier = Modifier.width(32.dp))
                    Avatar(entry.name, 42.dp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(if (entry.isMe) "${entry.name} (você)" else entry.name,
                            fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Colors.Title)
                        Text(entry.course, fontSize = 12.sp, color = Colors.Muted)
                    }
                    Text("${entry.points} pts", fontWeight = FontWeight.Bold, color = Colors.Green, fontSize = 14.sp)
                }
            }
        }
    }
}
