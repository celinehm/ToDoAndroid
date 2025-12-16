package com.example.myto_doapp

import java.util.*

data class Task(
    val id: Int,
    var title: String,
    var description: String = "",
    var category: String = "General",
    var priority: String = "Normal", // High, Medium, Low
    var isDone: Boolean = false,
    val dateCreation: Long = System.currentTimeMillis()
)
