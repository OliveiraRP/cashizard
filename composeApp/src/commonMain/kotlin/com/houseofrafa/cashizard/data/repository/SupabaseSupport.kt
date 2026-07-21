package com.houseofrafa.cashizard.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth

/** The current user id, or throw — used by writes that set `created_by`. */
internal suspend fun SupabaseClient.requireUserId(): String =
    auth.currentUserOrNull()?.id
        ?: error("No authenticated user — cannot perform this operation.")
