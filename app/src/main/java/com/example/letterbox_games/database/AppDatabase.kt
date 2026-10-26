package com.example.letterbox_games.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [JogoEntity::class, AmigoEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun jogoDao(): JogoDao
    abstract fun amigoDao(): AmigoDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun obter(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "letterbox_games.db"
                ).build().also { instancia = it }
            }
    }
}
