package com.leen.audiolibrary_tp2.ui.main

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import java.util.*

open class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        val sharedPreferences = newBase.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val lang = sharedPreferences.getString("langue", "fr") ?: "fr"
        super.attachBaseContext(updateBaseContextLocale(newBase, lang))
    }

    private fun updateBaseContextLocale(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }

    // Méthode à appeler dans les activités pour changer la langue dynamiquement
    protected fun setLocale(language: String) {
        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        prefs.edit().putString("langue", language).apply()
    }

    // Appliquer le thème sauvegardé pour chaque activité
    override fun onCreate(savedInstanceState: Bundle?) {
        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        val isDark = prefs.getBoolean("theme", false)
        val mode = if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)

        super.onCreate(savedInstanceState)
    }
}