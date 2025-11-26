package com.example.maidy

import android.app.Application
import android.content.pm.ApplicationInfo
import com.example.maidy.core.di.appModule
import com.example.maidy.core.util.NotificationHelper
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import android.util.Log

class MaidyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Initialize App Check
        val firebaseAppCheck = FirebaseAppCheck.getInstance()

        // TEMPORARY: Use Debug provider for testing locally installed APKs
        // Play Integrity requires installation from Play Store
        Log.d("AppCheckSetup", "Using Debug App Check Provider for testing")

        firebaseAppCheck.installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance()
        )

        /* TODO: Switch to Play Integrity after uploading to Play Store:
        val isDebuggable = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        
        firebaseAppCheck.installAppCheckProviderFactory(
            if (isDebuggable) {
                Log.d("AppCheckSetup", "Installing DebugAppCheckProviderFactory")
                DebugAppCheckProviderFactory.getInstance()
            } else {
                Log.d("AppCheckSetup", "Installing PlayIntegrityAppCheckProviderFactory")
                PlayIntegrityAppCheckProviderFactory.getInstance()
            }
        )
        */

        // Initialize Koin
        startKoin {
            androidContext(this@MaidyApplication)
            modules(appModule)
        }

        // Create notification channel
        NotificationHelper.createNotificationChannel(this)
    }
}
