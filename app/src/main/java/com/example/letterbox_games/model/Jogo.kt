package com.example.letterbox_games.model

import java.util.Locale
import java.util.UUID

/** Por que um jogo aparece na tela Trending. */
enum class Tendencia(val rotulo: String, val emoji: String) {
    EM_ALTA("Em alta", "🔥"),
    VOLTOU("Voltou com tudo", "🔁"),
    LANCAMENTO("Acabou de lançar", "🚀"),
    SEMPRE_EM_ALTA("Sempre em alta", "👑")
}

/**
 * Data class 1: um jogo.
 * - O catálogo (Trending) tem tendencia preenchida, descricao e nota da comunidade.
 * - "Meus jogos" são os que têm jogadoEm != null; nesses valem minhaNota, horas e resenha.
 * - personalizado = true quando o usuário criou o jogo pela tela Adicionar Jogo.
 */
data class Jogo(
    val id: String = UUID.randomUUID().toString(),
    val nome: String,
    val plataforma: String,
    val genero: String = "Outro",
    val ano: Int = 2026,
    val descricao: String = "",
    val notaComunidade: Double = 0.0,
    val avaliacoes: Int = 0,
    val tendencia: Tendencia? = null,
    val variacaoJogadores: Int = 0,
    val personalizado: Boolean = false,
    val minhaNota: Int = 0,
    val horas: Int = 0,
    val resenha: String = "",
    val jogadoEm: Long? = null
) {
    val jogado: Boolean get() = jogadoEm != null
}

const val UM_DIA_EM_MS = 86_400_000L
const val UMA_HORA_EM_MS = 3_600_000L

/** Texto curto de "há quanto tempo" (ex.: "hoje", "ontem", "há 5 dias"). */
fun tempoAtras(momento: Long, agora: Long = System.currentTimeMillis()): String {
    val diferenca = (agora - momento).coerceAtLeast(0)
    val horas = diferenca / UMA_HORA_EM_MS
    val dias = diferenca / UM_DIA_EM_MS
    return when {
        horas < 1 -> "agora há pouco"
        dias < 1 -> "há ${horas}h"
        dias == 1L -> "ontem"
        dias < 30 -> "há $dias dias"
        else -> "há ${dias / 30} ${if (dias / 30 == 1L) "mês" else "meses"}"
    }
}

fun Double?.comoNota(): String =
    if (this == null) "—" else String.format(Locale.forLanguageTag("pt-BR"), "%.1f", this)

fun Int.comoMilhar(): String =
    if (this >= 1000) String.format(Locale.forLanguageTag("pt-BR"), "%.1fk", this / 1000.0) else toString()
