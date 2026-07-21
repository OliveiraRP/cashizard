package com.houseofrafa.cashizard.data.remote

import com.houseofrafa.cashizard.config.BuildKonfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

/**
 * Builds the single Supabase client. Credentials come from BuildKonfig
 * (local.properties). The anon key + RLS is the whole security model — there is
 * no service key in the app.
 */
fun createCashizardSupabaseClient(): SupabaseClient =
    createSupabaseClient(
        supabaseUrl = BuildKonfig.SUPABASE_URL,
        supabaseKey = BuildKonfig.SUPABASE_ANON_KEY,
    ) {
        install(Auth)
        install(Postgrest)
    }
