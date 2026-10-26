package com.example.letterbox_games.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface JogoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(jogo: JogoEntity): Long

    @Query("SELECT * FROM jogos ORDER BY id")
    suspend fun buscarTodos(): List<JogoEntity>

    @Update
    suspend fun atualizar(jogo: JogoEntity)

    @Delete
    suspend fun deletar(jogo: JogoEntity)

    @Query("SELECT * FROM jogos WHERE chave = :chave LIMIT 1")
    suspend fun buscarPorId(chave: String): JogoEntity?
}

@Dao
interface AmigoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(amigo: AmigoEntity): Long

    @Query("SELECT * FROM amigos ORDER BY id DESC")
    suspend fun buscarTodos(): List<AmigoEntity>

    @Update
    suspend fun atualizar(amigo: AmigoEntity)

    @Delete
    suspend fun deletar(amigo: AmigoEntity)

    @Query("SELECT * FROM amigos WHERE chave = :chave LIMIT 1")
    suspend fun buscarPorId(chave: String): AmigoEntity?
}
