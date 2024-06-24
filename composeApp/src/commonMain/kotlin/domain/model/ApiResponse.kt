package domain.model

import kotlinx.serialization.SerialName

data class ApiResponse(
    val meta: MetaData,
    val data: Map<String, Currency>,
)

data class MetaData(
    @SerialName("last_updated_at")
    val lastUpdatedAt: String,
)

data class Currency(
    val code: String,
    val value: Double,
)
