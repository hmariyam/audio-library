package com.leen.audiolibrary_tp2.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Version 5 pour être capable de faire des changements à la base de données
@Database(entities = [Chanson::class, Artiste::class, Genre::class], version = 5)
abstract class AppDatabase : RoomDatabase() {

    abstract fun chansonDAO(): ChansonDAO

    abstract fun artisteDAO(): ArtisteDAO

    abstract fun genreDAO(): GenreDAO

    // Singleton
    companion object{
        // Peut importe le fil d'execution on accède à notre donnée, on va en avoir juste une
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Boilerplate code pris du cours
        fun getDatabase(context: Context): AppDatabase{
            return INSTANCE ?: synchronized(this){
                Room.databaseBuilder(
                    context.applicationContext,
                    // Déclaration du nom de notre base de données
                    AppDatabase::class.java, "audioLibrary_db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build().also { INSTANCE = it }
            }
        }
    }
}