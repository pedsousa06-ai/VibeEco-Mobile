package com.vibeeco.app.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vibeeco.app.data.api.RetrofitClient
import com.vibeeco.app.data.api.mensagemDeErro
import com.vibeeco.app.data.api.valorOuErro
import com.vibeeco.app.data.api.model.MissaoApiDto
import com.vibeeco.app.data.api.model.UsuarioEtapaRequest
import com.vibeeco.app.data.api.model.UsuarioMissaoRequest
import kotlinx.coroutines.launch

enum class StatusMissao { Disponivel, Andamento, Concluida }

data class MissaoUi(
    val missao: MissaoApiDto,
    val progresso: Int,
    val status: StatusMissao,
)

class MissoesViewModel : ViewModel() {

    private val api = RetrofitClient.vibeApi

    var cartoes by mutableStateOf<List<MissaoUi>>(emptyList())
        private set

    var carregando by mutableStateOf(false)
        private set

    var erro by mutableStateOf<String?>(null)
        private set

    init {
        carregar()
    }

    fun carregar() {
        viewModelScope.launch {
            carregando = true
            try {
                // Busca todas as missões do usuário
                val resposta = api.listarTodasMissoes(Session.userId)
                val lista = resposta.response?.missoes.orEmpty()

                cartoes = lista.map { dto ->
                    val progresso = dto.progressoMissao ?: 0
                    val status = when {
                        dto.statusMissao?.equals("Concluida", ignoreCase = true) == true || progresso >= 100 -> StatusMissao.Concluida
                        dto.statusMissao?.equals("Andamento", ignoreCase = true) == true || progresso > 0 -> StatusMissao.Andamento
                        else -> StatusMissao.Disponivel
                    }
                    MissaoUi(dto, progresso, status)
                }
                erro = null
            } catch (e: Exception) {
                erro = mensagemDeErro(e, "Não foi possível carregar as missões.")
            } finally {
                carregando = false
            }
        }
    }

    fun buscar(id: String): MissaoUi? = cartoes.firstOrNull { it.missao.idMissao.toString() == id }

    fun participar(id: String, onResultado: (String?) -> Unit) {
        val ui = buscar(id) ?: return
        val idMissao = ui.missao.idMissao ?: return

        viewModelScope.launch {
            try {
                val req = UsuarioMissaoRequest(idUsuario = Session.userId, idMissao = idMissao)
                api.entrarMissao(req)
                carregar() // Recarrega a lista após entrar
                onResultado(null) // null significa sucesso
            } catch (e: Exception) {
                onResultado(mensagemDeErro(e, "Erro ao entrar na missão."))
            }
        }
    }

    fun concluirEtapa(id: String, idEtapa: Int, onResultado: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val req = UsuarioEtapaRequest(idUsuario = Session.userId, idEtapa = idEtapa)
                val resposta = api.concluirEtapa(req).valorOuErro("Erro ao concluir etapa.")
                carregar() // Recarrega para ver o novo progresso
                // 1º parâmetro: true quando esta etapa fechou a missão inteira
                onResultado(resposta.status.equals("CONCLUIDA", ignoreCase = true), null)
            } catch (e: Exception) {
                onResultado(false, mensagemDeErro(e, "Erro ao concluir etapa."))
            }
        }
    }
}