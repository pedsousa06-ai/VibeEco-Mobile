package com.vibeeco.app.ui.community

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Heart
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Send
import kotlinx.coroutines.launch

private object PD {
    val Fundo = Color(0xFFF7FAF8)
    val Texto = Color(0xFF12261A)
    val TextoSuave = Color(0xFF5B6E62)
    val Borda = Color(0xFFDDE7E0)
    val Verde = Color(0xFF14603A)
    val VerdeClaro = Color(0xFFDCFCE7)
    val Vermelho = Color(0xFFE11D48)
}

/* ------------------------------------------------------------------ */
/*  MODELOS                                                            */
/* ------------------------------------------------------------------ */
data class ComentarioPost(
    val id: String,
    val autor: String,
    val iniciais: String,
    val texto: String,
)

data class PostDetalhe(
    val id: String,
    val autor: String,
    val iniciais: String,
    val tempo: String,
    val categoria: String?,
    val texto: String,
    val imagem: String? = null,
    val curtidas: Int = 0,
    val curtiu: Boolean = false,
    val comentarios: List<ComentarioPost> = emptyList(),
)

sealed interface PostDetalheState {
    data object Carregando : PostDetalheState
    data class Erro(val mensagem: String) : PostDetalheState
    data class Sucesso(val post: PostDetalhe) : PostDetalheState
}

/* ------------------------------------------------------------------ */
/*  REPOSITÓRIO (Interface)                                            */
/* ------------------------------------------------------------------ */
interface PostDetalheRepository {
    suspend fun buscarPostPorId(id: String): PostDetalhe
    suspend fun curtirPost(id: String, curtiu: Boolean)
    suspend fun criarComentario(idPost: String, texto: String)
}

/* ------------------------------------------------------------------ */
/*  VIEWMODEL                                                          */
/* ------------------------------------------------------------------ */
class PostDetalheViewModel : ViewModel() {

    // ✅ Agora usa a API real em vez do Fake
    private val repository: PostDetalheRepository = ApiPostDetalheRepository()

    var estado by mutableStateOf<PostDetalheState>(PostDetalheState.Carregando)
        private set

    var enviando by mutableStateOf(false)
        private set

    var erroComentario by mutableStateOf("")
        private set

    fun carregar(id: String, mostrarCarregando: Boolean = true) {
        viewModelScope.launch {
            if (mostrarCarregando) estado = PostDetalheState.Carregando

            estado = try {
                PostDetalheState.Sucesso(repository.buscarPostPorId(id))
            } catch (e: Exception) {
                PostDetalheState.Erro(
                    e.message ?: "Não foi possível carregar a publicação."
                )
            }
        }
    }

    fun curtir() {
        val atual = (estado as? PostDetalheState.Sucesso)?.post ?: return
        val novoEstado = !atual.curtiu

        // Atualiza a interface imediatamente.
        estado = PostDetalheState.Sucesso(
            atual.copy(
                curtiu = novoEstado,
                curtidas = (atual.curtidas + if (novoEstado) 1 else -1).coerceAtLeast(0),
            )
        )

        viewModelScope.launch {
            try {
                repository.curtirPost(atual.id, novoEstado)
            } catch (e: Exception) {
                // Falhou: volta ao estado real do servidor.
                carregar(atual.id, mostrarCarregando = false)
            }
        }
    }

    fun enviarComentario(texto: String) {
        val atual = (estado as? PostDetalheState.Sucesso)?.post ?: return

        if (texto.isBlank() || enviando) return

        viewModelScope.launch {
            enviando = true
            erroComentario = ""

            try {
                repository.criarComentario(atual.id, texto.trim())
                carregar(atual.id, mostrarCarregando = false)
            } catch (e: Exception) {
                erroComentario = e.message ?: "Não foi possível enviar o comentário."
            } finally {
                enviando = false
            }
        }
    }
}

/* ------------------------------------------------------------------ */
/*  TELA                                                               */
/* ------------------------------------------------------------------ */
@Composable
fun PostDetalheScreen(
    postId: String,
    usuarioIniciais: String = "VC",
    onVoltar: () -> Unit,
    viewModel: PostDetalheViewModel = viewModel(),
) {
    var textoComentario by remember { mutableStateOf("") }

    LaunchedEffect(postId) {
        viewModel.carregar(postId)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PD.Fundo)
            .imePadding(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Row(
                modifier = Modifier
                    .clickable(onClick = onVoltar)
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Lucide.ArrowLeft,
                    contentDescription = "Voltar",
                    tint = PD.TextoSuave,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text("Voltar", color = PD.TextoSuave, fontSize = 14.sp)
            }
        }

        when (val estado = viewModel.estado) {
            is PostDetalheState.Carregando -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(50.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = PD.Verde)
                            Spacer(Modifier.height(12.dp))
                            Text("Carregando publicação...", color = PD.TextoSuave)
                        }
                    }
                }
            }

            is PostDetalheState.Erro -> {
                item {
                    AvisoDetalhe(
                        mensagem = estado.mensagem,
                        onTentarNovamente = { viewModel.carregar(postId) },
                    )
                }
            }

            is PostDetalheState.Sucesso -> {
                val post = estado.post

                item {
                    CardDetalhe {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AvatarDetalhe(post.iniciais)

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 10.dp)
                                ) {
                                    Text(
                                        post.autor,
                                        color = PD.Texto,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        post.tempo,
                                        color = PD.TextoSuave,
                                        fontSize = 12.sp,
                                    )
                                }

                                if (!post.categoria.isNullOrBlank()) {
                                    Text(
                                        post.categoria,
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(Color(0xFFFEF3C7))
                                            .padding(horizontal = 10.dp, vertical = 5.dp),
                                        color = Color(0xFF8A5A00),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                            }

                            Text(
                                post.texto,
                                color = PD.Texto,
                                fontSize = 15.sp,
                                lineHeight = 23.sp,
                            )

                            if (!post.imagem.isNullOrBlank()) {
                                AsyncImage(
                                    model = post.imagem,
                                    contentDescription = "Imagem da publicação",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(240.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, PD.Borda, RoundedCornerShape(8.dp))
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Row(
                                    modifier = Modifier
                                        .clickable { viewModel.curtir() }
                                        .padding(horizontal = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(
                                        Lucide.Heart,
                                        contentDescription = "Curtir",
                                        tint = if (post.curtiu) PD.Vermelho else PD.TextoSuave,
                                        modifier = Modifier.size(21.dp),
                                    )
                                    Spacer(Modifier.width(7.dp))
                                    Text(
                                        "${post.curtidas} curtidas",
                                        color = if (post.curtiu) PD.Vermelho else PD.TextoSuave,
                                        fontSize = 14.sp,
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    CardDetalhe {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Text(
                                "Comentários (${post.comentarios.size})",
                                color = PD.Texto,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                            )

                            if (post.comentarios.isEmpty()) {
                                Text(
                                    "Nenhum comentário ainda. Seja o primeiro!",
                                    color = PD.TextoSuave,
                                    fontSize = 14.sp,
                                )
                            } else {
                                post.comentarios.forEach { comentario ->
                                    ComentarioItem(comentario)
                                }
                            }

                            if (viewModel.erroComentario.isNotBlank()) {
                                Text(
                                    viewModel.erroComentario,
                                    color = Color(0xFFB91C1C),
                                    fontSize = 12.sp,
                                )
                            }

                            val podeEnviar = textoComentario.isNotBlank() && !viewModel.enviando

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, PD.Borda, RoundedCornerShape(12.dp))
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                AvatarDetalhe(usuarioIniciais, 32.dp)

                                BasicTextField(
                                    value = textoComentario,
                                    onValueChange = { textoComentario = it },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 10.dp),
                                    textStyle = TextStyle(
                                        color = PD.Texto,
                                        fontSize = 14.sp,
                                    ),
                                    decorationBox = { innerTextField ->
                                        Box {
                                            if (textoComentario.isEmpty()) {
                                                Text(
                                                    "Escreva um comentário...",
                                                    color = PD.TextoSuave,
                                                    fontSize = 13.sp,
                                                )
                                            }
                                            innerTextField()
                                        }
                                    },
                                )

                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (podeEnviar) PD.Verde else PD.Borda)
                                        .clickable(enabled = podeEnviar) {
                                            viewModel.enviarComentario(textoComentario)
                                            textoComentario = ""
                                        },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    if (viewModel.enviando) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(17.dp),
                                            color = Color.White,
                                            strokeWidth = 2.dp,
                                        )
                                    } else {
                                        Icon(
                                            Lucide.Send,
                                            contentDescription = "Enviar comentário",
                                            tint = Color.White,
                                            modifier = Modifier.size(17.dp),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CardDetalhe(conteudo: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, PD.Borda, RoundedCornerShape(16.dp))
    ) {
        conteudo()
    }
}

@Composable
private fun AvatarDetalhe(
    iniciais: String,
    tamanho: Dp = 40.dp,
) {
    Box(
        modifier = Modifier
            .size(tamanho)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF22C55E), Color(0xFF0F7A3B))
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            iniciais,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun ComentarioItem(comentario: ComentarioPost) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        AvatarDetalhe(comentario.iniciais, 32.dp)

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp)
        ) {
            Text(
                comentario.autor,
                color = PD.Texto,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(3.dp))
            Text(
                comentario.texto,
                color = PD.Texto,
                fontSize = 14.sp,
                lineHeight = 20.sp,
            )
        }
    }
}

@Composable
private fun AvisoDetalhe(
    mensagem: String,
    onTentarNovamente: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, PD.Borda, RoundedCornerShape(16.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            mensagem,
            color = Color(0xFFB91C1C),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
        )

        Text(
            "Tentar novamente",
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(PD.Verde)
                .clickable(onClick = onTentarNovamente)
                .padding(horizontal = 18.dp, vertical = 10.dp),
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
        )
    }
}