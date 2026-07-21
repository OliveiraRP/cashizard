package com.houseofrafa.cashizard.presentation.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.houseofrafa.cashizard.domain.model.Space
import com.houseofrafa.cashizard.domain.session.SpaceSession
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * The signed-in shell's own state. Navigation lives in [MainComponent]; this
 * owns the active space and the post-write reload signal.
 */
class MainViewModel(
    private val spaceSession: SpaceSession,
) : ViewModel() {

    val activeSpace: StateFlow<Space?> = spaceSession.activeSpace

    init {
        viewModelScope.launch { spaceSession.ensureLoaded() }
    }

    /** A sheet attaches to a space, so it stays closed until one is resolved. */
    fun hasActiveSpace(): Boolean = spaceSession.activeSpaceId.value != null

    /** Called when a sheet saves, so every screen reading balances reloads. */
    fun onDataChanged() = spaceSession.notifyDataChanged()
}
