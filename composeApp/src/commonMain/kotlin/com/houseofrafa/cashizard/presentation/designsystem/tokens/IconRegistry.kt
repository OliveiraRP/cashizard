package com.houseofrafa.cashizard.presentation.designsystem.tokens

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.icons.lucide.Armchair
import com.composables.icons.lucide.ArrowRightLeft
import com.composables.icons.lucide.Baby
import com.composables.icons.lucide.Banknote
import com.composables.icons.lucide.Beer
import com.composables.icons.lucide.Bike
import com.composables.icons.lucide.BookOpen
import com.composables.icons.lucide.Briefcase
import com.composables.icons.lucide.Brush
import com.composables.icons.lucide.Building2
import com.composables.icons.lucide.Bus
import com.composables.icons.lucide.Calendar
import com.composables.icons.lucide.Camera
import com.composables.icons.lucide.Car
import com.composables.icons.lucide.CarTaxiFront
import com.composables.icons.lucide.Cat
import com.composables.icons.lucide.ChartPie
import com.composables.icons.lucide.CircleHelp
import com.composables.icons.lucide.Clapperboard
import com.composables.icons.lucide.Cloud
import com.composables.icons.lucide.Coffee
import com.composables.icons.lucide.CreditCard
import com.composables.icons.lucide.Croissant
import com.composables.icons.lucide.Dog
import com.composables.icons.lucide.DollarSign
import com.composables.icons.lucide.Dumbbell
import com.composables.icons.lucide.Euro
import com.composables.icons.lucide.Flame
import com.composables.icons.lucide.Fuel
import com.composables.icons.lucide.Gamepad
import com.composables.icons.lucide.Gamepad2
import com.composables.icons.lucide.Gift
import com.composables.icons.lucide.Globe
import com.composables.icons.lucide.GraduationCap
import com.composables.icons.lucide.Hammer
import com.composables.icons.lucide.HandCoins
import com.composables.icons.lucide.Heart
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Landmark
import com.composables.icons.lucide.Laptop
import com.composables.icons.lucide.Leaf
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Map
import com.composables.icons.lucide.MicVocal
import com.composables.icons.lucide.Mountain
import com.composables.icons.lucide.Music
import com.composables.icons.lucide.PenTool
import com.composables.icons.lucide.Percent
import com.composables.icons.lucide.Phone
import com.composables.icons.lucide.PiggyBank
import com.composables.icons.lucide.Pill
import com.composables.icons.lucide.Plane
import com.composables.icons.lucide.PlugZap
import com.composables.icons.lucide.Receipt
import com.composables.icons.lucide.Scissors
import com.composables.icons.lucide.Shirt
import com.composables.icons.lucide.ShoppingBag
import com.composables.icons.lucide.ShoppingCart
import com.composables.icons.lucide.Smartphone
import com.composables.icons.lucide.Snowflake
import com.composables.icons.lucide.Sparkles
import com.composables.icons.lucide.SquareParking
import com.composables.icons.lucide.Star
import com.composables.icons.lucide.Stethoscope
import com.composables.icons.lucide.Sun
import com.composables.icons.lucide.Tag
import com.composables.icons.lucide.Ticket
import com.composables.icons.lucide.TrainFront
import com.composables.icons.lucide.TrendingDown
import com.composables.icons.lucide.TrendingUp
import com.composables.icons.lucide.Tv
import com.composables.icons.lucide.Umbrella
import com.composables.icons.lucide.Undo2
import com.composables.icons.lucide.Utensils
import com.composables.icons.lucide.Vault
import com.composables.icons.lucide.Wallet
import com.composables.icons.lucide.Wifi
import com.composables.icons.lucide.Wrench

/**
 * Maps the kebab-case icon-name strings stored in the DB to Lucide vectors.
 * Covers every icon used by `supabase/002_seed_personal_space.sql` plus a set
 * of generic icons for the icon picker. Unknown names fall back to [fallbackIcon].
 *
 * A few DB names have no exact Lucide match and use the closest icon:
 *   bank -> Landmark, train -> TrainFront, brush-cleaning -> Brush.
 */
val icons: Map<String, ImageVector> = mapOf(
    // --- Accounts & wallets (from seed) ---
    "bank" to Lucide.Landmark,
    "calendar" to Lucide.Calendar,
    "plane" to Lucide.Plane,
    "gift" to Lucide.Gift,
    "smartphone" to Lucide.Smartphone,
    "piggybank" to Lucide.PiggyBank,
    "piggy-bank" to Lucide.PiggyBank,
    "trending-up" to Lucide.TrendingUp,
    "trending-down" to Lucide.TrendingDown,
    "vault" to Lucide.Vault,

    // --- Food ---
    "shopping-cart" to Lucide.ShoppingCart,
    "utensils" to Lucide.Utensils,
    "coffee" to Lucide.Coffee,
    "croissant" to Lucide.Croissant,
    "beer" to Lucide.Beer,

    // --- Subscriptions ---
    "music" to Lucide.Music,
    "tv" to Lucide.Tv,
    "dumbbell" to Lucide.Dumbbell,
    "cloud" to Lucide.Cloud,
    "pen-tool" to Lucide.PenTool,

    // --- Transport ---
    "fuel" to Lucide.Fuel,
    "train" to Lucide.TrainFront,
    "square-parking" to Lucide.SquareParking,
    "car-taxi-front" to Lucide.CarTaxiFront,
    "bike" to Lucide.Bike,
    "car" to Lucide.Car,
    "bus" to Lucide.Bus,

    // --- Household ---
    "armchair" to Lucide.Armchair,
    "plug-zap" to Lucide.PlugZap,
    "wifi" to Lucide.Wifi,
    "brush-cleaning" to Lucide.Brush,
    "wrench" to Lucide.Wrench,
    "hammer" to Lucide.Hammer,

    // --- Leisure ---
    "mic-vocal" to Lucide.MicVocal,
    "clapperboard" to Lucide.Clapperboard,
    "book-open" to Lucide.BookOpen,
    "gamepad-2" to Lucide.Gamepad2,
    "gamepad" to Lucide.Gamepad,
    "mountain" to Lucide.Mountain,

    // --- Income ---
    "banknote" to Lucide.Banknote,
    "undo-2" to Lucide.Undo2,
    "percent" to Lucide.Percent,
    "hand-coins" to Lucide.HandCoins,
    "dollar-sign" to Lucide.DollarSign,
    "euro" to Lucide.Euro,

    // --- Transfer ---
    "arrow-right-left" to Lucide.ArrowRightLeft,

    // --- Generic set (icon picker) ---
    "home" to Lucide.House,
    "heart" to Lucide.Heart,
    "star" to Lucide.Star,
    "baby" to Lucide.Baby,
    "dog" to Lucide.Dog,
    "cat" to Lucide.Cat,
    "shirt" to Lucide.Shirt,
    "pill" to Lucide.Pill,
    "umbrella" to Lucide.Umbrella,
    "sun" to Lucide.Sun,
    "laptop" to Lucide.Laptop,
    "phone" to Lucide.Phone,
    "camera" to Lucide.Camera,
    "scissors" to Lucide.Scissors,
    "leaf" to Lucide.Leaf,
    "flame" to Lucide.Flame,
    "snowflake" to Lucide.Snowflake,
    "map" to Lucide.Map,
    "globe" to Lucide.Globe,
    "briefcase" to Lucide.Briefcase,
    "graduation-cap" to Lucide.GraduationCap,
    "stethoscope" to Lucide.Stethoscope,
    "wallet" to Lucide.Wallet,
    "credit-card" to Lucide.CreditCard,
    "building" to Lucide.Building2,
    "receipt" to Lucide.Receipt,
    "tag" to Lucide.Tag,
    "ticket" to Lucide.Ticket,
    "shopping-bag" to Lucide.ShoppingBag,
    "pie-chart" to Lucide.ChartPie,
    "sparkles" to Lucide.Sparkles,
)

/** Fallback icon for unknown names. */
val fallbackIcon: ImageVector = Lucide.CircleHelp

/** Resolve a DB icon-name string to a vector, with a safe fallback. */
fun iconFor(name: String?): ImageVector = icons[name] ?: fallbackIcon
