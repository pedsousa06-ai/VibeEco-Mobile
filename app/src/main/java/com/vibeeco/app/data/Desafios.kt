package com.vibeeco.app.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonParser
import com.vibeeco.app.data.api.RetrofitClient
import com.vibeeco.app.data.api.categoriaDe
import com.vibeeco.app.data.api.mensagemDeErro
import com.vibeeco.app.data.api.model.DesafioApiDto
import com.vibeeco.app.data.api.model.StatusDesafioRequest
import com.vibeeco.app.data.api.model.UsuarioDesafioRequest
import com.vibeeco.app.data.api.paraInt
import kotlinx.coroutines.launch
import retrofit2.HttpException

enum class Escopo(val rotulo: String) {
    INDIVIDUAL("Individual"),
    COLETIVO("Coletivo"),
}

enum class Dificuldade(val rotulo: String, val fundo: Color, val texto: Color) {
    FACIL("Fácil", Color(0xFFDCFCE7), Color(0xFF15803D)),
    MEDIO("Médio", Color(0xFFFEF3C7), Color(0xFF92400E)),
    DIFICIL("Difícil", Color(0xFFFEE2E2), Color(0xFFB91C1C)),
}

/**
 * Desafio como o app mostra, montado a partir da API (VibeEco-Back-End-Users).
 * O banco guarda por desafio: moedas (MC), prazo, tipo, dificuldade, meta de participantes e categorias.
 * Não existem XP nem tarefas por desafio: o usuário marca o desafio inteiro como cumprido.
 */
data class Desafio(
    val id: String,                      // id_desafio
    val idUsuarioDesafio: Int?,          // vínculo do usuário com o desafio (null: ainda não entrou)
    val titulo: String,
    val descricao: String,
    val category: Category,
    val escopo: Escopo,
    val dificuldade: Dificuldade,
    val participantes: Int,
    val meta: Int?,                      // null: o desafio não tem meta de participantes
    val mc: Int,
    val dias: Int?,                      // null: sem prazo
    val encerrado: Boolean,
    val status: String?,                 // null | EM_ANDAMENTO | CONCLUIDO | ABANDONADO
    val progresso: Int,
) {
    val participando: Boolean get() = status == "EM_ANDAMENTO"
    val concluido: Boolean get() = status == "CONCLUIDO"
    val participacao: Int
        get() = if (meta == null || meta <= 0) 0 else (participantes * 100f / meta).toInt().coerceIn(0, 100)

    /** Texto curto do prazo, usado no cartão da lista */
    val rotuloDias: String
        get() = when {
            encerrado -> "Encerrado"
            dias == null -> "Sem prazo"
            dias == 0 -> "Termina hoje"
            dias == 1 -> "1 dia"
            else -> "$dias dias"
        }

    /** Texto do prazo na tela de detalhe */
    val rotuloPrazoCompleto: String
        get() = when {
            encerrado -> "Encerrado"
            dias == null -> "Sem prazo"
            dias == 0 -> "Termina hoje"
            dias == 1 -> "1 dia restante"
            else -> "$dias dias restantes"
        }
}

private fun dificuldadeDe(nome: String?): Dificuldade {
    val t = nome.orEmpty().trim().lowercase()
    return when {
        t.startsWith("dif") -> Dificuldade.DIFICIL
        t.startsWith("f") -> Dificuldade.FACIL
        else -> Dificuldade.MEDIO
    }
}

private fun DesafioApiDto.paraDesafio(): Desafio? {
    val id = idDesafio ?: return null
    return Desafio(
        id = id.toString(),
        idUsuarioDesafio = idUsuarioDesafio,
        titulo = nomeDesafio.orEmpty(),
        descricao = descricaoDesafio.orEmpty(),
        category = categoriaDe(categoria?.firstOrNull()?.nomeCategoria),
        escopo = if (tipoDesafio.orEmpty().trim().equals("Coletivo", ignoreCase = true)) Escopo.COLETIVO else Escopo.INDIVIDUAL,
        dificuldade = dificuldadeDe(nomeDificuldade),
        participantes = qtdeParticipantes.paraInt(),
        meta = metaParticipantes?.paraInt(),
        mc = coinsDesafio.paraInt(),
        dias = prazoDias?.paraInt(),
        encerrado = encerrado == true,
        status = statusDesafio,
        progresso = progresso.paraInt().coerceIn(0, 100),
    )
}

/** A API manda o "codigo" da regra de negócio no corpo do erro (ex.: DESAFIO_ENCERRADO). */
private fun codigoDoErro(e: Throwable): String? {
    if (e !is HttpException) return null
    return try {
        val corpo = e.response()?.errorBody()?.string() ?: return null
        JsonParser.parseString(corpo).asJsonObject.get("codigo")?.takeIf { it.isJsonPrimitive }?.asString
    } catch (ex: Exception) {
        null
    }
}

/** Mensagem para o usuário: o texto do servidor só aparece quando não há uma mensagem própria do app. */
private fun mensagemDesafio(e: Throwable, padrao: String): String = when (codigoDoErro(e)) {
    "DESAFIO_ENCERRADO" -> "O prazo deste desafio já acabou."
    "DESAFIO_JA_CONCLUIDO" -> "Você já concluiu este desafio."
    "DESAFIO_NAO_ESTA_EM_ANDAMENTO" -> "Participe do desafio antes de concluí-lo."
    else -> if ((e as? HttpException)?.code() == 404) "Este desafio não foi encontrado." else mensagemDeErro(e, padrao)
}

class DesafiosViewModel : ViewModel() {

    private val api = RetrofitClient.vibeApi

    var desafios by mutableStateOf<List<Desafio>>(emptyList())
        private set

    /** true só enquanto não há nada para mostrar; recarregar com a lista na tela não pisca. */
    var carregando by mutableStateOf(true)
        private set

    var erro by mutableStateOf<String?>(null)
        private set

    /** true enquanto uma ação (participar, concluir, abandonar) está sendo enviada: evita toque duplo. */
    var ocupado by mutableStateOf(false)
        private set

    init {
        carregar()
    }

    fun carregar() {
        viewModelScope.launch {
            if (desafios.isEmpty()) carregando = true
            try {
                desafios = api.listarDesafios(Session.userId)
                    .response?.usuarioDesafio.orEmpty()
                    .mapNotNull { it.paraDesafio() }
                erro = null
            } catch (e: HttpException) {
                if (e.code() == 404) {
                    // 404 = ainda não há desafios cadastrados
                    desafios = emptyList()
                    erro = null
                } else {
                    erro = mensagemDeErro(e, "Não foi possível carregar os desafios.")
                }
            } catch (e: Exception) {
                erro = mensagemDeErro(e, "Não foi possível carregar os desafios.")
            } finally {
                carregando = false
            }
        }
    }

    fun buscar(id: String): Desafio? = desafios.firstOrNull { it.id == id }

    /** Entra no desafio (ou volta a ele depois de abandonar). onResultado recebe null quando deu certo. */
    fun participar(id: String, onResultado: (String?) -> Unit = {}) {
        val idDesafio = id.toIntOrNull() ?: return
        executar(onResultado, "Não foi possível participar do desafio.") {
            api.participarDesafio(UsuarioDesafioRequest(Session.userId, idDesafio))
        }
    }

    /** Abandonar zera o progresso e tira o usuário da contagem de participantes. */
    fun abandonar(id: String, onResultado: (String?) -> Unit = {}) {
        val vinculo = buscar(id)?.idUsuarioDesafio ?: return
        executar(onResultado, "Não foi possível abandonar o desafio.") {
            api.atualizarStatusDesafio(vinculo, StatusDesafioRequest("ABANDONADO"))
        }
    }

    /** Marca o desafio como cumprido. A API paga as Moedas Verdes: onResultado recebe quantas foram. */
    fun concluir(id: String, onResultado: (moedas: Int, erro: String?) -> Unit) {
        val vinculo = buscar(id)?.idUsuarioDesafio ?: return
        if (ocupado) return
        ocupado = true
        viewModelScope.launch {
            try {
                val ganhas = api.atualizarStatusDesafio(vinculo, StatusDesafioRequest("CONCLUIDO"))
                    .response?.coinsGanhos.paraInt()
                recarregarSemEstado()
                onResultado(ganhas, null)
            } catch (e: Exception) {
                onResultado(0, mensagemDesafio(e, "Não foi possível concluir o desafio."))
            } finally {
                ocupado = false
            }
        }
    }

    private fun executar(
        onResultado: (String?) -> Unit,
        mensagemPadrao: String,
        chamada: suspend () -> Any?,
    ) {
        if (ocupado) return
        ocupado = true
        viewModelScope.launch {
            try {
                chamada()
                recarregarSemEstado()
                onResultado(null)
            } catch (e: Exception) {
                onResultado(mensagemDesafio(e, mensagemPadrao))
            } finally {
                ocupado = false
            }
        }
    }

    /** Atualiza a lista depois de uma ação; se falhar, mantém a lista atual (a ação já foi gravada). */
    private suspend fun recarregarSemEstado() {
        try {
            desafios = api.listarDesafios(Session.userId)
                .response?.usuarioDesafio.orEmpty()
                .mapNotNull { it.paraDesafio() }
        } catch (e: Exception) {
            // mantém o que já estava na tela
        }
    }
}
