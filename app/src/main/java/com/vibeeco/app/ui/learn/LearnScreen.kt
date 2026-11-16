package com.vibeeco.app.ui.learn

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.Star
import com.vibeeco.app.data.Article
import com.vibeeco.app.data.Category
import com.vibeeco.app.ui.components.GradientHeader
import com.vibeeco.app.ui.components.VibeCard
import com.vibeeco.app.ui.components.VibeProgressBar
import com.vibeeco.app.ui.theme.GoldXP
import com.vibeeco.app.ui.theme.GoldXPText

@Composable
fun LearnScreen(articles: List<Article>, readIds: Set<String>, onOpen: (Article) -> Unit) {
    var busca by remember { mutableStateOf("") }
    var filtro by remember { mutableStateOf<Category?>(null) }

    val categoriasDisponiveis: List<Category?> = remember(articles) {
        listOf<Category?>(null) + articles.map { it.category }.distinct()
    }

    val filtrados: List<Article> = remember(articles, busca, filtro, readIds) {
        articles
            .filter { a ->
                val catOk = filtro == null || a.category == filtro
                val termo = normalizar(busca.trim())
                val buscaOk = termo.isEmpty() || normalizar(a.title).contains(termo)
                catOk && buscaOk
            }
            .sortedBy { it.id in readIds }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            GradientHeader {
                Column(Modifier.statusBarsPadding()) {
                    Text("Aprender", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 26.sp)
                    Text("Conteúdos rápidos para uma vida mais sustentável.", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                    Spacer(Modifier.height(14.dp))
                    Text("${readIds.size} de ${articles.size} lidos", color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
                    Spacer(Modifier.height(8.dp))
                    val prog = if (articles.isEmpty()) 0f else readIds.size / articles.size.toFloat()
                    VibeProgressBar(prog.coerceIn(0f, 1f))
                }
            }
        }
        item {
            OutlinedTextField(
                value = busca,
                onValueChange = { busca = it },
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                placeholder = { Text("Buscar conteúdos...") },
                leadingIcon = { Icon(Lucide.Search, null, modifier = Modifier.size(18.dp)) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    cursorColor = MaterialTheme.colorScheme.primary,
                ),
            )
        }
        item {
            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categoriasDisponiveis) { cat ->
                    val selecionado = filtro == cat
                    Surface(
                        onClick = { filtro = cat },
                        shape = RoundedCornerShape(50),
                        color = if (selecionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, if (selecionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline),
                    ) {
                        Text(
                            text = cat?.label ?: "Todos",
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (selecionado) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        }
        if (filtrados.isEmpty()) {
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth().background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp)).padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("Nenhum conteúdo encontrado.", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("Tente outro termo ou escolha outra categoria.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.5.sp)
                }
            }
        }
        items(filtrados, key = { it.id }) { a ->
            ArticleCard(article = a, read = a.id in readIds, onClick = { onOpen(a) }, modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}

@Composable
private fun ArticleCard(article: Article, read: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    VibeCard(modifier = modifier.fillMaxWidth(), onClick = onClick) {
        Column {
            Box(
                modifier = Modifier.fillMaxWidth().height(100.dp).background(
                    if (read) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.secondaryContainer,
                    RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                ),
                contentAlignment = Alignment.Center,
            ) {
                Text(article.category.emoji, fontSize = 40.sp)
                Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.primary, modifier = Modifier.align(Alignment.BottomStart).padding(10.dp)) {
                    Text(article.category.label, color = MaterialTheme.colorScheme.onPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                }
                if (read) {
                    Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.primary, modifier = Modifier.align(Alignment.TopEnd).padding(10.dp)) {
                        Icon(Lucide.Check, "Concluído", tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.padding(5.dp).size(14.dp))
                    }
                }
            }
            Column(Modifier.padding(14.dp)) {
                Text(article.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Text(article.summary, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Lucide.Clock, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(13.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("${article.minutes} min", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Lucide.Star, null, tint = GoldXP, modifier = Modifier.size(13.dp))
                        Spacer(Modifier.width(3.dp))
                        Text(if (read) "Lido" else "+${article.points} XP", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (read) MaterialTheme.colorScheme.primary else GoldXPText)
                    }
                }
            }
        }
    }
}

private fun normalizar(texto: String): String {
    val nfd = java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD)
    return nfd.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "").lowercase()
}