package com.vibeeco.app.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vibeeco.app.data.AppConfig
import com.vibeeco.app.data.Category
import com.vibeeco.app.data.Challenge
import com.vibeeco.app.data.MissaoUi
import com.vibeeco.app.data.StatusMissao
import com.vibeeco.app.data.api.RetrofitClient
import com.vibeeco.app.data.api.model.MissaoApiDto
import com.vibeeco.app.data.api.model.MissaoCategoriaApiDto
import com.vibeeco.app.data.api.model.TelaInicioDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    sealed interface HomeUiState {
        data object Loading : HomeUiState

        data class Success(
            val data: TelaInicioDto,
            val desafiosConvertidos: List<Challenge>,
            val missoesConvertidas: List<MissaoUi>,
            val streakDias: Int
        ) : HomeUiState

        data class Error(
            val message: String
        ) : HomeUiState
    }

    private val api = RetrofitClient.vibeApi

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun carregarDados(idUsuario: Int = AppConfig.DEV_USER_ID) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            try {
                Log.d("VIBEECO_API", "Iniciando requisição da tela inicial")
                Log.d("VIBEECO_API", "ID do usuário: $idUsuario")

                val resposta = api.buscarTelaInicial(idUsuario)
                Log.d("VIBEECO_API", "Resposta recebida: $resposta")

                if (resposta.status == false) {
                    throw Exception(resposta.message ?: "A API retornou uma resposta inválida.")
                }

                val dados = resposta.response?.telaInicial
                if (dados == null) {
                    throw Exception("A API não retornou os dados da tela inicial.")
                }

                // Converter desafios
                val desafios = dados.desafios.orEmpty().mapNotNull { desafio ->
                    val id = desafio.id ?: return@mapNotNull null
                    Challenge(
                        id = id.toString(),
                        title = desafio.nome ?: "Desafio Sustentável",
                        description = "Participe deste desafio e contribua com o meio ambiente.",
                        category = Category.ENERGIA,
                        points = desafio.superCoins ?: 0,
                        co2Grams = 200
                    )
                }

                // Converter missões (Usando o MissaoApiDto com os atalhos)
                val missoes = dados.missoes.orEmpty().mapNotNull { missao ->
                    val id = missao.idMissao ?: missao.id ?: return@mapNotNull null
                    val progresso = missao.progresso ?: 0

                    val status = when {
                        progresso >= 100 -> StatusMissao.Concluida
                        progresso > 0 -> StatusMissao.Andamento
                        else -> StatusMissao.Disponivel
                    }

                    val dto = MissaoApiDto(
                        idMissao = id,
                        nomeMissao = missao.nome ?: "Missão Sustentável",
                        nomeDificuldade = missao.dificuldade ?: "Médio",
                        xpMissao = missao.qtdeXp ?: 0,
                        prazoDias = missao.prazoDias ?: 0,
                        progressoMissao = progresso,
                        categoria = listOf(MissaoCategoriaApiDto(nomeCategoria = "Geral"))
                    )

                    MissaoUi(
                        missao = dto,
                        progresso = progresso,
                        status = status
                    )
                }

                val streak = extrairStreak(dados)

                _uiState.value = HomeUiState.Success(
                    data = dados,
                    desafiosConvertidos = desafios,
                    missoesConvertidas = missoes,
                    streakDias = streak
                )
                Log.d("VIBEECO_API", "Home carregada com sucesso!")

            } catch (e: Exception) {
                Log.e("VIBEECO_API", "Falha na requisição da tela inicial", e)
                _uiState.value = HomeUiState.Error(
                    "Falha ao carregar dados: ${e.localizedMessage ?: "Erro desconhecido"}"
                )
            }
        }
    }

    private fun extrairStreak(dados: TelaInicioDto): Int {
        val ofensivas = dados.ofensiva.orEmpty()
        return ofensivas.count { it.statusOfensiva == 1 }
    }
}