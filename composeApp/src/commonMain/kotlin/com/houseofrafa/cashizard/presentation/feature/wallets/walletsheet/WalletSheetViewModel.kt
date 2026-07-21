package com.houseofrafa.cashizard.presentation.feature.wallets.walletsheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.houseofrafa.cashizard.domain.model.UpdateAccount
import com.houseofrafa.cashizard.domain.model.UpdateWallet
import com.houseofrafa.cashizard.domain.model.WalletType
import com.houseofrafa.cashizard.domain.repository.AccountRepository
import com.houseofrafa.cashizard.domain.repository.WalletRepository
import com.houseofrafa.cashizard.domain.session.SpaceSession
import com.houseofrafa.cashizard.domain.usecase.GetTransactionFeed
import com.houseofrafa.cashizard.domain.usecase.GetWalletsOverview
import com.houseofrafa.cashizard.presentation.common.AmountBuffer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn

/**
 * State for the wallet page sheet: the wallets overview, the space's feed, and
 * whichever of the two edit forms is currently open.
 */
class WalletSheetViewModel(
    private val walletRepository: WalletRepository,
    private val accountRepository: AccountRepository,
    private val getWalletsOverview: GetWalletsOverview,
    private val getTransactionFeed: GetTransactionFeed,
    private val spaceSession: SpaceSession,
) : ViewModel() {

    private val _state = MutableStateFlow(WalletSheetUiState())
    val state: StateFlow<WalletSheetUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun onRequestClose() = _state.update { it.copy(closeRequested = true) }

    /** Prefills the wallet form. Returns false when the wallet is not loaded yet. */
    fun startEditingWallet(walletId: String): Boolean {
        val wallet = _state.value.wallet(walletId) ?: return false
        _state.update { it.copy(editForm = EditWalletUiState.from(wallet)) }
        return true
    }

    /** Prefills the account form. Returns false when the account is not loaded yet. */
    fun startEditingAccount(accountId: String): Boolean {
        val account = _state.value.account(accountId) ?: return false
        _state.update { it.copy(accountForm = EditAccountUiState.from(account)) }
        return true
    }

    // ---- wallet form ------------------------------------------------------

    fun onNameChange(value: String) = updateForm { it.copy(name = value, errorMessage = null) }

    fun onIconChange(icon: String) = updateForm { it.copy(icon = icon) }

    fun onTypeChange(type: WalletType) = updateForm {
        // Goal and budget amounts are not interchangeable, and the other types
        // carry neither, so the amount is dropped whenever the type changes.
        it.copy(type = type, typeAmount = AmountBuffer.Empty, errorMessage = null)
    }

    fun onTypeAmountChange(value: String) =
        updateForm { it.copy(typeAmount = AmountBuffer.of(value)) }

    /** Linking an account is optional: tapping the linked one unlinks it again. */
    fun onAccountToggled(accountId: String) = updateForm {
        it.copy(accountId = if (it.accountId == accountId) null else accountId)
    }

    fun onSaveEdit() {
        val form = _state.value.editForm ?: return
        if (!form.canSave) return

        updateForm { it.copy(saving = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                walletRepository.updateWallet(
                    UpdateWallet(
                        id = form.walletId,
                        accountId = form.accountId,
                        name = form.name.trim(),
                        icon = form.icon,
                        type = form.type,
                        goalAmount = form.typeAmount.toMoney().takeIf { form.needsGoalAmount },
                        annualBudget = form.typeAmount.toMoney().takeIf { form.needsAnnualBudget },
                    ),
                )
                savedAndClose()
            } catch (e: Exception) {
                updateForm {
                    it.copy(saving = false, errorMessage = "Couldn't save. Please try again.")
                }
            }
        }
    }

    fun onArchive() {
        val form = _state.value.editForm ?: return
        if (form.busy) return

        updateForm { it.copy(archiving = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                walletRepository.setArchived(form.walletId, archived = true)
                savedAndClose()
            } catch (e: Exception) {
                updateForm {
                    it.copy(archiving = false, errorMessage = "Couldn't archive. Please try again.")
                }
            }
        }
    }

    private fun updateForm(transform: (EditWalletUiState) -> EditWalletUiState) =
        _state.update { it.copy(editForm = it.editForm?.let(transform)) }

    // ---- account form -----------------------------------------------------

    fun onAccountNameChange(value: String) =
        updateAccountForm { it.copy(name = value, errorMessage = null) }

    fun onSaveAccount() {
        val form = _state.value.accountForm ?: return
        if (!form.canSave) return

        updateAccountForm { it.copy(saving = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                accountRepository.updateAccount(
                    UpdateAccount(id = form.accountId, name = form.name.trim()),
                )
                savedAndClose()
            } catch (e: Exception) {
                updateAccountForm {
                    it.copy(saving = false, errorMessage = "Couldn't save. Please try again.")
                }
            }
        }
    }

    /**
     * "Archives" an account by deleting it. There is no archived flag on
     * accounts; the wallets FK is `on delete set null`, so its wallets are left
     * standalone rather than removed, which is the intended behaviour.
     */
    fun onArchiveAccount() {
        val form = _state.value.accountForm ?: return
        if (form.busy) return

        updateAccountForm { it.copy(archiving = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                accountRepository.deleteAccount(form.accountId)
                savedAndClose()
            } catch (e: Exception) {
                updateAccountForm {
                    it.copy(archiving = false, errorMessage = "Couldn't archive. Please try again.")
                }
            }
        }
    }

    private fun updateAccountForm(transform: (EditAccountUiState) -> EditAccountUiState) =
        _state.update { it.copy(accountForm = it.accountForm?.let(transform)) }

    private fun savedAndClose() {
        spaceSession.notifyDataChanged()
        onRequestClose()
    }

    private fun load() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, errorMessage = null) }
            try {
                val spaceId = spaceSession.requireSpaceId()
                val overview = getWalletsOverview(spaceId)
                // A wide window standing in for "all time": transactions are
                // dated by the user, so this comfortably brackets any real one.
                val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                val feed = getTransactionFeed(
                    spaceId,
                    LocalDate(2000, 1, 1),
                    today.plus(DatePeriod(years = 5)),
                )
                _state.update { it.copy(overview = overview, feed = feed, loading = false) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(loading = false, errorMessage = "Couldn't load this wallet.")
                }
            }
        }
    }
}
