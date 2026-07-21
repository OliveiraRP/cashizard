package com.houseofrafa.cashizard.domain.model

data class NewAccount(
    val spaceId: String,
    val name: String,
    val icon: String = "bank",
    val sortOrder: Int = 0,
)

data class UpdateAccount(
    val id: String,
    val name: String,
)
