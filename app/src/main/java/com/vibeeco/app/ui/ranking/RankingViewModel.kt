package com.vibeeco.app.ui.ranking

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vibeeco.app.data.RankEntry
import com.vibeeco.app.data.Session
import com.vibeeco.app.data.api.RetrofitClient
import com.vibeeco.app.data.api.model.RankingUserApiDto
import com.vibeeco.app.data.api.paraInt
import com.vibeeco.app.data.api.valorOuErro
import kotlinx.coroutines.launch

class RankingViewModel : ViewModel() {

    private val api = RetrofitClient.vibeApi

    var entradas by mutableStateOf<List<RankEntry>>(emptyList())
        private set
    var carregando by mutableStateOf(true)
        private set
    var erro by mutableStateOf<String?>(null)
        private set

    private var escopoAtual = EscopoRanking.Geral

    init {
        carregar()
    }

    fun carregar(escopo: EscopoRanking = escopoAtual) {
        escopoAtual = escopo
        viewModelScope.launch {
            carregando = true
            erro = null
            try {
                val resposta = try {
                    when (escopo) {
                        EscopoRanking.Geral -> api.listarRanking()
                        EscopoRanking.Instituicao -> api.listarRankingInstituicao(Session.userId)
                        EscopoRanking.Grupo -> api.listarRankingGrupo(Session.userId)
                    }
                } catch (e: retrofit2.HttpException) {
                    if (e.code() == 404) null else throw e
                }
                entradas = resposta
                    ?.valorOuErro("Não foi possível carregar o ranking.")
                    ?.ranking.orEmpty()
                    .map { it.paraRankEntry() }
                    // a view do ranking repete o usuário quando ele está em mais de um grupo
                    .distinctBy { it.id }
                    .sortedByDescending { it.points }
            } catch (e: Exception) {
                erro = e.message ?: "Não foi possível carregar o ranking."
            } finally {
                carregando = false
            }
        }
    }

    private fun RankingUserApiDto.paraRankEntry() = RankEntry(
        id = id ?: idUsuario ?: 0,
        nivel = nivel ?: 0,
        name = nomeUsuario ?: nome ?: "Usuário",
        course = listOfNotNull(instituicao, escolaridade ?: curso).joinToString(" · "),
        points = (xpUsuario ?: totalXp).paraInt(),
    )
}