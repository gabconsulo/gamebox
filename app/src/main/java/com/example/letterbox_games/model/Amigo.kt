package com.example.letterbox_games.model

import java.util.UUID

/** Data class 2: um amigo do usuário. */
data class Amigo(
    val id: String = UUID.randomUUID().toString(),
    val nome: String,
    val avatar: String = "🎮",
    val bio: String = ""
)

/** Uma partida que um amigo registrou: quem jogou, qual jogo, que nota deu e quando. */
data class Atividade(
    val id: String = UUID.randomUUID().toString(),
    val amigoId: String,
    val jogoId: String,
    val nota: Int,
    val comentario: String = "",
    val quando: Long
)

/** Item já "montado" da tela Atividade (partidas minhas e dos amigos misturadas). */
data class ItemFeed(
    val id: String,
    val autor: String,
    val avatar: String,
    val ehVoce: Boolean,
    val amigoId: String?,
    val jogo: Jogo,
    val nota: Int,
    val comentario: String,
    val quando: Long
)
