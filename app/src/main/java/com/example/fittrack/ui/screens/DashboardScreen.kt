package com.example.fittrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fittrack.viewmodel.WorkoutViewModel
import com.example.fittrack.navigation.Screen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: WorkoutViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.loadUserWorkouts()
    }

    val workouts by viewModel.workouts.collectAsState()

    // counts workouts in last 7 days
    val streakCount = workouts.count { it.date > System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Dashboard") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            // Streak Counter Card
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔥 7-Day Streak", style = MaterialTheme.typography.titleMedium)
                    Text("$streakCount Workouts", style = MaterialTheme.typography.headlineLarge)
                }
            }

            Text("Recent Workouts", style = MaterialTheme.typography.titleLarge)

            LazyColumn {
                items(workouts.take(5)) { workout -> // Only show top 5 on Dashboard
                    WorkoutRow(workout) {
                        navController.navigate("${Screen.WorkoutDetails.route}/${workout.id}")
                    }
                }
            }

            Button(
                onClick = { navController.navigate(Screen.WorkoutsList.route) },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text("View All Workouts")
            }
        }
    }
}

@Composable
fun WorkoutRow(workout: com.example.fittrack.data.model.Workout, onClick: () -> Unit) {
    val dateString = SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(workout.date))
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(workout.title, style = MaterialTheme.typography.titleMedium)
                Text(workout.category, style = MaterialTheme.typography.bodySmall)
            }
            Text(dateString)
        }
    }
}