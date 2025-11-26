package com.example.maidy.core.service

import com.example.maidy.core.data.MaidRepository
import com.example.maidy.core.data.UserRepository
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

/**
 * Manager for FCM token operations
 * Supports both users and maids
 */
class FcmTokenManager(
    private val userRepository: UserRepository,
    private val maidRepository: MaidRepository
) {

    /**
     * Get and save current FCM token for a user
     */
    suspend fun refreshToken(userId: String, isCustomer: Boolean = true): Result<String> {
        return try {
            val userType = if (isCustomer) "customer" else "maid"
            println("🔄 FcmTokenManager: Refreshing FCM token for $userType: $userId")

            // Get current FCM token
            val token = FirebaseMessaging.getInstance().token.await()
            println("🔑 FcmTokenManager: Got FCM token: $token")

            // Save to Firestore - different collections based on user type
            val result = if (isCustomer) {
                userRepository.updateFcmToken(userId, token)
            } else {
                maidRepository.updateFcmToken(userId, token)
            }

            if (result.isSuccess) {
                println("✅ FcmTokenManager: Token saved successfully for $userType")
                Result.success(token)
            } else {
                println("❌ FcmTokenManager: Failed to save token for $userType")
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
    suspend fun deleteToken(userId: String, isCustomer: Boolean = true): Result<Unit> {
        return try {
            val userType = if (isCustomer) "customer" else "maid"
            println("🗑️ FcmTokenManager: Deleting FCM token for $userType: $userId")

            // Delete token from Firebase
            FirebaseMessaging.getInstance().deleteToken().await()

            // Clear token in Firestore - different collections based on user type
            val result = if (isCustomer) {
                userRepository.updateFcmToken(userId, "")
            } else {
                maidRepository.updateFcmToken(userId, "")
            }

            if (result.isSuccess) {
                println("✅ FcmTokenManager: Token deleted successfully for $userType")
            }

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
