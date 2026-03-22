package com.houseofrafa.core.ui

import androidx.compose.ui.graphics.Color

/** Converts a hex color string (#RRGGBB or #AARRGGBB) to a Compose [Color]. */
fun String.toComposeColor(): Color {
    val hex = removePrefix("#")
    return try {
        when (hex.length) {
            6    -> Color(("FF$hex").toLong(16))
            8    -> Color(hex.toLong(16))
            else -> Color.Gray
        }
    } catch (_: NumberFormatException) {
        Color.Gray
    }
}
