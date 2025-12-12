package com.example.myto_doapp

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class CategoriesActivity : AppCompatActivity() {

    private lateinit var editCategory: EditText
    private lateinit var btnAdd: Button
    private lateinit var listView: ListView
    private lateinit var btnBack: Button
    private val categories = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.categories_activity)

        // ---- BOUTON RETOUR ----
        btnBack = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        // Liaison des vues
        editCategory = findViewById(R.id.editCategory)
        btnAdd = findViewById(R.id.btnAddCategory)
        listView = findViewById(R.id.listCategories)

        // Chargement des catégories sauvegardées
        val prefs = getSharedPreferences("categories", Context.MODE_PRIVATE)
        categories.addAll(prefs.getStringSet("categories_set", emptySet()) ?: emptySet())

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, categories)
        listView.adapter = adapter

        // Ajout d'une nouvelle catégorie
        btnAdd.setOnClickListener {
            val newCategory = editCategory.text.toString().trim()
            if (newCategory.isNotEmpty() && !categories.contains(newCategory)) {
                categories.add(newCategory)
                adapter.notifyDataSetChanged()
                editCategory.text.clear()
                prefs.edit().putStringSet("categories_set", categories.toSet()).apply()
            } else {
                Toast.makeText(this, "Catégorie vide ou déjà existante", Toast.LENGTH_SHORT).show()
            }
        }

        // ---- CLIC SIMPLE : CONFIRMATION DE SUPPRESSION ----
        listView.setOnItemClickListener { _, _, position, _ ->
            val cat = categories[position]
            AlertDialog.Builder(this)
                .setTitle("Supprimer la catégorie ?")
                .setMessage("Voulez-vous supprimer la catégorie \"$cat\" ?")
                .setPositiveButton("Supprimer") { _, _ ->
                    categories.removeAt(position)
                    adapter.notifyDataSetChanged()
                    prefs.edit().putStringSet("categories_set", categories.toSet()).apply()
                    Toast.makeText(this, "Catégorie '$cat' supprimée", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Annuler", null)
                .show()
        }

        // ---- CLIC LONG : suppression directe (optionnel) ----
        listView.setOnItemLongClickListener { _, _, position, _ ->
            val removed = categories.removeAt(position)
            adapter.notifyDataSetChanged()
            prefs.edit().putStringSet("categories_set", categories.toSet()).apply()
            Toast.makeText(this, "Catégorie '$removed' supprimée", Toast.LENGTH_SHORT).show()
            true
        }
    }
}
