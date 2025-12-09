package com.example.myto_doapp

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditTaskActivity : AppCompatActivity() {

    private lateinit var editTitle: EditText
    private lateinit var editDescription: EditText
    private lateinit var editCategory: EditText
    private lateinit var btnSave: Button
    private lateinit var btnBack: Button  // <- bouton retour

    private var taskId: Int = -1
    private var task: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)

        editTitle = findViewById(R.id.editTaskTitle)
        editDescription = findViewById(R.id.editTaskDescription)
        editCategory = findViewById(R.id.editTaskCategory)
        btnSave = findViewById(R.id.btnSaveTaskChanges)
        btnBack = findViewById(R.id.btnBackEdit) // <- récupérer le bouton retour

        // Action pour le bouton retour
        btnBack.setOnClickListener {
            finish() // Termine l'activité et retourne à MainActivity
        }

        taskId = intent.getIntExtra("task_id", -1)
        task = TaskRepository.tasks.find { it.id == taskId }

        task?.let {
            editTitle.setText(it.title)
            editDescription.setText(it.description)
            editCategory.setText(it.category)
        }

        btnSave.setOnClickListener {
            val newTitle = editTitle.text.toString()
            val newDescription = editDescription.text.toString()
            val newCategory = editCategory.text.toString()

            if (newTitle.isBlank()) {
                Toast.makeText(this, "Le titre ne peut pas être vide", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            task?.let {
                it.title = newTitle
                it.description = newDescription
                it.category = newCategory
                TaskRepository.addCategory(newCategory)
            }

            Toast.makeText(this, "Tâche modifiée", Toast.LENGTH_SHORT).show()

            val resultIntent = intent
            resultIntent.putExtra("task_id", taskId)
            resultIntent.putExtra("task_title", newTitle)
            resultIntent.putExtra("task_description", newDescription)
            resultIntent.putExtra("task_category", newCategory)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}