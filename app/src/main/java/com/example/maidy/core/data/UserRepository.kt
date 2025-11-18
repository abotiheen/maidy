package com.example.maidy.core.data

import android.net.Uri
import com.example.maidy.core.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class UserRepository(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
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
            println("📖 UserRepository: Fetching user from Firestore - ID: $userId")
            val userDoc = firestore.collection("users")
                .document(userId)
                .get()
                .await()
            
            println("📖 UserRepository: Document exists: ${userDoc.exists()}")
            val user = userDoc.toObject(User::class.java)
            if (user != null) {
                println("📖 UserRepository: User found - ${user.fullName}")
                Result.success(user)
            } else {
                println("❌ UserRepository: User document is null")
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            println("❌ UserRepository: Failed to fetch user - ${e.message}")
            e.printStackTrace()
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
    
    // Upload profile image to Firebase Storage
    suspend fun uploadProfileImage(userId: String, imageUri: Uri): Result<String> {
        return try {
            val fileName = "profile_images/${userId}/${UUID.randomUUID()}.jpg"
            println("📦 UserRepository: Uploading to path: $fileName")
            val storageRef = storage.reference.child(fileName)
            
            // Upload the file
            println("📦 UserRepository: Starting upload...")
            val uploadTask = storageRef.putFile(imageUri).await()
            println("📦 UserRepository: Upload complete! Bytes transferred: ${uploadTask.bytesTransferred}")
            
            // Get the download URL
            println("📦 UserRepository: Getting download URL...")
            val downloadUrl = storageRef.downloadUrl.await().toString()
            println("📦 UserRepository: Download URL obtained: $downloadUrl")
            
            Result.success(downloadUrl)
        } catch (e: Exception) {
            println("❌ UserRepository: Upload failed - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    // Update user's profile image URL in Firestore
    suspend fun updateProfileImageUrl(userId: String, imageUrl: String): Result<Unit> {
        return try {
            println("📝 UserRepository: Updating Firestore with image URL...")
            firestore.collection("users")
                .document(userId)
                .update("profileImageUrl", imageUrl)
                .await()
            println("📝 UserRepository: Firestore updated successfully!")
            Result.success(Unit)
        } catch (e: Exception) {
            println("❌ UserRepository: Firestore update failed - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    // Complete flow: Upload image and update user profile
    suspend fun uploadAndUpdateProfileImage(userId: String, imageUri: Uri): Result<String> {
        return try {
            // Upload image first
            val uploadResult = uploadProfileImage(userId, imageUri)
            if (uploadResult.isFailure) {
                return Result.failure(uploadResult.exceptionOrNull() ?: Exception("Upload failed"))
            }
            
            val imageUrl = uploadResult.getOrNull()!!
            
            // Update Firestore with the new URL
            val updateResult = updateProfileImageUrl(userId, imageUrl)
            if (updateResult.isFailure) {
                return Result.failure(updateResult.exceptionOrNull() ?: Exception("Update failed"))
            }
            
            Result.success(imageUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}