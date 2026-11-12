package com.vibeeco.app.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

data class Account(
    val name: String,
    val email: String,
    val matricula: String,
    val password: String,
    val mustChange: Boolean,
)

data class Progress(
    val points: Int = 0,
    val co2Grams: Int = 0,
    val actions: Int = 0,
    val streak: Int = 0,
    val lastDate: String = "",
    val doneDate: String = "",
    val doneIds: Set<String> = emptySet(),
    val readArticles: Set<String> = emptySet(),
)

/** Guarda contas, sessão e progresso no próprio aparelho. */
class Store(context: Context) {
    private val sp = context.getSharedPreferences("vibeeco_prefs", Context.MODE_PRIVATE)

    init { seedIfNeeded() }

    var onboardingSeen: Boolean
        get() = sp.getBoolean("onboarding_seen", false)
        set(v) { sp.edit().putBoolean("onboarding_seen", v).apply() }

    var notifications: Boolean
        get() = sp.getBoolean("notifications", true)
        set(v) { sp.edit().putBoolean("notifications", v).apply() }

    var sessionEmail: String?
        get() = sp.getString("session_email", null)
        set(v) {
            val e = sp.edit()
            if (v == null) e.remove("session_email") else e.putString("session_email", v)
            e.apply()
        }

    // ---------- contas ----------
    fun accounts(): List<Account> {
        val arr = JSONArray(sp.getString("accounts", "[]"))
        return List(arr.length()) { i ->
            val o = arr.getJSONObject(i)
            Account(
                o.getString("name"), o.getString("email"), o.getString("matricula"),
                o.getString("password"), o.getBoolean("mustChange"),
            )
        }
    }

    private fun saveAccounts(list: List<Account>) {
        val arr = JSONArray()
        list.forEach {
            arr.put(
                JSONObject()
                    .put("name", it.name).put("email", it.email)
                    .put("matricula", it.matricula).put("password", it.password)
                    .put("mustChange", it.mustChange)
            )
        }
        sp.edit().putString("accounts", arr.toString()).apply()
    }

    fun findAccount(id: String): Account? {
        val key = id.trim()
        return accounts().firstOrNull { it.email.equals(key, ignoreCase = true) || it.matricula == key }
    }

    fun updatePassword(email: String, newPassword: String, mustChange: Boolean = false) {
        saveAccounts(accounts().map {
            if (it.email.equals(email, ignoreCase = true)) it.copy(password = newPassword, mustChange = mustChange) else it
        })
    }

    // ---------- progresso ----------
    private fun toSet(arr: JSONArray?): Set<String> =
        if (arr == null) emptySet() else (0 until arr.length()).map { arr.getString(it) }.toSet()

    fun loadProgress(email: String): Progress {
        val raw = sp.getString("progress:" + email.lowercase(), null) ?: return Progress()
        val o = JSONObject(raw)
        return Progress(
            points = o.optInt("points"),
            co2Grams = o.optInt("co2Grams"),
            actions = o.optInt("actions"),
            streak = o.optInt("streak"),
            lastDate = o.optString("lastDate", ""),
            doneDate = o.optString("doneDate", ""),
            doneIds = toSet(o.optJSONArray("doneIds")),
            readArticles = toSet(o.optJSONArray("readArticles")),
        )
    }

    fun saveProgress(email: String, p: Progress) {
        val o = JSONObject()
            .put("points", p.points).put("co2Grams", p.co2Grams).put("actions", p.actions)
            .put("streak", p.streak).put("lastDate", p.lastDate).put("doneDate", p.doneDate)
            .put("doneIds", JSONArray(p.doneIds.toList()))
            .put("readArticles", JSONArray(p.readArticles.toList()))
        sp.edit().putString("progress:" + email.lowercase(), o.toString()).apply()
    }

    // Contas de demonstração (primeira execução)
    private fun seedIfNeeded() {
        if (sp.contains("accounts")) return
        saveAccounts(
            listOf(
                Account("Ana Beatriz Souza", "ana.beatriz@ufmg.br", "2023001234", "Vibe@1234", false),
                Account("Novo Aluno", "novo.aluno@ufmg.br", "2024005678", "Temp@1234", true),
            )
        )
        saveProgress(
            "ana.beatriz@ufmg.br",
            Progress(
                points = 230, co2Grams = 18400, actions = 14, streak = 3,
                lastDate = LocalDate.now().minusDays(1).toString(),
                readArticles = setOf("a1", "a2"),
            )
        )
    }
}
