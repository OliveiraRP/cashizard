package com.houseofrafa.cashizard.presentation.feature.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.houseofrafa.cashizard.domain.session.SpaceSession
import com.houseofrafa.cashizard.domain.usecase.GetTransactionFeed
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
 * The Transactions tab: one month at a time, grouped by day. Reloads when the
 * month changes, the space changes, or a transaction is saved.
 */
class TransactionsViewModel(
    private val getTransactionFeed: GetTransactionFeed,
    private val spaceSession: SpaceSession,
) : ViewModel() {

    private val _state = MutableStateFlow(TransactionsUiState(month = currentMonthStart()))
    val state: StateFlow<TransactionsUiState> = _state.asStateFlow()

    init {
        combine(
            spaceSession.activeSpaceId.filterNotNull(),
            _state.map { it.month }.distinctUntilChanged(),
            spaceSession.dataVersion,
        ) { spaceId, month, version -> Triple(spaceId, month, version) }
            .distinctUntilChanged()
            .onEach { (spaceId, month, _) -> load(spaceId, month) }
            .launchIn(viewModelScope)

        viewModelScope.launch { spaceSession.ensureLoaded() }
    }

    fun onPreviousMonth() = _state.update { it.copy(month = it.month.previousMonth()) }

    fun onNextMonth() = _state.update { it.copy(month = it.month.nextMonth()) }

    private fun load(spaceId: String, month: LocalDate) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, errorMessage = null) }
            try {
                val days = getTransactionFeed(
                    spaceId = spaceId,
                    from = month,
                    to = month.endOfMonth(),
                )
                _state.update {
                    it.copy(days = days, totals = days.monthTotals(), loading = false)
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(loading = false, errorMessage = "Couldn't load your transactions.")
                }
            }
        }
    }
}
