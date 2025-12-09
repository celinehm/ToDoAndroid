package com.example.myto_doapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class AddTaskActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etDesc: EditText
    private lateinit var etCategory: EditText
    private lateinit var spinnerPriority: Spinner
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var btnVoice: Button
    private lateinit var btnBack: Button

    private var isEditMode = false
    private var editTaskId: Int = -1  // pour identifier la tâche à modifier

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_task_activity)

        etTitle = findViewById(R.id.etTitle)
        etDesc = findViewById(R.id.etDescription)
        etCategory = findViewById(R.id.etCategory)
        spinnerPriority = findViewById(R.id.spinnerPriority)
        btnSave = findViewById(R.id.btnSaveTask)
        btnCancel = findViewById(R.id.btnCancel)
        btnVoice = findViewById(R.id.btnVoiceInput)
        btnBack = findViewById(R.id.btnBack)

        // Retour arrière
        btnBack.setOnClickListener {
            finish()
        }

        // Spinner priorité
        val priorities = arrayOf("High", "Medium", "Low")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priorities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPriority.adapter = adapter

        //-----------------------------------------------------------
        // 🔥 MODE MODIFICATION : si l'intent contient une tâche
        //-----------------------------------------------------------
        if (intent.hasExtra("edit_task_id")) {
            isEditMode = true
            editTaskId = intent.getIntExtra("edit_task_id", -1)

            etTitle.setText(intent.getStringExtra("edit_title"))
            etDesc.setText(intent.getStringExtra("edit_description"))
            etCategory.setText(intent.getStringExtra("edit_category"))

            val priority = intent.getStringExtra("edit_priority")
            val priorityIndex = priorities.indexOf(priority)
            if (priorityIndex >= 0) spinnerPriority.setSelection(priorityIndex)

            btnSave.text = "Modifier"
        }

        // Reconnaissance vocale
        val voiceLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    val spokenText = matches?.firstOrNull() ?: return@registerForActivityResult
                    etTitle.setText(spokenText)
                }
            }

        btnVoice.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            }
            voiceLauncher.launch(intent)
        }

        // Sauvegarder (ajout ou modification)
        btnSave.setOnClickListener {
            val resultIntent = Intent()

            resultIntent.putExtra("task_title", etTitle.text.toString())
            resultIntent.putExtra("task_description", etDesc.text.toString())
            resultIntent.putExtra("task_category", etCategory.text.toString())
            resultIntent.putExtra("task_priority", spinnerPriority.selectedItem.toString())

            if (isEditMode) {
                resultIntent.putExtra("is_edit_mode", true)
                resultIntent.putExtra("edit_task_id", editTaskId)
            } else {
                resultIntent.putExtra("is_edit_mode", false)
            }

            resultIntent.putExtra("task_is_done", false)

            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        // Annuler
        btnCancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }
}
