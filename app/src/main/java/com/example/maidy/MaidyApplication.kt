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
        val isDebuggable = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

        Log.d("AppCheckSetup", "isDebuggable (Flag): $isDebuggable")

        firebaseAppCheck.installAppCheckProviderFactory(
            if (isDebuggable) {
                Log.d("AppCheckSetup", "Installing DebugAppCheckProviderFactory")
                // Use Debug provider for local development
                // Look for the debug token in Logcat and add it to Firebase Console
                DebugAppCheckProviderFactory.getInstance()
            } else {
                Log.d("AppCheckSetup", "Installing PlayIntegrityAppCheckProviderFactory")
                // Use Play Integrity for production
                PlayIntegrityAppCheckProviderFactory.getInstance()
            }
        )

        // Initialize Koin
        startKoin {
            androidContext(this@MaidyApplication)
            modules(appModule)
        }

        // Create notification channel
        NotificationHelper.createNotificationChannel(this)
    }
}
