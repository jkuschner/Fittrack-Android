package com.example.fittrack.viewmodel

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fittrack.data.model.Exercise
import com.example.fittrack.data.model.Workout
import com.example.fittrack.data.repository.WorkoutRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WorkoutViewModel(
    private val repository: WorkoutRepository = WorkoutRepository()
) : ViewModel() {
    private val _workouts = MutableStateFlow<List<Workout>>(emptyList())
    val workouts: StateFlow<List<Workout>> = _workouts
    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises: StateFlow<List<Exercise>> = _exercises

    private var workoutsJob: Job? = null
    private var exercisesJob: Job? = null

    init {
        loadUserWorkouts()
    }

    fun loadUserWorkouts() {
        val userId = repository.getCurrentUserId()

        // cancel any previous listener
        workoutsJob?.cancel()

        if (userId.isNotEmpty()) {
            workoutsJob = viewModelScope.launch {
                repository.getWorkouts(userId).collect { list ->
                    _workouts.value = list
                }
            }
        } else {
            android.util.Log.e("Fittrack", "Attempted to load workouts with no User ID")
        }
    }

    fun addWorkout(workout: Workout) {
        viewModelScope.launch {
            repository.addWorkout(workout)
        }
    }

    fun deleteWorkout(workoutId: String) {
        viewModelScope.launch {
            repository.deleteWorkout(workoutId) { success ->
                // real-time listener in repo will auto-update list
            }
        }
    }

    fun updateWorkout(workout: Workout) {
        viewModelScope.launch {
            repository.updateWorkout(workout) { success ->

            }
        }
    }

    fun addExercise(workoutId: String, exercise: Exercise) {
        viewModelScope.launch {
            repository.addExercise(workoutId, exercise)
        }
    }

    fun loadExercises(workoutId: String) {
        exercisesJob?.cancel()

        exercisesJob = viewModelScope.launch {
            repository.getExercises(workoutId).collect { list ->
                _exercises.value = list
            }
        }
    }

    fun deleteExercise(workoutId: String, exerciseId: String) {
        repository.deleteExercise(workoutId, exerciseId)
    }

    fun updateExercise(workoutId: String, exercise: Exercise) {
        repository.updateExercise(workoutId, exercise)
    }

    fun getCurrentUserId(): String {
        return repository.getCurrentUserId()
    }

    fun clearData() {
        workoutsJob?.cancel()
        exercisesJob?.cancel()
        _workouts.value = emptyList()
        _exercises.value = emptyList()
    }
}