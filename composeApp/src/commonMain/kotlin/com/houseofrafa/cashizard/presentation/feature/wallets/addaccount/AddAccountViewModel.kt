package com.houseofrafa.cashizard.presentation.feature.wallets.addaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.houseofrafa.cashizard.domain.model.NewAccount
import com.houseofrafa.cashizard.domain.repository.AccountRepository
import com.houseofrafa.cashizard.domain.session.SpaceSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** The "New Wallet Account" sheet — a single name field. */
class AddAccountViewModel(
    private val accountRepository: AccountRepository,
    private val spaceSession: SpaceSession,
) : ViewModel() {

    private val _state = MutableStateFlow(AddAccountUiState())
    val state: StateFlow<AddAccountUiState> = _state.asStateFlow()

    fun onNameChange(value: String) =
        _state.update { it.copy(name = value, errorMessage = null) }

    fun onRequestClose() = _state.update { it.copy(closeRequested = true) }

    fun onSave() {
        val current = _state.value
        if (!current.canSave) return

        _state.update { it.copy(saving = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                accountRepository.createAccount(
                    NewAccount(spaceId = spaceSession.requireSpaceId(), name = current.name.trim()),
                )
                spaceSession.notifyDataChanged()
                onRequestClose()
            } catch (e: Exception) {
                _state.update {
                    it.copy(saving = false, errorMessage = "Couldn't save. Please try again.")
                }
            }
        }
    }
}
