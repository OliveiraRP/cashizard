package com.houseofrafa.cashizard.presentation.feature.analytics.filtercategories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.houseofrafa.cashizard.domain.repository.CategoryRepository
import com.houseofrafa.cashizard.domain.session.SpaceSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * The bulk "Filter categories" sheet. Loads every group with its categories,
 * pre-checks the ones currently counted in analytics, and on save writes back
 * only the categories whose flag changed.
 */
class FilterCategoriesViewModel(
    private val categoryRepository: CategoryRepository,
    private val spaceSession: SpaceSession,
) : ViewModel() {

    private val _state = MutableStateFlow(FilterCategoriesUiState())
    val state: StateFlow<FilterCategoriesUiState> = _state.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            try {
                val groups = categoryRepository.getGroupsWithCategories(spaceSession.requireSpaceId())
                val checked = groups
                    .flatMap { it.categories }
                    .filterNot { it.excludeFromAnalytics }
                    .map { it.id }
                    .toSet()
                _state.update { it.copy(groups = groups, checkedIds = checked, loading = false) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(loading = false, errorMessage = "Couldn't load your categories.")
                }
            }
        }
    }

    fun onToggle(categoryId: String) = _state.update {
        val checked = if (categoryId in it.checkedIds) {
            it.checkedIds - categoryId
        } else {
            it.checkedIds + categoryId
        }
        it.copy(checkedIds = checked)
    }

    fun onRequestClose() = _state.update { it.copy(closeRequested = true) }

    fun onSave() {
        val current = _state.value
        if (!current.canSave) return

        val changes = current.changes
        if (changes.isEmpty()) {
            onRequestClose()
            return
        }

        _state.update { it.copy(saving = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                categoryRepository.setExcludedFromAnalytics(changes)
                // Bumps the version the Analytics tab watches, so it recomputes.
                spaceSession.notifyDataChanged()
                onRequestClose()
            } catch (e: Exception) {
                _state.update {
                    it.copy(saving = false, errorMessage = "Couldn't save. Please try again.")
                }
            }
        }
    }
}
