package com.example.maidy.core.data

import com.example.maidy.core.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val firestore: FirebaseFirestore
) {
    // Create a new user
    suspend fun createUser(user: User): Result<String> {
        return try {
            // Generate a unique document ID first
            val userDocRef = firestore.collection("users").document()
            val userId = userDocRef.id
            
            // Create user with the generated ID
            val userWithId = user.copy(id = userId)
            
            // Save to Firestore
            userDocRef.set(userWithId).await()
            
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Check if phone number already exists
    suspend fun isPhoneNumberTaken(phoneNumber: String): Boolean {
        return try {
            val querySnapshot = firestore.collection("users")
                .whereEqualTo("phoneNumber", phoneNumber)
                .get()
                .await()

            !querySnapshot.isEmpty  // Returns true if phone exists
        } catch (e: Exception) {
            false
        }
    }
}