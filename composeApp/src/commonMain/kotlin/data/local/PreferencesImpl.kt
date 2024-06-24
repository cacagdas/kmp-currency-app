package data.local

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.toFlowSettings
import domain.PreferencesRepository
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalSettingsApi::class)
class PreferencesImpl(
    private val settings: Settings,
) : PreferencesRepository {
    companion object {
        const val LAST_UPDATED_TIME = "last_updated_time"
    }

    private val flowSettings = (settings as ObservableSettings).toFlowSettings()

    override suspend fun saveLastUpdatedTime(time: Long) {
        flowSettings.putLong(LAST_UPDATED_TIME, time)
    }

    override suspend fun isDataFresh(currentTime: Long): Boolean {
        val savedTime = flowSettings.getLong(LAST_UPDATED_TIME, 0L)

        return if (savedTime != 0L) {
            val currentInstant = Instant.fromEpochMilliseconds(currentTime)
            val savedInstant = Instant.fromEpochMilliseconds(savedTime)

            val currentDateTime = currentInstant.toLocalDateTime(TimeZone.currentSystemDefault())
            val savedDateTime = savedInstant.toLocalDateTime(TimeZone.currentSystemDefault())

            val difference = currentDateTime.date.daysUntil(savedDateTime.date)
            return difference < 1
        } else {
            false
        }
    }
}
