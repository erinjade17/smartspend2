package com.example.budgettrackerapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.budgettrackerapp.repository.UserRepository // Import your UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.budgettrackerapp.data.entities.User // Import your User entity

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository // Use the repository
) : ViewModel() {

    // --- Login ---
    private val _loginUiState = MutableStateFlow<LoginUiState>(LoginUiState.Initial)
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    fun login(username: String, password: String) {
        _loginUiState.value = LoginUiState.Loading
        viewModelScope.launch {
            try {
                val userResult = userRepository.loginUser(username, password) // Get Result
                userResult.fold(
                    onSuccess = { user ->
                        _loginUiState.value = LoginUiState.Success(user)
                    },
                    onFailure = { error ->
                        _loginUiState.value = LoginUiState.Error(error.message ?: "Login Failed")
                    }
                )
            } catch (e: Exception) {
                _loginUiState.value = LoginUiState.Error(e.message ?: "Login Failed")
            }
        }
    }

    // --- Register ---
    private val _registerUiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Initial)
    val registerUiState: StateFlow<RegisterUiState> = _registerUiState.asStateFlow()

    fun register(username: String, password: String) {
        _registerUiState.value = RegisterUiState.Loading
        viewModelScope.launch {
            try {
                val result = userRepository.registerUser(username, password) // Get Result
                result.fold(
                    onSuccess = { userId ->
                        _registerUiState.value = RegisterUiState.Success(userId)
                    },
                    onFailure = { error ->
                        _registerUiState.value = RegisterUiState.Error(error.message ?: "Registration Failed")
                    }
                )
            } catch (e: Exception) {
                _registerUiState.value = RegisterUiState.Error(e.message ?: "Registration Failed")
            }
        }
    }


    // --- UI States ---
    sealed class LoginUiState {
        object Initial : LoginUiState()
        object Loading : LoginUiState()
        data class Success(val user: User) : LoginUiState()
        data class Error(val message: String) : LoginUiState()
    }

    sealed class RegisterUiState {
        object Initial : RegisterUiState()
        object Loading : RegisterUiState()
        data class Success(val userId: Long) : RegisterUiState()
        data class Error(val message: String) : RegisterUiState()
    }
}
