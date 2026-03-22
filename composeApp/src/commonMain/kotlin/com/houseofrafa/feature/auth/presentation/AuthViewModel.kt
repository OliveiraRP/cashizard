package com.houseofrafa.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.houseofrafa.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isValidating: Boolean = true,
    val isLoginMode: Boolean = true,
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val navigateToHome: Boolean = false,
)

class AuthViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        validateSession()
    }

    private fun validateSession() {
        viewModelScope.launch {
            authRepository.validateSession()
                .onSuccess { isValid ->
                    _uiState.update { it.copy(isValidating = false, navigateToHome = isValid) }
                }
                .onFailure {
                    _uiState.update { it.copy(isValidating = false) }
                }
        }
    }

    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value, errorMessage = null) }
    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value, errorMessage = null) }
    fun onNameChange(value: String) = _uiState.update { it.copy(name = value, errorMessage = null) }

    fun toggleMode() = _uiState.update {
        it.copy(isLoginMode = !it.isLoginMode, errorMessage = null)
    }

    fun submit() {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) return
        if (!state.isLoginMode && state.name.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            if (state.isLoginMode) {
                authRepository.loginWithEmail(state.email.trim(), state.password)
                    .onSuccess {
                        _uiState.update { it.copy(isLoading = false, navigateToHome = true) }
                    }
                    .onFailure {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Invalid email or password") }
                    }
            } else {
                authRepository.signUpWithEmail(state.email.trim(), state.password, state.name.trim())
                    .onSuccess {
                        _uiState.update { it.copy(isLoading = false, navigateToHome = true) }
                    }
                    .onFailure { e ->
                        _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Sign up failed") }
                    }
            }
        }
    }
}
