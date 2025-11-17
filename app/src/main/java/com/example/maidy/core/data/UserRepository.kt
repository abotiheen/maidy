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
    
    // Login with phone and password (no OTP)
    suspend fun loginWithPassword(phoneNumber: String, password: String): Result<User> {
        return try {
            val querySnapshot = firestore.collection("users")
                .whereEqualTo("phoneNumber", phoneNumber)
                .limit(1)
                .get()
                .await()
            
            if (querySnapshot.isEmpty) {
                return Result.failure(Exception("User not found"))
            }
            
            val user = querySnapshot.documents[0].toObject(User::class.java)
            
            if (user == null) {
                return Result.failure(Exception("Invalid user data"))
            }
            
            // Verify password
            if (user.password != password) {
                return Result.failure(Exception("Incorrect password"))
            }
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}