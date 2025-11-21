package com.example.maidy.core.data

import com.example.maidy.core.model.Booking
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Repository for managing bookings in Firestore
 */
class BookingRepository(
    private val firestore: FirebaseFirestore
) {
    
    /**
     * Create a new booking in Firestore
     */
    suspend fun createBooking(booking: Booking): Result<String> {
        return try {
            println("📝 BookingRepository: Creating booking - ${booking.id}")
            println("📝 BookingRepository: Booking type - ${booking.bookingType}")
            println("📝 BookingRepository: Is recurring - ${booking.isRecurring}")
            
            val bookingWithTimestamp = booking.copy(
                updatedAt = Timestamp.now()
            )
            
            firestore.collection("bookings")
                .document(booking.id)
                .set(bookingWithTimestamp)
                .await()
                
            println("✅ BookingRepository: Booking created successfully - ID: ${booking.id}")
            Result.success(booking.id)
        } catch (e: Exception) {
            println("❌ BookingRepository: Failed to create booking - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    /**
     * Get a specific booking by ID
     */
    suspend fun getBookingById(bookingId: String): Result<Booking> {
        return try {
            println("📖 BookingRepository: Fetching booking - ID: $bookingId")
            val doc = firestore.collection("bookings")
                .document(bookingId)
                .get()
                .await()
                
            val booking = doc.toObject(Booking::class.java)
            if (booking != null) {
                println("✅ BookingRepository: Booking found")
                Result.success(booking)
            } else {
                println("❌ BookingRepository: Booking not found")
                Result.failure(Exception("Booking not found"))
            }
        } catch (e: Exception) {
            println("❌ BookingRepository: Failed to fetch booking - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    /**
     * Get all bookings for a specific user
     */
    suspend fun getUserBookings(userId: String): Result<List<Booking>> {
        return try {
            println("📖 BookingRepository: Fetching bookings for user - ID: $userId")
            val snapshot = firestore.collection("bookings")
                .whereEqualTo("userId", userId)
                .get()
                .await()
                
            val bookings = snapshot.documents.mapNotNull { it.toObject(Booking::class.java) }
            println("✅ BookingRepository: Found ${bookings.size} bookings")
            Result.success(bookings)
        } catch (e: Exception) {
            println("❌ BookingRepository: Failed to fetch user bookings - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    /**
     * Get all bookings for a specific maid
     */
    suspend fun getMaidBookings(maidId: String): Result<List<Booking>> {
        return try {
            println("📖 BookingRepository: Fetching bookings for maid - ID: $maidId")
            val snapshot = firestore.collection("bookings")
                .whereEqualTo("maidId", maidId)
                .get()
                .await()
                
            val bookings = snapshot.documents.mapNotNull { it.toObject(Booking::class.java) }
            println("✅ BookingRepository: Found ${bookings.size} bookings")
            Result.success(bookings)
        } catch (e: Exception) {
            println("❌ BookingRepository: Failed to fetch maid bookings - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    /**
     * Update booking status
     */
    suspend fun updateBookingStatus(bookingId: String, newStatus: com.example.maidy.core.model.BookingStatus): Result<Unit> {
        return try {
            println("📝 BookingRepository: Updating booking status - ID: $bookingId, New status: $newStatus")
            firestore.collection("bookings")
                .document(bookingId)
                .update(
                    mapOf(
                        "status" to newStatus.name,
                        "updatedAt" to Timestamp.now()
                    )
                )
                .await()
                
            println("✅ BookingRepository: Booking status updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            println("❌ BookingRepository: Failed to update booking status - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    /**
     * Cancel a booking
     */
    suspend fun cancelBooking(bookingId: String): Result<Unit> {
        return updateBookingStatus(bookingId, com.example.maidy.core.model.BookingStatus.CANCELLED)
    }
    
    /**
     * Update special instructions
     */
    suspend fun updateSpecialInstructions(bookingId: String, instructions: String): Result<Unit> {
        return try {
            println("📝 BookingRepository: Updating special instructions - ID: $bookingId")
            firestore.collection("bookings")
                .document(bookingId)
                .update(
                    mapOf(
                        "specialInstructions" to instructions,
                        "updatedAt" to Timestamp.now()
                    )
                )
                .await()
                
            println("✅ BookingRepository: Special instructions updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            println("❌ BookingRepository: Failed to update special instructions - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
}

