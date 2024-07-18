package presentation.screen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.remote.api.CurrencyApiService
import domain.MongoRepository
import domain.PreferencesRepository
import domain.model.Currency
import domain.model.RateStatus
import domain.model.RequestState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

sealed class HomeUiEvent {
    data object RefreshRates : HomeUiEvent()
}

class HomeViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val mongoDb: MongoRepository,
    private val service: CurrencyApiService,
) : ScreenModel {
    private var _rateStatus: MutableState<RateStatus> = mutableStateOf(RateStatus.Idle)
    val rateStatus: State<RateStatus> = _rateStatus

    private var _allCurrencies = mutableStateListOf<Currency>()
    val allCurrencies: List<Currency> = _allCurrencies

    private var _sourceCurrency: MutableState<RequestState<Currency>> =
        mutableStateOf(RequestState.Idle)
    val sourceCurrency: State<RequestState<Currency>> = _sourceCurrency

    private var _targetCurrency: MutableState<RequestState<Currency>> =
        mutableStateOf(RequestState.Idle)
    val targetCurrency: State<RequestState<Currency>> = _targetCurrency

    init {
        screenModelScope.launch {
            fetchNewRates()
            readSourceCurrencyCode()
            readTargetCurrencyCode()
        }
    }

    fun sendEvent(event: HomeUiEvent) {
        when (event) {
            HomeUiEvent.RefreshRates ->
                screenModelScope.launch {
                    fetchNewRates()
                }
        }
    }

    private fun readSourceCurrencyCode() {
        screenModelScope.launch {
            preferencesRepository.readSourceCurrencyCode().collectLatest { code ->
                val selectedCurrency = _allCurrencies.find { it.code == code.name }
                selectedCurrency?.let {
                    _sourceCurrency.value = RequestState.Success(it)
                } ?: run {
                    _sourceCurrency.value = RequestState.Error("Currency not found")
                }
            }
        }
    }

    private fun readTargetCurrencyCode() {
        screenModelScope.launch {
            preferencesRepository.readTargetCurrencyCode().collectLatest { code ->
                val selectedCurrency = _allCurrencies.find { it.code == code.name }
                selectedCurrency?.let {
                    _targetCurrency.value = RequestState.Success(it)
                } ?: run {
                    _targetCurrency.value = RequestState.Error("Currency not found")
                }
            }
        }
    }

    private suspend fun fetchNewRates() {
        try {
            val localCache = mongoDb.readCurrencyData().first()
            if (localCache.isSuccess()) {
                if (localCache.getSuccessData()?.isNotEmpty() == true) {
                    localCache.getSuccessData()?.let { _allCurrencies.addAll(it) }
                    if (!preferencesRepository.isDataFresh(
                            Clock.System.now().toEpochMilliseconds(),
                        )
                    ) {
                        cacheData()
                    }
                } else {
                    cacheData()
                }
            }
            getRateStatus()
        } catch (e: Exception) {
            println(e.message)
        }
    }

    private suspend fun cacheData() {
        val fetchedRates = service.getLatestExchangeRates()
        if (fetchedRates.isSuccess()) {
            mongoDb.cleanUp()
            fetchedRates.getSuccessData()?.let {
                it.forEach { currency ->
                    mongoDb.insertCurrencyData(currency)
                }
                _allCurrencies.addAll(it)
            }
        }
    }

    private suspend fun getRateStatus() {
        _rateStatus.value =
            if (preferencesRepository.isDataFresh(
                    Clock.System.now().toEpochMilliseconds(),
                )
            ) {
                RateStatus.Fresh
            } else {
                RateStatus.Stale
            }
    }
}
