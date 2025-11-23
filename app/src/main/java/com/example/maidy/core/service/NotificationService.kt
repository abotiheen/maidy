package com.example.maidy.core.service

import com.example.maidy.core.data.UserRepository
import com.example.maidy.core.model.BookingStatus
import com.example.maidy.core.util.NotificationHelper
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Service for sending notifications
 * This is used for local testing - in production, use Firebase Cloud Functions
 */
class NotificationService(
    private val userRepository: UserRepository,
    private val firestore: FirebaseFirestore
) {

    /**
     * Trigger notification when booking status changes
     * In production, this should be done via Firebase Cloud Functions
     */
    suspend fun notifyBookingStatusChange(
        bookingId: String,
        userId: String,
        newStatus: BookingStatus
    ): Result<Unit> {
        return try {
            println("📬 NotificationService: Preparing notification for booking: $bookingId")

            // Get user's FCM token
            val userResult = userRepository.getUserById(userId)
            if (userResult.isFailure) {
                println("❌ NotificationService: Failed to get user")
                return Result.failure(userResult.exceptionOrNull() ?: Exception("User not found"))
            }

            val user = userResult.getOrNull()!!
            val fcmToken = user.fcmToken

            if (fcmToken.isEmpty()) {
                println("⚠️ NotificationService: User has no FCM token")
                return Result.failure(Exception("User has no FCM token"))
            }

            // Get notification content
            val (title, message) = NotificationHelper.getStatusChangeNotification(newStatus.name)

            // Store notification in Firestore for Cloud Function to process
            // Cloud Function will read this and send via FCM
            val notification = hashMapOf(
                "userId" to userId,
                "fcmToken" to fcmToken,
                "bookingId" to bookingId,
                "title" to title,
                "body" to message,
                "type" to "BOOKING_STATUS_CHANGED",
                "status" to newStatus.name,
                "timestamp" to com.google.firebase.Timestamp.now(),
                "sent" to false
            )

            firestore.collection("notifications")
                .add(notification)
                .await()

            println("✅ NotificationService: Notification queued for Cloud Function")
            Result.success(Unit)
        } catch (e: Exception) {
            println("❌ NotificationService: Error - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Send a reminder notification for upcoming booking
     */
    suspend fun sendBookingReminder(
        bookingId: String,
        userId: String,
        message: String
    ): Result<Unit> {
        return try {
            val userResult = userRepository.getUserById(userId)
            if (userResult.isFailure) {
                return Result.failure(Exception("User not found"))
            }

            val user = userResult.getOrNull()!!
            val fcmToken = user.fcmToken

            if (fcmToken.isEmpty()) {
                return Result.failure(Exception("User has no FCM token"))
            }

            val notification = hashMapOf(
                "userId" to userId,
                "fcmToken" to fcmToken,
                "bookingId" to bookingId,
                "title" to "Booking Reminder",
                "body" to message,
                "type" to "BOOKING_REMINDER",
                "timestamp" to com.google.firebase.Timestamp.now(),
                "sent" to false
            )

            firestore.collection("notifications")
                .add(notification)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
