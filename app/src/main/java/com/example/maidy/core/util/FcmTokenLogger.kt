package com.example.maidy.core.util

import android.content.Context
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

/**
 * Utility for logging and displaying FCM token (for testing)
 * USE THIS FOR TESTING ONLY - Remove in production
 */
object FcmTokenLogger {

    /**
     * Get current FCM token and log it
     * Useful for debugging and testing notifications
     */
    suspend fun logCurrentToken(context: Context? = null): String? {
        return try {
            val token = FirebaseMessaging.getInstance().token.await()
            println("🔑🔑🔑 FCM TOKEN 🔑🔑🔑")
            println("Token: $token")
            println("🔑🔑🔑🔑🔑🔑🔑🔑🔑🔑🔑🔑")

            // Optionally show in Toast for easy copying
            context?.let {
                Toast.makeText(
                    it,
                    "FCM Token logged (check console)",
                    Toast.LENGTH_LONG
                ).show()
            }

            token
        } catch (e: Exception) {
            println("❌ Failed to get FCM token: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    /**
     * Check if FCM token exists and is valid
     */
    suspend fun checkToken(): Boolean {
        return try {
            val token = FirebaseMessaging.getInstance().token.await()
            val isValid = token.isNotEmpty()

            if (isValid) {
                println("✅ FCM Token is valid")
                println("Token length: ${token.length} characters")
            } else {
                println("❌ FCM Token is empty")
            }

            isValid
        } catch (e: Exception) {
            println("❌ Failed to check FCM token: ${e.message}")
            false
        }
    }

    /**
     * Print token in multiple formats for easy copying
     */
    suspend fun printTokenFormatted() {
        try {
            val token = FirebaseMessaging.getInstance().token.await()

            println("═══════════════════════════════════════════════════════════")
            println("FCM TOKEN - Copy this for testing")
            println("═══════════════════════════════════════════════════════════")
            println("")
            println("Plain Text:")
            println(token)
            println("")
            println("For Firebase Console:")
            println("→ Go to: Firebase Console → Cloud Messaging")
            println("→ Click: Send test message")
            println("→ Paste: $token")
            println("")
            println("For curl:")
            println("curl -X POST https://fcm.googleapis.com/fcm/send \\")
            println("  -H \"Authorization: key=YOUR_SERVER_KEY\" \\")
            println("  -H \"Content-Type: application/json\" \\")
            println("  -d '{\"to\":\"$token\",\"notification\":{\"title\":\"Test\",\"body\":\"Test message\"}}'")
            println("")
            println("═══════════════════════════════════════════════════════════")

        } catch (e: Exception) {
            println("❌ Failed to get FCM token: ${e.message}")
        }
    }
}
