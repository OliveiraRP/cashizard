package com.houseofrafa.cashizard.presentation.feature.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.houseofrafa.cashizard.domain.model.TransactionType
import com.houseofrafa.cashizard.domain.session.SpaceSession
import com.houseofrafa.cashizard.domain.usecase.GetSpendingBreakdown
import com.houseofrafa.cashizard.presentation.common.currentMonthStart
import com.houseofrafa.cashizard.presentation.common.endOfMonth
import com.houseofrafa.cashizard.presentation.common.nextMonth
import com.houseofrafa.cashizard.presentation.common.previousMonth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

/**
 * The Analytics tab: a month's expenses by category group, with a drill-down
 * into one group's categories.
 */
class AnalyticsViewModel(
    private val getSpendingBreakdown: GetSpendingBreakdown,
    private val spaceSession: SpaceSession,
) : ViewModel() {

    private val _state = MutableStateFlow(AnalyticsUiState(month = currentMonthStart()))
    val state: StateFlow<AnalyticsUiState> = _state.asStateFlow()

    init {
        combine(
            spaceSession.activeSpaceId.filterNotNull(),
            _state.map { it.month to it.drilledGroupId }.distinctUntilChanged(),
            spaceSession.dataVersion,
        ) { spaceId, monthAndGroup, version -> Triple(spaceId, monthAndGroup, version) }
            .distinctUntilChanged()
            .onEach { (spaceId, monthAndGroup, _) ->
                load(spaceId, monthAndGroup.first, monthAndGroup.second)
            }
            .launchIn(viewModelScope)

        viewModelScope.launch { spaceSession.ensureLoaded() }
    }

    // Leaving the month also leaves the drill-down: the group may have no
    // spending in the new month.
    fun onPreviousMonth() = _state.update { it.copy(month = it.month.previousMonth()).atTopLevel() }

    fun onNextMonth() = _state.update { it.copy(month = it.month.nextMonth()).atTopLevel() }

    fun onSliceClick(id: String) {
        val current = _state.value
        // Slices are categories once drilled in, and those do not drill further.
        if (current.isDrilled) return
        val name = current.breakdown?.slices?.firstOrNull { it.id == id }?.name ?: return
        _state.update { it.copy(drilledGroupId = id, drilledGroupName = name) }
    }

    fun onBackToGroups() = _state.update { it.atTopLevel() }

    private fun AnalyticsUiState.atTopLevel() =
        copy(drilledGroupId = null, drilledGroupName = null)

    private fun load(spaceId: String, month: LocalDate, groupId: String?) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, errorMessage = null) }
            try {
                val breakdown = getSpendingBreakdown(
                    spaceId = spaceId,
                    from = month,
                    to = month.endOfMonth(),
                    type = TransactionType.EXPENSE,
                    groupId = groupId,
                )
                _state.update { it.copy(breakdown = breakdown, loading = false) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(loading = false, errorMessage = "Couldn't load your analytics.")
                }
            }
        }
    }
}
