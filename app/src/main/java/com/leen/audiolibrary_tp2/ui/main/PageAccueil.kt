package com.leen.audiolibrary_tp2.ui.main

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.leen.audiolibrary_tp2.R

class PageAccueil : BaseActivity() {

    //pour appeler la page formulaire et librarie
    private val pageFormulaireLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result -> //si le résultat est ok, on peut appeler les données
        if (result.resultCode == RESULT_OK){
            val retour = result.data?.getStringExtra("resultat")
            Log.d(ContentValues.TAG, "Résultat: $retour")
        }
    }
    private val pageLibrarieLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result -> //si le résultat est ok, on peut appeler les données
        if (result.resultCode == RESULT_OK){
            val retour = result.data?.getStringExtra("resultat")
            Log.d(ContentValues.TAG, "Résultat: $retour")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accueil) //on appele le layout activity_accueil.xml

        // pour appeler le nom de l'utilisateur
        val nom = intent.getStringExtra("nom") //appeler le variable qui contient le nom
        Log.d(ContentValues.TAG, "onCreate : $nom") //afficher le nom dans la console
        val tvMessage = findViewById<TextView>(R.id.tvWelcome) //récupérer le textview
        tvMessage.text = getString(R.string.bonjour, nom) //afficher le nom dans le textview ^^


        //Pour passer à la page formulaire
        val btnEnvoyer = findViewById<Button>(R.id.btnAjouterChanson)
        //listener pour les boutons
        btnEnvoyer.setOnClickListener {
            Log.d(ContentValues.TAG, "btnEnvoyer onClick Ajouter Chanson")
            val intent = Intent(this, PageFormulaire::class.java) //on appelle la page formulaire
            pageFormulaireLauncher.launch(intent) //on lance la page formulaire
        }

        //Page Librarie
        val btnEnvoyer3 = findViewById<Button>(R.id.btnViewLibrary)
        btnEnvoyer3.setOnClickListener {
            Log.d(ContentValues.TAG, "btnEnvoyer onClick page librarie")
            val intent = Intent(this, PageLibrarie::class.java) //on appelle la page librarie
            pageLibrarieLauncher.launch(intent) //on lance la page librarie
        }
    }


    //Les fonctiones logs
    override fun onStart() { super.onStart(); Log.d(ContentValues.TAG, "onStart") }
    override fun onResume() { super.onResume(); Log.d(ContentValues.TAG, "onResume") }
    override fun onPause() { super.onPause(); Log.d(ContentValues.TAG, "onPause") }
    override fun onStop() { super.onStop(); Log.d(ContentValues.TAG, "onStop") }
    override fun onDestroy() { super.onDestroy(); Log.d(ContentValues.TAG, "onDestroy") }
}