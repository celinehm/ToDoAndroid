package com.example.myto_doapp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter

    private lateinit var searchInput: EditText
    private var showCompleted = true
    private var sortByDate = false
    private var sortByPriority = false

    private lateinit var editTaskLauncher: androidx.activity.result.ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DarkModeManager.applyNightModeIfEnabled(this)
        setContentView(R.layout.mainactivity)

        recyclerView = findViewById(R.id.recyclerViewTasks)
        val btnAddTask = findViewById<Button>(R.id.btn_add_task)
        val btnSettings = findViewById<Button>(R.id.btn_settings)
        searchInput = findViewById(R.id.search_input)

        recyclerView.layoutManager = LinearLayoutManager(this)
        taskAdapter = TaskAdapter(TaskRepository.tasks) { task -> onTaskClicked(task) }
        recyclerView.adapter = taskAdapter

        // Exemple de tâche initiale
        if (TaskRepository.tasks.isEmpty()) {
            TaskRepository.tasks.add(Task(id = 1, title = "Tâche test", category = "Test", priority = "High"))
            TaskRepository.addCategory("Test")
        }

        // === Launcher pour AJOUTER une tâche ===
        val addTaskLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data = result.data ?: return@registerForActivityResult
                    val newTask = Task(
                        id = TaskRepository.tasks.size + 1,
                        title = data.getStringExtra("task_title") ?: "",
                        description = data.getStringExtra("task_description") ?: "",
                        category = data.getStringExtra("task_category") ?: "General",
                        priority = data.getStringExtra("task_priority") ?: "Normal",
                        isDone = data.getBooleanExtra("task_is_done", false)
                    )
                    TaskRepository.tasks.add(newTask)
                    TaskRepository.addCategory(newTask.category)
                    updateTaskList()
                }
            }

        btnAddTask.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            addTaskLauncher.launch(intent)
        }

        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        // === Recherche ===
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateTaskList()
            }
        })

        // === Launcher pour MODIFIER une tâche ===
        editTaskLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data = result.data ?: return@registerForActivityResult
                    val id = data.getIntExtra("task_id", -1)

                    val task = TaskRepository.tasks.find { it.id == id } ?: return@registerForActivityResult

                    // Modification autorisée
                    task.title = data.getStringExtra("task_title") ?: task.title
                    task.description = data.getStringExtra("task_description") ?: task.description
                    task.category = data.getStringExtra("task_category") ?: task.category
                    TaskRepository.addCategory(task.category) // Ajoute la catégorie modifiée

                    updateTaskList()
                    Toast.makeText(this, "Tâche modifiée", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onResume() {
        super.onResume()
        val prefs = getSharedPreferences("mytodo_prefs", Context.MODE_PRIVATE)

        sortByDate = prefs.getBoolean("sort_by_date", false)
        sortByPriority = prefs.getBoolean("sort_by_priority", false)

        updateTaskList()
    }

    private fun updateTaskList() {
        val query = searchInput.text.toString()
        var filtered = TaskRepository.tasks.filter {
            (it.title.contains(query, ignoreCase = true)
                    || it.category.contains(query, ignoreCase = true)
                    || it.priority.contains(query, ignoreCase = true))
                    && (showCompleted || !it.isDone)
        }

        if (sortByDate) filtered = filtered.sortedBy { it.dateCreation }
        else if (sortByPriority) {
            val priorityOrder = mapOf("High" to 0, "Medium" to 1, "Low" to 2)
            filtered = filtered.sortedBy { priorityOrder[it.priority] ?: 3 }
        }

        taskAdapter = TaskAdapter(filtered) { task -> onTaskClicked(task) }
        recyclerView.adapter = taskAdapter
    }

    private fun onTaskClicked(task: Task) {
        val options = arrayOf(
            if (task.isDone) "Marquer comme non terminée" else "Marquer comme terminée",
            "Modifier",
            "Supprimer"
        )

        AlertDialog.Builder(this)
            .setTitle(task.title)
            .setItems(options) { _, which ->
                when (which) {

                    0 -> {
                        task.isDone = !task.isDone
                        updateTaskList()
                    }

                    1 -> {
                        val intent = Intent(this, EditTaskActivity::class.java)
                        intent.putExtra("task_id", task.id)
                        intent.putExtra("task_title", task.title)
                        intent.putExtra("task_description", task.description)
                        intent.putExtra("task_category", task.category)
                        editTaskLauncher.launch(intent)
                    }

                    2 -> {
                        TaskRepository.tasks.remove(task)
                        updateTaskList()
                        Toast.makeText(this, "Tâche supprimée", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .show()
    }
}
