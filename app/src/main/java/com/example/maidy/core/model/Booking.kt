package com.example.maidy.core.model

import com.google.firebase.Timestamp

/**
 * Booking data model representing a service booking in Firestore
 */
data class Booking(
    val id: String = "",
    
    // User information
    val userId: String = "",
    val userFullName: String = "",
    val userPhoneNumber: String = "",
    val userProfileImageUrl: String = "",
    
    // Maid information
    val maidId: String = "",
    val maidFullName: String = "",
    val maidPhoneNumber: String = "",
    val maidProfileImageUrl: String = "",
    val maidHourlyRate: Double = 0.0,
    
    // Booking type
    val bookingType: BookingType = BookingType.DEEP_CLEANING,
    
    // Schedule information
    val isRecurring: Boolean = false,
    
    // For ONE-TIME bookings
    val bookingDate: Timestamp? = null,  // Specific date for one-time bookings
    val bookingTime: String = "",         // e.g., "10:00 AM"
    
    // For RECURRING bookings
    val recurringType: RecurringType? = null,  // WEEKLY, BIWEEKLY, MONTHLY
    val preferredDay: String = "",             // e.g., "Monday", "Tuesday"
    val preferredHour: String = "",            // e.g., "10:00 AM"
    
    // Additional information
    val specialInstructions: String = "",
    
    // Status
    val status: BookingStatus = BookingStatus.CONFIRMED,
    
    // Timestamps
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
)

/**
 * Booking types corresponding to service types
 */
enum class BookingType {
    DEEP_CLEANING,
    STANDARD_CLEANING,
    MOVE_OUT_CLEAN;
    
    companion object {
        fun fromString(value: String): BookingType {
            return when (value.uppercase()) {
                "DEEP_CLEANING" -> DEEP_CLEANING
                "STANDARD_CLEANING" -> STANDARD_CLEANING
                "MOVE_OUT_CLEAN" -> MOVE_OUT_CLEAN
                else -> DEEP_CLEANING
            }
        }
    }
}

/**
 * Recurring booking types
 */
enum class RecurringType {
    WEEKLY,
    BIWEEKLY,
    MONTHLY;
    
    companion object {
        fun fromString(value: String): RecurringType {
            return when (value.uppercase()) {
                "WEEKLY" -> WEEKLY
                "BIWEEKLY" -> BIWEEKLY
                "MONTHLY" -> MONTHLY
                else -> WEEKLY
            }
        }
    }
}

/**
 * Booking status
 */
enum class BookingStatus {
    CONFIRMED,
    ON_THE_WAY,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED;
    
    companion object {
        fun fromString(value: String): BookingStatus {
            return when (value.uppercase()) {
                "CONFIRMED" -> CONFIRMED
                "ON_THE_WAY" -> ON_THE_WAY
                "IN_PROGRESS" -> IN_PROGRESS
                "COMPLETED" -> COMPLETED
                "CANCELLED" -> CANCELLED
                else -> CONFIRMED
            }
        }
    }
}

