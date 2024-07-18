package domain

import kotlinx.coroutines.flow.Flow
import util.CurrencyCode

interface PreferencesRepository {
    suspend fun saveLastUpdatedTime(time: Long)

    suspend fun isDataFresh(currentTime: Long): Boolean

    suspend fun saveSourceCurrencyCode(code: String)

    suspend fun saveTargetCurrencyCode(code: String)

    fun readSourceCurrencyCode(): Flow<CurrencyCode>

    fun readTargetCurrencyCode(): Flow<CurrencyCode>
}
