package com.vibeeco.app.ui.community

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/* ------------------------------------------------------------------ */
/*  MODELOS (equivalem ao que o Feed.vue consome)                      */
/* ------------------------------------------------------------------ */
data class Post(
    val id: String,
    val autor: String,
    val iniciais: String,
    val tempo: String,
    val categoria: String? = null,
    val texto: String,
    val imagem: String? = null, // URL remota ou Uri local (string)
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
/*  REPOSITÓRIO                                                        */
/*  Equivale a services/feed.js (listarPostsRecentes / criaPost).      */
/*  Troque FakeFeedRepository por uma implementação que chame a API.   */
/* ------------------------------------------------------------------ */
interface FeedRepository {
    suspend fun listarPostsRecentes(): List<Post>
    suspend fun criarPost(texto: String, categoriaId: String?, foto: Uri?)
}

class FakeFeedRepository : FeedRepository {
    private val posts = mutableListOf(
        Post(
            id = "p1",
            autor = "Carlos Mendes",
            iniciais = "CM",
            tempo = "há 2 h",
            categoria = "Conquista",
            texto = "Completei a Semana sem Carro! Foram 5 dias de bike e transporte público. 🚲",
            curtidas = 24,
            comentarios = 6,
        ),
        Post(
            id = "p2",
            autor = "Fernanda Lima",
            iniciais = "FL",
            tempo = "há 5 h",
            categoria = "Dica",
            texto = "Dica rápida: separar o lixo orgânico em casa reduz muito o volume que vai pro aterro.",
            curtidas = 18,
            comentarios = 3,
        ),
        Post(
            id = "p3",
            autor = "Rafael Costa",
            iniciais = "RC",
            tempo = "ontem",
            texto = "Alguém tem sugestão de onde descartar óleo de cozinha usado aqui na região?",
            curtidas = 7,
            comentarios = 9,
        ),
    )

    override suspend fun listarPostsRecentes(): List<Post> {
        delay(600)
        return posts.toList()
    }

    override suspend fun criarPost(texto: String, categoriaId: String?, foto: Uri?) {
        delay(700)
        posts.add(
            0,
            Post(
                id = "p-${System.currentTimeMillis()}",
                autor = "Você",
                iniciais = "VC",
                tempo = "agora",
                texto = texto,
                imagem = foto?.toString(),
            ),
        )
    }
}

/* ------------------------------------------------------------------ */
/*  VIEWMODEL                                                          */
/* ------------------------------------------------------------------ */
class FeedViewModel : ViewModel() {

    private val repo: FeedRepository = FakeFeedRepository()

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
