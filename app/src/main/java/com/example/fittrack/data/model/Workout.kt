package com.example.fittrack.data.model

import com.google.firebase.firestore.PropertyName
data class Workout(
    val id: String = "", // Firestore Doc ID
    val userId: String = "",
    val title: String = "",
    val date: Long = System.currentTimeMillis(),
    val category: String = "Strength",
    val durationMinutes: Int = 0
)
