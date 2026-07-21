package com.houseofrafa.cashizard.presentation.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.houseofrafa.cashizard.domain.model.AuthException
import com.houseofrafa.cashizard.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onEmailChange(value: String) = _state.update { it.copy(email = value, errorMessage = null) }

    fun onPasswordChange(value: String) =
        _state.update { it.copy(password = value, errorMessage = null) }

    fun onSubmit() {
        val current = _state.value
        if (!current.canSubmit) return

        _state.update { it.copy(submitting = true, errorMessage = null, infoMessage = null) }
        viewModelScope.launch {
            try {
                authRepository.signIn(current.email, current.password)
                // On success RootComponent swaps the stack; keep the spinner running
                // so the form cannot be resubmitted during the transition.
            } catch (e: AuthException) {
                _state.update { it.copy(submitting = false, errorMessage = e.userMessage()) }
            }
        }
    }

    fun onForgotPassword() {
        val email = _state.value.email
        if (email.isBlank()) {
            _state.update { it.copy(errorMessage = "Enter your email first.") }
            return
        }

        _state.update { it.copy(submitting = true, errorMessage = null, infoMessage = null) }
        viewModelScope.launch {
            try {
                authRepository.sendPasswordReset(email)
                _state.update {
                    it.copy(
                        submitting = false,
                        infoMessage = "If that address has an account, a reset link is on its way.",
                    )
                }
            } catch (e: AuthException) {
                _state.update { it.copy(submitting = false, errorMessage = e.userMessage()) }
            }
        }
    }
}
