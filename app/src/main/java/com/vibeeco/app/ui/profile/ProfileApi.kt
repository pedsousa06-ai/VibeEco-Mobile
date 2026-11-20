package com.vibeeco.app.ui.profile

import com.vibeeco.app.data.Session
import androidx.compose.runtime.snapshotFlow
import com.vibeeco.app.data.api.RetrofitClient
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