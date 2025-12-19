package com.example.fittrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fittrack.data.model.Workout
import com.example.fittrack.viewmodel.WorkoutViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWorkoutScreen(
    navController: NavController,
    viewModel: WorkoutViewModel,
    workoutId: String? = null
) {
    // check if in edit mode
    val isEditMode = workoutId != null

    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Strength") }
    var duration by remember { mutableStateOf("") }

    // Date Picker State
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val selectedDateText = datePickerState.selectedDateMillis?.let {
        dateFormatter.format(Date(it))
    } ?: "Select Date"

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("OK") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    LaunchedEffect(workoutId) {
        if (isEditMode) {
            val existing = viewModel.workouts.value.find { it.id == workoutId }
            existing?.let {
                title = it.title
                category = it.category
                duration = it.durationMinutes.toString()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Workout Title") },
                modifier = Modifier.fillMaxWidth()
            )

            // Date Selection Button
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(selectedDateText)
            }

            // Simple Category Selector (3 Buttons)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                listOf("Strength", "Cardio", "Flexibility").forEach { cat ->
                    FilterChip(
                        selected = category == cat,
                        onClick = { category = cat },
                        label = { Text(cat) }
                    )
                }
            }

            OutlinedTextField(
                value = duration,
                onValueChange = { duration = it },
                label = { Text("Duration (minutes)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val currentUserId = viewModel.getCurrentUserId()

                    val workout = Workout(
                        id = workoutId ?: "",
                        userId = currentUserId,
                        title = title,
                        category = category,
                        date = datePickerState.selectedDateMillis ?: System.currentTimeMillis(),
                        durationMinutes = duration.toIntOrNull() ?: 0
                    )

                    if (isEditMode) {
                        viewModel.updateWorkout(workout)
                    } else {
                        viewModel.addWorkout(workout)
                    }
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (isEditMode) "Update Workout" else "Save Workout")
            }
        }
    }
}