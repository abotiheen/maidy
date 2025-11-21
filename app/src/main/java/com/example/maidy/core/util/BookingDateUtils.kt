package com.example.maidy.core.util

import com.example.maidy.core.model.Booking
import com.example.maidy.core.model.RecurringType
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility functions for calculating booking dates and formatting
 */
object BookingDateUtils {
    
    /**
     * Calculate the next scheduled date for a booking
     * - For one-time bookings: returns the bookingDate
     * - For recurring bookings: calculates next occurrence based on preferredDay and recurringType
     */
    fun calculateNextScheduledDate(booking: Booking): Timestamp? {
        return if (booking.isRecurring) {
            calculateNextRecurringDate(
                preferredDay = booking.preferredDay,
                recurringType = booking.recurringType ?: RecurringType.WEEKLY,
                fromDate = booking.lastCompletedDate // If null, uses current date
            )
        } else {
            booking.bookingDate
        }
    }
    
    /**
     * Calculate the next occurrence of a recurring booking
     */
    fun calculateNextRecurringDate(
        preferredDay: String,
        recurringType: RecurringType,
        fromDate: Timestamp? = null
    ): Timestamp {
        val calendar = Calendar.getInstance()
        
        // Start from the last completed date or current date
        if (fromDate != null) {
            calendar.time = fromDate.toDate()
            // Add the interval first
            when (recurringType) {
                RecurringType.WEEKLY -> calendar.add(Calendar.DAY_OF_YEAR, 7)
                RecurringType.BIWEEKLY -> calendar.add(Calendar.DAY_OF_YEAR, 14)
                RecurringType.MONTHLY -> {
                    // For monthly, find the first occurrence of preferredDay in next month
                    calendar.add(Calendar.MONTH, 1)
                    calendar.set(Calendar.DAY_OF_MONTH, 1)
                    // Now find the first matching day
                    val targetDayOfWeek = getDayOfWeek(preferredDay)
                    while (calendar.get(Calendar.DAY_OF_WEEK) != targetDayOfWeek) {
                        calendar.add(Calendar.DAY_OF_MONTH, 1)
                    }
                }
            }
        } else {
            // First time scheduling - find next occurrence from now
            val targetDayOfWeek = getDayOfWeek(preferredDay)
            val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            
            when (recurringType) {
                RecurringType.WEEKLY, RecurringType.BIWEEKLY -> {
                    // Find next occurrence of the preferred day
                    var daysToAdd = targetDayOfWeek - currentDayOfWeek
                    if (daysToAdd <= 0) {
                        daysToAdd += 7 // Move to next week
                    }
                    calendar.add(Calendar.DAY_OF_YEAR, daysToAdd)
                }
                RecurringType.MONTHLY -> {
                    // Find first occurrence of preferred day in current or next month
                    // First, try current month
                    val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
                    calendar.set(Calendar.DAY_OF_MONTH, 1)
                    
                    // Find first matching day in current month
                    while (calendar.get(Calendar.DAY_OF_WEEK) != targetDayOfWeek) {
                        calendar.add(Calendar.DAY_OF_MONTH, 1)
                    }
                    
                    // If this date has passed, move to next month
                    if (calendar.get(Calendar.DAY_OF_MONTH) < currentDay) {
                        calendar.add(Calendar.MONTH, 1)
                        calendar.set(Calendar.DAY_OF_MONTH, 1)
                        while (calendar.get(Calendar.DAY_OF_WEEK) != targetDayOfWeek) {
                            calendar.add(Calendar.DAY_OF_MONTH, 1)
                        }
                    }
                }
            }
        }
        
        // Reset time to start of day
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        
        return Timestamp(calendar.time)
    }
    
    /**
     * Format a date for display: "Nov 21, 2024 at 10:00 AM"
     */
    fun formatBookingDateTime(date: Timestamp?, time: String): String {
        if (date == null) return "Date not set"
        
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(date.toDate())
        
        // Handle empty time string
        return if (time.isNotEmpty()) {
            "$formattedDate at $time"
        } else {
            "$formattedDate (time not set)"
        }
    }
    
    /**
     * Format date only: "Nov 21, 2024"
     */
    fun formatDate(date: Timestamp?): String {
        if (date == null) return "Date not set"
        
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return dateFormat.format(date.toDate())
    }
    
    /**
     * Check if a booking date is in the past
     */
    fun isBookingInPast(booking: Booking): Boolean {
        val nextDate = booking.nextScheduledDate ?: return false
        val now = Calendar.getInstance().time
        return nextDate.toDate().before(now)
    }
    
    /**
     * Convert day name to Calendar day constant
     */
    private fun getDayOfWeek(dayName: String): Int {
        return when (dayName.lowercase()) {
            "sunday" -> Calendar.SUNDAY
            "monday" -> Calendar.MONDAY
            "tuesday" -> Calendar.TUESDAY
            "wednesday" -> Calendar.WEDNESDAY
            "thursday" -> Calendar.THURSDAY
            "friday" -> Calendar.FRIDAY
            "saturday" -> Calendar.SATURDAY
            else -> Calendar.MONDAY // Default
        }
    }
    
    /**
     * Get display time from booking (handles both one-time and recurring)
     */
    fun getBookingTime(booking: Booking): String {
        return if (booking.isRecurring) {
            booking.preferredHour
        } else {
            booking.bookingTime
        }
    }
}

