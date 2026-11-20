package com.vibeeco.app.data.api

import com.google.gson.JsonParser
import com.vibeeco.app.data.Category
import com.vibeeco.app.data.api.model.ApiResponse
import retrofit2.HttpException
import java.io.IOException
import java.text.Normalizer
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Lança uma exceção com a mensagem da API quando status = false.
 * Devolve o conteúdo de "response" quando deu certo.
 */
fun <T> ApiResponse<T>.valorOuErro(mensagemPadrao: String): T {
    if (status == false) throw Exception(message ?: mensagemPadrao)
    return response ?: throw Exception(mensagemPadrao)
}

/** Para respostas em que só importa saber se deu certo (POST/PUT/DELETE). */
fun <T> ApiResponse<T>.garantirSucesso(mensagemPadrao: String) {
    if (status == false) throw Exception(message ?: mensagemPadrao)
}

/**
 * Transforma qualquer erro de chamada em um texto para mostrar ao usuário.
 * Para erros HTTP, usa o "message" (e o "field") que o back-end devolveu.
 */
fun mensagemDeErro(e: Throwable, padrao: String): String {
    if (e is HttpException) {
        val corpo = try {
            e.response()?.errorBody()?.string()
        } catch (ex: Exception) {
            null
        }
        return corpo?.let { extrairMensagem(it) } ?: padrao
    }
    if (e is IOException) {
        return "Sem conexão com o servidor. Verifique a rede e se a API está ligada."
    }
    return e.message?.takeIf { it.isNotBlank() } ?: padrao
}

private fun extrairMensagem(corpo: String): String? {
    return try {
        val json = JsonParser.parseString(corpo).asJsonObject
        fun texto(chave: String): String? =
            json.get(chave)
                ?.takeIf { it.isJsonPrimitive }
                ?.asString
                ?.takeIf { it.isNotBlank() }

        val mensagem = texto("message") ?: texto("mensagem")
        val campo = texto("field")
        if (mensagem != null && campo != null) "$mensagem $campo" else mensagem ?: campo
    } catch (ex: Exception) {
        null
    }
}

/** "João da Silva" -> "JS" */
fun iniciaisDe(nome: String?): String =
    nome.orEmpty().trim()
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifEmpty { "?" }

private val FORMATO_BR: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

private fun interpretarData(data: String): LocalDateTime? {
    // Formato que as views do banco devolvem: "19/11/2026 14:30"
    try {
        return LocalDateTime.parse(data.trim(), FORMATO_BR)
    } catch (e: Exception) {
    }
    // ISO com fuso: "2026-11-19T14:30:00.000Z"
    try {
        return OffsetDateTime.parse(data).atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
    } catch (e: Exception) {
    }
    // "2026-11-19 14:30:00"
    try {
        return LocalDateTime.parse(data.replace(" ", "T").removeSuffix("Z").substringBefore("."))
    } catch (e: Exception) {
    }
    return null
}

/**
 * Converte a data da API em texto relativo ("há 2 h", "ontem").
 * Se não conseguir interpretar, devolve o texto original.
 */
fun tempoRelativoApi(data: String?): String {
    if (data.isNullOrBlank()) return ""
    val momento = interpretarData(data) ?: return data
    val agora = LocalDateTime.now()
    val minutos = ChronoUnit.MINUTES.between(momento, agora).coerceAtLeast(0)
    val horas = minutos / 60
    val dias = horas / 24
    return when {
        minutos < 1 -> "agora"
        minutos < 60 -> "há $minutos min"
        horas < 24 -> "há $horas h"
        dias == 1L -> "ontem"
        dias < 30 -> "há $dias dias"
        else -> data.take(10)
    }
}

/** A API às vezes manda número como string, número ou decimal. */
fun Any?.paraInt(): Int = when (this) {
    is Number -> this.toInt()
    is String -> this.toDoubleOrNull()?.toInt() ?: 0
    else -> 0
}

/** O MySQL devolve booleano como 0/1, "0"/"1" ou true/false. */
fun Any?.paraBool(): Boolean = when (this) {
    is Boolean -> this
    is Number -> this.toInt() != 0
    is String -> this == "1" || this.equals("true", ignoreCase = true)
    else -> false
}

private fun semAcento(texto: String): String =
    Normalizer.normalize(texto, Normalizer.Form.NFD)
        .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
        .lowercase()

/**
 * O banco tem categorias como "Reciclagem", "Sustentabilidade" e "Uso consciente da água",
 * mas o app trabalha com 5 categorias fixas (ícone + cor). Esta função faz a ponte.
 * O que não for reconhecido cai em RESIDUOS.
 */
fun categoriaDe(nome: String?): Category {
    val t = semAcento(nome.orEmpty())
    return when {
        "energ" in t -> Category.ENERGIA
        "agua" in t || "hidric" in t -> Category.AGUA
        "recicl" in t || "resid" in t || "lixo" in t || "compost" in t -> Category.RESIDUOS
        "transp" in t || "mobil" in t || "bicic" in t -> Category.TRANSPORTE
        "aliment" in t || "comida" in t || "vegetar" in t -> Category.ALIMENTACAO
        else -> Category.RESIDUOS
    }
}
