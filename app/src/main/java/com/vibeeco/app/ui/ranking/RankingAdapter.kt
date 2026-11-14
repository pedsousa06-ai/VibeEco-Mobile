package com.vibeeco.app.ui.ranking

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.vibeeco.app.data.RankEntry

/**
 * Sobrecarga de compatibilidade: quem ainda chama RankingScreen(List<RankEntry>)
 * (VibeNavHost, AuthPreviews) continua compilando.
 *
 * ATENÇÃO: assumi que RankEntry tem os campos `name` (String) e `points` (Int).
 * Se o compilador reclamar, ajuste só em [toRankingData].
 */
@Composable
fun RankingScreen(
    entries: List<RankEntry>,
    onAbrirRecompensas: () -> Unit = {},
    meuNome: String? = null,
    modifier: Modifier = Modifier,
) {
    val dados = remember(entries, meuNome) { entries.toRankingData(meuNome) }
    val montar = remember(dados) { { _: EscopoRanking -> dados } }
    RankingScreen(
        montarRanking = montar,
        onAbrirRecompensas = onAbrirRecompensas,
        modifier = modifier,
    )
}

private fun List<RankEntry>.toRankingData(meuNome: String?): RankingData {
    val itens = sortedByDescending { it.points }.mapIndexed { i, e ->
        RankingItem(
            id = "${e.name}-$i",
            posicao = i + 1,
            nome = e.name,
            iniciais = e.name.trim()
                .split(" ")
                .filter { it.isNotBlank() }
                .take(2)
                .joinToString("") { it.first().uppercase() },
            instituicao = "VibeEco",
            nivel = e.points / 100 + 1,
            xp = e.points,
            conquistas = 0,
            voce = meuNome != null && e.name == meuNome,
        )
    }

    val eu = itens.firstOrNull { it.voce }
        ?: itens.lastOrNull()
        ?: RankingItem("eu", 1, "Você", "VC", "VibeEco", 1, 0, 0, voce = true)

    val total = itens.size.coerceAtLeast(1)
    val percentual = ((eu.posicao * 100f) / total).let { kotlin.math.ceil(it).toInt() }.coerceIn(1, 100)

    return RankingData(lista = itens, eu = eu, percentual = percentual)
}
