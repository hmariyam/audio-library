package com.leen.audiolibrary_tp2.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GenreDAO {

    // Vararg, le nombre d'arguments et variables
    // Donc, on peut lui passer plusieurs catégories une à la suite de l'autre
    // onConflict signifie que si on a un conflict, on ajoute une stratégie pour éviter que l'application crash
    // Ici, on lui dit d'ignorer le conflit
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    // peut pas être executer dans un fil d'execution primaire, donc
    // dans le view model, on le met dans un fil d'execution secondaire
    suspend fun insertAll(vararg genres: Genre)

    @Query("SELECT * FROM Genre")
    // live data variable so no need to put suspend
    fun getAll(): LiveData<List<Genre>>
}