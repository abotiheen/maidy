package com.example.maidy.core.data

import com.example.maidy.core.model.Booking
import com.example.maidy.core.model.BookingStatus
import com.example.maidy.core.util.BookingDateUtils
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
     * Automatically calculates nextScheduledDate based on booking type
     */
    suspend fun createBooking(booking: Booking): Result<String> {
        return try {
            println("📝 BookingRepository: Creating booking - ${booking.id}")
            println("📝 BookingRepository: Booking type - ${booking.bookingType}")
            println("📝 BookingRepository: Is recurring - ${booking.isRecurring}")
            
            // Calculate nextScheduledDate if not already set
            val nextScheduledDate = booking.nextScheduledDate 
                ?: BookingDateUtils.calculateNextScheduledDate(booking)
            
            val bookingWithSchedule = booking.copy(
                nextScheduledDate = nextScheduledDate,
                updatedAt = Timestamp.now()
            )
            
            firestore.collection("bookings")
                .document(booking.id)
                .set(bookingWithSchedule)
                .await()
                
            println("✅ BookingRepository: Booking created successfully - ID: ${booking.id}")
            println("✅ BookingRepository: Next scheduled date - ${nextScheduledDate?.toDate()}")
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
    
    /**
     * Complete a booking and handle recurring logic
     * - For one-time bookings: just marks as completed
     * - For recurring bookings: calculates next date, resets status to CONFIRMED
     */
    suspend fun completeBooking(bookingId: String): Result<Unit> {
        return try {
            println("📝 BookingRepository: Completing booking - ID: $bookingId")
            
            // First, fetch the booking to check if it's recurring
            val bookingResult = getBookingById(bookingId)
            if (bookingResult.isFailure) {
                return Result.failure(bookingResult.exceptionOrNull()!!)
            }
            
            val booking = bookingResult.getOrNull()!!
            
            if (booking.isRecurring) {
                // For recurring bookings: calculate next date and reset to CONFIRMED
                val nextDate = BookingDateUtils.calculateNextRecurringDate(
                    preferredDay = booking.preferredDay,
                    recurringType = booking.recurringType!!,
                    fromDate = booking.nextScheduledDate // Calculate from current scheduled date
                )
                
                firestore.collection("bookings")
                    .document(bookingId)
                    .update(
                        mapOf(
                            "status" to BookingStatus.CONFIRMED.name,
                            "lastCompletedDate" to Timestamp.now(),
                            "nextScheduledDate" to nextDate,
                            "updatedAt" to Timestamp.now()
                        )
                    )
                    .await()
                    
                println("✅ BookingRepository: Recurring booking completed and rescheduled to ${nextDate.toDate()}")
            } else {
                // For one-time bookings: just mark as completed
                firestore.collection("bookings")
                    .document(bookingId)
                    .update(
                        mapOf(
                            "status" to BookingStatus.COMPLETED.name,
                            "updatedAt" to Timestamp.now()
                        )
                    )
                    .await()
                    
                println("✅ BookingRepository: One-time booking marked as completed")
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            println("❌ BookingRepository: Failed to complete booking - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    /**
     * Get active bookings for a user (not completed/cancelled, or recurring with future dates)
     * Sorted by next scheduled date
     */
    suspend fun getActiveUserBookings(userId: String): Result<List<Booking>> {
        return try {
            println("📖 BookingRepository: Fetching active bookings for user - ID: $userId")
            
            // Fetch all bookings for user (without orderBy to avoid requiring composite index)
            val snapshot = firestore.collection("bookings")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            
            val now = Timestamp.now()
            val bookings = snapshot.documents
                .mapNotNull { it.toObject(Booking::class.java) }
                .filter { booking ->
                    // Include booking if:
                    // 1. Status is not COMPLETED or CANCELLED
                    // 2. AND next scheduled date is not in the past
                    val isActive = booking.status != BookingStatus.COMPLETED && 
                                   booking.status != BookingStatus.CANCELLED
                    val hasFutureDate = booking.nextScheduledDate?.let { it >= now } ?: false
                    
                    isActive && hasFutureDate
                }
                .sortedBy { it.nextScheduledDate } // Sort in-memory after filtering
            
            println("✅ BookingRepository: Found ${bookings.size} active bookings")
            Result.success(bookings)
        } catch (e: Exception) {
            println("❌ BookingRepository: Failed to fetch active user bookings - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
}

