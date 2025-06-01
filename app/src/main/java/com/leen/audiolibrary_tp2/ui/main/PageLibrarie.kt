package com.leen.audiolibrary_tp2.ui.main

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.text.TextWatcher
import android.text.Editable
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.leen.audiolibrary_tp2.R
import com.leen.audiolibrary_tp2.data.Artiste
import com.leen.audiolibrary_tp2.data.Genre
import com.leen.audiolibrary_tp2.ui.chansons.ChansonAdapter
import com.leen.audiolibrary_tp2.viewmodel.ArtisteViewModel
import com.leen.audiolibrary_tp2.viewmodel.ChansonViewModel
import com.leen.audiolibrary_tp2.viewmodel.GenreViewModel

class PageLibrarie : BaseActivity() {
    // JASKARAN: pour le dropdown menu : https://www.youtube.com/watch?v=jXSNobmB7u4&ab_channel=FineGap

    //pour appeler les pages: profile, accueil, formulaire
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
    ) { result ->
        if (result.resultCode == RESULT_OK){
            val retour = result.data?.getStringExtra("resultat")
            Log.d(ContentValues.TAG, "Résultat: $retour")
        }
    }

    private val pageFormulaireLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK){
            val retour = result.data?.getStringExtra("resultat")
            Log.d(ContentValues.TAG, "Résultat: $retour")
        }
    }

    // Ajout des ViewModels pour observer les données depuis la BD
    private val chansonViewModel: ChansonViewModel by viewModels()
    private val artisteViewModel: ArtisteViewModel by viewModels()
    private val genreViewModel: GenreViewModel by viewModels()

    // RecyclerView pour afficher la liste des chansons
    private lateinit var recyclerView: RecyclerView

    // Variables temporaires pour stocker les données avant de les passer à l'adapter
    private var artistes: List<Artiste>? = null
    private var genres: List<Genre>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_librarie)

        // Initialisation des nouveaux dropdowns artiste et genre
        val dropdownArtiste = findViewById<AutoCompleteTextView>(R.id.dropdownArtiste)
        val dropdownGenre = findViewById<AutoCompleteTextView>(R.id.dropdownGenre)

        // Champ de recherche texte
        val rechercheInput = findViewById<EditText>(R.id.rechercheInput)

        // Initialiser le RecyclerView
        recyclerView = findViewById(R.id.recyclerViewListe)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Fonction pour appliquer le filtrage combiné (appelée par les 3 champs)
        fun appliquerFiltrageCombiné() {
            val texte = rechercheInput.text.toString()
            val artiste = dropdownArtiste.text.toString()
            val genre = dropdownGenre.text.toString()
            chansonViewModel.rechercherParCriteres(texte, artiste, genre)
        }

        // Observer les artistes
        artisteViewModel.artistes.observe(this) { listeArtistes ->
            artistes = listeArtistes
            val nomsArtistes = listeArtistes.map { it.nom }
            val adapterArtiste = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, nomsArtistes)
            dropdownArtiste.setAdapter(adapterArtiste)
        }

        // Observer les genres
        genreViewModel.genres.observe(this) { listeGenres ->
            genres = listeGenres
            val nomsGenres = listeGenres.map { it.nom }
            val adapterGenre = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, nomsGenres)
            dropdownGenre.setAdapter(adapterGenre)
        }

        // Observer les chansons principales pour afficher tout au départ si champ vide
        chansonViewModel.chansons.observe(this) { chansons ->
            val texteRecherche = rechercheInput.text.toString()
            if (texteRecherche.isBlank()) {
                chansonViewModel.rechercherParNom("")
            }
        }

        // Recherche seulement quand on appuie sur "Enter"
        rechercheInput.setOnEditorActionListener { _, actionId, event ->
            // Si l'action est completé et on a cliqué sur "Enter"
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                appliquerFiltrageCombiné()
                true // Traiter l'événement (afficher les résultats)
            } else {
                false // Ne pas traiter l'événement (ne changer rien)
            }
        }

        // Recherche par nom dyanmique fait par Jaskaran : cause des erreurs avec le toast
//        rechercheInput.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//                appliquerFiltrageCombiné()
//            }
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//        })

        // Appels sur les dropdowns aussi
        dropdownArtiste.setOnItemClickListener { _, _, _, _ ->
            appliquerFiltrageCombiné()
        }
        dropdownGenre.setOnItemClickListener { _, _, _, _ ->
            appliquerFiltrageCombiné()
        }

        // Fonctionnalité du bouton Clear : réinitialise tous les champs et recharge les chansons
        val btnClear = findViewById<Button>(R.id.btnClear)
        btnClear.setOnClickListener {
            rechercheInput.setText("")
            dropdownArtiste.setText("", false)
            dropdownGenre.setText("", false)
            appliquerFiltrageCombiné()
        }

        // Observer les résultats filtrés et mettre à jour le RecyclerView
        chansonViewModel.chansonsFiltrees.observe(this) { chansonsFiltrees ->
            recyclerView.adapter = ChansonAdapter(
                chansonsFiltrees,
                chansonViewModel,
                artistes ?: emptyList(),
                genres ?: emptyList()
            )
        }

        // Observer les messages d'erreur pour afficher un toast
        chansonViewModel.messageErreur.observe(this) {
            Toast.makeText(this, getString(R.string.message_aucune_chanson), Toast.LENGTH_SHORT).show()
        }


        //les fonctionnalités des boutons
        val btnEnvoyer1 = findViewById<Button>(R.id.btnProfile)
        btnEnvoyer1.setOnClickListener {
            Log.d(ContentValues.TAG, "btnEnvoyer onClick revenir page profile")
            val intent = Intent(this, MainActivity::class.java) //on appelle la page profile
            mainActivityLauncher.launch(intent)
        }

        val btnEnvoyer2 = findViewById<Button>(R.id.btnAccueil)
        btnEnvoyer2.setOnClickListener {
            val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
            val nom = prefs.getString("nom", "") ?: "" //récupérer le nom sauvegardé sinon un string vide
            Log.d(ContentValues.TAG, "btnEnvoyer2 onClick : nom = $nom") //afficher le nom dans la console
            val intent = Intent(this, PageAccueil::class.java) //on appelle la page accueil
            intent.putExtra("nom", nom)
            pageAccueilLauncher.launch(intent)
        }

        val btnEnvoyer3 = findViewById<Button>(R.id.btnFormulaire)
        btnEnvoyer3.setOnClickListener {
            Log.d(ContentValues.TAG, "btnEnvoyer onClick revenir page formulaire")
            val intent = Intent(this, PageFormulaire::class.java) //on appelle la page formulaire
            pageFormulaireLauncher.launch(intent)
        }
    }
}