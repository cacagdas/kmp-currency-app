package data.remote.api

import data.remote.api.key.CURRENCY_API_KEY

class CurrencyApiService {
    companion object {
        const val ENDPOINT = "https://api.currencyapi.com/v3/latest"
        const val API_KEY = CURRENCY_API_KEY
    }
}