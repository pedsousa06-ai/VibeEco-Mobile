package com.vibeeco.app.data.api

import com.vibeeco.app.data.api.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface VibeApiService {

    // ---------- autenticação ----------
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResposta>

    @POST("auth/recuperarAcesso")
    suspend fun recuperarAcesso(@Body request: EmailRequest): ApiResponse<RecuperacaoResposta>

    @POST("auth/validarCodigo")
    suspend fun validarCodigo(@Body request: ValidarCodigoRequest): ApiResponse<TokenResposta>

    @POST("auth/redefinirSenha")
    suspend fun redefinirSenha(@Body request: RedefinirSenhaRequest): ApiResponse<Any>

    @POST("auth/primeiroAcesso")
    suspend fun primeiroAcesso(@Body request: PrimeiroAcessoRequest): ApiResponse<Any>

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

    @GET("ranking/instituicao/{id}")
    suspend fun listarRankingInstituicao(@Path("id") idUsuario: Int): ApiResponse<RankingWrapper>

    @GET("ranking/grupo/{id}")
    suspend fun listarRankingGrupo(@Path("id") idUsuario: Int): ApiResponse<RankingWrapper>

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

    /** Participar de uma missão (a rota antiga era usuario_missao). */
    @POST("usuarioMissao")
    suspend fun entrarMissao(@Body request: UsuarioMissaoRequest): ApiResponse<Any>

    /**
     * Conclui uma etapa. Na última etapa o back-end conclui a missão sozinho
     * e paga XP/moedas (não existe mais PUT usuario_missao/concluir).
     */
    @POST("usuarioEtapa")
    suspend fun concluirEtapa(@Body request: UsuarioEtapaRequest): ApiResponse<UsuarioEtapaResposta>

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

    /* ---------------- Perfil ---------------- */
    @GET("perfil/{id}")
    suspend fun buscarPerfil(@Path("id") idUsuario: Int): ApiResponse<PerfilWrapper>

    @PUT("perfil/{id}")
    suspend fun atualizarPerfil(
        @Path("id") idUsuario: Int,
        @Body request: PerfilEdicaoRequest
    ): ApiResponse<PerfilEdicaoResposta>

    /* ---------------- Desafios ---------------- */
    @GET("desafios/todosUsuariosDesafiosIdUsuario/{id}")
    suspend fun listarDesafios(@Path("id") idUsuario: Int): ApiResponse<DesafiosWrapper>

    @POST("usuarioDesafio")
    suspend fun participarDesafio(@Body request: UsuarioDesafioRequest): ApiResponse<UsuarioDesafioResposta>

    @PUT("usuarioDesafio/{id}")
    suspend fun atualizarStatusDesafio(
        @Path("id") idUsuarioDesafio: Int,
        @Body request: StatusDesafioRequest
    ): ApiResponse<UsuarioDesafioResposta>

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