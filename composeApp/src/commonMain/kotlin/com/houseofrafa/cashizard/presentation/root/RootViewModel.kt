package com.houseofrafa.cashizard.presentation.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.houseofrafa.cashizard.domain.model.AuthState
import com.houseofrafa.cashizard.domain.repository.AuthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn

/** Exposes the session so the root stack can follow it. */
class RootViewModel(
    authRepository: AuthRepository,
) : ViewModel() {

    val authState: StateFlow<AuthState> = authRepository.authState
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.Eagerly, AuthState.Unknown)
}
