package com.example.fittrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fittrack.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}
class AuthViewModel(private val repository: AuthRepository = AuthRepository()) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    // persistent login check
    val currentUser get() = repository.currentUser

    fun signUp(email: String, pass: String) {
        if (!validateInputs(email, pass)) return

        _authState.value = AuthState.Loading
        repository.signUp(email, pass) { success, error ->
            if (success) _authState.value = AuthState.Success
            else _authState.value = AuthState.Error(error ?: "Sign up failed")
        }
    }

    fun login(email: String, pass: String) {
        if (!validateInputs(email, pass)) return

        _authState.value = AuthState.Loading
        repository.login(email, pass) { success, error ->
            if (success) _authState.value = AuthState.Success
            else _authState.value = AuthState.Error(error ?: "Login failed")
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    // password validation (min 6 chars) and email format
    private fun validateInputs(email: String, pass: String) : Boolean {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _authState.value = AuthState.Error("Please enter a vlid email address.")
            return false
        }
        if (pass.length < 6) {
            _authState.value = AuthState.Error("Password must be at least 6 characters.")
            return false
        }
        return true
    }

    fun logout() {
        repository.logout()
        _authState.value = AuthState.Idle
    }
}