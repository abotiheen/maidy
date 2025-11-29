package com.example.maidy.core.data

import android.net.Uri
import com.example.maidy.core.model.Maid
import com.example.maidy.core.model.MaidReview
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class MaidRepository(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {

    /**
     * Upload maid profile image to Firebase Storage
     */
    suspend fun uploadMaidProfileImage(maidId: String, imageUri: Uri): Result<String> {
        return try {
            val fileName = "maid_images/${maidId}/${UUID.randomUUID()}.jpg"
            println("📦 MaidRepository: Uploading maid image to path: $fileName")
            val storageRef = storage.reference.child(fileName)

            // Upload the file
            println("📦 MaidRepository: Starting upload...")
            val uploadTask = storageRef.putFile(imageUri).await()
            println("📦 MaidRepository: Upload complete! Bytes transferred: ${uploadTask.bytesTransferred}")

            // Get the download URL
            println("📦 MaidRepository: Getting download URL...")
            val downloadUrl = storageRef.downloadUrl.await().toString()
            println("📦 MaidRepository: Download URL obtained: $downloadUrl")

            Result.success(downloadUrl)
        } catch (e: Exception) {
            println("❌ MaidRepository: Upload failed - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Add a new maid to the maids collection
     */
    suspend fun addMaid(maid: Maid): Result<String> {
        return try {
            println("📝 MaidRepository: Adding maid - ${maid.fullName}")
            firestore.collection("maids")
                .document(maid.id)
                .set(maid)
                .await()
            println("✅ MaidRepository: Maid added successfully - ID: ${maid.id}")
            Result.success(maid.id)
        } catch (e: Exception) {
            println("❌ MaidRepository: Failed to add maid - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Add a review to a maid's reviews subcollection
     */
    suspend fun addReview(maidId: String, review: MaidReview): Result<Unit> {
        return try {
            println("📝 MaidRepository: Adding review for maid $maidId")
            firestore.collection("maids")
                .document(maidId)
                .collection("reviews")
                .document(review.id)
                .set(review)
                .await()
            println("✅ MaidRepository: Review added successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            println("❌ MaidRepository: Failed to add review - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Add multiple reviews for a maid
     */
    suspend fun addReviews(maidId: String, reviews: List<MaidReview>): Result<Unit> {
        return try {
            println("📝 MaidRepository: Adding ${reviews.size} reviews for maid $maidId")
            reviews.forEach { review ->
                firestore.collection("maids")
                    .document(maidId)
                    .collection("reviews")
                    .document(review.id)
                    .set(review)
                    .await()
            }
            println("✅ MaidRepository: All reviews added successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            println("❌ MaidRepository: Failed to add reviews - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Get all maids (for future use)
     */
    suspend fun getAllMaids(): Result<List<Maid>> {
        return try {
            val snapshot = firestore.collection("maids")
                .get()
                .await()
            val maids = snapshot.documents.mapNotNull { it.toObject(Maid::class.java) }
            Result.success(maids)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Search maids by name (server-side with client-side case-insensitive filtering)
     *
     * Since Firestore queries are case-sensitive and we don't have a lowercase field,
     * we fetch all maids and filter client-side for case-insensitive matching.
     * This is a temporary solution until we add a searchable lowercase field.
     */
    suspend fun searchMaidsByName(query: String): Result<List<Maid>> {
        return try {
            if (query.isBlank()) {
                return Result.success(emptyList())
            }

            println("🔍 MaidRepository: Searching for maids with query: $query")

            // Fetch all maids (cached by Firestore for performance)
            val snapshot = firestore.collection("maids")
                .get()
                .await()

            val allMaids = snapshot.documents.mapNotNull { it.toObject(Maid::class.java) }

            // Filter client-side for case-insensitive matching
            val filteredMaids = allMaids.filter { maid ->
                maid.fullName.contains(query, ignoreCase = true)
            }

            println("✅ MaidRepository: Found ${filteredMaids.size} maids matching '$query'")

            Result.success(filteredMaids)
        } catch (e: Exception) {
            println("❌ MaidRepository: Search failed - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Get a specific maid by ID
     */
    suspend fun getMaidById(maidId: String): Result<Maid> {
        return try {
            val doc = firestore.collection("maids")
                .document(maidId)
                .get()
                .await()
            val maid = doc.toObject(Maid::class.java)
            if (maid != null) {
                Result.success(maid)
            } else {
                Result.failure(Exception("Maid not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get reviews for a specific maid
     */
    suspend fun getMaidReviews(maidId: String): Result<List<MaidReview>> {
        return try {
            val snapshot = firestore.collection("maids")
                .document(maidId)
                .collection("reviews")
                .get()
                .await()
            val reviews = snapshot.documents.mapNotNull { it.toObject(MaidReview::class.java) }
            Result.success(reviews)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========================================
    // NEW: Authentication-related methods
    // ========================================

    /**
     * Create maid profile with Firebase Auth UID (used during registration)
     */
    suspend fun createMaidProfile(maidId: String, maid: Maid): Result<Unit> {
        return try {
            val maidWithId = maid.copy(id = maidId)
            firestore.collection("maids")
                .document(maidId)
                .set(maidWithId)
                .await()
            println("✅ MaidRepository: Maid profile created - ID: $maidId")
            Result.success(Unit)
        } catch (e: Exception) {
            println("❌ MaidRepository: Failed to create maid profile - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Login with phone and password (no OTP) for maid authentication
     */
    suspend fun loginWithPassword(phoneNumber: String, password: String): Result<Maid> {
        return try {
            println("🔐 MaidRepository: Attempting login for phone: $phoneNumber")
            val querySnapshot = firestore.collection("maids")
                .whereEqualTo("phoneNumber", phoneNumber)
                .limit(1)
                .get()
                .await()

            if (querySnapshot.isEmpty) {
                println("❌ MaidRepository: No maid found with phone: $phoneNumber")
                return Result.failure(Exception("Maid not found"))
            }

            val maid = querySnapshot.documents[0].toObject(Maid::class.java)

            if (maid == null) {
                println("❌ MaidRepository: Invalid maid data")
                return Result.failure(Exception("Invalid maid data"))
            }

            // Verify password
            if (maid.password != password) {
                println("❌ MaidRepository: Incorrect password")
                return Result.failure(Exception("Incorrect password"))
            }

            println("✅ MaidRepository: Login successful for maid: ${maid.fullName}")
            Result.success(maid)
        } catch (e: Exception) {
            println("❌ MaidRepository: Login failed - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Update maid's profile image URL in Firestore
     */
    suspend fun updateProfileImageUrl(maidId: String, imageUrl: String): Result<Unit> {
        return try {
            println("📝 MaidRepository: Updating profile image URL for maid: $maidId")
            firestore.collection("maids")
                .document(maidId)
                .update("profileImageUrl", imageUrl)
                .await()
            println("✅ MaidRepository: Profile image URL updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            println("❌ MaidRepository: Failed to update profile image URL - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Complete flow: Upload image and update maid profile
     */
    suspend fun uploadAndUpdateProfileImage(maidId: String, imageUri: Uri): Result<String> {
        return try {
            // Upload image first
            val uploadResult = uploadMaidProfileImage(maidId, imageUri)
            if (uploadResult.isFailure) {
                return Result.failure(uploadResult.exceptionOrNull() ?: Exception("Upload failed"))
            }

            val imageUrl = uploadResult.getOrNull()!!

            // Update Firestore with the new URL
            val updateResult = updateProfileImageUrl(maidId, imageUrl)
            if (updateResult.isFailure) {
                return Result.failure(updateResult.exceptionOrNull() ?: Exception("Update failed"))
            }

            Result.success(imageUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update FCM token for push notifications
     */
    suspend fun updateFcmToken(maidId: String, token: String): Result<Unit> {
        return try {
            println("📝 MaidRepository: Updating FCM token for maid: $maidId")
            firestore.collection("maids")
                .document(maidId)
                .update("fcmToken", token)
                .await()
            println("✅ MaidRepository: FCM token updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            println("❌ MaidRepository: Failed to update FCM token - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Get FCM token for a maid
     */
    suspend fun getFcmToken(maidId: String): Result<String> {
        return try {
            val maidResult = getMaidById(maidId)
            if (maidResult.isSuccess) {
                val maid = maidResult.getOrNull()!!
                Result.success(maid.fcmToken)
            } else {
                Result.failure(maidResult.exceptionOrNull() ?: Exception("Maid not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update maid's availability status
     */
    suspend fun updateMaidAvailability(maidId: String, isAvailable: Boolean): Result<Unit> {
        return try {
            println("📝 MaidRepository: Updating availability for maid: $maidId to $isAvailable")
            firestore.collection("maids")
                .document(maidId)
                .update("available", isAvailable)
                .await()
            println("✅ MaidRepository: Availability updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            println("❌ MaidRepository: Failed to update availability - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Delete a maid from the maids collection
     */
    suspend fun deleteMaid(maidId: String): Result<Unit> {
        return try {
            println("🗑️ MaidRepository: Deleting maid - ID: $maidId")
            firestore.collection("maids")
                .document(maidId)
                .delete()
                .await()
            println("✅ MaidRepository: Maid deleted successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            println("❌ MaidRepository: Failed to delete maid - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
