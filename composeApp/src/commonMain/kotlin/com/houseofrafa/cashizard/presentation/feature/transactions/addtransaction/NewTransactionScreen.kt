package com.houseofrafa.cashizard.presentation.feature.transactions.addtransaction

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.ArrowRight
import com.composables.icons.lucide.CalendarDays
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.NotebookPen
import com.composables.icons.lucide.RefreshCw
import com.composables.icons.lucide.Wallet
import com.houseofrafa.cashizard.domain.model.TransactionType
import com.houseofrafa.cashizard.presentation.common.dayMonthLabel
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme
import com.houseofrafa.cashizard.presentation.designsystem.tokens.CategoryColors
import com.houseofrafa.cashizard.presentation.designsystem.components.AmountDisplay
import com.houseofrafa.cashizard.presentation.designsystem.components.CashSwitch
import com.houseofrafa.cashizard.presentation.designsystem.components.CategoryChip
import com.houseofrafa.cashizard.presentation.designsystem.components.CategoryChipWidth
import com.houseofrafa.cashizard.presentation.designsystem.components.FormCard
import com.houseofrafa.cashizard.presentation.designsystem.components.InlineDatePicker
import com.houseofrafa.cashizard.presentation.designsystem.components.InsetRow
import com.houseofrafa.cashizard.presentation.designsystem.components.Keypad
import com.houseofrafa.cashizard.presentation.designsystem.components.SectionHeader
import com.houseofrafa.cashizard.presentation.designsystem.components.SegmentedControl
import com.houseofrafa.cashizard.presentation.designsystem.tokens.iconFor
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

private val typeOrder = listOf(
    TransactionType.EXPENSE,
    TransactionType.INCOME,
    TransactionType.TRANSFER,
)

@Composable
fun NewTransactionScreen(
    component: AddTransactionSheetComponent,
    state: AddTransactionUiState,
    modifier: Modifier = Modifier,
) {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens
    val isTransfer = state.type == TransactionType.TRANSFER
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    var dateExpanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        SegmentedControl(
            options = listOf("Expense", "Income", "Transfer"),
            selectedIndex = typeOrder.indexOf(state.type),
            onSelect = { component.viewModel.onTypeChange(typeOrder[it]) },
            modifier = Modifier.padding(
                start = dimens.listPadding,
                end = dimens.listPadding,
                top = dimens.space20,
            ),
        )

        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
        ) {
            AmountDisplay(
                amount = state.amount.display,
                modifier = Modifier.padding(top = 22.dp, bottom = 18.dp),
            )

            // Wallet selection: one row for expense/income, two for a transfer.
            // Income lands in the destination wallet, so its single row edits
            // the "To" side; expense and a transfer's first row edit "From".
            val isIncome = state.type == TransactionType.INCOME
            val singleTarget = if (isIncome) WalletTarget.To else WalletTarget.From
            val singleWalletId = if (isIncome) state.toWalletId else state.fromWalletId
            FormCard(
                cornerRadius = dimens.radiusControl,
                modifier = Modifier.padding(horizontal = dimens.listPadding),
                rows = buildList {
                    add {
                        InsetRow(
                            label = if (isTransfer) "From" else "Wallet",
                            leadingIcon = Lucide.Wallet,
                            value = state.wallet(singleWalletId)?.name ?: "Choose",
                            showChevron = true,
                            onClick = { component.onPickWallet(singleTarget) },
                        )
                    }
                    if (isTransfer) {
                        add {
                            InsetRow(
                                label = "To",
                                leadingIcon = Lucide.ArrowRight,
                                value = state.wallet(state.toWalletId)?.name ?: "Choose",
                                showChevron = true,
                                onClick = { component.onPickWallet(WalletTarget.To) },
                            )
                        }
                    }
                },
            )

            // Transfers move money between wallets, so they carry no category.
            if (!isTransfer) {
                SectionHeader(
                    title = "Category",
                    style = CashizardTheme.typography.sectionLabelSmall,
                    actionLabel = "See all",
                    onActionClick = component::onPickCategory,
                    modifier = Modifier.padding(top = 6.dp),
                )
                CategoryStrip(component = component, state = state)
            }

            FormCard(
                cornerRadius = dimens.radiusControl,
                modifier = Modifier.padding(
                    start = dimens.listPadding,
                    end = dimens.listPadding,
                    top = 22.dp,
                ),
                rows = buildList {
                    add {
                        InsetRow(
                            label = "Date",
                            leadingIcon = Lucide.CalendarDays,
                            value = state.occurredOn.dateRowLabel(today),
                            showChevron = true,
                            onClick = { dateExpanded = !dateExpanded },
                        )
                    }
                    // Tapping the Date row reveals an inline calendar, iOS-style;
                    // picking a day applies it and collapses the calendar again.
                    if (dateExpanded) {
                        add {
                            InlineDatePicker(
                                selected = state.occurredOn,
                                onSelect = { date ->
                                    component.viewModel.onDateChange(date)
                                    dateExpanded = false
                                },
                            )
                        }
                    }
                    add {
                        NoteRow(
                            value = state.note,
                            onValueChange = component.viewModel::onNoteChange,
                        )
                    }
                    // Repeat sets up a recurring rule alongside a new transaction;
                    // it has no meaning when editing one that already exists.
                    if (!state.isEditing) {
                        add {
                            InsetRow(
                                label = "Repeat monthly",
                                subtitle = "on day ${state.occurredOn.dayOfMonth}",
                                leadingIcon = Lucide.RefreshCw,
                                minHeight = 58.dp,
                                trailing = {
                                    CashSwitch(
                                        checked = state.repeatMonthly,
                                        onCheckedChange = component.viewModel::onRepeatToggle,
                                    )
                                },
                            )
                        }
                    }
                },
            )

            if (state.isEditing) {
                DeleteTransactionButton(
                    enabled = !state.busy,
                    onClick = component.viewModel::onDelete,
                )
            }

            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage,
                    style = CashizardTheme.typography.footnote,
                    color = colors.errorText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimens.screenPadding, vertical = dimens.space12),
                )
            }

            Spacer(Modifier.height(dimens.space16))
        }

        Keypad(
            onDigit = component.viewModel::onDigit,
            onDecimal = component.viewModel::onDecimal,
            onBackspace = component.viewModel::onBackspace,
            modifier = Modifier
                .navigationBarsPadding()
                .padding(start = 10.dp, end = 10.dp, top = dimens.space16, bottom = dimens.space32),
        )
    }
}

/**
 * The Note row: a leading glyph, the "Note" label, and an inline single-line
 * field that fills the rest of the row. Tapping anywhere in the field focuses
 * it and raises the keyboard; empty shows a placeholder.
 */
@Composable
private fun NoteRow(value: String, onValueChange: (String) -> Unit) {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens
    val textStyle = CashizardTheme.typography.bodyLarge.copy(textAlign = TextAlign.End)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = dimens.rowHeightCompact)
            .padding(horizontal = dimens.space16, vertical = dimens.space8),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Lucide.NotebookPen,
            contentDescription = null,
            tint = colors.textSecondary,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(dimens.space12))
        Text(
            text = "Note",
            style = CashizardTheme.typography.bodyLarge,
            color = colors.textPrimary,
        )
        Spacer(Modifier.width(dimens.space12))
        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
            if (value.isEmpty()) {
                Text(
                    text = "Optional",
                    style = textStyle,
                    color = colors.textPlaceholder,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = textStyle.copy(color = colors.textPrimary),
                cursorBrush = SolidColor(colors.accent),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/** "Today, 19 July" for the current day; otherwise "19 July", with the year when it differs. */
private fun LocalDate.dateRowLabel(today: LocalDate): String {
    val dayMonth = dayMonthLabel()
    return when {
        this == today -> "Today, $dayMonth"
        year != today.year -> "$dayMonth $year"
        else -> dayMonth
    }
}

/** The destructive action at the foot of the edit form: a full-width red button. */
@Composable
private fun DeleteTransactionButton(enabled: Boolean, onClick: () -> Unit) {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens
    Box(
        modifier = Modifier
            .padding(start = dimens.listPadding, end = dimens.listPadding, top = 22.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimens.radiusControl))
            .background(colors.surface)
            .defaultMinSize(minHeight = dimens.rowHeightCompact)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = enabled,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Delete Transaction",
            style = CashizardTheme.typography.bodyLarge,
            color = colors.negative,
        )
    }
}

/** The horizontally scrolling shortcut strip of categories for the active type. */
@Composable
private fun CategoryStrip(
    component: AddTransactionSheetComponent,
    state: AddTransactionUiState,
) {
    val dimens = CashizardTheme.dimens

    BoxWithConstraints(Modifier.fillMaxWidth()) {
        // Chips are a fixed width and labels ellipsize rather than widen them,
        // so how many fit is exact arithmetic rather than a guess: n chips need
        // n*chip + (n-1)*gap, inside the padding at either end. Showing only
        // that many is what keeps the strip centred on every screen instead of
        // clipping the last chip on a narrow one.
        val available = maxWidth - dimens.space12 * 2 + dimens.space8
        val fits = (available / (CategoryChipWidth + dimens.space8)).toInt()

        // The most-used categories for this type, plus any just picked from
        // "See all"; the full list lives behind that action.
        val categories = state.categoryStrip(fits)

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            // Equal padding at both ends, so the last chip clears the edge
            // exactly the way the first one does.
            contentPadding = PaddingValues(
                horizontal = dimens.space12,
                vertical = dimens.space4,
            ),
            // Centres the strip. A scrolling Row cannot: it measures against an
            // unbounded width, so a centring arrangement has nothing to centre
            // within. This still scrolls if a screen is too narrow for even one.
            horizontalArrangement = Arrangement.spacedBy(
                dimens.space8,
                Alignment.CenterHorizontally,
            ),
        ) {
            items(categories, key = { it.id }) { category ->
                CategoryChip(
                    label = category.name,
                    icon = iconFor(category.icon),
                    color = CategoryColors.parse(state.colorOf(category)),
                    selected = category.id == state.categoryId,
                    onClick = { component.viewModel.onCategoryChosen(category.id) },
                )
            }
        }
    }
}
