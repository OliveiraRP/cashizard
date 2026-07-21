package com.houseofrafa.cashizard.domain.model

import kotlin.jvm.JvmInline
import kotlin.math.abs
import kotlin.math.roundToLong

/**
 * Money as an integer number of cents. Value class over [Long] — no floating
 * point rounding in the model. Formatting to European EUR lives in the design
 * system (see `Money.formatEur`).
 */
@JvmInline
value class Money(val cents: Long) {

    val isNegative: Boolean get() = cents < 0
    val isZero: Boolean get() = cents == 0L

    operator fun plus(other: Money): Money = Money(cents + other.cents)
    operator fun minus(other: Money): Money = Money(cents - other.cents)
    operator fun unaryMinus(): Money = Money(-cents)
    operator fun compareTo(other: Money): Int = cents.compareTo(other.cents)

    fun abs(): Money = Money(abs(cents))

    /** Whole and fractional parts of the absolute value, for formatters. */
    val wholeEuros: Long get() = abs(cents) / 100
    val remainderCents: Int get() = (abs(cents) % 100).toInt()

    companion object {
        val Zero: Money = Money(0)

        /** From a decimal euro amount (e.g. 12.34 -> 1234 cents). */
        fun ofEuros(euros: Double): Money = Money((euros * 100).roundToLong())

        /** From a decimal string using '.' as decimal separator, e.g. "12.34". */
        fun ofEurosString(value: String): Money? {
            val d = value.trim().toDoubleOrNull() ?: return null
            return ofEuros(d)
        }
    }
}
