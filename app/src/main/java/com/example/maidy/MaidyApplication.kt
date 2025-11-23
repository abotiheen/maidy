package com.example.maidy

import android.app.Application
import com.example.maidy.core.di.appModule
import com.example.maidy.core.util.NotificationHelper
import com.google.firebase.FirebaseApp
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MaidyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // App Check COMPLETELY DISABLED
        // Reason: Attestation failing with 403, causing OTP to fail
        // TODO: Fix App Check configuration before production:
        //  1. Ensure SHA-1/SHA-256 are correct
        //  2. Link Play Integrity API to Firebase project
        //  3. Register debug tokens for development
        //  4. Set App Check to "Unenforced" in Firebase Console during development
        //  5. Enable enforcement only for production builds

        // Initialize Koin
        startKoin {
            androidContext(this@MaidyApplication)
            modules(appModule)
        }

        // Create notification channel
        NotificationHelper.createNotificationChannel(this)
    }
}
