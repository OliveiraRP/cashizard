package com.houseofrafa.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable data object Auth    : Route
    @Serializable data object Wallets : Route
}
