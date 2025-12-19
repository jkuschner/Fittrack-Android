package com.example.fittrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.fittrack.navigation.Screen
import com.example.fittrack.ui.screens.AddExerciseScreen
import com.example.fittrack.ui.screens.LoginScreen
import com.example.fittrack.ui.theme.FitTrackTheme
import com.example.fittrack.viewmodel.AuthViewModel
import com.example.fittrack.ui.screens.AddWorkoutScreen
import com.example.fittrack.ui.screens.DashboardScreen
import com.example.fittrack.ui.screens.ProfileScreen
import com.example.fittrack.ui.screens.SignUpScreen
import com.example.fittrack.ui.screens.WorkoutDetailsScreen
import com.example.fittrack.ui.screens.WorkoutsListScreen
import com.example.fittrack.viewmodel.WorkoutViewModel
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitTrackTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()
                val workoutViewModel: WorkoutViewModel = viewModel()

                Scaffold(
                    bottomBar = {
                        // Only show bottom bar if the user is NOT on the Login/SignUp screens
                        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                        if (currentRoute != Screen.Login.route && currentRoute != Screen.SignUp.route) {
                            NavigationBar {
                                NavigationBarItem(
                                    selected = currentRoute == Screen.Dashboard.route,
                                    onClick = { navController.navigate(Screen.Dashboard.route) },
                                    icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                                    label = { Text("Dashboard") }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == Screen.WorkoutsList.route,
                                    onClick = { navController.navigate(Screen.WorkoutsList.route) },
                                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                                    label = { Text("Workouts") }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == Screen.Profile.route,
                                    onClick = { navController.navigate(Screen.Profile.route) },
                                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                                    label = { Text("Profile") }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Login.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Login.route) { LoginScreen(navController, authViewModel) }
                        composable(Screen.SignUp.route) { SignUpScreen(navController, authViewModel) }
                        composable(Screen.Dashboard.route) { DashboardScreen(navController, workoutViewModel) }
                        composable(Screen.WorkoutsList.route) { WorkoutsListScreen(navController, workoutViewModel) }
                        composable(Screen.Profile.route) { ProfileScreen(navController, authViewModel, workoutViewModel) }
                        composable("${Screen.WorkoutDetails.route}/{workoutId}") { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("workoutId") ?: ""
                            WorkoutDetailsScreen(navController, workoutViewModel, id)
                        }
                        composable(
                            route = "${Screen.AddWorkout.route}?workoutId={workoutId}"
                        ) { backStackEntry ->
                            val workoutId = backStackEntry.arguments?.getString("workoutId")
                            AddWorkoutScreen(navController, workoutViewModel, workoutId)
                        }
                        composable(
                            "${Screen.AddExercise.route}/{workoutId}?exerciseId={exerciseId}"
                        ) { backStackEntry ->
                            val workoutId = backStackEntry.arguments?.getString("workoutId") ?: ""
                            val exerciseId = backStackEntry.arguments?.getString("exerciseId")
                            AddExerciseScreen(navController, workoutViewModel, workoutId, exerciseId)
                        }
                    }
                }
            }
        }
    }
}