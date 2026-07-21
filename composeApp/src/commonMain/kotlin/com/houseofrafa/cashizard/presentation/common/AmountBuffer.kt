package com.houseofrafa.cashizard.presentation.common

import com.houseofrafa.cashizard.domain.model.Money
import kotlin.jvm.JvmInline

/**
 * The digits typed on the keypad, held as text so the display can show exactly
 * what was entered ("0", "12", "12,5") rather than a normalised figure.
 *
 * Kept as a value class over the raw string: at most one separator and at most
 * two decimals, which makes every reachable state a valid amount.
 */
@JvmInline
value class AmountBuffer private constructor(private val raw: String) {

    /** What the keypad readout shows; empty input reads as a plain zero. */
    val display: String get() = raw.ifEmpty { "0" }

    /** Exactly what was typed — text fields show their placeholder when empty. */
    val text: String get() = raw

    val isEmpty: Boolean get() = raw.isEmpty()

    fun withDigit(digit: Int): AmountBuffer {
        if (digit !in 0..9) return this
        val separatorIndex = raw.indexOf(SEPARATOR)
        // Already two decimals typed.
        if (separatorIndex >= 0 && raw.length - separatorIndex > 2) return this
        // Suppress leading zeros: "0" then "5" is 5, not 05.
        if (raw == "0") return AmountBuffer(digit.toString())
        if (raw.length >= MAX_LENGTH) return this
        return AmountBuffer(raw + digit)
    }

    fun withSeparator(): AmountBuffer = when {
        raw.contains(SEPARATOR) -> this
        raw.isEmpty() -> AmountBuffer("0$SEPARATOR")
        else -> AmountBuffer(raw + SEPARATOR)
    }

    fun backspace(): AmountBuffer =
        if (raw.isEmpty()) this else AmountBuffer(raw.dropLast(1))

    val isNegative: Boolean get() = raw.startsWith(MINUS)

    /** Flips the sign, for fields where a control sets it rather than the keyboard. */
    fun negated(): AmountBuffer =
        if (isNegative) AmountBuffer(raw.drop(1)) else AmountBuffer(MINUS + raw)

    /** Exact cents — parsed digit-wise so no floating point is involved. */
    fun toMoney(): Money {
        if (raw.isEmpty()) return Money.Zero
        val negative = raw.startsWith(MINUS)
        val digits = if (negative) raw.drop(1) else raw
        val separatorIndex = digits.indexOf(SEPARATOR)
        val whole = if (separatorIndex >= 0) digits.substring(0, separatorIndex) else digits
        val fraction = if (separatorIndex >= 0) digits.substring(separatorIndex + 1) else ""
        val euros = whole.toLongOrNull() ?: 0L
        val cents = fraction.padEnd(2, '0').take(2).toLongOrNull() ?: 0L
        val total = euros * 100 + cents
        return Money(if (negative) -total else total)
    }

    companion object {
        const val SEPARATOR = ','
        private const val MAX_LENGTH = 12

        val Empty = AmountBuffer("")

        const val MINUS = '-'

        /**
         * From free typing rather than the keypad: keeps digits and at most one
         * separator with two decimals, so a text field cannot reach a state the
         * keypad could not.
         *
         * [allowNegative] is for fields that may legitimately go below zero — a
         * wallet can open overdrawn, but a goal or annual budget cannot, and the
         * schema checks both are positive.
         */
        /** A stored amount as an editable buffer, e.g. 240000 cents → "2400,00". */
        fun of(money: Money): AmountBuffer {
            val cents = money.cents
            val negative = cents < 0
            val abs = if (negative) -cents else cents
            val whole = abs / 100
            val fraction = (abs % 100).toString().padStart(2, '0')
            val sign = if (negative) MINUS.toString() else ""
            return of("$sign$whole$SEPARATOR$fraction", allowNegative = negative)
        }

        fun of(text: String, allowNegative: Boolean = false): AmountBuffer {
            val builder = StringBuilder()
            var seenSeparator = false
            var decimals = 0
            // Only a leading sign counts; a stray minus mid-number is dropped.
            if (allowNegative && (text.startsWith(MINUS) || text.startsWith('−'))) {
                builder.append(MINUS)
            }
            for (char in text) {
                when {
                    char.isDigit() && seenSeparator && decimals >= 2 -> continue
                    char.isDigit() -> {
                        if (builder.length >= MAX_LENGTH) continue
                        builder.append(char)
                        if (seenSeparator) decimals++
                    }

                    (char == SEPARATOR || char == '.') && !seenSeparator -> {
                        // "," or "-," needs its leading zero: ",5" reads as 0,5.
                        if (builder.isEmpty() || builder.toString() == MINUS.toString()) {
                            builder.append('0')
                        }
                        builder.append(SEPARATOR)
                        seenSeparator = true
                    }
                }
            }
            return AmountBuffer(builder.toString())
        }
    }
}

/**
 * A stored amount as an editable buffer. Formatted money ("2.400,00 EUR") would
 * confuse [AmountBuffer.of], so the digits are rebuilt from the raw cents.
 */
fun Money.toAmountBuffer(): AmountBuffer {
    val whole = cents / 100
    val fraction = (cents % 100).toString().padStart(2, '0')
    return AmountBuffer.of("$whole,$fraction")
}