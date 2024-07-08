package domain.model

import androidx.compose.ui.graphics.Color

enum class RateStatus(
    val title: String,
    val color: Color,
) {
    Idle("Rates", Color.Gray),
    Fresh("Fresh rates", Color.Green),
    Stale("Rates are not fresh", Color.Red),
}
