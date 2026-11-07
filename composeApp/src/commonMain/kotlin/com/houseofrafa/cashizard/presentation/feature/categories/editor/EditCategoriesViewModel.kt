package com.houseofrafa.cashizard.presentation.feature.categories.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.houseofrafa.cashizard.domain.model.NewCategory
import com.houseofrafa.cashizard.domain.model.NewCategoryGroup
import com.houseofrafa.cashizard.domain.model.TransactionType
import com.houseofrafa.cashizard.domain.model.UpdateCategory
import com.houseofrafa.cashizard.domain.model.UpdateCategoryGroup
import com.houseofrafa.cashizard.domain.repository.CategoryRepository
import com.houseofrafa.cashizard.domain.session.SpaceSession
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

/**
 * Settings' standalone "Edit Categories" sheet. Owns the same group/category
 * CRUD the Add-Transaction sheet's category picker has, kept separate rather
 * than shared since the two flows have otherwise unrelated state.
 */
class EditCategoriesViewModel(
    private val categoryRepository: CategoryRepository,
    private val spaceSession: SpaceSession,
) : ViewModel() {

    private val _state = MutableStateFlow(EditCategoriesUiState())
    val state: StateFlow<EditCategoriesUiState> = _state.asStateFlow()

    private val _events = Channel<EditCategoriesEvent>(Channel.BUFFERED)
    val events: Flow<EditCategoriesEvent> = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            try {
                val groups = categoryRepository.getGroupsWithCategories(spaceSession.requireSpaceId())
                _state.update { it.copy(categoryGroups = groups, loading = false) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(loading = false, errorMessage = "Couldn't load your categories.")
                }
            }
        }
    }

    fun onRequestClose() = _state.update { it.copy(closeRequested = true) }

    /** From the "⋯" menu: start a blank group form. */
    fun startNewGroup() = _state.update { it.copy(groupForm = GroupFormUiState()) }

    /** From the "⋯" menu: start a blank category form, defaulting into expense. */
    fun startNewCategory() = _state.update {
        it.copy(categoryForm = CategoryFormUiState.creating(TransactionType.EXPENSE, it.groups))
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
                        UpdateCategoryGroup(id = form.groupId, name = form.name.trim(), color = color),
                    )
                }
                refreshCategories()
                _events.send(EditCategoriesEvent.FormSaved)
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
                _events.send(EditCategoriesEvent.FormSaved)
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

    private suspend fun refreshCategories() {
        val groups = categoryRepository.getGroupsWithCategories(spaceSession.requireSpaceId())
        _state.update { it.copy(categoryGroups = groups) }
    }
}

/** One-shot outcomes the sheet content turns into a pop. */
sealed interface EditCategoriesEvent {
    data object FormSaved : EditCategoriesEvent
}
