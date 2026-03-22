package com.houseofrafa.core.data.remote

import com.houseofrafa.BuildKonfig
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.SettingsSessionManager
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

val supabase = createSupabaseClient(
    supabaseUrl = BuildKonfig.SUPABASE_URL,
    supabaseKey = BuildKonfig.SUPABASE_ANON_KEY
) {
    install(Postgrest)
    install(Auth) {
        sessionManager = SettingsSessionManager()
        autoLoadFromStorage = true
        autoSaveToStorage = true
    }
}