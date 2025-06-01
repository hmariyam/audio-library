package com.leen.audiolibrary_tp2.viewmodel

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.leen.audiolibrary_tp2.ui.main.PageFormulaire
import com.leen.audiolibrary_tp2.R
import com.leen.audiolibrary_tp2.data.AppDatabase
import com.leen.audiolibrary_tp2.data.Artiste
import com.leen.audiolibrary_tp2.data.Chanson
import com.leen.audiolibrary_tp2.data.ChansonAvecArtisteGenre
import com.leen.audiolibrary_tp2.data.Genre
import com.leen.audiolibrary_tp2.ui.main.PageModification
import kotlinx.coroutines.launch

class ChansonViewModel(application: Application) : AndroidViewModel(application) {

    // Déclaration des attributs
    private val appContext = getApplication<Application>()
    private val chansonDAO = AppDatabase.getDatabase(appContext).chansonDAO()

    // Chercher tous les chansons à partir de la base de données
    val chansons: LiveData<List<ChansonAvecArtisteGenre>> = chansonDAO.getAll()

    // LiveData pour les chansons filtrées (recherche par nom ou filtres combinés)
    private val _chansonsFiltrees: MutableLiveData<List<ChansonAvecArtisteGenre>> = MutableLiveData()
    val chansonsFiltrees: LiveData<List<ChansonAvecArtisteGenre>> = _chansonsFiltrees

    // Fonction pour filtrer les chansons par nom (appelée depuis la UI)
    fun rechercherParNom(texte: String) {
        val listeOriginale = chansons.value ?: return
        val resultatFiltre = listeOriginale.filter {
            it.chanson.nom.contains(texte, ignoreCase = true)
        }
        _chansonsFiltrees.value = resultatFiltre

        // Afficher un message d'erreur si aucun résultat trouvé
        if (resultatFiltre.isEmpty()) {
            _messageErreur.value = appContext.getString(R.string.message_aucune_chanson)
        }
    }

    // Recherche combinée par nom, artiste et genre
    fun rechercherParCriteres(nom: String?, artisteNom: String?, genreNom: String?) {
        val listeOriginale = chansons.value ?: return

        val resultatFiltre = listeOriginale.filter {
            val matchNom = nom.isNullOrBlank() || it.chanson.nom.contains(nom, ignoreCase = true)
            val matchArtiste = artisteNom.isNullOrBlank() || it.artiste.nom.equals(artisteNom, ignoreCase = true)
            val matchGenre = genreNom.isNullOrBlank() || it.genre.nom.equals(genreNom, ignoreCase = true)
            matchNom && matchArtiste && matchGenre
        }

        _chansonsFiltrees.value = resultatFiltre

        if (resultatFiltre.isEmpty()) {
            _messageErreur.value = appContext.getString(R.string.message_aucune_chanson)
        }
    }

    // Message toast pour gérer l'exception du nom qui doit être unique
    private val _messageErreur : MutableLiveData<String> = MutableLiveData<String>()
    val messageErreur = _messageErreur

    // Message toast pour indiquer à l'utilisateur que l'ajout a été fait avec succès
    private val _messageSuccess: MutableLiveData<String> = MutableLiveData<String>()
    val messageSuccess = _messageSuccess

    // Message toast pour indiquer à l'utilisateur d'ajouter un artiste
    private val _messageErreurArtiste : MutableLiveData<String> = MutableLiveData<String>()
    val messageErreurArtiste = _messageErreurArtiste

    // Message toast pour indiquer à l'utilisateur d'ajouter un genre
    private val _messageErreurGenre : MutableLiveData<String> = MutableLiveData<String>()
    val messageErreurGenre = _messageErreurGenre

    // Message toast pour indiquer à l'utilisateur que la modification a été un succès
    private val _messageSuccessModification : MutableLiveData<String> = MutableLiveData<String>()
    val messageSuccessModification = _messageSuccessModification

    // Chanson sélectionné avec le id
    // Variable pour qu'il puisse tenir les changements nécéssaire
    private val _chansonSelectionner : MutableLiveData<Chanson> = MutableLiveData<Chanson>()

    // Méthode d'ajout d'une chanson
    fun ajouterChanson(nom: String, artiste : Artiste, genre : Genre) = viewModelScope.launch {
        if (artiste.id == -1) {
            _messageErreurArtiste.value = PageFormulaire.instance.getString(R.string.message_erreur_artiste)
        } else if (genre.id == -1) {
            _messageErreurGenre.value = PageFormulaire.instance.getString(R.string.message_erreur_genre)
        } else {
            try {
                chansonDAO.insert(Chanson(nom = nom, artisteId = artiste.id, genreId = genre.id))
                _messageSuccess.value = PageFormulaire.instance.getString(R.string.message_success)
            } catch (e: SQLiteConstraintException) {
                _messageErreur.value = PageFormulaire.instance.getString(R.string.message_exception)
            }
        }
    }

    // Méthode qui sélectionne le id de la chanson
    fun chercherChansonParId(id : Int) = viewModelScope.launch {
        val chanson = chansonDAO.getChansonById(id)
        _chansonSelectionner.postValue(chanson)
    }

    // Méthode pour la modification d'une chanson
    fun modifierChanson(nouveauNom : String, nouveauArtiste : Artiste, nouveauGenre : Genre) = viewModelScope.launch {
        try {
            val chanson = _chansonSelectionner.value
            val chansonModifier = chanson?.copy(nom = nouveauNom, artisteId = nouveauArtiste.id, genreId = nouveauGenre.id)
            if(chansonModifier != null){
                chansonDAO.update(chansonModifier)
                _chansonSelectionner.value = chansonModifier
                _messageSuccessModification.value = PageModification.instance.getString(R.string.message_success_modification)
            }
        } catch (e: SQLiteConstraintException) {
            _messageErreur.value = PageModification.instance.getString(R.string.message_exception)
        }
    }

    // Methode pour supprimer un chanson
    fun supprimerArticle(chanson: Chanson) = viewModelScope.launch {
        chansonDAO.delete(chanson)
    }
}