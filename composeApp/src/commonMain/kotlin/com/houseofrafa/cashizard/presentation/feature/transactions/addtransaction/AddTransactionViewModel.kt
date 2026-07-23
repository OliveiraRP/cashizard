package com.houseofrafa.cashizard.presentation.feature.transactions.addtransaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.houseofrafa.cashizard.domain.model.NewCategory
import com.houseofrafa.cashizard.domain.model.NewCategoryGroup
import com.houseofrafa.cashizard.domain.model.NewRecurringRule
import com.houseofrafa.cashizard.domain.model.NewTransaction
import com.houseofrafa.cashizard.domain.model.TransactionType
import com.houseofrafa.cashizard.domain.model.UpdateCategory
import com.houseofrafa.cashizard.domain.model.UpdateCategoryGroup
import com.houseofrafa.cashizard.domain.model.UpdateTransaction
import com.houseofrafa.cashizard.domain.repository.CategoryRepository
import com.houseofrafa.cashizard.domain.repository.RecurringRuleRepository
import com.houseofrafa.cashizard.domain.repository.TransactionRepository
import com.houseofrafa.cashizard.domain.repository.WalletRepository
import com.houseofrafa.cashizard.domain.session.SpaceSession
import com.houseofrafa.cashizard.domain.usecase.CreateTransaction
import com.houseofrafa.cashizard.presentation.designsystem.tokens.CategoryColors
import com.houseofrafa.cashizard.presentation.feature.categories.CategoryFormUiState
import com.houseofrafa.cashizard.presentation.feature.categories.GroupFormUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn

/**
 * The add-transaction sheet's form, including the category and group forms its
 * pickers push. It owns all of that state so a pushed picker can write a
 * selection back without the form being recreated.
 */
class AddTransactionViewModel(
    private val createTransaction: CreateTransaction,
    private val transactionRepository: TransactionRepository,
    private val walletRepository: WalletRepository,
    private val categoryRepository: CategoryRepository,
    private val recurringRuleRepository: RecurringRuleRepository,
    private val spaceSession: SpaceSession,
    /** When set, the sheet edits this transaction instead of creating one. */
    private val editing: EditingTransaction? = null,
) : ViewModel() {

    private val _state = MutableStateFlow(AddTransactionUiState.from(editing))
    val state: StateFlow<AddTransactionUiState> = _state.asStateFlow()

    private val _events = Channel<AddTransactionEvent>(Channel.BUFFERED)
    val events: Flow<AddTransactionEvent> = _events.receiveAsFlow()

    init {
        loadReferenceData()
    }

    fun onRequestClose() = _state.update { it.copy(closeRequested = true) }

    // ---- category & group forms -------------------------------------------

    /** From the picker's ⋯ menu: start a blank group form. */
    fun startNewGroup() = _state.update { it.copy(groupForm = GroupFormUiState()) }

    /** From the picker's ⋯ menu: start a blank category form. */
    fun startNewCategory() = _state.update {
        it.copy(categoryForm = CategoryFormUiState.creating(it.type, it.groups))
    }

    /** Prefills the group form. Returns false when the group is unknown. */
    fun startEditingGroup(groupId: String): Boolean {
        val group = _state.value.group(groupId) ?: return false
        _state.update { it.copy(groupForm = GroupFormUiState.editing(group)) }
        return true
    }

    /** Prefills the category form. Returns false when the category is unknown. */
    fun startEditingCategory(categoryId: String): Boolean {
        val current = _state.value
        val category = current.category(categoryId) ?: return false
        val group = current.groupOf(categoryId) ?: return false
        _state.update { it.copy(categoryForm = CategoryFormUiState.editing(category, group)) }
        return true
    }

    fun onGroupNameChange(value: String) = updateGroupForm {
        it.copy(name = value, errorMessage = null)
    }

    /**
     * Expense and income share a palette, so a color picked under one survives
     * a switch to the other. Only a move to or from transfer — which has its
     * own fixed gray — forces the color back to the new type's default.
     */
    fun onGroupTypeChange(type: TransactionType) = updateGroupForm { form ->
        val stillValid = form.color in CategoryColors.paletteHexFor(type)
        form.copy(
            type = type,
            color = if (stillValid) form.color else CategoryColors.defaultHexFor(type),
            errorMessage = null,
        )
    }

    fun onGroupColorChange(hex: String) = updateGroupForm { it.copy(color = hex) }

    fun onCategoryNameChange(value: String) = updateCategoryForm {
        it.copy(name = value, errorMessage = null)
    }

    fun onCategoryGroupChange(groupId: String) =
        updateCategoryForm { it.copy(groupId = groupId, errorMessage = null) }

    fun onCategoryIconChange(icon: String) = updateCategoryForm { it.copy(icon = icon) }

    fun onCategoryExcludeChange(excluded: Boolean) =
        updateCategoryForm { it.copy(excludeFromAnalytics = excluded) }

    fun onSaveGroup() {
        val form = _state.value.groupForm ?: return
        if (!form.canSave) return

        updateGroupForm { it.copy(saving = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                // A transfer group's color is fixed, so it is taken from the
                // palette rather than from the form.
                val color = if (form.picksColor) form.color else CategoryColors.TRANSFER_HEX
                if (form.groupId == null) {
                    categoryRepository.createGroup(
                        NewCategoryGroup(
                            spaceId = spaceSession.requireSpaceId(),
                            name = form.name.trim(),
                            color = color,
                            type = form.type,
                        ),
                    )
                } else {
                    categoryRepository.updateGroup(
                        UpdateCategoryGroup(
                            id = form.groupId,
                            name = form.name.trim(),
                            color = color,
                        ),
                    )
                }
                refreshCategories()
                // The form state is left in place: the outgoing child is still
                // on screen for the pop animation, and every entry point
                // rebuilds it before pushing the form again.
                _events.send(AddTransactionEvent.FormSaved)
            } catch (e: Exception) {
                updateGroupForm {
                    it.copy(saving = false, errorMessage = "Couldn't save. Please try again.")
                }
            }
        }
    }

    fun onSaveCategory() {
        val form = _state.value.categoryForm ?: return
        val groupId = form.groupId
        if (!form.canSave || groupId == null) return

        updateCategoryForm { it.copy(saving = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                if (form.categoryId == null) {
                    categoryRepository.createCategory(
                        NewCategory(
                            groupId = groupId,
                            name = form.name.trim(),
                            icon = form.icon,
                            excludeFromAnalytics = form.excludeFromAnalytics,
                        ),
                    )
                } else {
                    categoryRepository.updateCategory(
                        UpdateCategory(
                            id = form.categoryId,
                            groupId = groupId,
                            name = form.name.trim(),
                            icon = form.icon,
                            excludeFromAnalytics = form.excludeFromAnalytics,
                        ),
                    )
                }
                refreshCategories()
                // A category's analytics-exclusion flag may have changed, so the
                // Analytics tab behind the sheet must recompute its totals.
                spaceSession.notifyDataChanged()
                _events.send(AddTransactionEvent.FormSaved)
            } catch (e: Exception) {
                updateCategoryForm {
                    it.copy(saving = false, errorMessage = "Couldn't save. Please try again.")
                }
            }
        }
    }

    private fun updateGroupForm(transform: (GroupFormUiState) -> GroupFormUiState) =
        _state.update { it.copy(groupForm = it.groupForm?.let(transform)) }

    private fun updateCategoryForm(transform: (CategoryFormUiState) -> CategoryFormUiState) =
        _state.update { it.copy(categoryForm = it.categoryForm?.let(transform)) }

    /**
     * Re-reads the groups after an edit. A category the form just moved or
     * renamed may no longer match the transaction's type, so a selection that
     * has gone stale is cleared rather than left pointing at nothing.
     */
    private suspend fun refreshCategories() {
        val groups = categoryRepository.getGroupsWithCategories(spaceSession.requireSpaceId())
        _state.update { current ->
            val stillSelectable = groups
                .filter { it.group.type == current.type }
                .any { entry -> entry.categories.any { it.id == current.categoryId } }
            current.copy(
                categoryGroups = groups,
                categoryId = current.categoryId.takeIf { stillSelectable },
            )
        }
    }

    // ---- form intents -----------------------------------------------------

    fun onTypeChange(type: TransactionType) = _state.update { current ->
        current.copy(
            type = type,
            // A category from the previous type would violate the DB's type check.
            categoryId = null,
            // Income has no source wallet; its destination is the one wallet it
            // touches, so default it to the first wallet the way expense and
            // transfer default their source. Every other type clears the
            // destination — a transfer must never default it to the source.
            toWalletId = if (type == TransactionType.INCOME) {
                current.toWalletId ?: current.wallets.firstOrNull()?.id
            } else {
                null
            },
            errorMessage = null,
        )
    }

    fun onDigit(digit: Int) = _state.update { it.copy(amount = it.amount.withDigit(digit)) }

    fun onDecimal() = _state.update { it.copy(amount = it.amount.withSeparator()) }

    fun onBackspace() = _state.update { it.copy(amount = it.amount.backspace()) }

    fun onNoteChange(note: String) = _state.update { it.copy(note = note) }

    fun onDateChange(date: LocalDate) = _state.update { it.copy(occurredOn = date) }

    fun onRepeatToggle(enabled: Boolean) = _state.update { it.copy(repeatMonthly = enabled) }

    /** Chosen from the shortcut strip on the form — nothing to pop. */
    fun onCategoryChosen(categoryId: String) =
        _state.update { it.copy(categoryId = categoryId, errorMessage = null) }

    /**
     * Chosen from the pushed "See all" picker. A category that is not already on
     * the strip is promoted to its front so the pick is visible without
     * scrolling; one already on the strip is just selected in place.
     */
    fun onCategorySelected(categoryId: String) = _state.update { current ->
        val onStrip = current.categoryStrip().any { it.id == categoryId }
        val promoted = if (onStrip) {
            current.promotedCategoryIds
        } else {
            listOf(categoryId) + current.promotedCategoryIds.filterNot { it == categoryId }
        }
        current.copy(
            categoryId = categoryId,
            promotedCategoryIds = promoted,
            errorMessage = null,
        )
    }

    fun onWalletSelected(target: WalletTarget, walletId: String) = _state.update {
        when (target) {
            WalletTarget.From -> it.copy(fromWalletId = walletId, errorMessage = null)
            WalletTarget.To -> it.copy(toWalletId = walletId, errorMessage = null)
        }
    }

    fun onSave() {
        val current = _state.value
        if (!current.canSave) return

        val editingId = editing?.id
        _state.update { it.copy(saving = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                if (editingId == null) {
                    createTransaction(current.toNewTransaction())
                    if (current.repeatMonthly) createMonthlyRule(current)
                } else {
                    transactionRepository.updateTransaction(
                        UpdateTransaction(
                            id = editingId,
                            type = current.type,
                            amount = current.amount.toMoney(),
                            fromWalletId = current.effectiveFromWalletId,
                            toWalletId = current.effectiveToWalletId,
                            categoryId = current.categoryId,
                            occurredOn = current.occurredOn,
                            note = current.note.takeIf { it.isNotBlank() },
                        ),
                    )
                }

                spaceSession.notifyDataChanged()
                onRequestClose()
            } catch (e: IllegalArgumentException) {
                // Thrown by CreateTransaction's own validation.
                _state.update {
                    it.copy(saving = false, errorMessage = e.message ?: "That doesn't look right.")
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(saving = false, errorMessage = "Couldn't save. Please try again.")
                }
            }
        }
    }

    /** Deletes the transaction under edit, then closes and refreshes the tab. */
    fun onDelete() {
        val editingId = editing?.id ?: return
        if (_state.value.busy) return

        _state.update { it.copy(deleting = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                transactionRepository.deleteTransaction(editingId)
                spaceSession.notifyDataChanged()
                onRequestClose()
            } catch (e: Exception) {
                _state.update {
                    it.copy(deleting = false, errorMessage = "Couldn't delete. Please try again.")
                }
            }
        }
    }

    private fun AddTransactionUiState.toNewTransaction() = NewTransaction(
        type = type,
        amount = amount.toMoney(),
        fromWalletId = effectiveFromWalletId,
        toWalletId = effectiveToWalletId,
        categoryId = categoryId,
        occurredOn = occurredOn,
        note = note.takeIf { it.isNotBlank() },
    )

    /**
     * The transaction just saved covers this month; the rule starts next month so
     * pg_cron does not immediately materialise a duplicate.
     */
    private suspend fun createMonthlyRule(current: AddTransactionUiState) {
        recurringRuleRepository.createRule(
            NewRecurringRule(
                type = current.type,
                amount = current.amount.toMoney(),
                fromWalletId = current.effectiveFromWalletId,
                toWalletId = current.effectiveToWalletId,
                categoryId = current.categoryId,
                note = current.note.takeIf { it.isNotBlank() },
                dayOfMonth = current.occurredOn.dayOfMonth,
                // DatePeriod clamps to the target month's length, so the 31st
                // becomes the 28th/29th in February rather than overflowing.
                nextRun = current.occurredOn.plus(DatePeriod(months = 1)),
            ),
        )
    }

    private fun loadReferenceData() {
        viewModelScope.launch {
            try {
                val spaceId = spaceSession.requireSpaceId()
                val wallets = walletRepository.getWallets(spaceId)
                val groups = categoryRepository.getGroupsWithCategories(spaceId)
                _state.update {
                    it.copy(
                        wallets = wallets,
                        categoryGroups = groups,
                        // Preselect the first wallet only when creating; an edit
                        // already carries the transaction's own wallets.
                        fromWalletId = it.fromWalletId
                            ?: wallets.firstOrNull()?.id.takeIf { editing == null },
                        loading = false,
                    )
                }
                // Usage ranking is a nicety, not a blocker: it loads after the
                // form is already interactive and a failure just leaves the
                // strip in catalog order.
                loadCategoryUsage()
            } catch (e: Exception) {
                _state.update {
                    it.copy(loading = false, errorMessage = "Couldn't load your wallets.")
                }
            }
        }
    }

    /**
     * Counts how often each category has been used over the last year to rank
     * the shortcut strip. Read-only and best-effort — computed from the
     * transactions the app already stores, so it needs no new schema.
     */
    private suspend fun loadCategoryUsage() {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val from = today.minus(DatePeriod(years = 1))
        val usage = runCatching {
            transactionRepository
                .getTransactions(spaceSession.requireSpaceId(), from, today)
                .mapNotNull { it.categoryId }
                .groupingBy { it }
                .eachCount()
        }.getOrDefault(emptyMap())
        if (usage.isNotEmpty()) _state.update { it.copy(categoryUsage = usage) }
    }
}
