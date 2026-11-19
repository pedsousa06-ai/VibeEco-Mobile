package com.vibeeco.app.ui.community

/**
 * Fonte única de dados fake da comunidade.
 * Feed e Detalhe leem e escrevem aqui, então curtidas, comentários
 * e posts novos aparecem nas duas telas.
 *
 * Quando a API real existir, apague este arquivo e faça os
 * repositórios chamarem o back-end.
 */
object ComunidadeFakeStore {

    private val posts = mutableListOf(
        PostDetalhe(
            id = "p1",
            autor = "Carlos Mendes",
            iniciais = "CM",
            tempo = "há 2 h",
            categoria = "Conquista",
            texto = "Completei a Semana sem Carro! Foram 5 dias de bike e transporte público. 🚲",
            curtidas = 24,
            comentarios = listOf(
                ComentarioPost("c1", "Fernanda Lima", "FL", "Parabéns pela iniciativa! Muito bom! 🌱"),
                ComentarioPost("c2", "Rafael Costa", "RC", "Também estou participando desse desafio!"),
            ),
        ),
        PostDetalhe(
            id = "p2",
            autor = "Fernanda Lima",
            iniciais = "FL",
            tempo = "há 5 h",
            categoria = "Dica",
            texto = "Dica rápida: separar o lixo orgânico em casa reduz muito o volume que vai pro aterro.",
            curtidas = 18,
        ),
        PostDetalhe(
            id = "p3",
            autor = "Rafael Costa",
            iniciais = "RC",
            tempo = "ontem",
            categoria = null,
            texto = "Alguém tem sugestão de onde descartar óleo de cozinha usado aqui na região?",
            curtidas = 7,
        ),
    )

    fun todos(): List<PostDetalhe> = posts.toList()

    fun buscar(id: String): PostDetalhe? = posts.firstOrNull { it.id == id }

    fun adicionar(post: PostDetalhe) {
        posts.add(0, post)
    }

    fun atualizar(id: String, bloco: (PostDetalhe) -> PostDetalhe) {
        val i = posts.indexOfFirst { it.id == id }
        if (i >= 0) posts[i] = bloco(posts[i])
    }
}
