package com.leen.audiolibrary_tp2.data

import androidx.room.Embedded
import androidx.room.Relation

data class ChansonAvecArtisteGenre(

    // Intégration de la chanson
    @Embedded val chanson: Chanson,

    // Effectuer la relation entre la colonne artisteId, genreId,
    // et le id des deux tableaux (Artiste et Genre)
    @Relation(
        parentColumn = "artisteId", // Champs dans Chanson
        entityColumn = "id", // Champs dans Artiste
    )
    val artiste: Artiste,

    @Relation(
        parentColumn = "genreId", // Champs dans Chanson
        entityColumn = "id", // Champs dans Genre
    )
    val genre: Genre
)