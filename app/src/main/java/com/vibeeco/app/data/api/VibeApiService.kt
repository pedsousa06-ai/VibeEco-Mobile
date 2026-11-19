package com.vibeeco.app.data.api

import com.vibeeco.app.data.api.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface VibeApiService {

    /* ---------------- Tela Inicial ---------------- */
    @GET("telaInicio/{id}")
    suspend fun buscarTelaInicial(
        @Path("id") idUsuario: Int
    ): ApiResponse<TelaInicioWrapper>

    /* ---------------- Posts / Feed ---------------- */
    @GET("postRecente")
    suspend fun listarPostsRecentes(): ApiResponse<PostsWrapper>

    @GET("postCurtida")
    suspend fun listarPostsRelevantes(): ApiResponse<PostsWrapper>

    @GET("post/{id}")
    suspend fun buscarPost(
        @Path("id") id: Int
    ): ApiResponse<PostsWrapper>

    @Multipart
    @POST("post")
    suspend fun criarPost(
        @Part("descricao") descricao: RequestBody,
        @Part("id_usuario") idUsuario: RequestBody,
        @Part("categoria") categoria: RequestBody? = null,
        @Part anexos: List<MultipartBody.Part> = emptyList(),
    ): ApiResponse<Any>

    @DELETE("post/{id}")
    suspend fun excluirPost(
        @Path("id") id: Int
    ): ApiResponse<Any>

    /* ---------------- Curtidas ---------------- */
    @POST("postCurtida")
    suspend fun curtirPost(
        @Body request: CurtidaRequest
    ): ApiResponse<Any>

    @HTTP(method = "DELETE", path = "postCurtida", hasBody = true)
    suspend fun descurtirPost(
        @Body request: CurtidaRequest
    ): ApiResponse<Any>

    /* ---------------- Comentários ---------------- */
    @POST("postComentario")
    suspend fun adicionarComentario(
        @Body request: ComentarioRequest
    ): ApiResponse<Any>

    @GET("postComentarioIdPost/{id}")
    suspend fun listarComentariosPost(
        @Path("id") idPost: Int
    ): ApiResponse<PostComentariosWrapper>

    @DELETE("postComentario/{id}")
    suspend fun excluirComentario(
        @Path("id") idComentario: Int
    ): ApiResponse<Any>

    /* ---------------- Ranking ---------------- */
    @GET("ranking")
    suspend fun listarRanking(): ApiResponse<RankingWrapper>

    @PUT("ranking/{id}")
    suspend fun atualizarVisibilidadeRanking(
        @Path("id") idUsuario: Int,
        @Body request: RankingVisibilidadeRequest
    ): ApiResponse<Any>

    /* ---------------- Missões ---------------- */
    @GET("missoes/todasmissoes/{id}")
    suspend fun listarTodasMissoes(
        @Path("id") idUsuario: Int
    ): ApiResponse<MissoesWrapper>

    @GET("missoes/missoesDisponiveis/{id}")
    suspend fun listarMissoesDisponiveis(
        @Path("id") idUsuario: Int
    ): ApiResponse<MissoesWrapper>

    @GET("missoes/missoesAndamento/{id}")
    suspend fun listarMissoesAndamento(
        @Path("id") idUsuario: Int
    ): ApiResponse<MissoesWrapper>

    @GET("missoes/missoesConcluidas/{id}")
    suspend fun listarMissoesConcluidas(
        @Path("id") idUsuario: Int
    ): ApiResponse<MissoesWrapper>

    @POST("usuarioMissao")
    suspend fun entrarMissao(
        @Body request: UsuarioMissaoRequest
    ): ApiResponse<Any>

    @POST("usuarioEtapa")
    suspend fun concluirEtapa(
        @Body request: UsuarioEtapaRequest
    ): ApiResponse<Any>

    /* ---------------- Notificações ---------------- */
    @GET("notificacoes/{id}")
    suspend fun listarNotificacoes(
        @Path("id") idUsuario: Int
    ): List<NotificacaoApiDto>

    @PUT("notificacoes/{id}/lida")
    suspend fun marcarNotificacaoLida(
        @Path("id") idNotificacao: Int
    ): Response<Unit>

    @PUT("notificacoes/{id}/todas-lidas")
    suspend fun marcarTodasNotificacoesLidas(
        @Path("id") idUsuario: Int
    ): Response<Unit>

    /* ---------------- Histórico ---------------- */
    @GET("historico/{id}")
    suspend fun buscarHistorico(
        @Path("id") idUsuario: Int
    ): HistoricoWrapper

    /* ---------------- Configurações ---------------- */
    @GET("configuracoes/{id}")
    suspend fun buscarConfiguracoes(
        @Path("id") idUsuario: Int
    ): ConfiguracoesApiDto

    @PUT("configuracoes/{id}")
    suspend fun atualizarConfiguracoes(
        @Path("id") idUsuario: Int,
        @Body request: ConfiguracoesApiDto
    ): ConfiguracoesApiDto
}
