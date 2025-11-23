package com.example.maidy.core.service

import com.example.maidy.core.data.UserRepository
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

/**
 * Manager for FCM token operations
 */
class FcmTokenManager(
    private val userRepository: UserRepository
) {

    /**
     * Get and save current FCM token for a user
     */
    suspend fun refreshToken(userId: String): Result<String> {
        return try {
            println("🔄 FcmTokenManager: Refreshing FCM token for user: $userId")

            // Get current FCM token
            val token = FirebaseMessaging.getInstance().token.await()
            println("🔑 FcmTokenManager: Got FCM token: $token")

            // Save to Firestore
            val result = userRepository.updateFcmToken(userId, token)

            if (result.isSuccess) {
                println("✅ FcmTokenManager: Token saved successfully")
                Result.success(token)
            } else {
                println("❌ FcmTokenManager: Failed to save token")
                Result.failure(result.exceptionOrNull() ?: Exception("Failed to save token"))
            }
        } catch (e: Exception) {
            println("❌ FcmTokenManager: Error refreshing token - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Delete FCM token (useful for logout)
     */
    suspend fun deleteToken(userId: String): Result<Unit> {
        return try {
            println("🗑️ FcmTokenManager: Deleting FCM token for user: $userId")

            // Delete token from Firebase
            FirebaseMessaging.getInstance().deleteToken().await()

            // Clear token in Firestore
            userRepository.updateFcmToken(userId, "")

            println("✅ FcmTokenManager: Token deleted successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            println("❌ FcmTokenManager: Error deleting token - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Subscribe to a topic
     */
    suspend fun subscribeToTopic(topic: String): Result<Unit> {
        return try {
            println("📢 FcmTokenManager: Subscribing to topic: $topic")
            FirebaseMessaging.getInstance().subscribeToTopic(topic).await()
            println("✅ FcmTokenManager: Subscribed to topic: $topic")
            Result.success(Unit)
        } catch (e: Exception) {
            println("❌ FcmTokenManager: Error subscribing to topic - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Unsubscribe from a topic
     */
    suspend fun unsubscribeFromTopic(topic: String): Result<Unit> {
        return try {
            println("📢 FcmTokenManager: Unsubscribing from topic: $topic")
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic).await()
            println("✅ FcmTokenManager: Unsubscribed from topic: $topic")
            Result.success(Unit)
        } catch (e: Exception) {
            println("❌ FcmTokenManager: Error unsubscribing from topic - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
