package com.houseofrafa.cashizard.presentation.feature.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.ChevronLeft
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.Lucide
import com.houseofrafa.cashizard.domain.usecase.SpendingSlice
import com.houseofrafa.cashizard.presentation.common.ScreenHeader
import com.houseofrafa.cashizard.presentation.common.monthYearLabel
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme
import com.houseofrafa.cashizard.presentation.designsystem.tokens.CategoryColors
import com.houseofrafa.cashizard.presentation.designsystem.components.DonutChart
import com.houseofrafa.cashizard.presentation.designsystem.components.DonutSlice
import com.houseofrafa.cashizard.presentation.designsystem.components.FormCard
import com.houseofrafa.cashizard.presentation.designsystem.components.IconDisc
import com.houseofrafa.cashizard.presentation.designsystem.components.IconDiscStyle
import com.houseofrafa.cashizard.presentation.designsystem.components.MicroProgressBar
import com.houseofrafa.cashizard.presentation.designsystem.format.formatEur
import com.houseofrafa.cashizard.presentation.designsystem.tokens.iconFor
import kotlin.math.roundToInt

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel,
    spaceName: String?,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding(),
    ) {
        ScreenHeader(
            title = "Analytics",
            spaceName = spaceName,
            modifier = Modifier.padding(top = dimens.space16),
        )

        MonthSwitcher(
            label = state.month.monthYearLabel(),
            onPrevious = viewModel::onPreviousMonth,
            onNext = viewModel::onNextMonth,
        )

        when {
            state.loading && state.breakdown == null -> Centered {
                CircularProgressIndicator(
                    color = colors.accent,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(28.dp),
                )
            }

            state.errorMessage != null -> Centered {
                Text(
                    text = state.errorMessage.orEmpty(),
                    style = CashizardTheme.typography.footnote,
                    color = colors.textTertiary,
                    textAlign = TextAlign.Center,
                )
            }

            state.isEmpty -> Centered {
                Text(
                    text = "No spending recorded this month.",
                    style = CashizardTheme.typography.footnote,
                    color = colors.textTertiary,
                    textAlign = TextAlign.Center,
                )
            }

            else -> Breakdown(viewModel, state)
        }
    }
}

@Composable
private fun Breakdown(viewModel: AnalyticsViewModel, state: AnalyticsUiState) {
    val dimens = CashizardTheme.dimens
    val breakdown = state.breakdown ?: return
    val slices = breakdown.slices

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(top = 14.dp, bottom = 4.dp),
            contentAlignment = Alignment.Center,
        ) {
            DonutChart(
                slices = slices.map { slice ->
                    DonutSlice(
                        id = slice.id,
                        color = CategoryColors.parse(slice.colorHex),
                        fraction = slice.fraction,
                    )
                },
                onSliceClick = viewModel::onSliceClick,
            ) {
                DonutCentre(
                    label = state.drilledGroupName ?: "Total spent",
                    amount = breakdown.total.formatEur(),
                )
            }

            if (state.isDrilled) {
                BackToGroupsPill(
                    onClick = viewModel::onBackToGroups,
                    modifier = Modifier.align(Alignment.TopStart).padding(start = dimens.screenPadding),
                )
            }
        }

        FormCard(
            modifier = Modifier.padding(horizontal = dimens.listPadding, vertical = 10.dp),
            cornerRadius = dimens.radiusCardMedium,
            separatorInset = dimens.space16 + dimens.iconDiscAnalytics + dimens.space12,
            rows = slices.map { slice ->
                {
                    SliceRow(
                        slice = slice,
                        onClick = if (state.isDrilled) null else {
                            { viewModel.onSliceClick(slice.id) }
                        },
                    )
                }
            },
        )

        // Clears the floating button and the tab bar.
        Spacer(Modifier.height(120.dp))
    }
}

@Composable
private fun DonutCentre(label: String, amount: String) {
    val colors = CashizardTheme.colors
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.padding(horizontal = 40.dp),
    ) {
        Text(
            text = label,
            style = CashizardTheme.typography.caption,
            color = colors.textTertiary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = amount,
            style = CashizardTheme.typography.amountTitle,
            color = colors.textPrimary,
            maxLines = 1,
        )
    }
}

@Composable
private fun BackToGroupsPill(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens

    Row(
        modifier = modifier
            .background(colors.surfaceChip, RoundedCornerShape(dimens.radiusPill))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(start = 6.dp, end = dimens.space12, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Lucide.ChevronLeft,
            contentDescription = null,
            tint = colors.accent,
            modifier = Modifier.size(16.dp),
        )
        Spacer(Modifier.width(2.dp))
        Text(
            text = "Groups",
            style = CashizardTheme.typography.footnoteStrong,
            color = colors.accent,
        )
    }
}

/** A legend row: disc, name, amount, and a share bar in the group's color. */
@Composable
private fun SliceRow(slice: SpendingSlice, onClick: (() -> Unit)?) {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens
    val color = CategoryColors.parse(slice.colorHex)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick,
                    )
                } else {
                    Modifier
                },
            )
            .padding(horizontal = dimens.space16, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconDisc(
            icon = iconFor(slice.iconName),
            color = color,
            style = IconDiscStyle.Solid,
            size = dimens.iconDiscAnalytics,
            iconSize = dimens.space16,
        )
        Spacer(Modifier.width(dimens.space12))

        Column(Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(
                    text = slice.name,
                    style = CashizardTheme.typography.rowTitleSmall,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Spacer(Modifier.width(dimens.space8))
                Text(
                    text = slice.total.formatEur(),
                    style = CashizardTheme.typography.amountSmall,
                    color = colors.textPrimary,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimens.space8),
            ) {
                MicroProgressBar(
                    progress = slice.fraction,
                    color = color,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    // Rounded, not truncated: two slices of 68.97 and 31.03 must
                    // read 69 % and 31 %, not 68 % and 31 %.
                    text = "${(slice.fraction * 100).roundToInt()} %",
                    style = CashizardTheme.typography.caption2,
                    color = colors.textTertiary,
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(34.dp),
                )
            }
        }
    }
}

@Composable
private fun MonthSwitcher(label: String, onPrevious: () -> Unit, onNext: () -> Unit) {
    val colors = CashizardTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MonthChevron(Lucide.ChevronLeft, "Previous month", onPrevious)
        Text(
            text = label,
            style = CashizardTheme.typography.headline,
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
            maxLines = 1,
            softWrap = false,
            modifier = Modifier.width(160.dp),
        )
        MonthChevron(Lucide.ChevronRight, "Next month", onNext)
    }
}

@Composable
private fun MonthChevron(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    onClick: () -> Unit,
) {
    Icon(
        imageVector = icon,
        contentDescription = description,
        tint = CashizardTheme.colors.accent,
        modifier = Modifier
            .size(20.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
    )
}

@Composable
private fun Centered(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().padding(CashizardTheme.dimens.space32),
        contentAlignment = Alignment.Center,
    ) { content() }
}
