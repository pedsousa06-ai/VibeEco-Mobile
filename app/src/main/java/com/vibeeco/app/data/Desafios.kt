package com.vibeeco.app.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel

enum class Escopo(val rotulo: String) {
    INDIVIDUAL("Individual"),
    COLETIVO("Coletivo"),
}

enum class Dificuldade(val rotulo: String, val fundo: Color, val texto: Color) {
    FACIL("Fácil", Color(0xFFDCFCE7), Color(0xFF15803D)),
    MEDIO("Médio", Color(0xFFFEF3C7), Color(0xFF92400E)),
    DIFICIL("Difícil", Color(0xFFFEE2E2), Color(0xFFB91C1C)),
}

data class Tarefa(
    val id: String,
    val titulo: String,
    val descricao: String,
    val feita: Boolean = false,
)

data class Desafio(
    val id: String,
    val titulo: String,
    val descricao: String,
    val category: Category,
    val escopo: Escopo,
    val dificuldade: Dificuldade,
    val participantes: Int,
    val meta: Int,
    val xp: Int,
    val mc: Int,
    val dias: Int,
    val tarefas: List<Tarefa>,
    val participando: Boolean = false,
) {
    val tarefasFeitas: Int get() = tarefas.count { it.feita }
    val progresso: Int get() = if (tarefas.isEmpty()) 0 else tarefasFeitas * 100 / tarefas.size
    val concluido: Boolean get() = tarefas.isNotEmpty() && tarefasFeitas == tarefas.size
    val participacao: Int get() = if (meta <= 0) 0 else (participantes * 100f / meta).toInt().coerceIn(0, 100)
    val rotuloDias: String get() = if (dias == 1) "1 dia" else "$dias dias"
}

/** Dados de exemplo (trocar pela API quando o back estiver integrado). */
object DesafiosSeed {
    val lista = listOf(
        Desafio(
            "d1", "Semana do Banho Curto",
            "Reduza o tempo do banho durante sete dias e economize água e energia.",
            Category.AGUA, Escopo.INDIVIDUAL, Dificuldade.FACIL, 38, 60, 120, 15, 7,
            listOf(
                Tarefa("t1", "Banho de 5 minutos", "Use um cronômetro e termine o banho em até 5 minutos."),
                Tarefa("t2", "Feche o chuveiro ao ensaboar", "Desligue a água enquanto se ensaboa."),
                Tarefa("t3", "Registre sua economia", "Anote quantos dias você cumpriu a meta."),
            ),
        ),
        Desafio(
            "d2", "Campus Sem Desperdício",
            "Toda a turma aproveita as refeições e reduz o descarte de comida no campus.",
            Category.ALIMENTACAO, Escopo.COLETIVO, Dificuldade.MEDIO, 84, 150, 250, 30, 14,
            listOf(
                Tarefa("t1", "Sirva só o que vai comer", "Pegue porções menores e repita se precisar."),
                Tarefa("t2", "Leve as sobras para casa", "Use uma marmita reutilizável."),
                Tarefa("t3", "Convide um colega", "Chame alguém para participar com você."),
            ),
        ),
        Desafio(
            "d3", "Desligue e Economize",
            "Tire da tomada o que não está em uso e apague as luzes ao sair.",
            Category.ENERGIA, Escopo.INDIVIDUAL, Dificuldade.FACIL, 52, 80, 100, 10, 5,
            listOf(
                Tarefa("t1", "Apague a luz ao sair", "Faça isso em todos os ambientes."),
                Tarefa("t2", "Desconecte carregadores", "Retire da tomada quando não estiver usando."),
            ),
        ),
        Desafio(
            "d4", "Mutirão de Reciclagem",
            "Organize a coleta de recicláveis e eletrônicos com sua turma.",
            Category.RESIDUOS, Escopo.COLETIVO, Dificuldade.DIFICIL, 120, 200, 400, 50, 10,
            listOf(
                Tarefa("t1", "Separe os recicláveis", "Papel, plástico, vidro e metal em recipientes diferentes."),
                Tarefa("t2", "Leve ao ponto de coleta", "Entregue o material em um ponto de coleta seletiva."),
                Tarefa("t3", "Descarte eletrônicos corretamente", "Leve pilhas e aparelhos antigos a um local apropriado."),
                Tarefa("t4", "Compartilhe o resultado", "Conte à turma quanto vocês recolheram."),
            ),
        ),
        Desafio(
            "d5", "Mobilidade Limpa",
            "Troque o carro por caminhada, bicicleta ou transporte coletivo.",
            Category.TRANSPORTE, Escopo.INDIVIDUAL, Dificuldade.MEDIO, 27, 50, 180, 20, 7,
            listOf(
                Tarefa("t1", "Vá a pé em um trajeto curto", "Escolha um percurso de até 1 km."),
                Tarefa("t2", "Use transporte coletivo", "Troque uma viagem de carro por ônibus ou metrô."),
                Tarefa("t3", "Pedale ao menos uma vez", "Use bicicleta em algum trajeto da semana."),
            ),
        ),
        Desafio(
            "d6", "Segunda Sem Carne",
            "Experimente refeições vegetarianas e reduza sua pegada de carbono.",
            Category.ALIMENTACAO, Escopo.INDIVIDUAL, Dificuldade.FACIL, 61, 100, 140, 15, 1,
            listOf(
                Tarefa("t1", "Almoço vegetariano", "Faça uma refeição sem carne hoje."),
                Tarefa("t2", "Jantar vegetariano", "Repita a dose no jantar."),
            ),
        ),
    )
}

class DesafiosViewModel : ViewModel() {
    val desafios = mutableStateListOf<Desafio>().apply { addAll(DesafiosSeed.lista) }

    fun buscar(id: String): Desafio? = desafios.firstOrNull { it.id == id }

    fun participar(id: String) = atualizar(id) {
        if (it.participando) it else it.copy(participando = true, participantes = it.participantes + 1)
    }

    fun abandonar(id: String) = atualizar(id) {
        it.copy(
            participando = false,
            participantes = (it.participantes - 1).coerceAtLeast(0),
            tarefas = it.tarefas.map { t -> t.copy(feita = false) },
        )
    }

    fun alternarTarefa(id: String, tarefaId: String) = atualizar(id) { d ->
        if (!d.participando) d
        else d.copy(tarefas = d.tarefas.map { if (it.id == tarefaId) it.copy(feita = !it.feita) else it })
    }

    private fun atualizar(id: String, bloco: (Desafio) -> Desafio) {
        val i = desafios.indexOfFirst { it.id == id }
        if (i >= 0) desafios[i] = bloco(desafios[i])
    }
}
