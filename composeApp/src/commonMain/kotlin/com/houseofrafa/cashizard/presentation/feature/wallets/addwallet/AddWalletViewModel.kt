package com.houseofrafa.cashizard.presentation.feature.wallets.addwallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.houseofrafa.cashizard.domain.model.NewWallet
import com.houseofrafa.cashizard.domain.model.WalletType
import com.houseofrafa.cashizard.domain.repository.AccountRepository
import com.houseofrafa.cashizard.domain.repository.WalletRepository
import com.houseofrafa.cashizard.domain.session.SpaceSession
import com.houseofrafa.cashizard.presentation.common.AmountBuffer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * The "New Wallet" form. Icon and type are chosen on pushed stack entries, so
 * the form state lives here and survives those pushes.
 */
class AddWalletViewModel(
    private val walletRepository: WalletRepository,
    private val accountRepository: AccountRepository,
    private val spaceSession: SpaceSession,
) : ViewModel() {

    private val _state = MutableStateFlow(AddWalletUiState())
    val state: StateFlow<AddWalletUiState> = _state.asStateFlow()

    init {
        loadAccounts()
    }

    fun onRequestClose() = _state.update { it.copy(closeRequested = true) }

    fun onNameChange(value: String) =
        _state.update { it.copy(name = value, errorMessage = null) }

    fun onIconChange(icon: String) = _state.update { it.copy(icon = icon) }

    fun onTypeChange(type: WalletType) = _state.update {
        // Goal and budget amounts are not interchangeable, and the other types
        // must carry neither, so drop it whenever the type changes.
        it.copy(type = type, typeAmount = AmountBuffer.Empty, errorMessage = null)
    }

    fun onTypeAmountChange(value: String) =
        _state.update { it.copy(typeAmount = AmountBuffer.of(value)) }

    /** A wallet can start overdrawn, so this field accepts a negative. */
    fun onStartingBalanceChange(value: String) =
        _state.update { it.copy(startingBalance = AmountBuffer.of(value, allowNegative = true)) }

    /**
     * Flips the starting balance's sign. Soft keyboards do not reliably offer a
     * minus key, so the sign is a control rather than something to be typed.
     */
    fun onToggleStartingBalanceSign() = _state.update {
        it.copy(startingBalance = it.startingBalance.negated())
    }

    /** Linking an account is optional: tapping the linked one unlinks it again. */
    fun onAccountToggled(accountId: String) = _state.update {
        it.copy(accountId = if (it.accountId == accountId) null else accountId)
    }

    fun onSave() {
        val current = _state.value
        if (!current.canSave) return

        _state.update { it.copy(saving = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                walletRepository.createWallet(
                    NewWallet(
                        spaceId = spaceSession.requireSpaceId(),
                        accountId = current.accountId,
                        name = current.name.trim(),
                        icon = current.icon,
                        type = current.type,
                        goalAmount = current.typeAmount.toMoney()
                            .takeIf { current.needsGoalAmount },
                        annualBudget = current.typeAmount.toMoney()
                            .takeIf { current.needsAnnualBudget },
                        initialBalance = current.startingBalance.toMoney(),
                        sortOrder = 0,
                    ),
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

    private fun loadAccounts() {
        viewModelScope.launch {
            val accounts = runCatching { accountRepository.getAccounts(spaceSession.requireSpaceId()) }
                .getOrDefault(emptyList())
            // Nothing is preselected: linking an account is optional, and
            // guessing one would quietly file the wallet somewhere unintended.
            _state.update { it.copy(accounts = accounts, loading = false) }
        }
    }
}
