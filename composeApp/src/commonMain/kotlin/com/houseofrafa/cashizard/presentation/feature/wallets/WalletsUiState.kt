package com.houseofrafa.cashizard.presentation.feature.wallets

import com.houseofrafa.cashizard.domain.usecase.WalletsOverview

data class WalletsUiState(
    val overview: WalletsOverview? = null,
    val loading: Boolean = true,
    val errorMessage: String? = null,
)
