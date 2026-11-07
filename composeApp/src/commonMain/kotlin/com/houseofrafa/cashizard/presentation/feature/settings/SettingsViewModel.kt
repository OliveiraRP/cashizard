package com.houseofrafa.cashizard.presentation.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.houseofrafa.cashizard.domain.repository.AccountRepository
import com.houseofrafa.cashizard.domain.repository.AuthRepository
import com.houseofrafa.cashizard.domain.repository.BankAccountLinkRepository
import com.houseofrafa.cashizard.domain.repository.ProfileRepository
import com.houseofrafa.cashizard.domain.session.SpaceSession
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
 * The Settings tab. Loads the profile and bank links fresh whenever the active
 * space changes or anything elsewhere writes, the same way the other tabs do.
 */
class SettingsViewModel(
    private val profileRepository: ProfileRepository,
    private val bankAccountLinkRepository: BankAccountLinkRepository,
    private val accountRepository: AccountRepository,
    private val authRepository: AuthRepository,
    private val spaceSession: SpaceSession,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

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

    fun onUnlinkBankAccount(linkId: String) {
        viewModelScope.launch {
            try {
                bankAccountLinkRepository.deleteLink(linkId)
                spaceSession.notifyDataChanged()
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = "Couldn't remove that connection.") }
            }
        }
    }

    /** The root observes auth state and drops back to the auth stack on its own. */
    fun onSignOut() {
        viewModelScope.launch { authRepository.signOut() }
    }

    private fun load(spaceId: String) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, errorMessage = null) }
            try {
                val profile = profileRepository.getProfile()
                val links = bankAccountLinkRepository.getLinks(spaceId)
                val accountsById = accountRepository.getAccounts(spaceId).associateBy { it.id }

                _state.update {
                    it.copy(
                        displayName = profile.displayName,
                        bankLinks = links.map { link ->
                            BankLinkRow(
                                linkId = link.id,
                                accountName = accountsById[link.accountId]?.name ?: "Unknown account",
                                enableBankingAccountId = link.enableBankingAccountId,
                            )
                        },
                        loading = false,
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(loading = false, errorMessage = "Couldn't load settings.")
                }
            }
        }
    }
}
