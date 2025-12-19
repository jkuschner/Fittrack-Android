package com.example.fittrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fittrack.data.model.Exercise
import com.example.fittrack.data.model.Workout
import com.example.fittrack.viewmodel.WorkoutViewModel
import com.example.fittrack.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailsScreen(
    navController: NavController,
    viewModel: WorkoutViewModel,
    workoutId: String
) {
    val workout = viewModel.workouts.collectAsState().value.find { it.id == workoutId }

    val exercises by viewModel.exercises.collectAsState()
    var exerciseToDelete by remember { mutableStateOf<Exercise?>(null) }
    var exerciseToEdit by remember { mutableStateOf<Exercise?>(null) }

    // Trigger the data fetch when this screen first loads
    LaunchedEffect(workoutId) {
        viewModel.loadExercises(workoutId)
    }

    if (exerciseToDelete != null) {
        AlertDialog(
            onDismissRequest = { exerciseToDelete = null },
            title = { Text("Delete Exercise") },
            text = { Text("Are you sure you want to remove ${exerciseToDelete?.name}?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteExercise(workoutId, exerciseToDelete!!.id)
                    exerciseToDelete = null
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { exerciseToDelete = null }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(workout?.title ?: "Workout Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("${Screen.AddExercise.route}/$workoutId")
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Exercise")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            if (workout != null) {
                Text("Category: ${workout.category}", style = MaterialTheme.typography.bodyLarge)
                Text("Duration: ${workout.durationMinutes} mins", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Exercises", style = MaterialTheme.typography.headlineSmall)

                LazyColumn {
                    items(exercises) { exercise ->
                        ExerciseRow(
                            exercise = exercise,
                            onEdit = {
                                navController.navigate(
                                    "${Screen.AddExercise.route}/$workoutId?exerciseId=${exercise.id}"
                                )},
                            onDelete = { exerciseToDelete = exercise }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseRow(exercise: Exercise, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(exercise.name, style = MaterialTheme.typography.titleMedium)
                Text("${exercise.sets} sets x ${exercise.reps} reps", style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, "Edit") }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Delete", tint = Color.Red) }
        }
    }
}