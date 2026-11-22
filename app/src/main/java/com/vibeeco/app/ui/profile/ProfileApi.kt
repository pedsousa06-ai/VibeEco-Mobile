package com.vibeeco.app.ui.profile

import com.vibeeco.app.data.Session
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.graphics.Color
import com.composables.icons.lucide.BookOpen
import com.composables.icons.lucide.Flame
import com.composables.icons.lucide.Gift
import com.composables.icons.lucide.Leaf
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Target
import com.composables.icons.lucide.Trophy
import com.vibeeco.app.data.api.RetrofitClient
import com.vibeeco.app.data.api.paraInt
import com.vibeeco.app.data.api.model.PerfilEdicaoRequest
import com.vibeeco.app.data.api.model.AtividadeApiDto
import com.vibeeco.app.data.api.model.ConfiguracoesApiDto
import com.vibeeco.app.data.api.model.NotificacaoApiDto
import kotlinx.coroutines.flow.drop
import retrofit2.HttpException

private val api get() = RetrofitClient.vibeApi

private fun tipoDe(valor: String?): TipoAtividade =
    TipoAtividade.values().firstOrNull { it.name.equals(valor, ignoreCase = true) } ?: TipoAtividade.Missao

private fun NotificacaoApiDto.paraUi() = Notificacao(
    id = (id ?: 0).toString(), tipo = tipoDe(tipo), titulo = titulo.orEmpty(), texto = texto.orEmpty(),
    minutosAtras = minutosAtras ?: 0, lida = lida == true, detalhes = detalhes.orEmpty().map { (it.rotulo ?: "") to (it.valor ?: "") },
)

private fun AtividadeApiDto.paraUi() = Atividade(
    id = (id ?: "").toString(), tipo = tipoDe(tipo), rotulo = rotulo.orEmpty(), titulo = titulo.orEmpty(),
    diasAtras = diasAtras ?: 0, hora = hora.orEmpty(), xp = xp ?: 0, mc = mc ?: 0, progresso = progresso,
    descricao = descricao.orEmpty(), detalhes = detalhes.orEmpty().map { (it.rotulo ?: "") to (it.valor ?: "") },
)

private suspend fun <T> listaOuVazia(chamada: suspend () -> List<T>): List<T> =
    try { chamada() } catch (e: HttpException) { if (e.code() == 404) emptyList() else throw e }

suspend fun carregarNotificacoes() {
    val novas = listaOuVazia { api.listarNotificacoes(Session.userId) }.map { it.paraUi() }
    NotificacoesStore.itens.clear()
    NotificacoesStore.itens.addAll(novas)
}

suspend fun marcarNotificacaoLidaNaApi(id: String) {
    val n = id.toIntOrNull() ?: return
    api.marcarNotificacaoLida(n)
}

suspend fun marcarTodasNotificacoesLidasNaApi() {
    api.marcarTodasNotificacoesLidas(Session.userId)
}

suspend fun carregarHistorico() {
    val itens = try {
        api.buscarHistorico(Session.userId).atividades.orEmpty()
    } catch (e: HttpException) {
        if (e.code() == 404) emptyList() else throw e
    }
    ATIVIDADES.clear()
    ATIVIDADES.addAll(itens.map { it.paraUi() })
}

suspend fun carregarConfiguracoes() {
    val c = api.buscarConfiguracoes(Session.userId)
    c.idioma?.let { configuracoes.idioma = it }
    c.tema?.let { configuracoes.tema = it }
    c.altoContraste?.let { configuracoes.altoContraste = it }
    c.tamanhoFonte?.let { configuracoes.tamanhoFonte = it }
    c.animacoes?.let { configuracoes.animacoes = it }
    c.perfilPublico?.let { configuracoes.perfilPublico = it }
    c.aparecerNoRanking?.let { configuracoes.aparecerNoRanking = it }
    c.atividadesVisiveis?.let { configuracoes.atividadesVisiveis = it }
}

private fun instantaneo() = ConfiguracoesApiDto(
    idioma = configuracoes.idioma, tema = configuracoes.tema, altoContraste = configuracoes.altoContraste,
    tamanhoFonte = configuracoes.tamanhoFonte, animacoes = configuracoes.animacoes,
    aparecerNoRanking = configuracoes.aparecerNoRanking, atividadesVisiveis = configuracoes.atividadesVisiveis,
    perfilPublico = configuracoes.perfilPublico,
)

suspend fun salvarConfiguracoesAoMudar(aoErro: (Throwable) -> Unit = {}) {
    snapshotFlow { instantaneo() }.drop(1).collect { atual ->
        try { api.atualizarConfiguracoes(Session.userId, atual) } catch (e: Exception) { aoErro(e) }
    }
}

/* ------------------------------------------------------------------ */
/*  PERFIL (GET/PUT /perfil/{id})                                      */
/* ------------------------------------------------------------------ */
private val ICONES_CONQUISTA = listOf(Lucide.Leaf, Lucide.Flame, Lucide.Trophy, Lucide.Target, Lucide.BookOpen, Lucide.Gift)
private val CORES_CONQUISTA = listOf(Color(0xFF16A34A), Color(0xFFE8590C), Color(0xFFD97706), Color(0xFF3B6FE0))

private val PT_BR = java.util.Locale("pt", "BR")

/** "2026-11-21T10:00:00.000Z" -> "novembro de 2026" (vazio quando não há data) */
private fun desdeDe(data: String?): String {
    if (data.isNullOrBlank()) return ""
    return try {
        java.time.OffsetDateTime.parse(data)
            .format(java.time.format.DateTimeFormatter.ofPattern("MMMM 'de' yyyy", PT_BR))
    } catch (e: Exception) {
        ""
    }
}

/** Variação da última semana contra a anterior. Sem XP na anterior não existe porcentagem. */
private fun variacaoDe(semanas: List<SemanaXp>): String {
    val ultimo = semanas.lastOrNull()?.xp ?: 0
    val anterior = semanas.getOrNull(semanas.size - 2)?.xp ?: 0
    return when {
        anterior > 0 -> {
            val v = (ultimo - anterior) * 100.0 / anterior
            (if (v >= 0) "+" else "") + String.format(PT_BR, "%.1f%%", v)
        }
        ultimo > 0 -> "+$ultimo XP"
        else -> "—"
    }
}

/** Busca o perfil completo e preenche o PerfilStore e o PerfilDados. */
suspend fun carregarPerfilDaApi() {
    val p = api.buscarPerfil(Session.userId).response?.perfil ?: return
    val u = p.usuario ?: return

    val nivel = when {
        u.numeroLevel == null -> "Nível 1"
        u.nomeLevel.isNullOrBlank() -> "Nível ${u.numeroLevel}"
        else -> "Nível ${u.numeroLevel} - ${u.nomeLevel}"
    }
    PerfilStore.aplicarApi(
        PerfilUsuario(
            nome = u.nome.orEmpty(), nomePublico = u.nomePublico.orEmpty(), email = u.email.orEmpty(),
            matricula = u.matricula.orEmpty(), curso = u.curso.orEmpty(), localizacao = u.localizacao.orEmpty(),
            bio = u.bio.orEmpty(), foto = u.foto.orEmpty(), instituicao = u.instituicao.orEmpty(),
            nivel = nivel, desde = desdeDe(u.dataCadastro),
        )
    )

    PerfilDados.xpTotal = u.xp ?: 0
    PerfilDados.posicao = p.ranking?.posicao
    PerfilDados.totalParticipantes = p.ranking?.total ?: 0
    PerfilDados.ganhos = p.desempenho.orEmpty().mapValues { (_, d) ->
        GanhosPeriodo(xp = d.xpGanho ?: 0, mc = d.mcGanho ?: 0, missoes = d.missoesConcluidas ?: 0)
    }

    val semanas = p.evolucaoXp.orEmpty().map { SemanaXp("S${it.semana ?: 0}", it.xp ?: 0) }
    PerfilDados.evolucao = semanas
    PerfilDados.evolucaoVariacao = variacaoDe(semanas)

    val c = p.conquistas
    PerfilDados.totalConquistas = c?.total ?: 0
    PerfilDados.conquistas = c?.lista.orEmpty().take(4).mapIndexed { i, x ->
        Conquista(
            id = (x.id ?: i).toString(),
            titulo = x.nome.orEmpty(),
            descricao = x.descricao.orEmpty(),
            icone = ICONES_CONQUISTA[i % ICONES_CONQUISTA.size],
            cor = CORES_CONQUISTA[i % CORES_CONQUISTA.size],
            desbloqueada = x.desbloqueada == true,
        )
    }
}

/** Missões mostradas no fim do perfil (as mesmas da aba Missões). */
suspend fun carregarMissoesDoPerfil() {
    val lista = try {
        api.listarTodasMissoes(Session.userId).response?.missoes.orEmpty()
    } catch (e: HttpException) {
        if (e.code() == 404) emptyList() else throw e
    }
    PerfilDados.missoes = lista.mapNotNull { m ->
        val id = m.idMissao ?: return@mapNotNull null
        Missao(
            id = id.toString(),
            titulo = m.nomeMissao.orEmpty(),
            categoria = m.categoria.orEmpty().firstOrNull()?.nomeCategoria.orEmpty(),
            dificuldade = m.nomeDificuldade.orEmpty(),
            resumo = m.descricaoMissao.orEmpty(),
            progresso = (m.progressoMissao ?: 0).coerceIn(0, 100),
            xp = m.xpMissao ?: 0,
            mc = m.coinsMissao ?: 0,
            prazoDias = m.prazoDias,
        )
    }
}

/** Grava a edição do perfil no servidor e só então atualiza a tela. Lança exceção se falhar. */
suspend fun salvarPerfilNaApi(f: FormPerfil) {
    val r = api.atualizarPerfil(
        Session.userId,
        PerfilEdicaoRequest(f.nome.trim(), f.nomePublico, f.curso, f.localizacao, f.bio),
    ).response
    PerfilStore.atualizar(
        FormPerfil(
            nome = r?.nome ?: f.nome.trim(),
            nomePublico = r?.nomePublico ?: f.nomePublico,
            curso = r?.curso ?: f.curso,
            localizacao = r?.localizacao ?: f.localizacao,
            bio = r?.bio ?: f.bio,
        )
    )
}
