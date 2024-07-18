package data.local

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.toFlowSettings
import domain.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import util.CurrencyCode

@OptIn(ExperimentalSettingsApi::class)
class PreferencesImpl(
    private val settings: Settings,
) : PreferencesRepository {
    companion object {
        const val LAST_UPDATED_TIME = "lastUpdatedTime"
        const val SOURCE_CURRENCY_CODE = "sourceCurrencyCode"
        const val TARGET_CURRENCY_CODE = "targetCurrencyCode"

        val DEFAULT_SOURCE_CURRENCY_CODE = CurrencyCode.EUR.name
        val DEFAULT_TARGET_CURRENCY_CODE = CurrencyCode.TRY.name
    }

    private val flowSettings = (settings as ObservableSettings).toFlowSettings()

    override suspend fun saveLastUpdatedTime(time: Long) {
        flowSettings.putLong(LAST_UPDATED_TIME, time)
    }

    override suspend fun isDataFresh(currentTime: Long): Boolean {
        val savedTime = flowSettings.getLong(LAST_UPDATED_TIME, 0L)

        /*return if (savedTime != 0L) {
            val currentInstant = Instant.fromEpochMilliseconds(currentTime)
            val savedInstant = Instant.fromEpochMilliseconds(savedTime)

            val currentDateTime = currentInstant.toLocalDateTime(TimeZone.currentSystemDefault())
            val savedDateTime = savedInstant.toLocalDateTime(TimeZone.currentSystemDefault())

            val difference = currentDateTime.date.daysUntil(savedDateTime.date)
            return difference < 1
        } else {
            false
        }*/
        return (Clock.System.now().toEpochMilliseconds() - savedTime) < 60_000
    }

    override suspend fun saveSourceCurrencyCode(code: String) {
        flowSettings.putString(SOURCE_CURRENCY_CODE, code)
    }

    override suspend fun saveTargetCurrencyCode(code: String) {
        flowSettings.putString(TARGET_CURRENCY_CODE, code)
    }

    override fun readSourceCurrencyCode(): Flow<CurrencyCode> =
        flowSettings
            .getStringFlow(SOURCE_CURRENCY_CODE, DEFAULT_SOURCE_CURRENCY_CODE)
            .map { CurrencyCode.valueOf(it) }

    override fun readTargetCurrencyCode(): Flow<CurrencyCode> =
        flowSettings
            .getStringFlow(TARGET_CURRENCY_CODE, DEFAULT_TARGET_CURRENCY_CODE)
            .map { CurrencyCode.valueOf(it) }
}
