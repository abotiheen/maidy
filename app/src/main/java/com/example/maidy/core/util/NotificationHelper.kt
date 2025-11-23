package com.example.maidy.core.util

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

/**
 * Helper class for managing notifications
 */
object NotificationHelper {

    private const val CHANNEL_ID = "maidy_booking_notifications"
    private const val CHANNEL_NAME = "Booking Notifications"
    private const val CHANNEL_DESCRIPTION = "Notifications for booking status updates"

    /**
     * Create notification channel (required for Android 8.0+)
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Show a booking status notification
     */
    fun showBookingNotification(
        context: Context,
        title: String,
        message: String,
        bookingId: String? = null
    ) {
        // Check notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                println("⚠️ Notification permission not granted")
                return
            }
        }

        // Create intent to open the app
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            bookingId?.let { putExtra("bookingId", it) }
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .build()

        // Show notification
        val notificationId = bookingId?.hashCode() ?: System.currentTimeMillis().toInt()
        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }

    /**
     * Check if notification permission is granted
     */
    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Permission not required for Android 12 and below
        }
    }

    /**
     * Get status change notification title and message
     */
    fun getStatusChangeNotification(status: String): Pair<String, String> {
        return when (status.uppercase()) {
            "PENDING" -> Pair(
                "Booking Pending",
                "Your booking request has been received and is pending confirmation."
            )

            "CONFIRMED" -> Pair(
                "Booking Confirmed! 🎉",
                "Your booking has been confirmed. Your maid will arrive at the scheduled time."
            )

            "ON_THE_WAY" -> Pair(
                "Maid On The Way! 🚗",
                "Your maid is on the way to your location."
            )

            "IN_PROGRESS" -> Pair(
                "Service Started 🧹",
                "Your maid has started the cleaning service."
            )

            "COMPLETED" -> Pair(
                "Service Completed ✅",
                "Your cleaning service has been completed. Please rate your experience!"
            )

            "CANCELLED" -> Pair(
                "Booking Cancelled",
                "Your booking has been cancelled."
            )

            else -> Pair(
                "Booking Update",
                "Your booking status has been updated."
            )
        }
    }
}
