package domain.model

import androidx.compose.ui.graphics.Color

enum class RateStatus(
    val title: String,
    val color: Color,
) {
    Idle("Rates", Color.LightGray),
    Fresh("Fresh rates", Color.White),
    Stale("Rates are not fresh", Color.Magenta),
}
