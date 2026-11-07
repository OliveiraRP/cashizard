package com.houseofrafa.cashizard.presentation.feature.settings.rules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.houseofrafa.cashizard.domain.model.NewBankCategorizationRule
import com.houseofrafa.cashizard.domain.model.TransactionType
import com.houseofrafa.cashizard.domain.repository.BankCategorizationRuleRepository
import com.houseofrafa.cashizard.domain.repository.CategoryRepository
import com.houseofrafa.cashizard.domain.repository.WalletRepository
import com.houseofrafa.cashizard.domain.session.SpaceSession
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Settings' "Automatic Categorization Rules" sheet. */
class EditRulesViewModel(
    private val bankCategorizationRuleRepository: BankCategorizationRuleRepository,
    private val categoryRepository: CategoryRepository,
    private val walletRepository: WalletRepository,
    private val spaceSession: SpaceSession,
) : ViewModel() {

    private val _state = MutableStateFlow(EditRulesUiState())
    val state: StateFlow<EditRulesUiState> = _state.asStateFlow()

    private val _events = Channel<EditRulesEvent>(Channel.BUFFERED)
    val events: Flow<EditRulesEvent> = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            try {
                reload()
                _state.update { it.copy(loading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, errorMessage = "Couldn't load your rules.") }
            }
        }
    }

    fun onRequestClose() = _state.update { it.copy(closeRequested = true) }

    fun startNewRule() = _state.update { it.copy(ruleForm = RuleFormUiState()) }

    /** Prefills the form from an existing rule. Returns false when the rule is unknown. */
    fun startEditingRule(ruleId: String): Boolean {
        val current = _state.value
        val row = current.rules.firstOrNull { it.ruleId == ruleId } ?: return false
        val type = current.groupOf(row.categoryId)?.type ?: TransactionType.EXPENSE
        _state.update {
            it.copy(
                ruleForm = RuleFormUiState(
                    ruleId = row.ruleId,
                    matchText = row.matchText,
                    type = type,
                    categoryId = row.categoryId,
                    walletId = row.walletId,
                ),
            )
        }
        return true
    }

    fun onMatchTextChange(value: String) = updateForm { it.copy(matchText = value, errorMessage = null) }

    fun onTypeChange(type: TransactionType) = updateForm {
        // A category from the previous type would no longer be selectable.
        it.copy(type = type, categoryId = null, errorMessage = null)
    }

    fun onCategorySelected(categoryId: String) = updateForm { it.copy(categoryId = categoryId, errorMessage = null) }

    fun onWalletSelected(walletId: String) = updateForm { it.copy(walletId = walletId, errorMessage = null) }

    fun onSaveRule() {
        val form = _state.value.ruleForm ?: return
        val categoryId = form.categoryId
        val walletId = form.walletId
        if (!form.canSave || categoryId == null || walletId == null) return

        updateForm { it.copy(saving = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                bankCategorizationRuleRepository.upsertRule(
                    NewBankCategorizationRule(
                        spaceId = spaceSession.requireSpaceId(),
                        matchText = form.matchText,
                        categoryId = categoryId,
                        walletId = walletId,
                    ),
                )
                reload()
                _events.send(EditRulesEvent.FormSaved)
            } catch (e: Exception) {
                updateForm { it.copy(saving = false, errorMessage = "Couldn't save. Please try again.") }
            }
        }
    }

    fun onDeleteRule(ruleId: String) {
        viewModelScope.launch {
            try {
                bankCategorizationRuleRepository.deleteRule(ruleId)
                reload()
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = "Couldn't delete that rule.") }
            }
        }
    }

    private fun updateForm(transform: (RuleFormUiState) -> RuleFormUiState) =
        _state.update { it.copy(ruleForm = it.ruleForm?.let(transform)) }

    private suspend fun reload() {
        val spaceId = spaceSession.requireSpaceId()
        val rules = bankCategorizationRuleRepository.getRules(spaceId)
        val groups = categoryRepository.getGroupsWithCategories(spaceId)
        val wallets = walletRepository.getWallets(spaceId, includeArchived = true)

        val categoriesById = groups.flatMap { it.categories }.associateBy { it.id }
        val walletsById = wallets.associateBy { it.id }

        _state.update {
            it.copy(
                rules = rules.map { rule ->
                    RuleRow(
                        ruleId = rule.id,
                        matchText = rule.matchText,
                        categoryId = rule.categoryId,
                        categoryName = categoriesById[rule.categoryId]?.name ?: "Unknown category",
                        walletId = rule.walletId,
                        walletName = walletsById[rule.walletId]?.name ?: "Unknown wallet",
                    )
                },
                categoryGroups = groups,
                wallets = wallets,
            )
        }
    }
}

/** One-shot outcomes the sheet content turns into a pop. */
sealed interface EditRulesEvent {
    data object FormSaved : EditRulesEvent
}
