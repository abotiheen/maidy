package com.example.maidy

import android.app.Application
import com.example.maidy.core.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MaidyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Koin
        startKoin {
            androidContext(this@MaidyApplication)
            modules(appModule)
        }
    }
}