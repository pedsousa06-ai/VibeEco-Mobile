
package com.vibeeco.app.data.api.model

import com.google.gson.annotations.SerializedName

/*
 * Envelope padrão das respostas da API do VibeEco.
 */
data class ApiResponse<T>(
    @SerializedName("status")
    val status: Boolean? = null,

    @SerializedName("status_code")
    val statusCode: Int? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("response")
    val response: T? = null
)

/* ------------------------------------------------------------------ */
/* 1. TELA INICIAL                                                    */
/* ------------------------------------------------------------------ */

data class TelaInicioWrapper(
    @SerializedName("tela_inicial")
    val telaInicial: TelaInicioDto? = null
)

data class TelaInicioDto(
    @SerializedName("usuario")
    val usuario: UsuarioInicioDto? = null,

    @SerializedName("missoes_em_andamento")
    val missoes: List<MissaoInicioDto>? = emptyList(),

    @SerializedName("conteudos_recomendados")
    val conteudos: List<ConteudoInicioDto>? = emptyList(),

    @SerializedName("ofensiva")
    val ofensiva: List<OfensivaInicioDto>? = emptyList(),

    @SerializedName("desafios_destaque")
    val desafios: List<DesafioInicioDto>? = emptyList(),

    @SerializedName("conquistas_recentes")
    val conquistas: List<ConquistaInicioDto>? = emptyList()
)

data class UsuarioInicioDto(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("nome")
    val nome: String? = null,

    @SerializedName("nome_usuario")
    val nomeUsuario: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("foto_usuario")
    val fotoPerfil: String? = null,

    @SerializedName("total_xp")
    val totalXp: Int? = null,

    @SerializedName("xp_usuario")
    val xpUsuario: Int? = null,

    @SerializedName("coins")
    val coins: Int? = null,

    @SerializedName("coins_usuario")
    val coinsUsuario: Int? = null,

    @SerializedName("total_moeda_verde")
    val totalMoedaVerde: Int? = null,

    @SerializedName("nivel_atual")
    val nivelAtual: Int? = null,

    @SerializedName("nome_level")
    val nomeLevel: String? = null,

    @SerializedName("proximo_nivel")
    val proximoNivel: Int? = null,

    @SerializedName("nome_proximo_level")
    val nomeProximoLevel: String? = null,

    @SerializedName("xp_proximo_level")
    val xpProximoLevel: Int? = null,

    @SerializedName("xp_faltante")
    val xpFaltante: Int? = null,

    @SerializedName("progresso_percentual")
    val progressoPercentual: Any? = null,

    @SerializedName("posicao_ranking")
    val posicaoRanking: Int? = null,

    @SerializedName("ranking_top_percentual")
    val rankingTopPercentual: Any? = null,

    @SerializedName("qtd_missoes_em_andamento")
    val qtdMissoesAndamento: Int? = null,

    @SerializedName("qtd_missoes_concluidas")
    val qtdMissoesConcluidas: Int? = null,

    @SerializedName("qtd_desafios_ativos")
    val qtdDesafiosAtivos: Int? = null
) {
    val nomeExibicao: String
        get() = nomeUsuario?.takeIf { it.isNotBlank() }
            ?: nome.orEmpty().ifEmpty { "Usuário" }

    val xpExibicao: Int
        get() = xpUsuario ?: totalXp ?: 0

    val coinsExibicao: Int
        get() = coinsUsuario ?: totalMoedaVerde ?: coins ?: 0
}

data class MissaoInicioDto(
    @SerializedName("id_usuario_missao")
    val id: Int? = null,

    @SerializedName("id_usuario")
    val idUsuario: Int? = null,

    @SerializedName("id_missao")
    val idMissao: Int? = null,

    @SerializedName("nome_missao")
    val nome: String? = null,

    @SerializedName("qtde_coins")
    val qtdeCoins: Int? = null,

    @SerializedName("qtde_xp")
    val qtdeXp: Int? = null,

    @SerializedName("progresso")
    val progresso: Int? = null,

    @SerializedName("prazo_dias")
    val prazoDias: Int? = null,

    @SerializedName("nome_dificuldade")
    val dificuldade: String? = null
)

data class ConteudoInicioDto(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("id_usuario")
    val idUsuario: Int? = null,

    @SerializedName("id_conteudo_educativo")
    val idConteudoEducativo: Int? = null,

    @SerializedName("nome_conteudo_educativo")
    val nome: String? = null,

    @SerializedName("xp_conteudo_educativo")
    val qtdeXp: Int? = null,

    @SerializedName("status")
    val status: Int? = null,

    @SerializedName("data_acesso")
    val dataAcesso: String? = null,

    @SerializedName("tempo_acesso_min")
    val duracaoLeitura: Int? = null
)

data class OfensivaInicioDto(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("id_usuario")
    val idUsuario: Int? = null,

    @SerializedName("data_ofensiva")
    val dataOfensiva: String? = null,

    @SerializedName("dia_semana")
    val diaSemana: String? = null,

    @SerializedName("status_ofensiva")
    val statusOfensiva: Int? = null
)

data class DesafioInicioDto(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("nome_desafio")
    val nome: String? = null,

    @SerializedName("super_coins")
    val superCoins: Int? = null,

    @SerializedName("qtde_participantes")
    val participantes: Int? = null,

    @SerializedName("prazo_dias")
    val dias: Int? = null
)

data class ConquistaInicioDto(
    @SerializedName("id_usuario_conquista")
    val id: Int? = null,

    @SerializedName("id_usuario")
    val idUsuario: Int? = null,

    @SerializedName("id_conquista")
    val idConquista: Int? = null,

    @SerializedName("data_conquista")
    val dataConquista: String? = null,

    @SerializedName("nome_conquista")
    val nome: String? = null,

    @SerializedName("descricao_conquista")
    val descricao: String? = null,

    @SerializedName("img_conquista")
    val img: String? = null
)

/* ------------------------------------------------------------------ */
/* 2. POSTS / FEED                                                    */
/* ------------------------------------------------------------------ */

data class PostsWrapper(
    @SerializedName("post")
    val post: List<PostApiDto>? = emptyList(),

    @SerializedName("count")
    val count: Int? = null
)

data class PostApiDto(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("nome_usuario")
    val nomeUsuario: String? = null,

    @SerializedName("foto_usuario")
    val fotoUsuario: String? = null,

    @SerializedName("descricao_post")
    val descricaoPost: String? = null,

    @SerializedName("data_publicacao")
    val dataPublicacao: String? = null,

    @SerializedName("qtde_curtida")
    val qtdeCurtida: Any? = null,

    @SerializedName("categoria")
    val categoria: List<PostCategoriaApiDto>? = emptyList(),

    @SerializedName("comentario")
    val comentario: List<PostComentarioApiDto>? = emptyList(),

    @SerializedName("anexo")
    val anexo: List<PostAnexoApiDto>? = emptyList()
)

data class PostCategoriaApiDto(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("id_categoria")
    val idCategoria: Int? = null,

    @SerializedName("nome_categoria")
    val nomeCategoria: String? = null,

    @SerializedName("nome")
    val nome: String? = null
)

data class PostComentarioApiDto(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("id_post_comentario")
    val idPostComentario: Int? = null,

    @SerializedName("id_usuario")
    val idUsuario: Int? = null,

    @SerializedName("id_post")
    val idPost: Int? = null,

    @SerializedName("nome_usuario")
    val nomeUsuario: String? = null,

    @SerializedName("nome_comentario")
    val nomeComentario: String? = null,

    @SerializedName("comentario")
    val comentario: String? = null,

    @SerializedName("data_comentario")
    val dataComentario: String? = null
)

data class PostComentariosWrapper(
    @SerializedName("post_comentario")
    val comentarios: List<PostComentarioApiDto>? = emptyList()
)

data class PostAnexoApiDto(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("id_anexo")
    val idAnexo: Int? = null,

    @SerializedName("nome_anexo")
    val nomeAnexo: String? = null,

    @SerializedName("endereco_url")
    val enderecoUrl: String? = null
)

data class CurtidaRequest(
    @SerializedName("id_usuario")
    val idUsuario: Int,

    @SerializedName("id_post")
    val idPost: Int
)

data class ComentarioRequest(
    @SerializedName("id_usuario")
    val idUsuario: Int,

    @SerializedName("id_post")
    val idPost: Int,

    @SerializedName("comentario")
    val comentario: String
)

/* ------------------------------------------------------------------ */
/* 3. RANKING                                                         */
/* ------------------------------------------------------------------ */

data class RankingWrapper(
    @SerializedName("ranking")
    val ranking: List<RankingUserApiDto>? = emptyList()
)

data class RankingUserApiDto(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("id_usuario")
    val idUsuario: Int? = null,

    @SerializedName("nome")
    val nome: String? = null,

    @SerializedName("nome_usuario")
    val nomeUsuario: String? = null,

    @SerializedName("total_xp")
    val totalXp: Int? = null,

    @SerializedName("xp_usuario")
    val xpUsuario: Int? = null,

    @SerializedName("total_moeda_verde")
    val totalMoedaVerde: Int? = null,

    @SerializedName("coins")
    val coins: Int? = null,

    @SerializedName("curso")
    val curso: String? = null,

    @SerializedName("instituicao")
    val instituicao: String? = null,

    @SerializedName("posicao")
    val posicao: Int? = null,

    @SerializedName("foto_perfil")
    val fotoPerfil: String? = null
)

data class RankingVisibilidadeRequest(
    @SerializedName("exibir_ranking")
    val exibirRanking: Boolean
)

/* ------------------------------------------------------------------ */
/* 4. MISSÕES                                                         */
/* ------------------------------------------------------------------ */

data class MissoesWrapper(
    @SerializedName("missoes")
    val missoes: List<MissaoApiDto>? = emptyList()
)

data class MissaoApiDto(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("id_missao")
    val idMissao: Int? = null,

    @SerializedName("nome")
    val nome: String? = null,

    @SerializedName("descricao")
    val descricao: String? = null,

    @SerializedName("coins")
    val coins: Int? = null,

    @SerializedName("total_xp_missao")
    val totalXpMissao: Int? = null,

    @SerializedName("data_inicio")
    val dataInicio: String? = null,

    @SerializedName("data_termino")
    val dataTermino: String? = null,

    @SerializedName("id_dificuldade")
    val idDificuldade: Any? = null,

    @SerializedName("nome_dificuldade")
    val nomeDificuldade: String? = null,

    @SerializedName("progresso")
    val progresso: Int? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("categoria")
    val categoria: List<MissaoCategoriaApiDto>? = emptyList()
)

data class MissaoCategoriaApiDto(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("nome_categoria")
    val nomeCategoria: String? = null
)

data class UsuarioMissaoRequest(
    @SerializedName("id_usuario")
    val idUsuario: Int,

    @SerializedName("id_missao")
    val idMissao: Int
)

data class UsuarioEtapaRequest(
    @SerializedName("id_usuario")
    val idUsuario: Int,

    @SerializedName("id_etapa")
    val idEtapa: Int
)

/* ------------------------------------------------------------------ */
/* 5. NOTIFICAÇÕES                                                    */
/* ------------------------------------------------------------------ */

data class NotificacaoApiDto(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("tipo")
    val tipo: String? = null,

    @SerializedName("titulo")
    val titulo: String? = null,

    @SerializedName("texto")
    val texto: String? = null,

    @SerializedName("minutosAtras")
    val minutosAtras: Int? = null,

    @SerializedName("lida")
    val lida: Boolean? = null,

    @SerializedName("detalhes")
    val detalhes: List<NotificacaoDetalheApiDto>? = emptyList()
)

data class NotificacaoDetalheApiDto(
    @SerializedName("rotulo")
    val rotulo: String? = null,

    @SerializedName("valor")
    val valor: String? = null
)

/* ------------------------------------------------------------------ */
/* 6. HISTÓRICO                                                       */
/* ------------------------------------------------------------------ */

data class HistoricoWrapper(
    @SerializedName("atividades")
    val atividades: List<AtividadeApiDto>? = emptyList()
)

data class AtividadeApiDto(
    @SerializedName("id")
    val id: Any? = null,

    @SerializedName("diasAtras")
    val diasAtras: Int? = null,

    @SerializedName("hora")
    val hora: String? = null,

    @SerializedName("tipo")
    val tipo: String? = null,

    @SerializedName("rotulo")
    val rotulo: String? = null,

    @SerializedName("titulo")
    val titulo: String? = null,

    @SerializedName("xp")
    val xp: Int? = null,

    @SerializedName("mc")
    val mc: Int? = null,

    @SerializedName("progresso")
    val progresso: Int? = null,

    @SerializedName("descricao")
    val descricao: String? = null,

    @SerializedName("detalhes")
    val detalhes: List<AtividadeDetalheApiDto>? = emptyList()
)

data class AtividadeDetalheApiDto(
    @SerializedName("rotulo")
    val rotulo: String? = null,

    @SerializedName("valor")
    val valor: String? = null
)

/* ------------------------------------------------------------------ */
/* 7. CONFIGURAÇÕES                                                   */
/* ------------------------------------------------------------------ */

data class ConfiguracoesApiDto(
    @SerializedName("idioma")
    val idioma: String? = null,

    @SerializedName("tema")
    val tema: String? = null,

    @SerializedName("altoContraste")
    val altoContraste: Boolean? = null,

    @SerializedName("tamanhoFonte")
    val tamanhoFonte: String? = null,

    @SerializedName("animacoes")
    val animacoes: Boolean? = null,

    @SerializedName("aparecerNoRanking")
    val aparecerNoRanking: Boolean? = null,

    @SerializedName("atividadesVisiveis")
    val atividadesVisiveis: Boolean? = null,

    @SerializedName("perfilPublico")
    val perfilPublico: Boolean? = null
)
