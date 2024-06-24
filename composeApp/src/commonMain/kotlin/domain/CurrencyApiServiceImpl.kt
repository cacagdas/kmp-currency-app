package domain

import data.remote.api.CurrencyApiService
import data.remote.api.key.CURRENCY_API_KEY
import domain.model.ApiResponse
import domain.model.Currency
import domain.model.RequestState
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class CurrencyApiServiceImpl : CurrencyApiService {
    companion object {
        const val ENDPOINT = "https://api.currencyapi.com/v3/latest"
        const val API_KEY = CURRENCY_API_KEY
    }

    private val client =
        HttpClient {
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    },
                )
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 15000L
                connectTimeoutMillis = 15000L
                socketTimeoutMillis = 15000L
            }
            install(DefaultRequest) {
                headers {
                    append("apikey", API_KEY)
                }
            }
        }

    override suspend fun getLatestExchangeRates(): RequestState<List<Currency>> =
        try {
            val response = client.get(ENDPOINT)
            if (response.status.value != 200) {
                RequestState.Error("Failed to fetch data")
            } else {
                val responseBody = Json.decodeFromString<ApiResponse>(response.body())
                RequestState.Success(responseBody.data.values.toList())
            }
        } catch (e: Exception) {
            RequestState.Error(e.message ?: "An error occurred")
        }
}
