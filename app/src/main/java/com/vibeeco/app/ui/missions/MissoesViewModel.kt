package com.vibeeco.app.data

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel

enum class StatusMissao { Disponivel, Andamento, Concluida }

data class MissaoUi(
    val missao: Mission,
    val progresso: Int,
    val status: StatusMissao,
)

class MissoesViewModel : ViewModel() {
    private val progresso = mutableStateMapOf<String, Int>()

    private fun progressoDe(m: Mission) = progresso[m.id] ?: m.progress

    private fun statusDe(p: Int) = when {
        p >= 100 -> StatusMissao.Concluida
        p > 0 -> StatusMissao.Andamento
        else -> StatusMissao.Disponivel
    }

    /** Lê o mapa de estado, então a UI recompõe sozinha. */
    val cartoes: List<MissaoUi>
        get() = Content.missions.map {
            val p = progressoDe(it)
            MissaoUi(it, p, statusDe(p))
        }

    fun buscar(id: String): MissaoUi? = cartoes.firstOrNull { it.missao.id == id }

    fun participar(id: String) {
        val ui = buscar(id) ?: return
        if (ui.progresso == 0) progresso[id] = 10
    }

    /** Retorna true se a missão acabou de ser concluída. */
    fun avancar(id: String, passo: Int = 25): Boolean {
        val ui = buscar(id) ?: return false
        if (ui.status == StatusMissao.Concluida) return false
        val novo = (ui.progresso + passo).coerceAtMost(100)
        progresso[id] = novo
        return novo >= 100
    }
}
