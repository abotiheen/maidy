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
     * Get a specific maid by ID (for future use)
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
}

