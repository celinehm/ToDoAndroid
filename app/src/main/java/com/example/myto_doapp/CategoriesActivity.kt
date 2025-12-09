package com.example.myto_doapp

import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class CategoriesActivity : AppCompatActivity() {

    private lateinit var editCategory: EditText
    private lateinit var btnAdd: Button
    private lateinit var listView: ListView
    private val categories = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.categories_activity)

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

        // Suppression d'une catégorie par clic long
        listView.setOnItemLongClickListener { _, _, position, _ ->
            val removed = categories.removeAt(position)
            adapter.notifyDataSetChanged()
            prefs.edit().putStringSet("categories_set", categories.toSet()).apply()
            Toast.makeText(this, "Catégorie '$removed' supprimée", Toast.LENGTH_SHORT).show()
            true
        }
    }
}
