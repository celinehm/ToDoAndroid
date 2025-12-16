package com.example.myto_doapp

import java.text.SimpleDateFormat
import java.util.*

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy à HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
