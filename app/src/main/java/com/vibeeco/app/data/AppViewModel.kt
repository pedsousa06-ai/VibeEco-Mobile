package com.vibeeco.app.data

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.vibeeco.app.data.api.RetrofitClient
import com.vibeeco.app.data.api.garantirSucesso
import com.vibeeco.app.data.api.mensagemDeErro
import com.vibeeco.app.data.api.model.EmailRequest
import com.vibeeco.app.data.api.model.LoginRequest
import com.vibeeco.app.data.api.model.PrimeiroAcessoRequest
import com.vibeeco.app.data.api.model.RedefinirSenhaRequest
import com.vibeeco.app.data.api.model.ValidarCodigoRequest
import com.vibeeco.app.data.api.valorOuErro
import java.time.LocalDate

sealed interface LoginResult {
    data object Success : LoginResult
    data object FirstAccess : LoginResult
    data class Error(val message: String) : LoginResult
}

class AppViewModel(app: Application) : AndroidViewModel(app) {

    private val store = Store(app)

    var user by mutableStateOf<User?>(null)
        private set
    var progress by mutableStateOf(Progress())
        private set
    var notifications by mutableStateOf(store.notifications)
        private set
    var pendingCode by mutableStateOf<String?>(null)
        private set

    private var pendingEmail: String? = null
    private var resetToken: String? = null

    private val api get() = RetrofitClient.vibeApi

    val onboardingSeen: Boolean get() = store.onboardingSeen

    init {
        // restaura a sessão guardada (token JWT ainda dentro da validade)
        val token = store.authToken
        if (Session.tokenVigente(token) && store.authUserId > 0) {
            Session.iniciar(store.authUserId, token!!)
            carregarUsuario(store.authName, store.authEmail, store.authMatricula)
        } else {
            store.limparAuth()
        }
    }

    // ---------- sessão ----------
    private fun today() = LocalDate.now().toString()
    private fun yesterday() = LocalDate.now().minusDays(1).toString()

    private fun normalize(p: Progress): Progress {
        var n = p
        if (n.doneDate != today()) n = n.copy(doneDate = today(), doneIds = emptySet())
        if (n.lastDate != today() && n.lastDate != yesterday()) n = n.copy(streak = 0)
        return n
    }

    private fun carregarUsuario(nome: String, email: String, matricula: String) {
        user = User(
            name = nome,
            email = email,
            matricula = matricula,
            mustChangePassword = false,
            registration = matricula,
        )
        progress = normalize(store.loadProgress(email))
    }

    private fun update(p: Progress) {
        progress = p
        user?.let { store.saveProgress(it.email, p) }
    }

    fun finishOnboarding() { store.onboardingSeen = true }

    /**
     * Login de verdade: POST /auth/login (e-mail ou matrícula + senha).
     * "remember" = true guarda o token no aparelho para entrar direto na próxima vez.
     */
    suspend fun login(identifier: String, password: String, remember: Boolean): LoginResult {
        if (identifier.isBlank() || password.isBlank()) return LoginResult.Error("Informe e-mail ou matrícula e a senha.")
        return try {
            val r = api.login(LoginRequest(identifier.trim(), password)).valorOuErro("Não foi possível entrar.")
            val nome = r.usuario.nome.orEmpty()
            val email = r.usuario.email.orEmpty()
            val matricula = r.usuario.matricula.orEmpty()

            Session.iniciar(r.usuario.id, r.token)
            carregarUsuario(nome, email, matricula)

            if (remember) {
                store.authToken = r.token
                store.authUserId = r.usuario.id
                store.authName = nome
                store.authEmail = email
                store.authMatricula = matricula
            } else {
                store.limparAuth()
            }
            LoginResult.Success
        } catch (e: Exception) {
            LoginResult.Error(mensagemDeErro(e, "Não foi possível entrar. Tente novamente."))
        }
    }

    fun logout() {
        user = null
        progress = Progress()
        Session.limpar()
        store.limparAuth()
        store.sessionEmail = null
    }

    // ---------- recuperação de senha (API) ----------
    /** Pede o código por e-mail. Retorna null se deu certo ou a mensagem de erro. */
    suspend fun startRecovery(email: String): String? {
        return try {
            val r = api.recuperarAcesso(EmailRequest(email.trim())).response
            pendingEmail = email.trim()
            pendingCode = r?.codigoDev // só vem em desenvolvimento (EXPOR_CODIGO_DEV=true na API)
            resetToken = null
            null
        } catch (e: Exception) {
            mensagemDeErro(e, "Não foi possível enviar o código. Tente novamente.")
        }
    }

    suspend fun resendCode() {
        val email = pendingEmail ?: return
        try {
            pendingCode = api.recuperarAcesso(EmailRequest(email)).response?.codigoDev
            resetToken = null
        } catch (e: Exception) {
            // o aviso "reenviado" da tela continua; um novo pedido falho não muda o código atual
        }
    }

    suspend fun verifyCode(code: String): Boolean {
        val email = pendingEmail ?: return false
        return try {
            resetToken = api.validarCodigo(ValidarCodigoRequest(email, code.trim())).valorOuErro("Código inválido.").token
            true
        } catch (e: Exception) {
            false
        }
    }

    /** Retorna null se a senha foi redefinida ou a mensagem de erro. */
    suspend fun resetPassword(newPassword: String): String? {
        val token = resetToken ?: return "Sessão expirada. Refaça a recuperação de senha."
        return try {
            api.redefinirSenha(RedefinirSenhaRequest(token, newPassword)).garantirSucesso("Não foi possível alterar a senha.")
            pendingEmail = null; pendingCode = null; resetToken = null
            null
        } catch (e: Exception) {
            mensagemDeErro(e, "Não foi possível alterar a senha. Tente novamente.")
        }
    }

    /** Troca de senha com o usuário logado. Retorna null em caso de sucesso ou a mensagem de erro. */
    suspend fun changePassword(current: String?, newPassword: String): String? {
        val u = user ?: return "Sessão expirada. Entre novamente."
        if (current.isNullOrBlank()) return "Informe a senha atual."
        if (newPassword == current) return "A nova senha deve ser diferente da atual."
        return try {
            api.primeiroAcesso(PrimeiroAcessoRequest(u.email, current, newPassword))
                .garantirSucesso("Não foi possível alterar a senha.")
            null
        } catch (e: Exception) {
            mensagemDeErro(e, "Não foi possível alterar a senha. Tente novamente.")
        }
    }

    /** A API não revela se um usuário existe; a validação acontece ao criar a senha. */
    suspend fun verifyFirstAccessUser(identifier: String): Pair<Boolean, String?> {
        if (identifier.isBlank()) return false to "Informe seu e-mail ou matrícula."
        return true to null
    }

    /** Primeiro acesso: troca a senha provisória e já autentica o usuário. Retorna null se OK ou o erro. */
    suspend fun completeFirstAccess(identifier: String, currentPassword: String?, newPassword: String): String? {
        if (currentPassword.isNullOrBlank()) return "Informe a senha provisória."
        if (newPassword == currentPassword) return "A nova senha deve ser diferente da provisória."
        return try {
            api.primeiroAcesso(PrimeiroAcessoRequest(identifier.trim(), currentPassword, newPassword))
                .garantirSucesso("Não foi possível criar a senha.")
            when (val r = login(identifier, newPassword, remember = true)) {
                is LoginResult.Error -> r.message
                else -> null
            }
        } catch (e: Exception) {
            mensagemDeErro(e, "Não foi possível criar a senha. Tente novamente.")
        }
    }

    // ---------- desafios ----------
    /** Retorna true se o desafio ficou concluído. */
    fun toggleChallenge(c: Challenge): Boolean {
        val p = normalize(progress)
        return if (c.id in p.doneIds) {
            update(p.copy(
                doneIds = p.doneIds - c.id,
                points = (p.points - c.points).coerceAtLeast(0),
                co2Grams = (p.co2Grams - c.co2Grams).coerceAtLeast(0),
                actions = (p.actions - 1).coerceAtLeast(0),
            ))
            false
        } else {
            val streak = when (p.lastDate) {
                today() -> p.streak.coerceAtLeast(1)
                yesterday() -> p.streak + 1
                else -> 1
            }
            update(p.copy(
                doneIds = p.doneIds + c.id,
                points = p.points + c.points,
                co2Grams = p.co2Grams + c.co2Grams,
                actions = p.actions + 1,
                streak = streak,
                lastDate = today(),
            ))
            true
        }
    }

    // ---------- conteúdos ----------
    fun markArticleRead(a: Article): Boolean {
        val p = progress
        if (a.id in p.readArticles) return false
        update(p.copy(points = p.points + a.points, readArticles = p.readArticles + a.id))
        return true
    }

    // ---------- ranking e perfil ----------
    fun updateNotifications(enabled: Boolean) {
        notifications = enabled
        store.notifications = enabled
    }
}