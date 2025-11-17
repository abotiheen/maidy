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
    
    // Login - authenticate user with phone and password
    suspend fun loginUser(phoneNumber: String, password: String): Result<User> {
        return try {
            // Query for user with matching phone number
            val querySnapshot = firestore.collection("users")
                .whereEqualTo("phoneNumber", phoneNumber)
                .limit(1)  // Only need one result
                .get()
                .await()
            
            // Check if user exists
            if (querySnapshot.isEmpty) {
                return Result.failure(Exception("User not found"))
            }
            
            // Get the user document
            val userDoc = querySnapshot.documents[0]
            val user = userDoc.toObject(User::class.java)
            
            // Check if user data is valid
            if (user == null) {
                return Result.failure(Exception("Invalid user data"))
            }
            
            // Verify password
            if (user.password != password) {
                return Result.failure(Exception("Incorrect password"))
            }
            
            // Login successful
            Result.success(user)
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