package com.houseofrafa.feature.wallet.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.houseofrafa.feature.wallet.domain.model.Account
import com.houseofrafa.feature.wallet.domain.model.WalletType
import com.houseofrafa.feature.wallet.domain.repository.WalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WalletsUiState(
    val isLoading: Boolean = true,
    val accounts: List<Account> = emptyList(),
    val netWorth: Double = 0.0,
    val investmentsTotal: Double = 0.0,
    val errorMessage: String? = null,
)

class WalletsViewModel(
    private val walletRepository: WalletRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WalletsUiState())
    val uiState: StateFlow<WalletsUiState> = _uiState.asStateFlow()

    init {
        loadWallets()
    }

    fun loadWallets() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            walletRepository.getAccounts()
                .onSuccess { accounts ->
                    val allWallets = accounts.flatMap { it.wallets }
                    val netWorth = allWallets
                        .filter { !it.archived && it.includeNetWorth }
                        .sumOf { it.balance }
                    val investmentsTotal = allWallets
                        .filter { !it.archived && it.walletType == WalletType.INVESTMENTS }
                        .sumOf { it.balance }
                    _uiState.update {
                        it.copy(
                            isLoading        = false,
                            accounts         = accounts,
                            netWorth         = netWorth,
                            investmentsTotal = investmentsTotal,
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Unknown error") }
                }
        }
    }
}
