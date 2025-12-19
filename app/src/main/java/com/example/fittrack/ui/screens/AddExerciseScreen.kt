package com.example.fittrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fittrack.data.model.Exercise
import com.example.fittrack.viewmodel.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExerciseScreen(
    navController: NavController,
    viewModel: WorkoutViewModel,
    workoutId: String,
    exerciseId: String? = null
) {
    val isEditMode = exerciseId != null

    var name by remember { mutableStateOf("") }
    var sets by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }

    LaunchedEffect(exerciseId) {
        if (isEditMode) {
            val existing = viewModel.exercises.value.find { it.id == exerciseId }
            existing?.let {
                name = it.name
                sets = it.sets.toString()
                reps = it.reps.toString()
                weight = it.weight.toString()
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Update Exercise" else "Add Exercise") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Exercise Name (e.g., Bench Press)") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = sets,
                    onValueChange = { sets = it },
                    label = { Text("Sets") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = reps,
                    onValueChange = { reps = it },
                    label = { Text("Reps") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Weight (kg)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val exercise = Exercise(
                        id = exerciseId ?: "",
                        workoutId = workoutId,
                        name = name,
                        sets = sets.toIntOrNull() ?: 0,
                        reps = reps.toIntOrNull() ?: 0,
                        weight = weight.toDoubleOrNull() ?: 0.0
                    )

                    if (isEditMode) {
                        viewModel.updateExercise(workoutId, exercise)
                    } else {
                        viewModel.addExercise(workoutId, exercise)
                    }
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && sets.isNotBlank()
            ) {
                Text(if (isEditMode) "Update Exercise" else "Add Exercise")
            }
        }
    }
}
