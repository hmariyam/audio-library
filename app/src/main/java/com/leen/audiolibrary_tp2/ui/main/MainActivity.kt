package com.leen.audiolibrary_tp2.ui.main

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import com.leen.audiolibrary_tp2.R

private const val PREFS_KEY_NOM = "nom"
private const val PREFS_KEY_LANG = "langue"
private const val PREFS_KEY_THEME = "theme"

class MainActivity : BaseActivity() {

    //pour appeler la page accueil
    private val pageAccueilLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result -> //si le résultat est ok, on peut appeler les données
        if (result.resultCode == RESULT_OK){
            val retour = result.data?.getStringExtra("resultat")
            Log.d(ContentValues.TAG, "Résultat: $retour")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)

        // Appliquer le thème sauvegardé dès le début
        val themeIsDark = prefs.getBoolean(PREFS_KEY_THEME, false)
        AppCompatDelegate.setDefaultNightMode(
            if (themeIsDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        // Appliquer la langue sauvegardée dès le début
        val lang = prefs.getString(PREFS_KEY_LANG, "fr") ?: "fr"
        setLocale(lang)

        super.onCreate(savedInstanceState)
        Log.d(ContentValues.TAG, "onCreate")
        setContentView(R.layout.activity_main)

        // Gestion du thème avec le Switch
        val themeSwitch = findViewById<Switch>(R.id.themeSwitch)

        // Récupère la préférence
        val isDarkMode = prefs.getBoolean(PREFS_KEY_THEME, false)

        // Applique l'état visuel sans déclencher d'action
        themeSwitch.setOnCheckedChangeListener(null)
        themeSwitch.isChecked = isDarkMode

        // Applique un redémarrage immédiat sur tout changement (1 seul clic)
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(PREFS_KEY_THEME, isChecked).apply()
            val newMode = if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(newMode)

            // Relance immédiate de l'application (pas juste l'activité)
            val intent = baseContext.packageManager.getLaunchIntentForPackage(baseContext.packageName)
            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finishAffinity()
            overridePendingTransition(0, 0)
        }


        // Gestion de la langue avec le Spinner (utilise string-array dans arrays.xml)
        val languageSpinner = findViewById<Spinner>(R.id.languageSpinner)
        val langCodes = resources.getStringArray(R.array.langues_disponibles).toList()
        val adapterLangue = ArrayAdapter(this, android.R.layout.simple_spinner_item, langCodes)
        adapterLangue.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapterLangue

        val savedLang = prefs.getString(PREFS_KEY_LANG, "fr")
        val currentIndex = langCodes.indexOfFirst {
            (it == "Français" && savedLang == "fr") || (it == "English" && savedLang == "en")
        }
        languageSpinner.setSelection(if (currentIndex != -1) currentIndex else 0)

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val selectedLang = when (langCodes[position]) {
                    "English" -> "en"
                    "Français" -> "fr"
                    else -> "fr"
                }
                if (selectedLang != prefs.getString(PREFS_KEY_LANG, "fr")) {
                    prefs.edit().putString(PREFS_KEY_LANG, selectedLang).apply()
                    recreate() // recharge l’activité pour appliquer la nouvelle langue
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        //Pour sauvegarder le nom inséré au lancement de l'application
        val inputNom = findViewById<EditText>(R.id.nameSpace) //récupérer l'input de l'utilisateur
        val nom = prefs.getString(PREFS_KEY_NOM, "") //récupérer le nom sauvegardé ^^
        inputNom.setText(nom) //afficher le nom

        inputNom.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(nom: Editable?) { //permet de sauvegarder le nom à chaque modification
                Log.d("MainActivity", "afterTextChanged : $nom")
                prefs.edit().putString(PREFS_KEY_NOM, nom.toString()).apply() //sauvegarder le nom
            }
        })

        //Pour passer à la page d'accueil
        val btnEnvoyer = findViewById<Button>(R.id.btnSaveProfile) //récupérer le bouton
        val afficherNom = findViewById<EditText>(R.id.nameSpace) //récupérer l'input de l'utilisateur

        //listener pour le bouton
        btnEnvoyer.setOnClickListener {
            val nom = afficherNom.text.toString()
            //Toast pour forcer l'utilisateur à entrer un nom
            if (nom.isEmpty()) {
                Toast.makeText(this, getString(R.string.toastProfile), Toast.LENGTH_SHORT).show()
            } else {
                Log.d(ContentValues.TAG, "btnEnvoyer onClick : $nom")
                val intent = Intent(this, PageAccueil::class.java) //on appelle la page d'accueil
                intent.putExtra("nom", nom) //on passe le nom à la page d'accueil
                pageAccueilLauncher.launch(intent) //on lance la page d'accueil
            }
        }
    }

    //Les fonctiones logs
    override fun onStart() { super.onStart(); Log.d(ContentValues.TAG, "onStart") }
    override fun onResume() { super.onResume(); Log.d(ContentValues.TAG, "onResume") }
    override fun onPause() { super.onPause(); Log.d(ContentValues.TAG, "onPause") }
    override fun onStop() { super.onStop(); Log.d(ContentValues.TAG, "onStop") }
    override fun onDestroy() { super.onDestroy(); Log.d(ContentValues.TAG, "onDestroy") }
}