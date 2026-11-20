package com.vibeeco.app.ui.community

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/* ------------------------------------------------------------------ */
/*  MODELOS                                                            */
/* ------------------------------------------------------------------ */
data class Post(
    val id: String,
    val autor: String,
    val iniciais: String,
    val tempo: String,
    val categoria: String? = null,
    val texto: String,
    val imagem: String? = null,
    val curtidas: Int = 0,
    val comentarios: Int = 0,
)

data class Categoria(val id: String, val nome: String)

data class RankingSemanaItem(
    val id: Int,
    val nome: String,
    val iniciais: String,
    val xp: Int,
    val medalha: String,
)

data class DesafioAtivo(val id: Int, val titulo: String, val detalhe: String)

sealed interface FeedState {
    data object Carregando : FeedState
    data class Erro(val mensagem: String) : FeedState
    data class Sucesso(val posts: List<Post>) : FeedState
}

/* ------------------------------------------------------------------ */
/*  REPOSITÓRIO (Interface)                                            */
/* ------------------------------------------------------------------ */
interface FeedRepository {
    suspend fun listarPostsRecentes(): List<Post>
    suspend fun criarPost(texto: String, categoriaId: String?, foto: Uri?)
}

/* ------------------------------------------------------------------ */
/*  VIEWMODEL                                                          */
/* ------------------------------------------------------------------ */
// ✅ Agora herda de AndroidViewModel para ter acesso ao Context
class FeedViewModel(application: Application) : AndroidViewModel(application) {

    // ✅ Passa o contexto da aplicação para o repositório da API
    private val repo: FeedRepository = ApiFeedRepository(application.applicationContext)

    var estado by mutableStateOf<FeedState>(FeedState.Carregando)
        private set
    var enviando by mutableStateOf(false)
        private set
    var erroPublicar by mutableStateOf("")
        private set

    // TODO: preencher com as categorias da API quando existir uma rota que as liste
    val categorias: List<Categoria> = emptyList()

    init {
        carregar(mostrarCarregando = true)
    }

    fun carregar(mostrarCarregando: Boolean = true) {
        if (mostrarCarregando) estado = FeedState.Carregando
        viewModelScope.launch {
            estado = try {
                FeedState.Sucesso(repo.listarPostsRecentes())
            } catch (e: Exception) {
                FeedState.Erro(e.message ?: "Não foi possível carregar as publicações.")
            }
        }
    }

    fun limparErroPublicar() {
        erroPublicar = ""
    }

    fun publicar(texto: String, categoriaId: String?, foto: Uri?, onSucesso: () -> Unit) {
        if (enviando) return
        enviando = true
        erroPublicar = ""
        viewModelScope.launch {
            try {
                repo.criarPost(texto, categoriaId, foto)
                onSucesso()
                carregar(mostrarCarregando = false)
            } catch (e: Exception) {
                erroPublicar = e.message ?: "Não foi possível publicar."
            } finally {
                enviando = false
            }
        }
    }
}