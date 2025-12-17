package com.gndy.camman.di

import com.gndy.camman.data.local.DatabaseFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { DatabaseFactory(androidContext()) }
}
