package com.example.fittrack.data.model

data class Exercise(
    val id: String = "",
    val workoutId: String = "", // ID of the parent workout
    val name: String = "",
    val sets: Int = 0,
    val reps: Int = 0,
    val weight: Double = 0.0
)
