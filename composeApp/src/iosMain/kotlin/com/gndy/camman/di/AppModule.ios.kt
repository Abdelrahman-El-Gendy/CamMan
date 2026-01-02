package com.gndy.camman.di

import com.gndy.camman.data.local.DatabaseFactory
import com.gndy.camman.data.location.IosLocationService
import com.gndy.camman.data.location.LocationService
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { DatabaseFactory() }
    
    // Location Service (platform-specific implementation)
    single<LocationService> { IosLocationService() }
}

fun initKoin() {
    startKoin {
        modules(sharedModule, platformModule)
    }
}
