package com.example.fittrack.data.repository

import androidx.compose.runtime.snapshotFlow
import com.example.fittrack.data.model.Exercise
import com.example.fittrack.data.model.Workout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import com.google.firebase.firestore.toObjects

class WorkoutRepository {
    private val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
    private val auth = com.google.firebase.auth.FirebaseAuth.getInstance()

    fun getWorkouts(userId: String) = callbackFlow {
        // TODO: remove temporary log message
        android.util.Log.d("FitTrack", "Querying workouts for ID: $userId")

        if (userId.isEmpty()) {
            trySend(emptyList())
            return@callbackFlow
        }

        val listener = db.collection("workouts")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val data = snapshot?.toObjects<Workout>() ?: emptyList()
                trySend(data)
            }
        awaitClose { listener.remove() }
    }

    fun addWorkout(workout: Workout) {
        val userId = auth.currentUser?.uid ?: return
        val docRef = db.collection("workouts").document()
        val workoutWithId = workout.copy(id = docRef.id, userId = userId)
        docRef.set(workoutWithId)
    }

    fun deleteWorkout(workoutId: String, onResult: (Boolean) -> Unit) {
        db.collection("workouts").document(workoutId)
            .delete()
            .addOnCompleteListener { onResult(it.isSuccessful) }
    }

    fun updateWorkout(workout: Workout, onResult: (Boolean) -> Unit) {
        db.collection("workouts").document(workout.id)
            .set(workout)
            .addOnCompleteListener { onResult(it.isSuccessful) }
    }

    fun getExercises(workoutId: String) = callbackFlow {
        val listener = db.collection("workouts")
            .document(workoutId)
            .collection("exercises")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val data = snapshot?.toObjects(Exercise::class.java) ?: emptyList()
                trySend(data)
            }
        awaitClose { listener.remove() }
    }

    fun addExercise(workoutId: String, exercise: Exercise) {
        val docRef = db.collection("workouts")
            .document(workoutId)
            .collection("exercises")
            .document()

        val exerciseWithId = exercise.copy(id = docRef.id)
        docRef.set(exerciseWithId)
            .addOnSuccessListener {
                android.util.Log.d("Firestore", "Exercise successfully added")
            }
            .addOnFailureListener { e ->
                android.util.Log.d("Firestore", "Error adding exercise", e)
            }
    }

    fun deleteExercise(workoutId: String, exerciseId: String) {
        db.collection("workouts")
            .document(workoutId)
            .collection("exercises")
            .document(exerciseId)
            .delete()
    }

    fun updateExercise(workoutId: String, exercise: Exercise) {
        db.collection("workouts")
            .document(workoutId)
            .collection("exercises")
            .document(exercise.id)
            .set(exercise)
    }

    fun getCurrentUserId(): String {
        val user = FirebaseAuth.getInstance().currentUser
        android.util.Log.d("Fittrack", "Repository checking Auth. Current User: ${user?.email}, UID: ${user?.uid}")
        return user?.uid ?: ""
    }
}