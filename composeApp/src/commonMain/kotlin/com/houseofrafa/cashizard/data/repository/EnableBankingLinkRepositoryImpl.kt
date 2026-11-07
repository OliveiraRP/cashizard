package com.houseofrafa.cashizard.data.repository

import com.houseofrafa.cashizard.config.BuildKonfig
import com.houseofrafa.cashizard.domain.model.AspspOption
import com.houseofrafa.cashizard.domain.repository.EnableBankingLinkRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// Wire types for the Edge Functions' HTTP contract — distinct from data/dto's
// table-backed DTOs, since these describe a function call, not a Postgrest row.
@Serializable
private data class AspspDto(val name: String, val country: String)

@Serializable
private data class AspspsResponseDto(val aspsps: List<AspspDto>)

@Serializable
private data class StartAuthRequestDto(
    @SerialName("account_id") val accountId: String,
    @SerialName("aspsp_name") val aspspName: String,
    @SerialName("aspsp_country") val aspspCountry: String,
)

@Serializable
private data class StartAuthResponseDto(val url: String)

/**
 * Calls the `eb-aspsps` / `eb-start-auth` Edge Functions directly over HTTPS.
 * Plain Ktor rather than supabase-kt's Functions plugin — this is the only
 * feature that needs it, and a direct client keeps full control of the
 * request/response shape.
 */
class EnableBankingLinkRepositoryImpl(
    private val client: SupabaseClient,
) : EnableBankingLinkRepository {

    private val http = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    private val functionsBaseUrl = "${BuildKonfig.SUPABASE_URL}/functions/v1"

    override suspend fun getAspsps(): List<AspspOption> {
        val response: AspspsResponseDto = http.get("$functionsBaseUrl/eb-aspsps") {
            header(HttpHeaders.Authorization, "Bearer ${requireAccessToken()}")
        }.body()
        return response.aspsps.map { AspspOption(name = it.name, country = it.country) }
    }

    override suspend fun startAuth(accountId: String, aspspName: String, aspspCountry: String): String {
        val response: StartAuthResponseDto = http.post("$functionsBaseUrl/eb-start-auth") {
            header(HttpHeaders.Authorization, "Bearer ${requireAccessToken()}")
            contentType(ContentType.Application.Json)
            setBody(StartAuthRequestDto(accountId = accountId, aspspName = aspspName, aspspCountry = aspspCountry))
        }.body()
        return response.url
    }

    private fun requireAccessToken(): String =
        client.auth.currentAccessTokenOrNull() ?: error("Not signed in")
}
