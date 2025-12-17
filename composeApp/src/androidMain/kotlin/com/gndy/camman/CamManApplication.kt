package com.gndy.camman

import android.app.Application
import com.gndy.camman.di.platformModule
import com.gndy.camman.di.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class CamManApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@CamManApplication)
            androidLogger()
            modules(sharedModule, platformModule)
        }
    }
}
