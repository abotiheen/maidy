package com.example.maidy.core.data

import com.example.maidy.core.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val firestore: FirebaseFirestore
) {
    // Create user profile with Firebase Auth UID
    suspend fun createUserProfile(userId: String, user: User): Result<Unit> {
        return try {
            val userWithId = user.copy(id = userId)
            firestore.collection("users")
                .document(userId)
                .set(userWithId)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Get user by ID
    suspend fun getUserById(userId: String): Result<User> {
        return try {
            val userDoc = firestore.collection("users")
                .document(userId)
                .get()
                .await()
            
            val user = userDoc.toObject(User::class.java)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}