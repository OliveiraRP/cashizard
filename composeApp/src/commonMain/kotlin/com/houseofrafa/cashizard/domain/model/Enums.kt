package com.houseofrafa.cashizard.domain.model

/** Direction/semantics of a transaction (and of a category group). */
enum class TransactionType(val wire: String) {
    EXPENSE("expense"),
    INCOME("income"),
    TRANSFER("transfer");

    companion object {
        fun fromWire(value: String): TransactionType =
            entries.firstOrNull { it.wire == value }
                ?: error("Unknown transaction type: $value")
    }
}

/** Wallet behavior. See the schema's chk_type_fields for the field rules. */
enum class WalletType(val wire: String) {
    EXPENSE("expense"),
    GOAL("goal"),
    BUDGET("budget"),
    SAVINGS("savings"),
    INVESTMENT("investment");

    /** Savings & investment wallets contribute to the space's savings total. */
    val countsTowardSavings: Boolean get() = this == SAVINGS || this == INVESTMENT

    companion object {
        fun fromWire(value: String): WalletType =
            entries.firstOrNull { it.wire == value }
                ?: error("Unknown wallet type: $value")
    }
}

/** A member's role within a space. */
enum class SpaceRole(val wire: String) {
    OWNER("owner"),
    MEMBER("member");

    companion object {
        fun fromWire(value: String): SpaceRole =
            entries.firstOrNull { it.wire == value } ?: MEMBER
    }
}
