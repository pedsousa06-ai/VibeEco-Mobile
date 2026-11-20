package com.vibeeco.app.data

object Session {
    // ✅ Inicializado com 1, que é o ID que existe no seu banco (Ana Silva)
    var userId: Int = AppConfig.DEV_USER_ID
    var token: String? = null

    fun limpar() {
        // Se quiser que ao sair ele volte para 0 (ou mantenha o 1 para testes)
        userId = 0
        token = null
    }
}