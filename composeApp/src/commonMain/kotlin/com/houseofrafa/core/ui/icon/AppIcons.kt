package com.houseofrafa.core.ui.icon

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
}