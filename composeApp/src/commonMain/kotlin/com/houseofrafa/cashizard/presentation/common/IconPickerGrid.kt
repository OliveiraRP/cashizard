package com.houseofrafa.cashizard.presentation.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme
import com.houseofrafa.cashizard.presentation.designsystem.components.IconDisc
import com.houseofrafa.cashizard.presentation.designsystem.components.IconDiscStyle
import com.houseofrafa.cashizard.presentation.designsystem.components.SearchField
import com.houseofrafa.cashizard.presentation.designsystem.tokens.iconFor
import com.houseofrafa.cashizard.presentation.designsystem.tokens.icons

/**
 * The searchable icon registry as a grid of discs, with [selected] filled in.
 * Shared by the wallet and category forms, which both pick from the same
 * registry of DB icon names.
 */
@Composable
fun IconPickerGrid(
    selected: String,
    onIconSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = CashizardTheme.colors.accent,
) {
    val dimens = CashizardTheme.dimens
    var query by remember { mutableStateOf("") }

    val names = icons.keys
        .filter { query.isBlank() || it.contains(query, ignoreCase = true) }
        .sorted()

    Column(modifier.fillMaxSize()) {
        SearchField(
            value = query,
            onValueChange = { query = it },
            placeholder = "Search icons",
            modifier = Modifier.padding(
                start = dimens.listPadding,
                end = dimens.listPadding,
                top = dimens.space20,
            ),
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(COLUMNS),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = dimens.listPadding,
                end = dimens.listPadding,
                top = dimens.space20,
                bottom = dimens.space32,
            ),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            items(names, key = { it }) { name ->
                Box(contentAlignment = Alignment.Center) {
                    IconDisc(
                        icon = iconFor(name),
                        color = color,
                        style = if (name == selected) IconDiscStyle.Solid else IconDiscStyle.Tinted,
                        size = dimens.iconDiscChip,
                        iconSize = 20.dp,
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onIconSelected(name) },
                        ),
                    )
                }
            }
        }
    }
}

private const val COLUMNS = 6
