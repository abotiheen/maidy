package com.example.maidy

import android.app.Application
import com.example.maidy.BuildConfig
import com.example.maidy.core.di.appModule
import com.google.firebase.FirebaseApp
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MaidyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Initialize Firebase App Check
        // Temporarily disabled to fix OTP issues
        // TODO: Re-enable after registering debug token in Firebase Console
        /*
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            if (BuildConfig.DEBUG) {
                // Use Debug provider for development/testing
                DebugAppCheckProviderFactory.getInstance()
            } else {
                // Use Play Integrity provider for production
                PlayIntegrityAppCheckProviderFactory.getInstance()
            }
        )
        */

        // Initialize Koin
        startKoin {
            androidContext(this@MaidyApplication)
            modules(appModule)
        }
    }
}