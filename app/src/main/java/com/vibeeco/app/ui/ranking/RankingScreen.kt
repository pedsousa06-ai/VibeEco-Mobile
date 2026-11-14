package com.vibeeco.app.ui.ranking

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Building2
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.ChevronUp
import com.composables.icons.lucide.Crown
import com.composables.icons.lucide.Gift
import com.composables.icons.lucide.Globe
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Medal
import com.composables.icons.lucide.Star
import com.composables.icons.lucide.TrendingUp
import com.composables.icons.lucide.Users
import java.text.NumberFormat
import java.util.Locale

/* ------------------------------------------------------------------ */
/*  CORES (equivalentes ao CSS do desktop)                             */
/*  Troque por cores do seu tema (ui/theme) se já tiver as mesmas.     */
/* ------------------------------------------------------------------ */
private object RankColors {
    val Texto = Color(0xFF12261A)          // var(--texto)
    val TextoSuave = Color(0xFF5B6E62)     // var(--texto-suave)
    val Verde600 = Color(0xFF16A34A)       // var(--verde-600)
    val Borda = Color(0xFFDDE7E0)          // var(--borda)

    val AbasFundo = Color(0xFFE6EEE8)
    val LinhaPar = Color(0xFFF3F8F4)
    val Cabecalho = Color(0xFFF4F8F5)

    val VoceFundo = Color(0xFFDDFBE8)
    val VoceBorda = Color(0xFF4ADE80)
    val VoceTexto = Color(0xFF0F5B2D)
    val VoceSub = Color(0xFF2D6A45)
    val VoceTag = Color(0xFF15803D)

    val Botao = Color(0xFF14603A)

    val XpTexto = Color(0xFF8A5A00)
    val XpEstrela = Color(0xFFF5B301)

    val Ouro = Color(0xFFE8B10A)
    val Prata = Color(0xFF8A9CA8)
    val Bronze = Color(0xFFB37A35)
    val Coroa = Color(0xFFF5C518)

    val PodioGradiente = listOf(Color(0xFF0F4524), Color(0xFF145C2F), Color(0xFF17702F))
    val AvatarPodio = Color(0xFF3D6B4D)
    val AvatarGradiente = listOf(Color(0xFF22C55E), Color(0xFF0F7A3B))
}

/* ------------------------------------------------------------------ */
/*  MODELOS                                                            */
/*  Se já existirem equivalentes em data/Models.kt, use os seus e      */
/*  apague estes (me mande o Models.kt que eu ligo direto).            */
/* ------------------------------------------------------------------ */
enum class EscopoRanking(val nome: String, val sufixo: String, val icone: ImageVector) {
    Geral("Geral", "geral", Lucide.Globe),
    Instituicao("Instituição", "na instituição", Lucide.Building2),
    Grupo("Grupo", "no grupo", Lucide.Users),
}

data class RankingItem(
    val id: String,
    val posicao: Int,
    val nome: String,
    val iniciais: String,
    val instituicao: String,
    val nivel: Int,
    val xp: Int,
    val conquistas: Int = 0,
    val voce: Boolean = false,
)

data class RankingData(
    val lista: List<RankingItem>,
    val eu: RankingItem,
    val percentual: Int,
)

private const val LIMITE = 7 // posições visíveis antes de "Ver mais"

private sealed interface Linha {
    data class Usuario(val item: RankingItem) : Linha
    data object Separador : Linha
}

/* ------------------------------------------------------------------ */
/*  TELA                                                               */
/* ------------------------------------------------------------------ */
@Composable
fun RankingScreen(
    montarRanking: (EscopoRanking) -> RankingData,
    onAbrirRecompensas: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var escopo by rememberSaveable { mutableStateOf(EscopoRanking.Geral) }
    var expandido by rememberSaveable { mutableStateOf(false) }

    // Recalcula ao trocar de aba (e quando o XP mudar, se montarRanking ler de um State)
    val ranking = remember(escopo, montarRanking) { montarRanking(escopo) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7FAF8)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            AbasEscopo(
                selecionado = escopo,
                onSelecionar = {
                    escopo = it
                    expandido = false
                },
            )
        }
        item(key = "podio-${escopo.name}") { Podio(ranking.lista.take(3)) }
        item {
            CardVoce(
                eu = ranking.eu,
                percentual = ranking.percentual,
                sufixo = escopo.sufixo,
                onAbrirRecompensas = onAbrirRecompensas,
            )
        }
        item {
            Classificacao(
                ranking = ranking,
                expandido = expandido,
                onAlternar = { expandido = !expandido },
            )
        }
    }
}

/* ---------- ABAS ---------- */
@Composable
private fun AbasEscopo(selecionado: EscopoRanking, onSelecionar: (EscopoRanking) -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(RankColors.AbasFundo)
            .padding(4.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        EscopoRanking.entries.forEach { aba ->
            val ativa = aba == selecionado
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(9.dp))
                    .then(
                        if (ativa) Modifier.shadow(1.dp, RoundedCornerShape(9.dp)).background(Color.White)
                        else Modifier
                    )
                    .selectable(selected = ativa, role = Role.Tab, onClick = { onSelecionar(aba) })
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = aba.icone,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = if (ativa) RankColors.Verde600 else RankColors.TextoSuave,
                )
                Text(
                    text = aba.nome,
                    fontSize = 14.5.sp,
                    fontWeight = if (ativa) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (ativa) RankColors.Texto else RankColors.TextoSuave,
                    maxLines = 1,
                )
            }
        }
    }
}

/* ---------- PÓDIO ---------- */
@Composable
private fun Podio(top3: List<RankingItem>) {
    // Ordem visual: 2º, 1º, 3º
    val ordem = listOfNotNull(top3.getOrNull(1), top3.getOrNull(0), top3.getOrNull(2))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(RankColors.PodioGradiente))
            .padding(start = 16.dp, end = 16.dp, top = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Top 3 da Semana",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(22.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.Bottom,
        ) {
            ordem.forEach { item ->
                ColunaPodio(item = item, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ColunaPodio(item: RankingItem, modifier: Modifier = Modifier) {
    val cor = when (item.posicao) {
        1 -> RankColors.Ouro
        2 -> RankColors.Prata
        else -> RankColors.Bronze
    }
    val alturaFinal = when (item.posicao) {
        1 -> 144.dp
        2 -> 112.dp
        else -> 96.dp
    }

    // Bloco "sobe" ao aparecer (equivale à animação `subir` do CSS)
    var visivel by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visivel = true }
    val altura by animateDpAsState(
        targetValue = if (visivel) alturaFinal else 0.dp,
        animationSpec = tween(durationMillis = 700, delayMillis = if (item.posicao == 1) 250 else 150),
        label = "alturaPodio",
    )

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        if (item.posicao == 1) {
            Icon(
                imageVector = Lucide.Crown,
                contentDescription = null,
                tint = RankColors.Coroa,
                modifier = Modifier.size(26.dp),
            )
            Spacer(Modifier.height(2.dp))
        }
        Box(
            modifier = Modifier
                .size(56.dp)
                .border(3.dp, cor, CircleShape)
                .padding(3.dp)
                .clip(CircleShape)
                .background(RankColors.AvatarPodio),
            contentAlignment = Alignment.Center,
        ) {
            Text(item.iniciais, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = item.nome,
            color = Color.White,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        NumeroXp(valor = item.xp, cor = Color.White.copy(alpha = 0.8f), tamanho = 12.sp)
        Spacer(Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(altura)
                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                .background(
                    if (item.posicao == 1) Brush.verticalGradient(listOf(Color(0xFFF0BD1A), Color(0xFFD9A206)))
                    else Brush.verticalGradient(listOf(cor, cor))
                )
                .padding(top = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(Lucide.Medal, contentDescription = "${item.posicao}º lugar: ${item.nome}", tint = Color.White, modifier = Modifier.size(22.dp))
            Text("#${item.posicao}", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

/** "1.250 XP" com contagem animada (equivale ao NumeroAnimado.vue). */
@Composable
private fun NumeroXp(valor: Int, cor: Color, tamanho: androidx.compose.ui.unit.TextUnit, peso: FontWeight = FontWeight.Normal) {
    val animado by animateIntAsState(targetValue = valor, animationSpec = tween(900), label = "xp")
    Text(text = "${formatar(animado)} XP", color = cor, fontSize = tamanho, fontWeight = peso)
}

private fun formatar(n: Int): String = NumberFormat.getInstance(Locale("pt", "BR")).format(n)

/* ---------- SUA POSIÇÃO ---------- */
@Composable
private fun CardVoce(
    eu: RankingItem,
    percentual: Int,
    sufixo: String,
    onAbrirRecompensas: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(RankColors.VoceFundo)
            .border(1.5.dp, RankColors.VoceBorda, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("#${eu.posicao}", color = RankColors.VoceTexto, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
            Avatar(iniciais = eu.iniciais, tamanho = 42.dp)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Você — ${eu.nome}",
                    color = RankColors.VoceTexto,
                    fontSize = 15.5.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row {
                    Text("Nível ${eu.nivel} · ", color = RankColors.VoceSub, fontSize = 12.5.sp)
                    NumeroXp(eu.xp, RankColors.VoceSub, 12.5.sp)
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Lucide.TrendingUp, null, tint = RankColors.VoceTexto, modifier = Modifier.size(16.dp))
                Text("Top $percentual% $sufixo", color = RankColors.VoceTexto, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
            // Link para a tela de Recompensas
            Button(
                onClick = onAbrirRecompensas,
                modifier = Modifier.height(40.dp),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RankColors.Botao, contentColor = Color.White),
            ) {
                Icon(Lucide.Gift, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Recompensas", fontSize = 14.5.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/* ---------- CLASSIFICAÇÃO ---------- */
@Composable
private fun Classificacao(ranking: RankingData, expandido: Boolean, onAlternar: () -> Unit) {
    val linhas = remember(ranking, expandido) {
        val visiveis = if (expandido) ranking.lista else ranking.lista.take(LIMITE)
        buildList<Linha> {
            visiveis.forEach { add(Linha.Usuario(it)) }
            // Se você não está na lista curta, aparece separado no final
            if (visiveis.none { it.voce }) {
                add(Linha.Separador)
                add(Linha.Usuario(ranking.eu))
            }
        }
    }
    val podeExpandir = ranking.lista.size > LIMITE

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, RankColors.Borda, RoundedCornerShape(16.dp))
            .animateContentSize(),
    ) {
        // Cabeçalho (no celular só # / Usuário / XP, igual ao breakpoint do desktop)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(RankColors.Cabecalho)
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CabecalhoTexto("#", Modifier.width(48.dp))
            CabecalhoTexto("Usuário", Modifier.weight(1f))
            CabecalhoTexto("XP", Modifier.width(84.dp), TextAlign.End)
        }
        HorizontalDivisor()

        var indiceZebra = 0
        linhas.forEach { linha ->
            when (linha) {
                is Linha.Separador -> Text(
                    text = "···",
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    textAlign = TextAlign.Center,
                    color = RankColors.TextoSuave,
                    fontSize = 18.sp,
                    letterSpacing = 4.sp,
                )
                is Linha.Usuario -> {
                    LinhaUsuario(item = linha.item, zebra = indiceZebra % 2 == 1)
                    indiceZebra++
                }
            }
        }

        if (podeExpandir) {
            HorizontalDivisor()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clickable(onClick = onAlternar),
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (expandido) "Ver menos" else "Ver mais posições",
                    color = RankColors.Verde600,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Icon(
                    imageVector = if (expandido) Lucide.ChevronUp else Lucide.ChevronDown,
                    contentDescription = null,
                    tint = RankColors.Verde600,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Composable
private fun CabecalhoTexto(texto: String, modifier: Modifier, alinhamento: TextAlign = TextAlign.Start) {
    Text(
        text = texto.uppercase(),
        modifier = modifier,
        textAlign = alinhamento,
        color = RankColors.TextoSuave,
        fontSize = 11.5.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.7.sp,
    )
}

@Composable
private fun HorizontalDivisor() {
    Box(Modifier.fillMaxWidth().height(1.dp).background(RankColors.Borda))
}

@Composable
private fun LinhaUsuario(item: RankingItem, zebra: Boolean) {
    val fundo = when {
        item.voce -> RankColors.VoceFundo
        zebra -> RankColors.LinhaPar
        else -> Color.Transparent
    }
    val corMedalha = when (item.posicao) {
        1 -> RankColors.Ouro
        2 -> RankColors.Prata
        3 -> RankColors.Bronze
        else -> null
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(fundo)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.width(48.dp), contentAlignment = Alignment.CenterStart) {
            if (corMedalha != null) {
                Icon(Lucide.Medal, contentDescription = "${item.posicao}º lugar", tint = corMedalha, modifier = Modifier.size(22.dp))
            } else {
                Text("#${item.posicao}", color = RankColors.TextoSuave, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Avatar(iniciais = item.iniciais, tamanho = 38.dp)
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.nome,
                        color = if (item.voce) RankColors.VoceTexto else RankColors.Texto,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    if (item.voce) {
                        Text(" (você)", color = RankColors.VoceTag, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                Text(
                    text = "${item.instituicao} · Nível ${item.nivel}",
                    color = RankColors.TextoSuave,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Row(
            modifier = Modifier.width(84.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Lucide.Star, null, tint = RankColors.XpEstrela, modifier = Modifier.size(13.dp))
            Text(formatar(item.xp), color = RankColors.XpTexto, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun Avatar(iniciais: String, tamanho: Dp) {
    Box(
        modifier = Modifier
            .size(tamanho)
            .clip(CircleShape)
            .background(Brush.linearGradient(RankColors.AvatarGradiente)),
        contentAlignment = Alignment.Center,
    ) {
        Text(iniciais, color = Color.White, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
    }
}

/* ------------------------------------------------------------------ */
/*  PREVIEW COM DADOS FICTÍCIOS                                        */
/* ------------------------------------------------------------------ */
private fun dadosDeExemplo(escopo: EscopoRanking): RankingData {
    val nomes = listOf(
        "Marina Costa", "Lucas Almeida", "Beatriz Souza", "Rafael Lima", "Camila Rocha",
        "Pedro Nunes", "Julia Martins", "Diego Ferreira", "Ana Paula", "Bruno Carvalho",
    )
    val lista = nomes.mapIndexed { i, nome ->
        RankingItem(
            id = "u$i",
            posicao = i + 1,
            nome = nome,
            iniciais = nome.split(" ").take(2).joinToString("") { it.first().toString() },
            instituicao = "Escola Verde",
            nivel = 12 - i,
            xp = 3200 - i * 210,
            conquistas = 20 - i,
        )
    }
    val eu = RankingItem("eu", 12, "Você", "VC", "Escola Verde", 6, 1180, 8, voce = true)
    return RankingData(lista = lista, eu = eu, percentual = 15)
}

@Preview(showBackground = true, heightDp = 1200)
@Composable
private fun RankingScreenPreview() {
    RankingScreen(montarRanking = ::dadosDeExemplo, onAbrirRecompensas = {})
}