package com.example.myto_doapp

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object DarkModeManager {

    private const val PREFS_NAME = "mytodo_prefs"
    private const val NIGHT_MODE_KEY = "night_mode"

    // Activer ou désactiver le dark mode
    fun setNightMode(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(NIGHT_MODE_KEY, enabled).apply()

        if (enabled) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    // Vérifier si le dark mode est activé
    fun isNightModeEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(NIGHT_MODE_KEY, false)
    }

    // Appliquer le mode au démarrage d'une Activity
    fun applyNightModeIfEnabled(context: Context) {
        if (isNightModeEnabled(context)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
