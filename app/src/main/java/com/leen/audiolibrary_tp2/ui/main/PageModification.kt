package com.leen.audiolibrary_tp2.ui.main

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.leen.audiolibrary_tp2.R
import com.leen.audiolibrary_tp2.data.Artiste
import com.leen.audiolibrary_tp2.data.Genre
import com.leen.audiolibrary_tp2.viewmodel.ArtisteViewModel
import com.leen.audiolibrary_tp2.viewmodel.ChansonViewModel
import com.leen.audiolibrary_tp2.viewmodel.GenreViewModel

class PageModification : BaseActivity() {

    // Companion object pour mettre les attributs dans leurs éléments u.i respectives
    companion object {
        lateinit var instance : PageModification
            private set
    }

    // Déclarations des variables
    val chansonViewModel : ChansonViewModel by viewModels()
    val genreViewModel : GenreViewModel by viewModels()
    val artisteViewModel : ArtisteViewModel by viewModels()

    // pour appeler les pages: profile, accueil, librarie
    private val mainActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val retour = result.data?.getStringExtra("resultat")
            Log.d(ContentValues.TAG, "Résultat: $retour")
        }
    }

    private val pageAccueilLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val retour = result.data?.getStringExtra("resultat")
            Log.d(ContentValues.TAG, "Résultat: $retour")
        }
    }

    private val pageLibrarieLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val retour = result.data?.getStringExtra("resultat")
            Log.d(ContentValues.TAG, "Résultat: $retour")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modification)
        instance = this

        // Éléments ui pour le formulaire de modification
        val etNomChanson = findViewById<EditText>(R.id.et_nom_chanson_modification)
        val spinnerArtiste = findViewById<Spinner>(R.id.spinner_artiste)
        val spinnerGenre = findViewById<Spinner>(R.id.spinner_genre)

        // Chercher le bouton de modification avec son id
        val btnModifier = findViewById<Button>(R.id.btn_modifier)

        // Mettre le nom de la chanson dans l'éditeur du nom
        // Réception de la donnée du adapter
        etNomChanson.setText(intent.getStringExtra("nomChanson"))

        // Recevoir l'id de la chanson avec intent
        val chansonId = intent.getIntExtra("chansonId", 0)

        // Si le id de la chanson n'est pas égale à zéro, on cherche l'id véritable de la chanson
        // Important sinon ca va toujours être zéro
        if(chansonId != 0){
            chansonViewModel.chercherChansonParId(chansonId)
        }


        // Les fonctionnalités des boutons
        //Page Profile
        val btnEnvoyer1 = findViewById<Button>(R.id.btnProfile)
        btnEnvoyer1.setOnClickListener {
            Log.d(ContentValues.TAG, "btnEnvoyer onClick revenir page profile")
            val intent = Intent(this, MainActivity::class.java)
            mainActivityLauncher.launch(intent)
        }

        //Page Accueil
        val btnEnvoyer2 = findViewById<Button>(R.id.btnAccueil)
        btnEnvoyer2.setOnClickListener {
            val prefs = getSharedPreferences("prefs", MODE_PRIVATE) //récupérer le nom sauvegardé
            val nom = prefs.getString("nom", "") ?: "" //récupérer le nom sauvegardé sinon un string vide
            Log.d(ContentValues.TAG, "btnEnvoyer2 onClick : nom = $nom")
            val intent = Intent(this, PageAccueil::class.java)
            intent.putExtra("nom", nom)
            pageAccueilLauncher.launch(intent)
        }

        // Page Librarie
        val btnEnvoyer3 = findViewById<Button>(R.id.btnLibrairie)
        btnEnvoyer3.setOnClickListener {
            Log.d(ContentValues.TAG, "btnEnvoyer onClick revenir page librarie")
            val intent = Intent(this, PageLibrarie::class.java)
            pageLibrarieLauncher.launch(intent)
        }

        // Méthode pour observer les changements et les données dans le spinner des genres
        genreViewModel.genres.observe(this){ genres ->
            Log.d("PageFormulaire", "Liste des genres : $genres")
            val genresAvecOptionVide = mutableListOf(
                Genre(id = -1, nom = getString(R.string.aucun_genre_selectionne))
            )
            genresAvecOptionVide.addAll(genres)

            val genreAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genresAvecOptionVide)

            // Attribuer l'adapter des genres au spinner pour voir les informations dedans
            spinnerGenre.adapter = genreAdapter

            // Mettre le genre de la chanson dans son emplacement respective, le spinner
            // Réception de la donnée du adapter
            spinnerGenre.setSelection(intent.getIntExtra("genreChanson", 0))
        }

        // Méthode pour observer les changements et les données dans le spinner des artistes
        artisteViewModel.artistes.observe(this){ artistes ->
            Log.d("PageFormulaire", "Liste des genres : $artistes")
            val artistesAvecOptionVide = mutableListOf(
                Artiste(id = -1, nom = getString(R.string.aucun_artiste_selectionne))
            )
            artistesAvecOptionVide.addAll(artistes)

            val artisteAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, artistesAvecOptionVide)

            // Attribuer l'adapter des artistes au spinner pour voir les informations dedans
            spinnerArtiste.adapter = artisteAdapter

            // Mettre l'artiste de la chanson dans son emplacement respective, le spinner
            // Réception de la donnée du adapter
            spinnerArtiste.setSelection(intent.getIntExtra("artisteChanson", 0))
        }

        // Méthode pour effectuer la modification (même façon que l'ajout, mais la méthode du view model change)
        btnModifier.setOnClickListener{
            // Chercher le text entrez dans le input
            val nomChanson = etNomChanson.text.toString()
            // Chercher les attributs sélectionné --> Je ne sais pas si ça respecte
            // l'architecture MVVM
            val artisteSelectionner = spinnerArtiste.selectedItem as Artiste
            val genreSelectionner = spinnerGenre.selectedItem as Genre
            // Modifier la chanson à partir de la méthode dans le ViewModel
            chansonViewModel.modifierChanson(nomChanson, artisteSelectionner, genreSelectionner)
        }

        // Toast qui affiche un message de succès pour la modification
        chansonViewModel.messageSuccessModification.observe(this) {
            Toast.makeText(this, getString(R.string.message_success_modification), Toast.LENGTH_SHORT).show()
        }

        // Toast qui gère les chansons ayant le même nom
        chansonViewModel.messageErreur.observe(this) {
            Toast.makeText(this, getString(R.string.message_exception), Toast.LENGTH_SHORT).show()
        }
    }
}