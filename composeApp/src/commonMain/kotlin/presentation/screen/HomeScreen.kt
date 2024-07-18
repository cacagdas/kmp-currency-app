package presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import domain.model.CurrencyType
import presentation.component.CurrencyPickerDialog
import presentation.component.HomeHeader

class HomeScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = getScreenModel<HomeViewModel>()
        val rateStatus by viewModel.rateStatus
        val allCurrencies = viewModel.allCurrencies
        val sourceCurrency by viewModel.sourceCurrency
        val targetCurrency by viewModel.targetCurrency

        var amount by rememberSaveable { mutableStateOf(0.0) }

        var selectedCurrencyType: CurrencyType by remember {
            mutableStateOf(CurrencyType.None)
        }
        var dialogShown by remember { mutableStateOf(false) }

        if (dialogShown) {
            CurrencyPickerDialog(
                allCurrencies,
                selectedCurrencyType,
                { dialogShown = false },
                { dialogShown = false },
            )
        }

        Column(
            Modifier.fillMaxSize().background(Color.White),
        ) {
            HomeHeader(
                status = rateStatus,
                sourceCurrency = sourceCurrency,
                targetCurrency = targetCurrency,
                amount = amount,
                onAmountChange = { amount = it },
                onRatesRefresh = {
                    viewModel.sendEvent(
                        HomeUiEvent.RefreshRates,
                    )
                },
                onSwitchCurrencies = {
                    viewModel.sendEvent(
                        HomeUiEvent.SwitchCurrencies,
                    )
                },
            )
        }
    }
}
