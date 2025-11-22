package com.elnfach.crawl_realms.app

import android.app.Application
import com.elnfach.crawl_realms.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(appModule)
            printLogger(Level.DEBUG)
        }
    }
}