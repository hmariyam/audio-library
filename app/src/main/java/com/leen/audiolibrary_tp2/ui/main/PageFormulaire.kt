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

class PageFormulaire : BaseActivity() {

    // Déclaration d'un singleton
    // Pertinent pour le message d'erreur
    companion object {
        lateinit var instance : PageFormulaire
        private set
    }

    // Déclarations des variables
    val chansonViewModel : ChansonViewModel by viewModels()
    val genreViewModel : GenreViewModel by viewModels()
    val artisteViewModel : ArtisteViewModel by viewModels()

    // pour appeler les pages
    private val mainActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result -> //si le résultat est ok, on peut appeler les données
        if (result.resultCode == RESULT_OK){
            val retour = result.data?.getStringExtra("resultat") //récupérer le résultat
            Log.d(ContentValues.TAG, "Résultat: $retour") //afficher le résultat dans la console
        }
    }

    private val pageAccueilLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result -> //si le résultat est ok, on peut appeler les données
        if (result.resultCode == RESULT_OK){
            val retour = result.data?.getStringExtra("resultat") //récupérer le résultat
            Log.d(ContentValues.TAG, "Résultat: $retour") //afficher le résultat dans la console
        }
    }

    private val pageLibrarieLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result -> //si le résultat est ok, on peut appeler les données
        if (result.resultCode == RESULT_OK){
            val retour = result.data?.getStringExtra("resultat") //récupérer le résultat
            Log.d(ContentValues.TAG, "Résultat: $retour") //afficher le résultat dans la console
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulaire)
        instance = this

        // Déclaration des variables
        val spinnerArtiste = findViewById<Spinner>(R.id.spinner_artiste)
        val spinnerGenre = findViewById<Spinner>(R.id.spinner_genre)
        val btnAjouter = findViewById<Button>(R.id.btn_ajouter)
        val etNouvelleChanson : EditText = findViewById(R.id.et_nom_chanson)


        //les fonctionnalités des boutons
        //Page Profile
        val btnEnvoyer1 = findViewById<Button>(R.id.btnProfile)
        btnEnvoyer1.setOnClickListener {
            Log.d(ContentValues.TAG, "btnEnvoyer onClick revenir page profile")
            val intent = Intent(this, MainActivity::class.java)
            mainActivityLauncher.launch(intent) //on lance la page profile
        }

        //Page Accueil
        val btnEnvoyer2 = findViewById<Button>(R.id.btnAccueil)
        btnEnvoyer2.setOnClickListener {
            //récupérer le nom sauvegardé
            val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
            val nom = prefs.getString("nom", "") ?: "" //récupérer le nom sauvegardé ^^ sinon un string vide
            Log.d(ContentValues.TAG, "btnEnvoyer2 onClick : nom = $nom")
            val intent = Intent(this, PageAccueil::class.java)
            intent.putExtra("nom", nom)
            pageAccueilLauncher.launch(intent) //on lance la page d'accueil
        }

        //Page Librarie
        val btnEnvoyer3 = findViewById<Button>(R.id.btnLibrairie)
        btnEnvoyer3.setOnClickListener {
            Log.d(ContentValues.TAG, "btnEnvoyer onClick revenir page librarie")
            val intent = Intent(this, PageLibrarie::class.java)
            pageLibrarieLauncher.launch(intent) //on lance la page librarie
        }

        // Écouter pour le bouton Ajouter
        btnAjouter.setOnClickListener{
            val nomChanson = etNouvelleChanson.text.toString()
            if(nomChanson.isNotEmpty()){
                // Chercher les attributs sélectionné --> Je ne sais pas si ça respecte
                // l'architecture MVVM
                val artisteSelectionner = spinnerArtiste.selectedItem as Artiste
                val genreSelectionner = spinnerGenre.selectedItem as Genre
                // Ajouter la chanson à partir de la méthode dans le ViewModel
                chansonViewModel.ajouterChanson(nomChanson, artisteSelectionner, genreSelectionner)
                etNouvelleChanson.text.clear()
                // On revient à la première position du spinner pour
                // avoir aucun artiste ou genre sélectionner
                spinnerArtiste.setSelection(0)
                spinnerGenre.setSelection(0)
            }
        }

        // Méthode pour observer les changements et les données dans le spinner des genres
        genreViewModel.genres.observe(this){ genres ->
            Log.d("PageFormulaire", "Liste des genres : $genres")
            val genresAvecOptionVide = mutableListOf(
                Genre(id = -1, nom = getString(R.string.aucun_genre_selectionne))
            )
            genresAvecOptionVide.addAll(genres)

            val genreAdapter =
                ArrayAdapter(this, android.R.layout.simple_spinner_item, genresAvecOptionVide)

            // Attribuer l'adapter des genres au spinner pour voir les informations dedans
            spinnerGenre.adapter = genreAdapter
        }

        // Méthode pour observer les changements et les données dans le spinner des artistes
        artisteViewModel.artistes.observe(this){ artistes ->
            Log.d("PageFormulaire", "Liste des genres : $artistes")
            val artistesAvecOptionVide = mutableListOf(
                Artiste(id = -1, nom = getString(R.string.aucun_artiste_selectionne))
            )
            artistesAvecOptionVide.addAll(artistes)

            val artisteAdapter =
                ArrayAdapter(this, android.R.layout.simple_spinner_item, artistesAvecOptionVide)

            // Attribuer l'adapter des artistes au spinner pour voir les informations dedans
            spinnerArtiste.adapter = artisteAdapter
        }

        // Toast qui gère les chansons ayant le même nom
        chansonViewModel.messageErreur.observe(this) {
            Toast.makeText(this, getString(R.string.message_exception), Toast.LENGTH_SHORT).show()
        }

        // Toast qui affiche le message de succès de l'ajout d'une chanson
        chansonViewModel.messageSuccess.observe(this){
            Toast.makeText(this, getString(R.string.message_success), Toast.LENGTH_SHORT).show()
        }

        // Toast qui affiche un message pour dire à l'utilisateur d'ajouter un artiste à leur chanson
        chansonViewModel.messageErreurArtiste.observe(this){
            Toast.makeText(this, getString(R.string.message_erreur_artiste), Toast.LENGTH_SHORT).show()
        }

        // Toast qui affiche un message pour dire à l'utilisateur d'ajouter un genre à leur chanson
        chansonViewModel.messageErreurGenre.observe(this){
            Toast.makeText(this, getString(R.string.message_erreur_genre), Toast.LENGTH_SHORT).show()
        }
    }
}