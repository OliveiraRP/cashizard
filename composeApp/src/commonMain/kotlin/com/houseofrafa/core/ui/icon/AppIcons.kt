package com.houseofrafa.core.ui.icon

import androidx.compose.ui.graphics.vector.ImageVector
import compose.icons.TablerIcons
import compose.icons.tablericons.*

object AppIcons {

    // -------------------------------------------------------------------------
    // Navigation  — bottom bar, screen headers, back buttons
    // -------------------------------------------------------------------------
    object Navigation {
        val Home         = TablerIcons.Home
        val Transactions = TablerIcons.List
        val Wallets      = TablerIcons.CreditCard
        val Analytics    = TablerIcons.ChartBar
        val Settings     = TablerIcons.Settings
        val Profile      = TablerIcons.User
        val Back         = TablerIcons.ChevronLeft
        val Close        = TablerIcons.X
        val ChevronRight = TablerIcons.ChevronRight
        val ChevronDown  = TablerIcons.ChevronDown
        val ChevronUp    = TablerIcons.ChevronUp
    }

    // -------------------------------------------------------------------------
    // Actions  — buttons, toolbars, swipe actions
    // -------------------------------------------------------------------------
    object Actions {
        val Add           = TablerIcons.Plus
        val Edit          = TablerIcons.Pencil
        val Delete        = TablerIcons.Trash
        val Search        = TablerIcons.Search
        val Filter        = TablerIcons.Filter
        val Sort          = TablerIcons.ArrowsSort
        val Share         = TablerIcons.Share
        val Copy          = TablerIcons.Copy
        val More          = TablerIcons.Dots
        val MoreVertical  = TablerIcons.DotsVertical
        val Check         = TablerIcons.Check
        val Refresh       = TablerIcons.Refresh
        val Notifications = TablerIcons.Bell
        val Calendar      = TablerIcons.Calendar
        val Attach        = TablerIcons.Paperclip
        val Download      = TablerIcons.Download
        val Upload        = TablerIcons.Upload
    }

    // -------------------------------------------------------------------------
    // Status / Feedback  — alerts, toasts, empty states
    // -------------------------------------------------------------------------
    object Status {
        val Success       = TablerIcons.CircleCheck
        val Error         = TablerIcons.CircleX
        val Warning       = TablerIcons.AlertTriangle
        val Info          = TablerIcons.InfoCircle
        val Empty         = TablerIcons.Inbox
        val Lock          = TablerIcons.Lock
        val Visibility    = TablerIcons.Eye
        val VisibilityOff = TablerIcons.EyeOff
        val Loading       = TablerIcons.Clock
    }

    // -------------------------------------------------------------------------
    // Finance  — wallets, accounts, transaction categories
    // -------------------------------------------------------------------------
    object Finance {
        val Wallet         = TablerIcons.Wallet
        val Banknote       = TablerIcons.Businessplan
        val CreditCard     = TablerIcons.CreditCard
        val Savings        = TablerIcons.Coin
        val Goal           = TablerIcons.Target
        val ShoppingCart   = TablerIcons.ShoppingCart
        val Gift           = TablerIcons.Gift
        val Travel         = TablerIcons.Plane
        val Car            = TablerIcons.Car
        val Home           = TablerIcons.Home
        val Food           = TablerIcons.Pizza
        val Coffee         = TablerIcons.Mug
        val Health         = TablerIcons.Heart
        val Education      = TablerIcons.School
        val Entertainment  = TablerIcons.Movie
        val Utilities      = TablerIcons.Bolt
        val Briefcase      = TablerIcons.Briefcase
        val Star           = TablerIcons.Star
        val CurrencyDollar = TablerIcons.CurrencyDollar
    }

    // -------------------------------------------------------------------------
    // fromName — maps an icon name string (stored in DB) to an ImageVector.
    // Falls back to Finance.Wallet for unknown names.
    // -------------------------------------------------------------------------
    fun fromName(name: String): ImageVector = when (name.lowercase()) {
        "wallet"          -> Finance.Wallet
        "banknote",
        "businessplan"    -> Finance.Banknote
        "creditcard",
        "credit_card"     -> Finance.CreditCard
        "savings",
        "pigmoney",
        "pig_money"       -> Finance.Savings
        "goal", "target"  -> Finance.Goal
        "shoppingcart",
        "shopping_cart"   -> Finance.ShoppingCart
        "gift"            -> Finance.Gift
        "travel", "plane" -> Finance.Travel
        "car"             -> Finance.Car
        "home"            -> Finance.Home
        "food", "salad"   -> Finance.Food
        "coffee"          -> Finance.Coffee
        "health", "heart" -> Finance.Health
        "education",
        "school"          -> Finance.Education
        "entertainment",
        "devicetv",
        "tv"              -> Finance.Entertainment
        "utilities",
        "bolt"            -> Finance.Utilities
        "briefcase"       -> Finance.Briefcase
        "star"            -> Finance.Star
        "currencydollar",
        "currency_dollar" -> Finance.CurrencyDollar
        "transactions",
        "list"            -> Navigation.Transactions
        "analytics",
        "chartbar",
        "chart_bar"       -> Navigation.Analytics
        "settings"        -> Navigation.Settings
        "profile", "user" -> Navigation.Profile
        else              -> Finance.Wallet
    }
}