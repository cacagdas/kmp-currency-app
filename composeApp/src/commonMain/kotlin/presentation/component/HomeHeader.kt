package presentation.component

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import data.local.PreferencesImpl.Companion.DEFAULT_SOURCE_CURRENCY_CODE
import domain.model.Currency
import domain.model.CurrencyType
import domain.model.DisplayResult
import domain.model.RateStatus
import domain.model.RequestState
import util.CurrencyCode
import util.displayCurrentDateTime

@Composable
fun HomeHeader(
    status: RateStatus,
    sourceCurrency: RequestState<Currency>,
    targetCurrency: RequestState<Currency>,
    amount: Double,
    onAmountChange: (Double) -> Unit,
    onRatesRefresh: () -> Unit,
    onSwitchCurrencies: () -> Unit,
    onCurrencyTypeSelect: (CurrencyType) -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
            .background(Color.Blue)
            .padding(24.dp),
    ) {
        Spacer(Modifier.height(24.dp))
        RateStatus(status, onRatesRefresh)
        Spacer(Modifier.height(24.dp))
        CurrencyInputs(
            sourceCurrency,
            targetCurrency,
            onSwitchCurrencies,
            onCurrencyTypeSelect,
        )
        Spacer(Modifier.height(24.dp))
        AmountInput(
            amount,
            onAmountChange,
        )
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

@Composable
fun CurrencyInputs(
    sourceCurrency: RequestState<Currency>,
    targetCurrency: RequestState<Currency>,
    onSwitchCurrencies: () -> Unit,
    onCurrencyTypeSelect: (CurrencyType) -> Unit,
) {
    var animationStarted by remember { mutableStateOf(false) }
    val animatedRotation by animateFloatAsState(
        targetValue = if (animationStarted) 180f else 0f,
        animationSpec = tween(durationMillis = 500),
    )
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CurrencyView(
            "from",
            sourceCurrency,
        ) {
            if (sourceCurrency.isSuccess()) {
                onCurrencyTypeSelect(
                    CurrencyType.Source(
                        CurrencyCode.valueOf(
                            sourceCurrency.getSuccessData()?.code ?: DEFAULT_SOURCE_CURRENCY_CODE,
                        ),
                    ),
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        IconButton(
            modifier =
                Modifier
                    .padding(top = 24.dp)
                    .graphicsLayer {
                        rotationY = animatedRotation
                    },
            onClick = {
                animationStarted = !animationStarted
                onSwitchCurrencies()
            },
        ) {
            Icon(
                Icons.Default.PlayArrow,
                null,
                Modifier.size(24.dp),
                tint = Color.White,
            )
        }
        Spacer(Modifier.height(16.dp))
        CurrencyView(
            "to",
            targetCurrency,
        ) {
            if (targetCurrency.isSuccess()) {
                onCurrencyTypeSelect(
                    CurrencyType.Target(
                        CurrencyCode.valueOf(
                            sourceCurrency.getSuccessData()?.code ?: DEFAULT_SOURCE_CURRENCY_CODE,
                        ),
                    ),
                )
            }
        }
    }
}

@Composable
fun RowScope.CurrencyView(
    placeholder: String,
    currency: RequestState<Currency>,
    onClick: () -> Unit,
) {
    Column(Modifier.weight(1f)) {
        Text(
            modifier = Modifier.padding(start = 12.dp),
            text = placeholder,
            fontSize = MaterialTheme.typography.bodySmall.fontSize,
            color = Color.White,
        )
        Spacer(Modifier.height(4.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(size = 8.dp))
                .background(Color.LightGray)
                .height(54.dp)
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            currency.DisplayResult(
                onSuccess = { data ->
                    Icon(
                        Icons.Default.PlayArrow,
                        null,
                        Modifier.size(24.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        CurrencyCode.valueOf(data?.code ?: CurrencyCode.TRY.name).name,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        color = Color.White,
                    )
                },
            )
        }
    }
}

@Composable
fun AmountInput(
    amount: Double,
    onAmountChange: (Double) -> Unit,
) {
    TextField(
        "$amount",
        { onAmountChange(it.toDouble()) },
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .animateContentSize()
            .height(54.dp),
        singleLine = true,
        keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
            ),
    )
}
