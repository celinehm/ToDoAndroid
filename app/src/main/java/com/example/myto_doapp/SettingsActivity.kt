package com.example.myto_doapp

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var switchSortByDate: Switch
    private lateinit var switchSortByPriority: Switch
    private lateinit var btnCategories: Button
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        switchSortByDate = findViewById(R.id.switchSortByDate)
        switchSortByPriority = findViewById(R.id.switchSortByPriority)
        btnCategories = findViewById(R.id.btnCategories)
        btnBack = findViewById(R.id.btnBack)

        val switchNightMode = findViewById<Switch>(R.id.switchNightMode)

        // Initialiser le Switch avec l'état actuel
        switchNightMode.isChecked = DarkModeManager.isNightModeEnabled(this)

        // Mettre à jour le mode uniquement quand l'utilisateur change le switch
        switchNightMode.setOnCheckedChangeListener { _, isChecked ->
            DarkModeManager.setNightMode(this, isChecked)
        }


        val prefs = getSharedPreferences("mytodo_prefs", Context.MODE_PRIVATE)

        // Initialiser les Switch
        switchSortByDate.isChecked = prefs.getBoolean("sort_by_date", false)
        switchSortByPriority.isChecked = prefs.getBoolean("sort_by_priority", false)

        switchSortByDate.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("sort_by_date", isChecked).apply()
        }

        switchSortByPriority.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("sort_by_priority", isChecked).apply()
        }

        btnCategories.setOnClickListener {
            // Logique pour afficher les catégories
        }

        btnBack.setOnClickListener {
            finish() // revient à MainActivity
        }
    }
}
