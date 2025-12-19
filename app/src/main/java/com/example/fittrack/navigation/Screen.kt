package com.example.fittrack.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Dashboard : Screen("dashboard")
    object WorkoutsList : Screen("workouts_list")
    object WorkoutDetails : Screen("workout_details")
    object AddWorkout : Screen("add_workout")
    object AddExercise : Screen("add_exercise")
    object Profile : Screen("profile")
}