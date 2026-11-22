package com.vibeeco.app.ui.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.icons.lucide.BookOpen
import com.composables.icons.lucide.Flame
import com.composables.icons.lucide.Coins
import com.composables.icons.lucide.Gift
import com.composables.icons.lucide.Leaf
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Medal
import com.composables.icons.lucide.Target
import com.composables.icons.lucide.Trophy
import com.composables.icons.lucide.Zap

/* ------------------------------------------------------------------ */
/*  TIPOS                                                             */
/* ------------------------------------------------------------------ */
enum class TipoAtividade(
    val rotulo: String,
    val cor: Color,
    val fundo: Color,
    val icone: ImageVector,
    val destinoRotulo: String,
) {
    Missao("Missão", Color(0xFFE8590C), Color(0x24E8590C), Lucide.Target, "Ver missões"),
    Conteudo("Conteúdo", Color(0xFF3B6FE0), Color(0x243B6FE0), Lucide.BookOpen, "Ver conteúdos"),
    Desafio("Desafio", Color(0xFF7C3AED), Color(0x247C3AED), Lucide.Flame, "Ver desafios"),
    Conquista("Conquista", Color(0xFFD97706), Color(0x24D97706), Lucide.Medal, "Ver conquistas"),
    Recompensa("Recompensa", Color(0xFF16A34A), Color(0x2416A34A), Lucide.Gift, "Ver recompensas"),
}

/* ------------------------------------------------------------------ */
/*  PERFIL                                                            */
/* ------------------------------------------------------------------ */
data class PerfilUsuario(
    val nome: String = "",
    val nomePublico: String = "",
    val email: String = "",
    val matricula: String = "",
    val curso: String = "",
    val localizacao: String = "",
    val bio: String = "",
    val foto: String = "",
    val instituicao: String = "",
    val nivel: String = "Nível 1",
    val desde: String = "",
) {
    val iniciais: String
        get() = nome.trim().split(" ").filter { it.isNotBlank() }.take(2)
            .joinToString("") { it.first().uppercase() }.ifEmpty { "VC" }
}

data class FormPerfil(
    val nome: String,
    val nomePublico: String,
    val curso: String,
    val localizacao: String,
    val bio: String,
) {
    companion object {
        fun de(p: PerfilUsuario) = FormPerfil(p.nome, p.nomePublico, p.curso, p.localizacao, p.bio)
    }
}

object PerfilStore {
    var perfil by mutableStateOf(PerfilUsuario())
        private set
    private var sincronizado = false

    /** true depois que o perfil veio da API: a partir daí o nome, o e-mail e o nível são os do servidor. */
    var daApi by mutableStateOf(false)
        private set

    /** Substitui o perfil pelos dados reais vindos da API (GET /perfil/{id}). */
    fun aplicarApi(p: PerfilUsuario) {
        perfil = p
        daApi = true
    }

    fun sincronizar(nome: String?, email: String?, nivel: String) {
        if (daApi) return
        if (sincronizado) {
            perfil = perfil.copy(nivel = nivel)
            return
        }
        sincronizado = true
        perfil = perfil.copy(
            nome = nome.orEmpty(),
            nomePublico = nome.orEmpty().split(" ").firstOrNull().orEmpty(),
            email = email.orEmpty(),
            nivel = nivel,
        )
    }

    fun atualizar(f: FormPerfil) {
        perfil = perfil.copy(
            nome = f.nome.trim(),
            nomePublico = f.nomePublico,
            curso = f.curso,
            localizacao = f.localizacao,
            bio = f.bio,
        )
    }
}

/* ------------------------------------------------------------------ */
/*  CONFIGURAÇÕES                                                     */
/* ------------------------------------------------------------------ */
class Configuracoes {
    var idioma by mutableStateOf("pt-BR")
    var tema by mutableStateOf("claro")
    var altoContraste by mutableStateOf(false)
    var tamanhoFonte by mutableStateOf("medio")
    var animacoes by mutableStateOf(true)
    var perfilPublico by mutableStateOf(true)
    var aparecerNoRanking by mutableStateOf(true)
    var atividadesVisiveis by mutableStateOf(true)
}

val configuracoes = Configuracoes()

/* ------------------------------------------------------------------ */
/*  DESEMPENHO, EVOLUÇÃO E CONQUISTAS (dados reais da API)             */
/* ------------------------------------------------------------------ */
val PERIODOS = listOf("7" to "7 dias", "30" to "30 dias", "todo" to "Todo período")

data class CartaoDesempenho(
    val id: String,
    val icone: ImageVector,
    val valor: String,
    val rotulo: String,
    val nota: String,
)

data class SemanaXp(val semana: String, val xp: Int) {
    fun altura(maximo: Int): Float = (xp.toFloat() / maximo).coerceIn(0.04f, 1f)
}

data class Conquista(
    val id: String,
    val titulo: String,
    val descricao: String,
    val icone: ImageVector,
    val cor: Color,
    val desbloqueada: Boolean,
)

data class GanhosPeriodo(val xp: Int = 0, val mc: Int = 0, val missoes: Int = 0)

/** Tudo que a tela de perfil mostra além dos dados pessoais. É preenchido por carregarPerfilDaApi(). */
object PerfilDados {
    var xpTotal by mutableStateOf(0)
    var posicao by mutableStateOf<Int?>(null)
    var totalParticipantes by mutableStateOf(0)
    var ganhos by mutableStateOf<Map<String, GanhosPeriodo>>(emptyMap())
    var evolucao by mutableStateOf<List<SemanaXp>>(emptyList())
    var evolucaoVariacao by mutableStateOf("—")
    var conquistas by mutableStateOf<List<Conquista>>(emptyList())
    var totalConquistas by mutableStateOf(0)
    var missoes by mutableStateOf<List<Missao>>(emptyList())
}

private fun milhar(n: Int): String = "%,d".format(java.util.Locale("pt", "BR"), n)

private val NOTA_PERIODO = mapOf("7" to "nos últimos 7 dias", "30" to "nos últimos 30 dias", "todo" to "desde o início")

fun obterDesempenho(periodo: String): List<CartaoDesempenho> {
    val g = PerfilDados.ganhos[periodo] ?: GanhosPeriodo()
    val nota = NOTA_PERIODO[periodo] ?: ""
    val total = PerfilDados.totalParticipantes
    return listOf(
        CartaoDesempenho("xp", Lucide.Zap, milhar(PerfilDados.xpTotal), "XP acumulado", "+${milhar(g.xp)} $nota"),
        CartaoDesempenho(
            "ranking", Lucide.Trophy,
            PerfilDados.posicao?.let { "#$it" } ?: "—", "Posição no ranking",
            if (total > 0) "Entre ${milhar(total)} participantes" else "Ranking indisponível",
        ),
        CartaoDesempenho("missoes", Lucide.Target, "${g.missoes}", "Missões concluídas", nota),
        CartaoDesempenho("mc", Lucide.Coins, milhar(g.mc), "Moedas Verdes ganhas", nota),
    )
}

/* ------------------------------------------------------------------ */
/*  MISSÕES                                                           */
/* ------------------------------------------------------------------ */
data class Missao(
    val id: String,
    val titulo: String,
    val categoria: String,
    val dificuldade: String,
    val resumo: String,
    val progresso: Int,
    val xp: Int,
    val mc: Int,
    val prazoDias: Int? = null,
) {
    val status: String
        get() = when {
            progresso >= 100 -> "concluida"
            progresso > 0 -> "andamento"
            else -> "disponivel"
        }
}

fun rotuloPrazo(dias: Int): String = when {
    dias <= 0 -> "Encerra hoje"
    dias == 1 -> "Falta 1 dia"
    else -> "Faltam $dias dias"
}


/* ------------------------------------------------------------------ */
/*  HISTÓRICO                                                         */
/* ------------------------------------------------------------------ */
data class Atividade(
    val id: String,
    val tipo: TipoAtividade,
    val rotulo: String,
    val titulo: String,
    val diasAtras: Int,
    val hora: String,
    val xp: Int = 0,
    val mc: Int = 0,
    val progresso: Int? = null,
    val descricao: String,
    val detalhes: List<Pair<String, String>> = emptyList(),
)

// ✅ MUDOU AQUI: De 'val ATIVIDADES = listOf(...)' para 'val ATIVIDADES = mutableStateListOf(...)'
val ATIVIDADES = mutableStateListOf(
    Atividade("a1", TipoAtividade.Missao, "Missão concluída", "Reciclagem em Casa", 0, "14:32", 120, 15, 100,
        "Você separou o lixo reciclável por sete dias seguidos. Ótimo trabalho!",
        listOf("Categoria" to "Resíduos", "Dificuldade" to "Fácil", "Duração" to "7 dias")),
    Atividade("a2", TipoAtividade.Conteudo, "Conteúdo lido", "Como compostar em apartamento", 0, "10:05", 30, 0, null,
        "Guia rápido para começar a compostagem em espaços pequenos.",
        listOf("Tempo de leitura" to "6 min", "Categoria" to "Resíduos")),
    Atividade("a3", TipoAtividade.Desafio, "Desafio em andamento", "Semana sem Carro", 1, "18:20", 50, 0, 60,
        "Você já completou 3 dos 5 dias do desafio. Continue assim!",
        listOf("Participantes" to "234", "Dias restantes" to "2")),
    Atividade("a4", TipoAtividade.Conquista, "Conquista desbloqueada", "Eco Iniciante", 1, "09:10", 100, 20, null,
        "Você completou sua primeira missão e ganhou o emblema Eco Iniciante.",
        listOf("Raridade" to "Comum")),
    Atividade("a5", TipoAtividade.Recompensa, "Recompensa resgatada", "Ecobag VibeEco", 2, "16:45", 0, -300, null,
        "Você trocou 300 Moedas Verdes por uma Ecobag VibeEco.",
        listOf("Tipo" to "Físico", "Código" to "VIBE-7K2MQ9")),
    Atividade("a6", TipoAtividade.Missao, "Missão concluída", "Consumo Consciente de Água", 3, "20:12", 90, 10, 100,
        "Você reduziu o tempo de banho e registrou a economia de água.",
        listOf("Categoria" to "Água", "Dificuldade" to "Médio")),
    Atividade("a7", TipoAtividade.Conteudo, "Conteúdo lido", "Energia solar no dia a dia", 3, "08:40", 30, 0, null,
        "Como a energia solar funciona e como ela pode reduzir sua conta de luz.",
        listOf("Tempo de leitura" to "8 min")),
    Atividade("a8", TipoAtividade.Desafio, "Desafio concluído", "Desafio da Água", 5, "19:00", 150, 25, 100,
        "Você completou o Desafio da Água e ficou entre os melhores da turma.",
        listOf("Participantes" to "412", "Posição final" to "#18")),
)

/* ------------------------------------------------------------------ */
/*  NOTIFICAÇÕES                                                      */
/* ------------------------------------------------------------------ */
data class Notificacao(
    val id: String,
    val tipo: TipoAtividade,
    val titulo: String,
    val texto: String,
    val minutosAtras: Int,
    val lida: Boolean,
    val detalhes: List<Pair<String, String>> = emptyList(),
)

fun tempoRelativo(minutos: Int): String = when {
    minutos < 1 -> "agora"
    minutos < 60 -> "há $minutos min"
    minutos < 1440 -> "há ${minutos / 60} h"
    minutos < 2880 -> "ontem"
    else -> "há ${minutos / 1440} dias"
}

object NotificacoesStore {
    val itens = mutableStateListOf(
        Notificacao("n1", TipoAtividade.Missao, "Nova missão disponível", "Horta no Apartamento já está disponível para você.", 15, false,
            listOf("Missão" to "Horta no Apartamento", "Recompensa" to "180 XP + 20 MC")),
        Notificacao("n2", TipoAtividade.Desafio, "Desafio termina em breve", "A Semana sem Carro termina em 2 dias. Faltam 2 check-ins.", 120, false,
            listOf("Desafio" to "Semana sem Carro", "Progresso" to "60%")),
        Notificacao("n3", TipoAtividade.Conquista, "Nova conquista!", "Você desbloqueou o emblema Eco Iniciante.", 600, false,
            listOf("Emblema" to "Eco Iniciante", "Bônus" to "100 XP + 20 MC")),
        Notificacao("n4", TipoAtividade.Missao, "Missão concluída", "Você concluiu Reciclagem em Casa e ganhou 120 XP.", 1500, true,
            listOf("Missão" to "Reciclagem em Casa")),
        Notificacao("n5", TipoAtividade.Conquista, "Emblema desbloqueado", "Leitor Verde: você leu 5 conteúdos.", 4000, true,
            listOf("Emblema" to "Leitor Verde")),
    )

    val naoLidas: Int get() = itens.count { !it.lida }

    fun marcarComoLida(id: String) {
        val i = itens.indexOfFirst { it.id == id }
        if (i >= 0 && !itens[i].lida) itens[i] = itens[i].copy(lida = true)
    }

    fun marcarTodasComoLidas() {
        for (i in itens.indices) if (!itens[i].lida) itens[i] = itens[i].copy(lida = true)
    }
}