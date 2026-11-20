package com.vibeeco.app.data.api

import com.vibeeco.app.data.api.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface VibeApiService {

    @GET("telaInicio/{id}")
    suspend fun buscarTelaInicial(@Path("id") idUsuario: Int): ApiResponse<TelaInicioWrapper>

    @GET("postRecente")
    suspend fun listarPostsRecentes(): ApiResponse<PostsWrapper>

    @GET("postCurtida")
    suspend fun listarPostsRelevantes(): ApiResponse<PostsWrapper>

    @GET("post/{id}")
    suspend fun buscarPost(@Path("id") id: Int): ApiResponse<PostsWrapper>

    @Multipart
    @POST("post")
    suspend fun criarPost(
        @Part("descricao") descricao: RequestBody,
        @Part("id_usuario") idUsuario: RequestBody,
        @Part("categoria") categoria: RequestBody? = null,
        @Part anexos: List<MultipartBody.Part> = emptyList(),
    ): ApiResponse<Any>

    @DELETE("post/{id}")
    suspend fun excluirPost(@Path("id") id: Int): ApiResponse<Any>

    @POST("postCurtida")
    suspend fun curtirPost(@Body request: CurtidaRequest): ApiResponse<Any>

    @HTTP(method = "DELETE", path = "postCurtida", hasBody = true)
    suspend fun descurtirPost(@Body request: CurtidaRequest): ApiResponse<Any>

    @POST("postComentario")
    suspend fun adicionarComentario(@Body request: ComentarioRequest): ApiResponse<Any>

    @GET("postComentarioIdPost/{id}")
    suspend fun listarComentariosPost(@Path("id") idPost: Int): ApiResponse<PostComentariosWrapper>

    @DELETE("postComentario/{id}")
    suspend fun excluirComentario(@Path("id") idComentario: Int): ApiResponse<Any>

    @GET("ranking")
    suspend fun listarRanking(): ApiResponse<RankingWrapper>

    @PUT("ranking/{id}")
    suspend fun atualizarVisibilidadeRanking(
        @Path("id") idUsuario: Int,
        @Body request: RankingVisibilidadeRequest
    ): ApiResponse<Any>

    @GET("missoes/todasmissoes/{id}")
    suspend fun listarTodasMissoes(@Path("id") idUsuario: Int): ApiResponse<MissoesWrapper>

    @GET("missoes/missoesDisponiveis/{id}")
    suspend fun listarMissoesDisponiveis(@Path("id") idUsuario: Int): ApiResponse<MissoesWrapper>

    @GET("missoes/missoesAndamento/{id}")
    suspend fun listarMissoesAndamento(@Path("id") idUsuario: Int): ApiResponse<MissoesWrapper>

    @GET("missoes/missoesConcluidas/{id}")
    suspend fun listarMissoesConcluidas(@Path("id") idUsuario: Int): ApiResponse<MissoesWrapper>

    @POST("usuarioMissao")
    suspend fun entrarMissao(@Body request: UsuarioMissaoRequest): ApiResponse<Any>

    @POST("usuarioEtapa")
    suspend fun concluirEtapa(@Body request: UsuarioEtapaRequest): ApiResponse<Any>

    @GET("notificacoes/{id}")
    suspend fun listarNotificacoes(@Path("id") idUsuario: Int): List<NotificacaoApiDto>

    @PUT("notificacoes/{id}/lida")
    suspend fun marcarNotificacaoLida(@Path("id") idNotificacao: Int): Response<Unit>

    @PUT("notificacoes/{id}/todas-lidas")
    suspend fun marcarTodasNotificacoesLidas(@Path("id") idUsuario: Int): Response<Unit>

    @GET("historico/{id}")
    suspend fun buscarHistorico(@Path("id") idUsuario: Int): HistoricoWrapper

    @GET("configuracoes/{id}")
    suspend fun buscarConfiguracoes(@Path("id") idUsuario: Int): ConfiguracoesApiDto

    @PUT("configuracoes/{id}")
    suspend fun atualizarConfiguracoes(
        @Path("id") idUsuario: Int,
        @Body request: ConfiguracoesApiDto
    ): ConfiguracoesApiDto

    /* ---------------- Conteúdos Educativos ---------------- */
    @GET("conteudoEducativo/usuario/{idUsuario}")
    suspend fun listarConteudos(@Path("idUsuario") idUsuario: Int): ApiResponse<ListaConteudoWrapper>

    @POST("conteudoEducativo/{id}/usuario/{idUsuario}/concluir")
    suspend fun concluirConteudo(
        @Path("id") idConteudo: Int,
        @Path("idUsuario") idUsuario: Int,
        @Body request: ConcluirConteudoRequest
    ): ApiResponse<ConcluirConteudoResposta>
}