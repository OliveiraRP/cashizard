package com.houseofrafa.cashizard.presentation.feature.settings.connectbank

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.houseofrafa.cashizard.domain.model.AspspOption
import com.houseofrafa.cashizard.domain.repository.AccountRepository
import com.houseofrafa.cashizard.domain.repository.BankAccountLinkRepository
import com.houseofrafa.cashizard.domain.repository.EnableBankingLinkRepository
import com.houseofrafa.cashizard.domain.session.SpaceSession
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * The "Connect Bank Account" sheet: pick a real-world account, pick a bank,
 * open the bank's login in the system browser, then confirm the link landed.
 * The private key never reaches this ViewModel — [EnableBankingLinkRepository]
 * talks to the `eb-aspsps` / `eb-start-auth` Edge Functions, which hold it.
 */
class ConnectBankAccountViewModel(
    private val accountRepository: AccountRepository,
    private val bankAccountLinkRepository: BankAccountLinkRepository,
    private val enableBankingLinkRepository: EnableBankingLinkRepository,
    private val spaceSession: SpaceSession,
) : ViewModel() {

    private val _state = MutableStateFlow(ConnectBankAccountUiState())
    val state: StateFlow<ConnectBankAccountUiState> = _state.asStateFlow()

    private val _events = Channel<ConnectBankAccountEvent>(Channel.BUFFERED)
    val events: Flow<ConnectBankAccountEvent> = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            try {
                val accounts = accountRepository.getAccounts(spaceSession.requireSpaceId())
                _state.update {
                    it.copy(accounts = accounts, selectedAccountId = accounts.firstOrNull()?.id, loading = false)
                }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, errorMessage = "Couldn't load your accounts.") }
            }
        }
    }

    fun onRequestClose() = _state.update { it.copy(closeRequested = true) }

    /** Hardware/gesture back: steps back through the flow before closing the sheet. */
    fun onBackPressed() {
        if (_state.value.step == ConnectStep.PickAccount) onRequestClose() else onBack()
    }

    fun onAccountSelected(accountId: String) =
        _state.update { it.copy(selectedAccountId = accountId, errorMessage = null) }

    fun onContinueToBankPicker() {
        _state.update { it.copy(step = ConnectStep.PickBank, errorMessage = null) }
        if (_state.value.aspsps.isEmpty()) loadAspsps()
    }

    fun onBack() = _state.update {
        it.copy(
            step = when (it.step) {
                ConnectStep.PickAccount -> ConnectStep.PickAccount
                ConnectStep.PickBank -> ConnectStep.PickAccount
                ConnectStep.WaitingForBank -> ConnectStep.PickBank
            },
            errorMessage = null,
        )
    }

    fun onBankQueryChange(value: String) = _state.update { it.copy(bankQuery = value) }

    fun onAspspSelected(aspsp: AspspOption) = _state.update { it.copy(selectedAspsp = aspsp, errorMessage = null) }

    fun onStartAuth() {
        val current = _state.value
        val accountId = current.selectedAccountId ?: return
        val aspsp = current.selectedAspsp ?: return
        if (!current.canStartAuth) return

        _state.update { it.copy(startingAuth = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val url = enableBankingLinkRepository.startAuth(accountId, aspsp.name, aspsp.country)
                _state.update { it.copy(startingAuth = false, step = ConnectStep.WaitingForBank) }
                _events.send(ConnectBankAccountEvent.OpenUrl(url))
            } catch (e: Exception) {
                _state.update {
                    it.copy(startingAuth = false, errorMessage = "DEBUG: ${e::class.simpleName}: ${e.message}")
                }
            }
        }
    }

    /** Called when the user taps "Done" after finishing the bank's login in the browser. */
    fun onCheckLinked() {
        val accountId = _state.value.selectedAccountId ?: return

        _state.update { it.copy(checkingLink = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val links = bankAccountLinkRepository.getLinks(spaceSession.requireSpaceId())
                if (links.any { it.accountId == accountId }) {
                    spaceSession.notifyDataChanged()
                    onRequestClose()
                } else {
                    _state.update {
                        it.copy(
                            checkingLink = false,
                            errorMessage = "Not linked yet — finish signing in with your bank in the browser, then try again.",
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(checkingLink = false, errorMessage = "Couldn't check. Please try again.") }
            }
        }
    }

    private fun loadAspsps() {
        _state.update { it.copy(loadingBanks = true) }
        viewModelScope.launch {
            try {
                val aspsps = enableBankingLinkRepository.getAspsps()
                _state.update { it.copy(aspsps = aspsps, loadingBanks = false) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(loadingBanks = false, errorMessage = "Couldn't load the list of banks.")
                }
            }
        }
    }
}

/** One-shot outcomes the sheet content reacts to, rather than persisted state. */
sealed interface ConnectBankAccountEvent {
    data class OpenUrl(val url: String) : ConnectBankAccountEvent
}
