package com.example.myto_doapp

object TaskRepository {

    // Liste globale de toutes les tâches
    val tasks = mutableListOf<Task>()

    // Liste des catégories existantes
    private val categories = mutableSetOf<String>()

    fun addCategory(category: String) {
        if (category.isNotBlank()) {
            categories.add(category)
        }
    }

    fun getAllCategories(): List<String> {
        return categories.toList()
    }

    fun getTasksByCategory(category: String): List<Task> {
        return tasks.filter { it.category == category }
    }

    fun clearCategories() {
        categories.clear()
    }
}
