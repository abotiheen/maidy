package com.example.maidy.core.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.maidy.MainActivity
import com.example.maidy.R
import com.example.maidy.core.data.NotificationPreferencesManager
import com.example.maidy.core.data.UserRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/**
 * Firebase Cloud Messaging Service
 * Handles incoming push notifications and token refresh
 */
class MaidyFirebaseMessagingService : FirebaseMessagingService() {

    private val userRepository: UserRepository by inject()
    private val notificationPreferences: NotificationPreferencesManager by inject()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        private const val CHANNEL_ID = "maidy_booking_notifications"
        private const val CHANNEL_NAME = "Booking Notifications"
        private const val CHANNEL_DESCRIPTION = "Notifications for booking status updates"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    /**
     * Called when a new FCM token is generated
     * This happens on app install, token refresh, or device restore
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        println("🔑 FCM Token: $token")

        // Save the token to Firestore for the current user
        serviceScope.launch {
            saveTokenToFirestore(token)
        }
    }

    /**
     * Called when a message is received from FCM
     */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        println("📬 Message received from: ${message.from}")

        // Check if message contains a data payload
        if (message.data.isNotEmpty()) {
            println("📦 Message data payload: ${message.data}")
            handleDataPayload(message.data)
        }

        // Check if message contains a notification payload
        message.notification?.let {
            println("🔔 Message Notification Body: ${it.body}")
            showNotification(
                title = it.title ?: "Maidy",
                body = it.body ?: "",
                bookingId = message.data["bookingId"]
            )
        }
    }

    /**
     * Handle data payload from FCM
     */
    private fun handleDataPayload(data: Map<String, String>) {
        val notificationType = data["type"]
        val title = data["title"] ?: "Booking Update"
        val body = data["body"] ?: ""
        val bookingId = data["bookingId"]

        when (notificationType) {
            "BOOKING_STATUS_CHANGED" -> {
                showNotification(title, body, bookingId)
            }

            "BOOKING_REMINDER" -> {
                showNotification(title, body, bookingId)
            }

            else -> {
                showNotification(title, body, bookingId)
            }
        }
    }

    /**
     * Display notification to user
     */
    private fun showNotification(title: String, body: String, bookingId: String?) {
        // Check if user has disabled notifications
        if (!notificationPreferences.areNotificationsEnabled()) {
            println("🔕 Notifications disabled by user - skipping")
            return
        }

        // Check notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                println("⚠️ Notification permission not granted")
                return
            }
        }

        // Create intent to open the app
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            bookingId?.let { putExtra("bookingId", it) }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build notification
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .build()

        // Show notification
        val notificationId = bookingId?.hashCode() ?: System.currentTimeMillis().toInt()
        NotificationManagerCompat.from(this).notify(notificationId, notification)

        println("✅ Notification displayed: $title")
    }

    /**
     * Create notification channel for Android 8.0+
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                enableVibration(true)
            }

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            println("✅ Notification channel created")
        }
    }

    /**
     * Save FCM token to Firestore
     */
    private suspend fun saveTokenToFirestore(token: String) {
        try {
            // Get current user ID from SessionManager or SharedPreferences
            val sharedPrefs = getSharedPreferences("maidy_session", Context.MODE_PRIVATE)
            val userId = sharedPrefs.getString("user_id", null)

            if (userId != null) {
                val result = userRepository.updateFcmToken(userId, token)
                if (result.isSuccess) {
                    println("✅ FCM token saved to Firestore for user: $userId")
                } else {
                    println("❌ Failed to save FCM token: ${result.exceptionOrNull()?.message}")
                }
            } else {
                println("⚠️ No user logged in, FCM token not saved")
            }
        } catch (e: Exception) {
            println("❌ Error saving FCM token: ${e.message}")
            e.printStackTrace()
        }
    }
}
