package com.vibeeco.app.ui.community

import com.vibeeco.app.data.Session
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.vibeeco.app.data.api.RetrofitClient
import com.vibeeco.app.data.api.garantirSucesso
import com.vibeeco.app.data.api.iniciaisDe
import com.vibeeco.app.data.api.model.ComentarioRequest
import com.vibeeco.app.data.api.model.CurtidaRequest
import com.vibeeco.app.data.api.model.PostApiDto
import com.vibeeco.app.data.api.paraInt
import com.vibeeco.app.data.api.tempoRelativoApi
import com.vibeeco.app.data.api.valorOuErro
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

/* ------------------------------------------------------------------ */
/*  Conversão API -> modelos da tela                                    */
/* ------------------------------------------------------------------ */

private fun PostApiDto.paraPost(): Post {
    val nome = nomeUsuario ?: "Usuário"
    return Post(
        id = (id ?: 0).toString(),
        autor = nome,
        iniciais = iniciaisDe(nome),
        tempo = tempoRelativoApi(dataPublicacao),
        categoria = categoria.orEmpty().firstOrNull()?.let { it.nomeCategoria ?: it.nome },
        texto = descricaoPost.orEmpty(),
        imagem = anexo.orEmpty().firstOrNull()?.enderecoUrl,
        curtidas = qtdeCurtida.paraInt(),
        comentarios = comentario.orEmpty().size,
    )
}

private fun PostApiDto.paraPostDetalhe(): PostDetalhe {
    val nome = nomeUsuario ?: "Usuário"
    return PostDetalhe(
        id = (id ?: 0).toString(),
        autor = nome,
        iniciais = iniciaisDe(nome),
        tempo = tempoRelativoApi(dataPublicacao),
        categoria = categoria.orEmpty().firstOrNull()?.let { it.nomeCategoria ?: it.nome },
        texto = descricaoPost.orEmpty(),
        imagem = anexo.orEmpty().firstOrNull()?.enderecoUrl,
        curtidas = qtdeCurtida.paraInt(),
        // A API não informa se o usuário logado já curtiu; começa como "não curtiu".
        curtiu = false,
        comentarios = comentario.orEmpty().map {
            val autor = it.nomeUsuario ?: "Usuário"
            ComentarioPost(
                id = (it.idPostComentario ?: it.id ?: 0).toString(),
                autor = autor,
                iniciais = iniciaisDe(autor),
                // A API devolve o texto do comentário no campo "nome_comentario".
                texto = (it.nomeComentario ?: it.comentario).orEmpty(),
            )
        },
    )
}

/* ------------------------------------------------------------------ */
/*  FEED                                                                */
/* ------------------------------------------------------------------ */

/**
 * @param contexto usado só para ler a foto escolhida (content://) e enviar como anexo.
 *                 Passe o applicationContext.
 */
class ApiFeedRepository(private val contexto: Context? = null) : FeedRepository {

    private val api = RetrofitClient.vibeApi

    override suspend fun listarPostsRecentes(): List<Post> {
        val resposta = try {
            api.listarPostsRecentes()
        } catch (e: retrofit2.HttpException) {
            // O back-end responde 404 quando ainda não há nenhum post.
            if (e.code() == 404) return emptyList()
            throw e
        }
        if (resposta.statusCode == 404) return emptyList()
        return resposta.valorOuErro("Não foi possível carregar as publicações.")
            .post.orEmpty()
            .map { it.paraPost() }
    }

    override suspend fun criarPost(texto: String, categoriaId: String?, foto: Uri?) {
        fun corpo(valor: String): RequestBody = valor.toRequestBody("text/plain".toMediaTypeOrNull())

        // O back-end espera "categoria" como JSON: [{"id": 1}]
        val categoriaJson = categoriaId?.toIntOrNull()?.let { corpo("""[{"id":$it}]""") }

        val anexos = mutableListOf<MultipartBody.Part>()
        if (foto != null && contexto != null) {
            lerAnexo(contexto, foto)?.let { anexos.add(it) }
        }

        api.criarPost(
            descricao = corpo(texto),
            idUsuario = corpo(Session.userId.toString()),
            categoria = categoriaJson,
            anexos = anexos,
        ).garantirSucesso("Não foi possível publicar.")
    }

    private fun lerAnexo(contexto: Context, uri: Uri): MultipartBody.Part? {
        val resolver = contexto.contentResolver
        val bytes = resolver.openInputStream(uri)?.use { it.readBytes() } ?: return null
        val tipo = resolver.getType(uri) ?: "image/jpeg"
        val nome = resolver.query(uri, null, null, null, null)?.use { c ->
            val i = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (i >= 0 && c.moveToFirst()) c.getString(i) else null
        } ?: "foto.jpg"
        return MultipartBody.Part.createFormData(
            "anexos",
            nome,
            bytes.toRequestBody(tipo.toMediaTypeOrNull()),
        )
    }
}

/* ------------------------------------------------------------------ */
/*  DETALHE DO POST                                                     */
/* ------------------------------------------------------------------ */

class ApiPostDetalheRepository : PostDetalheRepository {

    private val api = RetrofitClient.vibeApi

    override suspend fun buscarPostPorId(id: String): PostDetalhe {
        val idPost = id.toIntOrNull() ?: throw Exception("Publicação inválida.")
        val post = api.buscarPost(idPost)
            .valorOuErro("Publicação não encontrada.")
            .post.orEmpty()
            .firstOrNull()
            ?: throw Exception("Publicação não encontrada.")
        return post.paraPostDetalhe()
    }

    override suspend fun curtirPost(id: String, curtiu: Boolean) {
        val corpo = CurtidaRequest(idUsuario = Session.userId, idPost = id.toIntOrNull() ?: return)
        val resposta = if (curtiu) api.curtirPost(corpo) else api.descurtirPost(corpo)
        resposta.garantirSucesso("Não foi possível atualizar a curtida.")
    }

    override suspend fun criarComentario(idPost: String, texto: String) {
        api.adicionarComentario(
            ComentarioRequest(
                idUsuario = Session.userId,
                idPost = idPost.toIntOrNull() ?: throw Exception("Publicação inválida."),
                comentario = texto,
            )
        ).garantirSucesso("Não foi possível enviar o comentário.")
    }
}
