package com.geocache.zumhi.worldtreasure.tanveer

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class BaseApp: Application() {

    override fun onCreate() {

        startKoin {
            androidLogger()
            androidContext(this@BaseApp)
            modules(listOf(applicationModule))
        }
        super.onCreate()
    }
}