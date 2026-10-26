package com.example.letterbox_games.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.letterbox_games.model.Amigo
import com.example.letterbox_games.model.Jogo
import com.example.letterbox_games.model.Tendencia

@Entity(tableName = "jogos", indices = [Index(value = ["chave"], unique = true)])
data class JogoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val chave: String,
    val nome: String,
    val plataforma: String,
    val genero: String,
    val ano: Int,
    val descricao: String,
    val notaComunidade: Double,
    val avaliacoes: Int,
    val tendencia: String?,
    val variacaoJogadores: Int,
    val personalizado: Boolean,
    val minhaNota: Int,
    val horas: Int,
    val resenha: String,
    val jogadoEm: Long?
)

@Entity(tableName = "amigos", indices = [Index(value = ["chave"], unique = true)])
data class AmigoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val chave: String,
    val nome: String,
    val avatar: String,
    val bio: String
)

fun Jogo.toEntity(idBanco: Int = 0) = JogoEntity(
    id = idBanco,
    chave = id,
    nome = nome,
    plataforma = plataforma,
    genero = genero,
    ano = ano,
    descricao = descricao,
    notaComunidade = notaComunidade,
    avaliacoes = avaliacoes,
    tendencia = tendencia?.name,
    variacaoJogadores = variacaoJogadores,
    personalizado = personalizado,
    minhaNota = minhaNota,
    horas = horas,
    resenha = resenha,
    jogadoEm = jogadoEm
)

fun JogoEntity.toModelo() = Jogo(
    id = chave,
    nome = nome,
    plataforma = plataforma,
    genero = genero,
    ano = ano,
    descricao = descricao,
    notaComunidade = notaComunidade,
    avaliacoes = avaliacoes,
    tendencia = tendencia?.let(Tendencia::valueOf),
    variacaoJogadores = variacaoJogadores,
    personalizado = personalizado,
    minhaNota = minhaNota,
    horas = horas,
    resenha = resenha,
    jogadoEm = jogadoEm
)

fun Amigo.toEntity(idBanco: Int = 0) = AmigoEntity(
    id = idBanco,
    chave = id,
    nome = nome,
    avatar = avatar,
    bio = bio
)

fun AmigoEntity.toModelo() = Amigo(
    id = chave,
    nome = nome,
    avatar = avatar,
    bio = bio
)
