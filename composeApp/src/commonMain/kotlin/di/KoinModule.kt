package di

import com.russhwolf.settings.Settings
import data.local.PreferencesImpl
import data.remote.api.CurrencyApiService
import domain.CurrencyApiServiceImpl
import domain.PreferencesRepository
import org.koin.core.context.startKoin
import org.koin.dsl.module

val appModule =
    module {
        single<CurrencyApiService> { CurrencyApiServiceImpl(get()) }
        single<PreferencesRepository> { PreferencesImpl(get()) }
        single { Settings() }
    }

fun initKoin() {
    startKoin {
        modules(appModule)
    }
}
