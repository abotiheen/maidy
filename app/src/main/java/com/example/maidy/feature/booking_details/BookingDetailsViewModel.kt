package com.example.maidy.feature.booking_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maidy.core.data.BookingRepository
import com.example.maidy.core.data.MaidRepository
import com.example.maidy.core.data.SessionManager
import com.example.maidy.core.data.UserRepository
import com.example.maidy.core.model.Booking
import com.example.maidy.core.model.BookingType
import com.example.maidy.core.model.RecurringType
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * ViewModel for the Booking Details screen
 * Manages the state for service booking with unidirectional data flow
 */
class BookingDetailsViewModel(
    private val maidId: String,
    private val maidRepository: MaidRepository,
    private val userRepository: UserRepository,
    private val bookingRepository: BookingRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(BookingDetailsUiState())
    val uiState: StateFlow<BookingDetailsUiState> = _uiState.asStateFlow()
    
    init {
        loadMaidDetails()
    }
    
    /**
     * Load maid details from Firebase
     */
    private fun loadMaidDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMaid = true) }
            
            val result = maidRepository.getMaidById(maidId)
            result.fold(
                onSuccess = { maid ->
                    println("✅ BookingDetailsViewModel: Maid loaded - ${maid.fullName}")
                    _uiState.update { 
                        it.copy(
                            maidName = maid.fullName,
                            maidRating = maid.averageRating,
                            maidReviewsCount = maid.reviewCount,
                            maidHourlyRate = maid.hourlyRate,
                            maidPhoneNumber = maid.phoneNumber,
                            maidProfileImageUrl = maid.profileImageUrl,
                            isVerified = maid.verified,
                            isLoadingMaid = false
                        )
                    }
                },
                onFailure = { error ->
                    println("❌ BookingDetailsViewModel: Failed to load maid - ${error.message}")
                    _uiState.update { 
                        it.copy(
                            isLoadingMaid = false,
                            errorMessage = "Failed to load maid details: ${error.message}"
                        )
                    }
                }
            )
        }
    }
    
    /**
     * Handle UI events from the screen
     */
    fun onEvent(event: BookingDetailsEvent) {
        when (event) {
            is BookingDetailsEvent.ServiceTypeSelected -> {
                _uiState.update { it.copy(selectedServiceType = event.serviceType) }
            }
            is BookingDetailsEvent.ScheduleTypeSelected -> {
                _uiState.update { it.copy(scheduleType = event.scheduleType) }
            }
            is BookingDetailsEvent.DateSelected -> {
                _uiState.update { it.copy(selectedDate = event.date) }
            }
            is BookingDetailsEvent.TimeSelected -> {
                _uiState.update { it.copy(selectedTime = event.time) }
            }
            is BookingDetailsEvent.DaySelected -> {
                // For recurring bookings, only allow single day selection
                _uiState.update { it.copy(selectedDay = event.day) }
            }
            is BookingDetailsEvent.RecurringTypeSelected -> {
                _uiState.update { it.copy(recurringType = event.recurringType) }
            }
            is BookingDetailsEvent.RecurringTimeSelected -> {
                _uiState.update { it.copy(recurringTime = event.time) }
            }
            is BookingDetailsEvent.NotesChanged -> {
                _uiState.update { it.copy(specialInstructions = event.notes) }
            }
            BookingDetailsEvent.ConfirmBooking -> {
                confirmBooking()
            }
        }
    }
    
    /**
     * Process booking confirmation
     * Creates booking in Firebase with all required information
     */
    private fun confirmBooking() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // Get current user ID
                val userId = sessionManager.getCurrentUserId()
                if (userId == null) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = "User not logged in"
                        )
                    }
                    return@launch
                }
                
                // Fetch current user details
                val userResult = userRepository.getUserById(userId)
                if (userResult.isFailure) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to load user details"
                        )
                    }
                    return@launch
                }
                
                val user = userResult.getOrNull()!!
                val currentState = _uiState.value
                
                // Generate booking ID
                val bookingId = UUID.randomUUID().toString()
                
                // Convert ServiceType to BookingType
                val bookingType = when (currentState.selectedServiceType) {
                    ServiceType.DEEP_CLEANING -> BookingType.DEEP_CLEANING
                    ServiceType.STANDARD_CLEANING -> BookingType.STANDARD_CLEANING
                    ServiceType.MOVE_OUT_CLEAN -> BookingType.MOVE_OUT_CLEAN
                }
                
                // Create booking based on schedule type
                val booking = if (currentState.scheduleType == ScheduleType.ONE_TIME) {
                    // Parse date string to Timestamp
                    val bookingDate = parseDateToTimestamp(currentState.selectedDate)
                    
                    Booking(
                        id = bookingId,
                        userId = user.id,
                        userFullName = user.fullName,
                        userPhoneNumber = user.phoneNumber,
                        userProfileImageUrl = user.profileImageUrl,
                        maidId = maidId,
                        maidFullName = currentState.maidName,
                        maidPhoneNumber = currentState.maidPhoneNumber,
                        maidProfileImageUrl = currentState.maidProfileImageUrl,
                        maidHourlyRate = currentState.maidHourlyRate,
                        bookingType = bookingType,
                        isRecurring = false,
                        bookingDate = bookingDate,
                        bookingTime = currentState.selectedTime,
                        specialInstructions = currentState.specialInstructions
                    )
                } else {
                    // Recurring booking - use selected recurring type
                    val recurringType = currentState.recurringType
                    
                    // Get preferred day (full name like "Monday", "Tuesday")
                    val preferredDay = currentState.selectedDay?.fullName ?: ""
                    
                    Booking(
                        id = bookingId,
                        userId = user.id,
                        userFullName = user.fullName,
                        userPhoneNumber = user.phoneNumber,
                        userProfileImageUrl = user.profileImageUrl,
                        maidId = maidId,
                        maidFullName = currentState.maidName,
                        maidPhoneNumber = currentState.maidPhoneNumber,
                        maidProfileImageUrl = currentState.maidProfileImageUrl,
                        maidHourlyRate = currentState.maidHourlyRate,
                        bookingType = bookingType,
                        isRecurring = true,
                        recurringType = recurringType,
                        preferredDay = preferredDay,
                        preferredHour = currentState.recurringTime,
                        specialInstructions = currentState.specialInstructions
                    )
                }
                
                // Save booking to Firebase
                val result = bookingRepository.createBooking(booking)
                
                result.fold(
                    onSuccess = { bookingId ->
                        println("✅ BookingDetailsViewModel: Booking created - ID: $bookingId")
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                bookingConfirmed = true
                            )
                        }
                    },
                    onFailure = { error ->
                        println("❌ BookingDetailsViewModel: Failed to create booking - ${error.message}")
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                errorMessage = "Failed to create booking: ${error.message}"
                            )
                        }
                    }
                )
                
            } catch (e: Exception) {
                println("❌ BookingDetailsViewModel: Exception - ${e.message}")
                e.printStackTrace()
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "An error occurred: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * Parse date string to Timestamp
     * Format expected: "November 20, 2024"
     */
    private fun parseDateToTimestamp(dateString: String): Timestamp {
        return try {
            val format = SimpleDateFormat("MMMM dd, yyyy", Locale.US)
            val date = format.parse(dateString)
            if (date != null) {
                Timestamp(date)
            } else {
                Timestamp.now()
            }
        } catch (e: Exception) {
            println("⚠️ BookingDetailsViewModel: Failed to parse date - ${e.message}")
            Timestamp.now()
        }
    }
}

/**
 * UI State for Booking Details screen
 */
data class BookingDetailsUiState(
    // Maid information
    val maidName: String = "",
    val maidRating: Double = 0.0,
    val maidReviewsCount: Int = 0,
    val maidHourlyRate: Double = 0.0,
    val maidPhoneNumber: String = "",
    val maidProfileImageUrl: String = "",
    val isVerified: Boolean = false,
    
    // Booking details
    val selectedServiceType: ServiceType = ServiceType.DEEP_CLEANING,
    val scheduleType: ScheduleType = ScheduleType.ONE_TIME,
    val selectedDate: String = "November 20, 2024",
    val selectedTime: String = "10:00 AM",
    val selectedDay: Day? = null,  // Single day for recurring bookings
    val recurringType: RecurringType = RecurringType.WEEKLY,  // Weekly, Biweekly, or Monthly
    val recurringTime: String = "10:00 AM",
    val specialInstructions: String = "",
    
    // Loading states
    val isLoadingMaid: Boolean = false,
    val isLoading: Boolean = false,
    val bookingConfirmed: Boolean = false,
    val errorMessage: String? = null
)

/**
 * Events that can be triggered from the UI
 */
sealed class BookingDetailsEvent {
    data class ServiceTypeSelected(val serviceType: ServiceType) : BookingDetailsEvent()
    data class ScheduleTypeSelected(val scheduleType: ScheduleType) : BookingDetailsEvent()
    data class DateSelected(val date: String) : BookingDetailsEvent()
    data class TimeSelected(val time: String) : BookingDetailsEvent()
    data class DaySelected(val day: Day) : BookingDetailsEvent()
    data class RecurringTypeSelected(val recurringType: RecurringType) : BookingDetailsEvent()
    data class RecurringTimeSelected(val time: String) : BookingDetailsEvent()
    data class NotesChanged(val notes: String) : BookingDetailsEvent()
    object ConfirmBooking : BookingDetailsEvent()
}

/**
 * Service types available for booking
 */
enum class ServiceType(val title: String, val description: String) {
    DEEP_CLEANING("Deep Cleaning", "Thorough cleaning of entire home"),
    STANDARD_CLEANING("Standard Cleaning", "Regular maintenance cleaning"),
    MOVE_OUT_CLEAN("Move-out Clean", "Complete cleaning for moving")
}

/**
 * Schedule types for booking
 */
enum class ScheduleType {
    ONE_TIME,
    RECURRING
}

/**
 * Days of the week for recurring bookings
 */
enum class Day(val shortName: String, val fullName: String) {
    SUNDAY("S", "Sunday"),
    MONDAY("M", "Monday"),
    TUESDAY("T", "Tuesday"),
    WEDNESDAY("W", "Wednesday"),
    THURSDAY("T", "Thursday"),
    FRIDAY("F", "Friday"),
    SATURDAY("S", "Saturday")
}

