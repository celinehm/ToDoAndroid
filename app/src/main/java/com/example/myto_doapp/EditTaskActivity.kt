package com.example.myto_doapp

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditTaskActivity : AppCompatActivity() {

    private lateinit var editTitle: EditText
    private lateinit var editDescription: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var btnSave: Button
    private lateinit var btnBack: Button

    private var taskId: Int = -1
    private var task: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)

        // Views
        editTitle = findViewById(R.id.editTaskTitle)
        editDescription = findViewById(R.id.editTaskDescription)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        btnSave = findViewById(R.id.btnSaveTaskChanges)
        btnBack = findViewById(R.id.btnBackEdit)

        btnBack.setOnClickListener { finish() }

        // Récupérer l’ID de la tâche
        taskId = intent.getIntExtra("task_id", -1)
        task = TaskRepository.tasks.find { it.id == taskId }

        // Pré-remplir les champs
        task?.let {
            editTitle.setText(it.title)
            editDescription.setText(it.description)
        }

        //  CHARGER LES CATÉGORIES (MÊME LOGIQUE QUE AddTaskActivity)
        val prefs = getSharedPreferences("categories", Context.MODE_PRIVATE)
        val savedCategories =
            prefs.getStringSet("categories_set", emptySet())?.toMutableList()
                ?: mutableListOf()

        if (savedCategories.isEmpty()) {
            savedCategories.add("General")
        }

        val adapterCategories =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, savedCategories)
        adapterCategories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapterCategories

        // Sélectionner la catégorie actuelle
        task?.let {
            val index = savedCategories.indexOf(it.category)
            if (index >= 0) spinnerCategory.setSelection(index)
        }

        // ENREGISTRER LES MODIFICATIONS
        btnSave.setOnClickListener {

            val newTitle = editTitle.text.toString().trim()
            val newDescription = editDescription.text.toString().trim()
            val newCategory = spinnerCategory.selectedItem.toString()

            if (newTitle.isEmpty()) {
                Toast.makeText(this, "Le titre ne peut pas être vide", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Modifier la tâche en mémoire
            task?.let {
                it.title = newTitle
                it.description = newDescription
                it.category = newCategory
            }

            // Sauvegarder TOUTES les tâches dans SharedPreferences
            saveTasks()

            // Retour vers MainActivity
            val resultIntent = intent
            resultIntent.putExtra("task_id", taskId)
            resultIntent.putExtra("task_title", newTitle)
            resultIntent.putExtra("task_description", newDescription)
            resultIntent.putExtra("task_category", newCategory)
            setResult(Activity.RESULT_OK, resultIntent)

            Toast.makeText(this, "Tâche modifiée", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    // SAUVEGARDE DES TÂCHES
    private fun saveTasks() {
        val prefs = getSharedPreferences("tasks_prefs", Context.MODE_PRIVATE)

        val taskStrings = TaskRepository.tasks.map {
            "${it.id}||${it.title}||${it.description}||${it.category}||${it.priority}||${it.isDone}||${it.dateCreation}"
        }

        prefs.edit().putStringSet("tasks_set", taskStrings.toSet()).apply()
    }
}
