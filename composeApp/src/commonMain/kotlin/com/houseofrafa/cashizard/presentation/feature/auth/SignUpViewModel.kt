package com.houseofrafa.cashizard.presentation.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.houseofrafa.cashizard.domain.model.AuthException
import com.houseofrafa.cashizard.domain.repository.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(SignUpUiState())
    val state: StateFlow<SignUpUiState> = _state.asStateFlow()

    private val _events = Channel<SignUpEvent>(Channel.BUFFERED)
    val events: Flow<SignUpEvent> = _events.receiveAsFlow()

    fun onNameChange(value: String) = _state.update { it.copy(name = value, errorMessage = null) }

    fun onEmailChange(value: String) = _state.update { it.copy(email = value, errorMessage = null) }

    fun onPasswordChange(value: String) =
        _state.update { it.copy(password = value, errorMessage = null) }

    fun onSubmit() {
        val current = _state.value
        if (!current.canSubmit) return

        _state.update { it.copy(submitting = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val signedIn = authRepository.signUp(
                    email = current.email,
                    password = current.password,
                    displayName = current.name,
                )
                if (!signedIn) _events.send(SignUpEvent.ConfirmationRequired)
            } catch (e: AuthException) {
                _state.update { it.copy(submitting = false, errorMessage = e.userMessage()) }
            }
        }
    }
}
