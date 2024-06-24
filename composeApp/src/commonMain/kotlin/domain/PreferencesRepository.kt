package domain

interface PreferencesRepository {
    suspend fun saveLastUpdatedTime(time: Long)
    suspend fun isDataFresh(currentTime: Long): Boolean
}