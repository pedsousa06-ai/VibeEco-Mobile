package com.vibeeco.app.ui.learn

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibeeco.app.data.Article
import com.vibeeco.app.data.Category
import com.vibeeco.app.ui.components.*
import com.vibeeco.app.ui.theme.VibeColors

@Composable
fun LearnScreen(
    articles: List<Article>,
    readIds: Set<String>,
    onOpen: (Article) -> Unit,
) {
    var selected by remember { mutableStateOf<Category?>(null) }
    val filtered = articles.filter { selected == null || it.category == selected }

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            GradientHeader {
                Text("Aprender", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 26.sp)
                Text("Conteúdos rápidos para uma vida mais sustentável.", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                Spacer(Modifier.height(14.dp))
                Text("${readIds.size} de ${articles.size} lidos", color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
                VibeProgressBar(readIds.size / articles.size.toFloat())
            }
        }
        item { CategoryChips(selected) { selected = it } }
        items(filtered, key = { it.id }) { a ->
            val read = a.id in readIds
            VibeCard(Modifier.padding(horizontal = 16.dp).fillMaxWidth(), onClick = { onOpen(a) }) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                    Box(
                        Modifier.size(56.dp).background(VibeColors.GreenSoft, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center,
                    ) { Text(a.category.emoji, fontSize = 26.sp) }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(a.category.label.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold,
                            color = VibeColors.Green, letterSpacing = 0.8.sp)
                        Text(a.title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = VibeColors.Title)
                        Text(a.summary, fontSize = 12.sp, color = VibeColors.Muted, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            if (read) "✓ Lido · ${a.minutes} min" else "${a.minutes} min · +${a.points} pts",
                            fontSize = 12.sp, fontWeight = FontWeight.Medium,
                            color = if (read) VibeColors.Success else VibeColors.Muted,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ArticleDetailScreen(
    article: Article,
    isRead: Boolean,
    onBack: () -> Unit,
    onMarkRead: () -> Unit,
) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        GradientHeader {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
            }
            Spacer(Modifier.height(8.dp))
            Text(article.category.emoji, fontSize = 44.sp)
            Spacer(Modifier.height(8.dp))
            Text(article.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 30.sp)
            Spacer(Modifier.height(6.dp))
            Text("${article.category.label} · ${article.minutes} min de leitura",
                color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
        }
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            article.body.split("\n\n").forEach {
                Text(it, fontSize = 16.sp, lineHeight = 25.sp, color = VibeColors.Label)
            }
            Spacer(Modifier.height(8.dp))
            PrimaryButton(
                text = if (isRead) "Lido ✓ — pontos já recebidos" else "Marcar como lido (+${article.points} pts)",
                onClick = onMarkRead,
                enabled = !isRead,
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}
