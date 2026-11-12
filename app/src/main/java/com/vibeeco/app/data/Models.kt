package com.vibeeco.app.data

enum class Category(val label: String, val emoji: String) {
    ENERGIA("Energia", "⚡"),
    AGUA("Água", "💧"),
    RESIDUOS("Resíduos", "♻️"),
    TRANSPORTE("Transporte", "🚲"),
    ALIMENTACAO("Alimentação", "🥗"),
}

data class User(
    val name: String,
    val email: String,
    val matricula: String,
    val mustChangePassword: Boolean,
)

data class Challenge(
    val id: String,
    val title: String,
    val description: String,
    val category: Category,
    val points: Int,
    val co2Grams: Int,
)

data class Article(
    val id: String,
    val title: String,
    val summary: String,
    val body: String,
    val category: Category,
    val minutes: Int,
    val points: Int = 10,
)

data class RankEntry(
    val name: String,
    val course: String,
    val points: Int,
    val isMe: Boolean = false,
)

object Levels {
    private val names = listOf("Semente", "Broto", "Muda", "Arbusto", "Árvore", "Floresta")
    private val emojis = listOf("🌰", "🌱", "🌿", "🪴", "🌳", "🌲")

    fun level(points: Int) = points / 100 + 1
    fun name(points: Int) = names[(level(points) - 1).coerceAtMost(names.lastIndex)]
    fun emoji(points: Int) = emojis[(level(points) - 1).coerceAtMost(emojis.lastIndex)]
    fun progress(points: Int) = (points % 100) / 100f
    fun remaining(points: Int) = 100 - points % 100
}

fun formatCo2(grams: Int): String =
    if (grams >= 1000) String.format(java.util.Locale("pt", "BR"), "%.1f kg", grams / 1000.0)
    else "$grams g"

object Content {
    val challenges = listOf(
        Challenge("c1", "Banho de 5 minutos", "Reduza o tempo do banho e economize água e energia.", Category.AGUA, 20, 300),
        Challenge("c2", "Torneira fechada ao escovar os dentes", "Feche a torneira enquanto escova e ensaboa.", Category.AGUA, 10, 100),
        Challenge("c3", "Desligar aparelhos da tomada", "Tire da tomada o que não está em uso.", Category.ENERGIA, 15, 250),
        Challenge("c4", "Apagar a luz ao sair", "Ao sair de um ambiente, apague as luzes.", Category.ENERGIA, 10, 150),
        Challenge("c5", "Usar sacola retornável", "Leve sua própria sacola nas compras.", Category.RESIDUOS, 10, 80),
        Challenge("c6", "Separar o lixo reciclável", "Separe papel, plástico, vidro e metal do lixo comum.", Category.RESIDUOS, 20, 400),
        Challenge("c7", "Garrafa reutilizável", "Use sua garrafa em vez de copos descartáveis.", Category.RESIDUOS, 10, 60),
        Challenge("c8", "Ir a pé, de bike ou de ônibus", "Troque o carro por uma opção mais limpa.", Category.TRANSPORTE, 30, 1200),
        Challenge("c9", "Refeição sem carne", "Experimente uma refeição vegetariana hoje.", Category.ALIMENTACAO, 25, 1500),
        Challenge("c10", "Zero desperdício de comida", "Aproveite tudo o que está no prato e na geladeira.", Category.ALIMENTACAO, 20, 500),
    )

    val articles = listOf(
        Article(
            "a1", "O que é pegada de carbono?",
            "Entenda como suas escolhas diárias se transformam em emissões.",
            "A pegada de carbono é a soma dos gases de efeito estufa gerados por nossas atividades: o que comemos, como nos locomovemos, quanta energia consumimos e o que descartamos.\n\nO primeiro passo para reduzi-la é conhecer onde ela é maior. Transporte, alimentação e energia elétrica costumam ser os maiores vilões na rotina de quem vive em cidades.\n\nA boa notícia: pequenas mudanças repetidas todos os dias, como desligar aparelhos, caminhar mais e evitar desperdício, somam um impacto real ao longo do tempo.",
            Category.ENERGIA, 3,
        ),
        Article(
            "a2", "Como economizar água em casa",
            "Hábitos simples que poupam milhares de litros por ano.",
            "A água potável é um recurso limitado e seu uso consciente começa dentro de casa. Banhos mais curtos, torneiras fechadas durante a escovação e a correção de vazamentos já fazem grande diferença.\n\nUma torneira pingando ou uma descarga com vazamento podem desperdiçar muita água ao longo do mês sem que ninguém perceba. Vale revisar as instalações periodicamente.\n\nSempre que possível, reaproveite a água, por exemplo a da máquina de lavar, para limpar quintais e calçadas.",
            Category.AGUA, 3,
        ),
        Article(
            "a3", "Reciclagem: o que vai em cada lixeira",
            "Aprenda a separar corretamente e facilite o trabalho da coleta seletiva.",
            "A coleta seletiva usa cores padronizadas: azul para papel, vermelho para plástico, verde para vidro, amarelo para metal e marrom para resíduos orgânicos.\n\nAntes de descartar, esvazie e enxágue as embalagens. Restos de comida contaminam o material e podem impedir a reciclagem.\n\nLembre-se de que reduzir e reutilizar vêm antes de reciclar. O melhor resíduo é aquele que nem chega a ser gerado.",
            Category.RESIDUOS, 4,
        ),
        Article(
            "a4", "Mobilidade sustentável no dia a dia",
            "Caminhar, pedalar e usar transporte coletivo faz bem ao planeta e a você.",
            "Os meios de transporte individuais motorizados estão entre as principais fontes de emissões nas cidades. Trocar parte dos deslocamentos por caminhada, bicicleta ou transporte coletivo reduz a poluição e o trânsito.\n\nPara trajetos curtos, a bicicleta costuma ser tão rápida quanto o carro e ainda ajuda na saúde física e mental.\n\nQuando o carro for necessário, a carona solidária divide as emissões entre várias pessoas.",
            Category.TRANSPORTE, 3,
        ),
        Article(
            "a5", "Alimentação e clima",
            "O que está no seu prato também influencia o meio ambiente.",
            "A produção de alimentos consome terra, água e energia. Dietas com mais vegetais, frutas e grãos costumam ter menor impacto ambiental do que dietas com muita carne.\n\nVocê não precisa mudar tudo de uma vez: começar com uma refeição sem carne por dia ou por semana já é um ótimo passo.\n\nPlanejar as compras, guardar bem os alimentos e aproveitar sobras também reduz o desperdício e economiza dinheiro.",
            Category.ALIMENTACAO, 3,
        ),
        Article(
            "a6", "Energia no campus: onde dá para economizar",
            "Salas, laboratórios e bibliotecas também podem consumir menos.",
            "Ambientes compartilhados costumam ter lâmpadas e equipamentos ligados sem necessidade. Desligar projetores, computadores e ar-condicionado ao fim do uso é uma ação simples e coletiva.\n\nAproveitar a luz natural e manter janelas e portas fechadas com o ar-condicionado ligado aumenta a eficiência.\n\nSe você notar desperdícios, comunique a administração. A conscientização de uma pessoa inspira a de muitas outras.",
            Category.ENERGIA, 3,
        ),
    )

    val tips = listOf(
        "Leve uma sacola retornável na mochila: ela ocupa pouco espaço e evita dezenas de sacolas plásticas por ano.",
        "Descongele os alimentos na geladeira em vez de usar o micro-ondas. Você gasta menos energia.",
        "Prefira lâmpadas de LED: duram mais e consomem muito menos energia.",
        "Antes de imprimir, pense: precisa mesmo? Se sim, use frente e verso.",
        "Compre apenas o que vai consumir. Planejar a semana reduz o desperdício de comida.",
        "Banhos mais curtos economizam água e energia ao mesmo tempo.",
        "Doe ou troque roupas e livros em vez de descartar. Dê uma segunda vida aos objetos.",
    )

    val rivals = listOf(
        RankEntry("Lucas Menezes", "Engenharia Ambiental", 410),
        RankEntry("Mariana Costa", "Biologia", 365),
        RankEntry("Pedro Alves", "Geografia", 330),
        RankEntry("Juliana Rocha", "Arquitetura", 295),
        RankEntry("Rafael Lima", "Engenharia Civil", 240),
        RankEntry("Camila Torres", "Química", 205),
        RankEntry("Gabriel Nunes", "Administração", 170),
        RankEntry("Beatriz Souza", "Letras", 120),
    )
}
