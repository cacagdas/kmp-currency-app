package presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import domain.model.RateStatus
import util.displayCurrentDateTime

@Composable
fun HomeHeader(
    status: RateStatus,
    onRatesRefresh: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
            .background(Color.LightGray)
            .padding(24.dp),
    ) {
        Spacer(Modifier.height(24.dp))
        RateStatus(status, onRatesRefresh)
    }
}

@Composable
fun RateStatus(
    status: RateStatus,
    onRatesRefresh: () -> Unit,
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row {
            Icon(
                Icons.Default.PlayArrow,
                null,
                Modifier.size(56.dp),
            )
            Spacer(Modifier.size(12.dp))
            Column {
                Text(
                    displayCurrentDateTime(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = status.color,
                )
                Text(
                    status.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = status.color,
                )
            }
        }
        if (status == RateStatus.Stale) {
            IconButton(onRatesRefresh) {
                Icon(
                    Icons.Default.Refresh,
                    null,
                    Modifier.size(24.dp),
                )
            }
        }
    }
}
