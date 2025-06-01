package com.leen.audiolibrary_tp2.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// Tableau des chansons
@Entity(indices = [Index(value = ["nom"], unique = true)])
data class Chanson(

    // Déclaration des attributs
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nom : String,
    val artisteId : Int,
    val genreId: Int,
)