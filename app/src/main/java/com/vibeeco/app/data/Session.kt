package com.vibeeco.app.data

import android.util.Base64
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.json.JSONObject

/** Sessão do usuário logado: id e token JWT devolvidos por POST /auth/login. */
object Session {
    var userId: Int = 0
    var token: String? = null

    /** Vira true quando a API recusa o token (vencido/inválido); a navegação então volta ao login. */
    var expirada by mutableStateOf(false)

    fun iniciar(id: Int, jwt: String) {
        userId = id
        token = jwt
        expirada = false
    }

    fun limpar() {
        userId = 0
        token = null
    }

    /** Lê o "exp" do JWT (segundos) sem validar a assinatura: quem valida é a API. */
    fun tokenVigente(jwt: String?): Boolean {
        if (jwt.isNullOrBlank()) return false
        return try {
            val corpo = jwt.split(".")[1]
            val json = JSONObject(String(Base64.decode(corpo, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP), Charsets.UTF_8))
            json.optLong("exp", 0L) * 1000 > System.currentTimeMillis()
        } catch (e: Exception) {
            false
        }
    }
}
