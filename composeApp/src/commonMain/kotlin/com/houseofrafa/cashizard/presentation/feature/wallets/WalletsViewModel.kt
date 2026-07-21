package com.houseofrafa.cashizard.presentation.feature.wallets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.houseofrafa.cashizard.domain.session.SpaceSession
import com.houseofrafa.cashizard.domain.usecase.GetWalletsOverview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * The Wallets tab. Reloads whenever the active space changes or a transaction
 * is saved, since every balance here is computed server-side and a new
 * transaction invalidates all of them.
 */
class WalletsViewModel(
    private val getWalletsOverview: GetWalletsOverview,
    private val spaceSession: SpaceSession,
) : ViewModel() {

    private val _state = MutableStateFlow(WalletsUiState())
    val state: StateFlow<WalletsUiState> = _state.asStateFlow()

    init {
        combine(
            spaceSession.activeSpaceId.filterNotNull(),
            spaceSession.dataVersion,
        ) { spaceId, version -> spaceId to version }
            .distinctUntilChanged()
            .onEach { (spaceId, _) -> load(spaceId) }
            .launchIn(viewModelScope)

        viewModelScope.launch { spaceSession.ensureLoaded() }
    }

    private fun load(spaceId: String) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, errorMessage = null) }
            try {
                val overview = getWalletsOverview(spaceId)
                _state.update { it.copy(overview = overview, loading = false) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(loading = false, errorMessage = "Couldn't load your wallets.")
                }
            }
        }
    }
}
