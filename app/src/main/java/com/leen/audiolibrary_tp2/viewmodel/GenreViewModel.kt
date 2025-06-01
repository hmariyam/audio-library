package com.leen.audiolibrary_tp2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.leen.audiolibrary_tp2.data.AppDatabase
import com.leen.audiolibrary_tp2.data.Genre
import kotlinx.coroutines.launch

class GenreViewModel(application: Application) : AndroidViewModel(application) {

    // Cherche la base de données
    private val db = AppDatabase.getDatabase(application)

    // Cherche le DAO de AppDataBase
    private val genreDAO = db.genreDAO()

    // Variables pour pouvoir chercher nos catégories
    // pour pouvoir les observers
    val genres: LiveData<List<Genre>> = genreDAO.getAll()

    // Insertion de données pour les genres des chansons
    init {
        // Fil d'execution secondaire
        viewModelScope.launch {
            // Insérer des genres dans la table Genre
            db.genreDAO().insertAll(
                Genre(nom = "Pop"),
                Genre(nom = "R&B"),
                Genre(nom = "Hip-hop"),
                Genre(nom = "Métal"),
                Genre(nom = "Jazz"),
                Genre(nom = "Disco"),
                Genre(nom = "K-pop")
            )
        }
    }
}