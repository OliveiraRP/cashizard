package com.houseofrafa.cashizard.presentation.feature.settings.editprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.houseofrafa.cashizard.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** The "Edit Name" sheet — a single name field, prefilled with the current profile. */
class EditProfileViewModel(
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(EditProfileUiState())
    val state: StateFlow<EditProfileUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val profile = profileRepository.getProfile()
                _state.update { it.copy(name = profile.displayName, loading = false) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(loading = false, errorMessage = "Couldn't load your profile.")
                }
            }
        }
    }

    fun onNameChange(value: String) = _state.update { it.copy(name = value, errorMessage = null) }

    fun onRequestClose() = _state.update { it.copy(closeRequested = true) }

    fun onSave() {
        val current = _state.value
        if (!current.canSave) return

        _state.update { it.copy(saving = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                profileRepository.updateDisplayName(current.name.trim())
                onRequestClose()
            } catch (e: Exception) {
                _state.update {
                    it.copy(saving = false, errorMessage = "Couldn't save. Please try again.")
                }
            }
        }
    }
}
