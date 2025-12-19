package com.example.fittrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fittrack.data.model.Workout
import com.example.fittrack.viewmodel.WorkoutViewModel
import com.example.fittrack.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutsListScreen(navController: NavController, viewModel: WorkoutViewModel) {
    // every time user goes to the workoutList, fetch ID and load specific data
    LaunchedEffect(Unit) {
        viewModel.loadUserWorkouts()
    }

    val allWorkouts by viewModel.workouts.collectAsState()
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Strength", "Cardio", "Flexibility")

    // DELETE DIALOG STATE
    var workoutToDelete by remember { mutableStateOf<Workout?>(null) }
    // EDIT DIALOG STATE
    var workoutToEdit by remember { mutableStateOf<Workout?>(null) }

    val filteredWorkouts = if (selectedCategory == "All") allWorkouts
    else allWorkouts.filter { it.category == selectedCategory }

    // CONFIRMATION DIALOG
    if (workoutToDelete != null) {
        AlertDialog(
            onDismissRequest = { workoutToDelete = null },
            title = { Text("Delete Workout?") },
            text = { Text("Are you sure you want to delete '${workoutToDelete?.title}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteWorkout(workoutToDelete!!.id)
                        workoutToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { workoutToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (workoutToEdit != null) {
        // Local state for the text fields, initialized with current workout values
        var editedTitle by remember { mutableStateOf(workoutToEdit!!.title) }
        var editedDuration by remember { mutableStateOf(workoutToEdit!!.durationMinutes.toString()) }

        AlertDialog(
            onDismissRequest = { workoutToEdit = null },
            title = { Text("Edit Workout") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editedTitle,
                        onValueChange = { editedTitle = it },
                        label = { Text("Title") }
                    )
                    OutlinedTextField(
                        value = editedDuration,
                        onValueChange = { editedDuration = it },
                        label = { Text("Duration (mins)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Create a copy of the workout with new values
                        val updatedWorkout = workoutToEdit!!.copy(
                            title = editedTitle,
                            durationMinutes = editedDuration.toIntOrNull() ?: 0
                        )
                        viewModel.updateWorkout(updatedWorkout)
                        workoutToEdit = null // Close dialog
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { workoutToEdit = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("All Workouts") }) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddWorkout.route) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Workout")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            ScrollableTabRow(selectedTabIndex = categories.indexOf(selectedCategory)) {
                categories.forEach { cat ->
                    Tab(selected = selectedCategory == cat, onClick = { selectedCategory = cat }) {
                        Text(cat, modifier = Modifier.padding(16.dp))
                    }
                }
            }
            LazyColumn {
                items(filteredWorkouts) { workout ->
                    WorkoutRow(
                        workout = workout,
                        onClick = {
                            navController.navigate("${Screen.WorkoutDetails.route}/${workout.id}")
                        },
                        onDelete = { workoutToDelete = workout },
                        onEdit = {
                            navController.navigate("${Screen.AddWorkout.route}?workoutId=${workout.id}")
                        }
                    )
                }
            }
        }
    }
}

// THE ROW DEFINITION
@Composable
fun WorkoutRow(
    workout: Workout,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(workout.title, style = MaterialTheme.typography.titleMedium)
                Text("${workout.durationMinutes} mins | ${workout.category}", style = MaterialTheme.typography.bodySmall)
            }
            // Edit Button
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
            }
            // Delete Button
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}