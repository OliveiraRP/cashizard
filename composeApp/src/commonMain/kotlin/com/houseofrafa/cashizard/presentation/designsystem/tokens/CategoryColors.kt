package com.houseofrafa.cashizard.presentation.designsystem.tokens

import androidx.compose.ui.graphics.Color
import com.houseofrafa.cashizard.domain.model.TransactionType

/**
 * Fixed category-group color palettes, taken from `Budget Manager.dc.html`
 * (`groupPickerVals`). Groups store a hex string in the DB; the color picker
 * offers exactly these values.
 *
 * Expense and income share one 16-color palette. Transfer groups are always
 * [transfer] gray with the `arrow-right-left` icon and are not user-pickable.
 */
object CategoryColors {

    /**
     * The palettes are declared as hex because that is what the DB stores and
     * what the pickers write back; the [Color] lists below are derived so the
     * two can never drift apart.
     */

    /** The 16 shared palette colors, in the design's order (4 rows of 4). */
    val sharedHex: List<String> = listOf(
        "#FF453A", // red
        "#FF9F0A", // orange
        "#FFD60A", // yellow
        "#D8B26B", // sand
        "#A8D129", // lime
        "#32D74A", // green
        "#00A88E", // teal
        "#64D2FF", // sky
        "#0A84FF", // blue
        "#BF5AF2", // purple
        "#FF2D92", // magenta
        "#FF8DAB", // rose
        "#D0BCFF", // lilac
        "#A8354F", // wine
        "#C96F4A", // clay
        "#A2845E", // brown
    )

    /** The single transfer color — always gray, never user-pickable. */
    const val TRANSFER_HEX: String = "#98989D"

    /**
     * The literal transfer gray. Declared before the derived palettes because
     * [parse] falls back to it while those are still being initialised.
     */
    private val fallback = Color(0xFF98989D)

    val shared: List<Color> = sharedHex.map(::parse)

    val transfer: Color = parse(TRANSFER_HEX)

    /** Palette to offer for a given group type (transfer returns just gray). */
    fun paletteFor(groupType: TransactionType): List<Color> =
        if (isPickable(groupType)) shared else listOf(transfer)

    /** The same palette as hex, for writing a picked color back to the DB. */
    fun paletteHexFor(groupType: TransactionType): List<String> =
        if (isPickable(groupType)) sharedHex else listOf(TRANSFER_HEX)

    /** The color a brand-new group of this type starts on, as the design sets it. */
    fun defaultHexFor(groupType: TransactionType): String = when (groupType) {
        TransactionType.EXPENSE -> sharedHex[2]
        TransactionType.INCOME -> sharedHex[0]
        TransactionType.TRANSFER -> TRANSFER_HEX
    }

    /** Transfer groups are fixed gray, so they offer no choice at all. */
    fun isPickable(groupType: TransactionType): Boolean = groupType != TransactionType.TRANSFER

    /**
     * Parse a stored `#RRGGBB` hex string into a [Color]. Falls back to gray for
     * null/blank/malformed values so the UI never crashes on bad data.
     */
    fun parse(hex: String?): Color {
        if (hex.isNullOrBlank()) return fallback
        val cleaned = hex.trim().removePrefix("#")
        val rgb = when (cleaned.length) {
            6 -> cleaned.toLongOrNull(16)
            8 -> cleaned.toLongOrNull(16) // ARGB
            else -> null
        } ?: return fallback
        return if (cleaned.length == 6) {
            Color(0xFF000000 or rgb)
        } else {
            Color(rgb)
        }
    }
}
