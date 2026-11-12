package com.vibeeco.app.data

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
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
    private var codeVerified = false

    val onboardingSeen: Boolean get() = store.onboardingSeen

    init {
        store.sessionEmail?.let { email -> store.findAccount(email)?.let { load(it) } }
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

    // ✅ TOTALMENTE LIMPO E CORRIGIDO
    private fun load(acc: Account) {
        user = User(
            name = acc.name,
            email = acc.email,
            matricula = acc.matricula,
            mustChangePassword = acc.mustChange,
            registration = acc.matricula // Usando matricula para preencher registration também
        )
        progress = normalize(store.loadProgress(acc.email))
    }


    private fun update(p: Progress) {
        progress = p
        user?.let { store.saveProgress(it.email, p) }
    }

    fun finishOnboarding() { store.onboardingSeen = true }

    fun login(identifier: String, password: String, remember: Boolean): LoginResult {
        val acc: Account
        if (AppConfig.SIMULATED_LOGIN) {
            // Login simulado: usa a conta digitada, se existir; senão, a conta de demonstração.
            acc = store.findAccount(identifier)
                ?: store.findAccount(AppConfig.DEMO_EMAIL)
                        ?: return LoginResult.Error("Conta de demonstração não encontrada.")
        } else {
            acc = store.findAccount(identifier) ?: return LoginResult.Error("Usuário não encontrado.")
            if (acc.password != password) return LoginResult.Error("Senha incorreta.")
        }
        load(acc)
        store.sessionEmail = if (remember && !acc.mustChange) acc.email else null
        return if (acc.mustChange) LoginResult.FirstAccess else LoginResult.Success
    }

    fun logout() {
        user = null
        progress = Progress()
        store.sessionEmail = null
    }

    // ---------- recuperação de senha ----------
    private fun newCode() = (100000..999999).random().toString()

    /** Gera o código (simulando o envio por e-mail). Retorna false se o e-mail não existe. */
    fun startRecovery(email: String): Boolean {
        val acc = store.findAccount(email) ?: return false
        pendingEmail = acc.email
        pendingCode = newCode()
        codeVerified = false
        return true
    }

    fun resendCode() { pendingCode = newCode(); codeVerified = false }

    fun verifyCode(code: String): Boolean {
        val ok = code == pendingCode
        if (ok) codeVerified = true
        return ok
    }

    fun resetPassword(newPassword: String): Boolean {
        val email = pendingEmail ?: return false
        if (!codeVerified) return false
        store.updatePassword(email, newPassword, mustChange = false)
        pendingEmail = null; pendingCode = null; codeVerified = false
        return true
    }

    /** Retorna null em caso de sucesso ou a mensagem de erro. */
    fun changePassword(current: String?, newPassword: String): String? {
        val u = user ?: return "Sessão expirada. Entre novamente."
        val acc = store.findAccount(u.email) ?: return "Conta não encontrada."
        if (current != null && current != acc.password) return "Senha atual incorreta."
        if (newPassword == acc.password) return "A nova senha deve ser diferente da atual."
        store.updatePassword(acc.email, newPassword, mustChange = false)
        user = u.copy(mustChangePassword = false)
        store.sessionEmail = u.email
        return null
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
    fun ranking(): List<RankEntry> {
        val me = user ?: return Content.rivals
        return (Content.rivals + RankEntry(me.name, "Você", progress.points, isMe = true))
            .sortedByDescending { it.points }
    }

    fun updateNotifications(enabled: Boolean) {
        notifications = enabled
        store.notifications = enabled
    }
}