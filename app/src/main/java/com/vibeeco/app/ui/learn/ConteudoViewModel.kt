package com.vibeeco.app.ui.learn

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vibeeco.app.data.Article
import com.vibeeco.app.data.Session
import com.vibeeco.app.data.api.RetrofitClient
import com.vibeeco.app.data.api.categoriaDe
import com.vibeeco.app.data.api.mensagemDeErro
import com.vibeeco.app.data.api.model.ConcluirConteudoRequest
import com.vibeeco.app.data.api.model.ConteudoApiDto
import com.vibeeco.app.data.api.paraBool
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class ResultadoConteudo(val erro: String? = null, val xpGanho: Int = 0, val jaConcluido: Boolean = false)

class ConteudoViewModel : ViewModel() {

    private val api = RetrofitClient.vibeApi

    var artigos by mutableStateOf<List<Article>>(emptyList())
        private set
    var lidos by mutableStateOf<Set<String>>(emptySet())
        private set
    var carregando by mutableStateOf(false)
        private set
    var erro by mutableStateOf<String?>(null)
        private set

    private var inicioLeitura = 0L

    fun carregar() {
        viewModelScope.launch {
            carregando = true
            try {
                // A API retorna ApiResponse<ListaConteudoWrapper>
                val resposta = try {
                    api.listarConteudos(Session.userId)
                } catch (e: HttpException) {
                    if (e.code() == 404) return@launch
                    throw e
                }

                val lista = resposta.response?.response.orEmpty()

                artigos = lista.map { it.paraArtigo() }
                lidos = lista.filter { it.concluido.paraBool() }.map { (it.id ?: 0).toString() }.toSet()
                erro = null
            } catch (e: Exception) {
                erro = mensagemDeErro(e, "Não foi possível carregar os conteúdos.")
            } finally {
                carregando = false
            }
        }
    }

    fun iniciarLeitura() {
        inicioLeitura = System.currentTimeMillis()
    }

    fun concluir(artigo: Article, aoTerminar: (ResultadoConteudo) -> Unit) {
        val id = artigo.id.toIntOrNull() ?: return
        viewModelScope.launch {
            val minutos = if (inicioLeitura == 0L) 1
            else ((System.currentTimeMillis() - inicioLeitura) / 60000L).toInt().coerceAtLeast(1)

            val resultado = try {
                val r = api.concluirConteudo(id, Session.userId, ConcluirConteudoRequest(minutos)).response
                ResultadoConteudo(xpGanho = r?.xpGanho ?: 0, jaConcluido = r?.jaConcluido == true)
            } catch (e: Exception) {
                ResultadoConteudo(erro = mensagemDeErro(e, "Não foi possível concluir a leitura."))
            }
            if (resultado.erro == null) lidos = lidos + artigo.id
            aoTerminar(resultado)
        }
    }

    private fun ConteudoApiDto.paraArtigo() = Article(
        id = (id ?: 0).toString(),
        title = nome ?: "Conteúdo",
        summary = descricao.orEmpty().lineSequence().firstOrNull().orEmpty().take(140),
        body = descricao.orEmpty(),
        category = categoriaDe(categoria),
        minutes = duracaoLeitura ?: 1,
        points = qtdeXp ?: 0,
    )
}