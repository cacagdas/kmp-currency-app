package di

import com.russhwolf.settings.Settings
import data.local.MongoImpl
import data.local.PreferencesImpl
import data.remote.api.CurrencyApiService
import domain.CurrencyApiServiceImpl
import domain.MongoRepository
import domain.PreferencesRepository
import org.koin.core.context.startKoin
import org.koin.dsl.module
import presentation.screen.HomeViewModel

val appModule =
    module {
        single { Settings() }
        single<MongoRepository> { MongoImpl() }
        single<PreferencesRepository> { PreferencesImpl(get()) }
        single<CurrencyApiService> { CurrencyApiServiceImpl(get()) }
        factory { HomeViewModel(get(), get(), get()) }
    }

fun initKoin() {
    startKoin {
        modules(appModule)
    }
}
