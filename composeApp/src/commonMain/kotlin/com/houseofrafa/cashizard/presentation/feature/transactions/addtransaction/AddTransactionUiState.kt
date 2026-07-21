package com.houseofrafa.cashizard.presentation.feature.transactions.addtransaction

import com.houseofrafa.cashizard.domain.model.Category
import com.houseofrafa.cashizard.domain.model.CategoryGroup
import com.houseofrafa.cashizard.domain.model.CategoryGroupWithCategories
import com.houseofrafa.cashizard.domain.model.TransactionType
import com.houseofrafa.cashizard.domain.model.Wallet
import com.houseofrafa.cashizard.presentation.common.AmountBuffer
import com.houseofrafa.cashizard.presentation.feature.categories.CategoryFormUiState
import com.houseofrafa.cashizard.presentation.feature.categories.GroupFormUiState
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

/**
 * The most category shortcuts the strip will ever show before "See all".
 *
 * It is an upper bound, not a target: the strip shows however many actually fit
 * the screen it is drawn on, so a narrow phone gets fewer and stays centred.
 */
private const val CATEGORY_STRIP_SIZE = 6

data class AddTransactionUiState(
    val type: TransactionType = TransactionType.EXPENSE,
    val amount: AmountBuffer = AmountBuffer.Empty,
    val fromWalletId: String? = null,
    val toWalletId: String? = null,
    val categoryId: String? = null,
    val occurredOn: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
    val note: String = "",
    val repeatMonthly: Boolean = false,
    val wallets: List<Wallet> = emptyList(),
    val categoryGroups: List<CategoryGroupWithCategories> = emptyList(),
    /** How many transactions reference each category, for ranking the strip. */
    val categoryUsage: Map<String, Int> = emptyMap(),
    /**
     * Categories the user picked from the full "See all" list that were not
     * already on the strip. They are pulled to the front so a just-chosen
     * category is visible without scrolling. Lives only for this sheet session.
     */
    val promotedCategoryIds: List<String> = emptyList(),
    /** Non-null only while a group form is on the stack. */
    val groupForm: GroupFormUiState? = null,
    /** Non-null only while a category form is on the stack. */
    val categoryForm: CategoryFormUiState? = null,
    /** True when the sheet is editing an existing transaction rather than creating one. */
    val isEditing: Boolean = false,
    val loading: Boolean = true,
    val saving: Boolean = false,
    val deleting: Boolean = false,
    val errorMessage: String? = null,
    /** The sheet has asked to close; the host plays the dismiss animation. */
    val closeRequested: Boolean = false,
) {
    val categories: List<Category> get() = categoryGroups.flatMap { it.categories }

    val groups: List<CategoryGroup> get() = categoryGroups.map { it.group }

    fun group(id: String?): CategoryGroup? = groups.firstOrNull { it.id == id }

    /** The group a category is filed under, for prefilling the edit form. */
    fun groupOf(categoryId: String): CategoryGroup? =
        categoryGroups.firstOrNull { entry -> entry.categories.any { it.id == categoryId } }?.group

    fun wallet(id: String?): Wallet? = wallets.firstOrNull { it.id == id }

    fun category(id: String?): Category? = categories.firstOrNull { it.id == id }

    /** Group color for a category, used by the chips and the selected disc. */
    fun colorOf(category: Category): String? =
        categoryGroups.firstOrNull { entry -> entry.categories.any { it.id == category.id } }
            ?.group
            ?.color

    /**
     * The active type's categories, most-used first. [categoryGroups] already
     * arrives in catalog order and sorting is stable, so equally-used
     * categories keep that order rather than shuffling between opens.
     */
    fun categoriesByUsage(): List<Category> =
        categoryGroups
            .filter { it.group.type == type }
            .flatMap { it.categories }
            .sortedByDescending { categoryUsage[it.id] ?: 0 }

    /**
     * The shortcut strip: the top [limit] most-used categories, with any
     * session-promoted picks pulled ahead of them.
     *
     * [limit] is how many the caller has room for; it is capped here so the
     * strip never grows past [CATEGORY_STRIP_SIZE] on a wide screen.
     */
    fun categoryStrip(limit: Int = CATEGORY_STRIP_SIZE): List<Category> {
        val byUsage = categoriesByUsage()
        val byId = byUsage.associateBy { it.id }
        val promoted = promotedCategoryIds.mapNotNull { byId[it] }
        return (promoted + byUsage)
            .distinctBy { it.id }
            .take(limit.coerceIn(1, CATEGORY_STRIP_SIZE))
    }

    val busy: Boolean get() = saving || deleting

    /**
     * The wallet ids as the schema expects them for the current type: income
     * carries only a destination, expense only a source, transfer both. The form
     * keeps whatever the pickers set; these apply the type rule at read time.
     */
    val effectiveFromWalletId: String? get() = fromWalletId.takeIf { type != TransactionType.INCOME }
    val effectiveToWalletId: String? get() = toWalletId.takeIf { type != TransactionType.EXPENSE }

    /** Mirrors the schema's chk_wallets_by_type so Save is only offered when valid. */
    val canSave: Boolean
        get() = !busy && !amount.toMoney().isZero && when (type) {
            TransactionType.EXPENSE -> fromWalletId != null && categoryId != null
            TransactionType.INCOME -> toWalletId != null && categoryId != null
            TransactionType.TRANSFER ->
                fromWalletId != null && toWalletId != null && fromWalletId != toWalletId
        }

    companion object {
        /** Blank for a new transaction, seeded for an edit. */
        fun from(editing: EditingTransaction?): AddTransactionUiState = editing?.let {
            AddTransactionUiState(
                type = it.type,
                amount = AmountBuffer.of(it.amount),
                fromWalletId = it.fromWalletId,
                toWalletId = it.toWalletId,
                categoryId = it.categoryId,
                occurredOn = it.occurredOn,
                note = it.note,
                isEditing = true,
            )
        } ?: AddTransactionUiState()
    }
}
