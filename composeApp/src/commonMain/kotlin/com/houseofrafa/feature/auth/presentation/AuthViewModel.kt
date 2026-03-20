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
    val token: String = "",
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
        validateStoredToken()
    }

    private fun validateStoredToken() {
        viewModelScope.launch {
            authRepository.validateStoredToken()
                .onSuccess { user ->
                    _uiState.update { it.copy(isValidating = false, navigateToHome = user != null) }
                }
                .onFailure {
                    _uiState.update { it.copy(isValidating = false) }
                }
        }
    }

    fun onTokenChange(value: String) {
        _uiState.update { it.copy(token = value, errorMessage = null) }
    }

    fun login() {
        val token = _uiState.value.token.trim()
        if (token.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            authRepository.loginWithToken(token)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, navigateToHome = true) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Unknown error") }
                }
        }
    }
}
