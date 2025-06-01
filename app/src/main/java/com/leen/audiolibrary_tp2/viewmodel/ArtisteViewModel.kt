package com.leen.audiolibrary_tp2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.leen.audiolibrary_tp2.data.AppDatabase
import com.leen.audiolibrary_tp2.data.Artiste
import kotlinx.coroutines.launch

class ArtisteViewModel(application: Application) : AndroidViewModel(application) {

    // Cherche la base de données
    private val db = AppDatabase.getDatabase(application)

    // Cherche le DAO de AppDataBase
    private val artisteDAO = db.artisteDAO()

    // Variables pour pouvoir chercher nos catégories
    // pour pouvoir les observers
    val artistes: LiveData<List<Artiste>> = artisteDAO.getAll()

    // Insertion de données pour les genres des chansons
    init {
        // Fil d'execution secondaire
        viewModelScope.launch {
            // Insérer des genres dans la table Genre
            db.artisteDAO().insertAll(
                Artiste(nom = "Taylor Swift"),
                Artiste(nom = "Rihanna"),
                Artiste(nom = "Indila"),
                Artiste(nom = "Céline Dion"),
                Artiste(nom = "BTS"),
                Artiste(nom = "Weezer"),
                Artiste(nom = "Ed Sheeran")
            )
        }
    }
}