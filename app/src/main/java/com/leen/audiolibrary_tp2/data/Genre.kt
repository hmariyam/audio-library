package com.leen.audiolibrary_tp2.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// Tableau des genres
@Entity(indices = [Index(value = ["nom"], unique = true)])
data class Genre(

    // Déclaration des attributs
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nom : String
){
    // Affichage du nom du genre
    override fun toString(): String {
        return nom
    }
}