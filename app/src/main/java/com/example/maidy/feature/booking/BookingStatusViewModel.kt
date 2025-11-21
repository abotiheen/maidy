package com.example.maidy.feature.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maidy.core.data.BookingRepository
import com.example.maidy.core.data.MaidRepository
import com.example.maidy.core.model.Booking
import com.example.maidy.core.model.BookingStatus
import com.example.maidy.core.model.RecurringType
import com.example.maidy.core.util.BookingDateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MaidInfo(
    val name: String = "Jane Doe",
    val rating: Float = 4.9f,
    val profileImageUrl: String = "",
    val arrivingInMinutes: Int = 15,
    val description: String = "Experienced professional cleaner with 5+ years expertise in residential cleaning.",
    val phoneNumber: String = ""
)

data class BookingServiceDetails(
    val service: String = "Standard Cleaning",
    val date: String = "Nov 18, 2023",
    val time: String = "10:00 AM"
)

data class RecurringBookingDetails(
    val frequency: String = "Weekly",
    val day: String = "Monday",
    val time: String = "10:00 AM"
)

data class BookingStatusUiState(
    val isLoading: Boolean = true,
    val bookingDetails: BookingServiceDetails = BookingServiceDetails(),
    val currentStatus: BookingStatus = BookingStatus.PENDING,
    val maidInfo: MaidInfo = MaidInfo(),
    val statusMessage: String = "",
    val isRecurring: Boolean = false,
    val recurringDetails: RecurringBookingDetails? = null,
    val errorMessage: String? = null,
    val showCancelDialog: Boolean = false,
    val shouldOpenDialer: String? = null, // Phone number to dial, null when not needed
    val shouldNavigateToSOS: Boolean = false // Navigate to SOS screen
)

class BookingStatusViewModel(
    private val bookingId: String,
    private val bookingRepository: BookingRepository,
    private val maidRepository: MaidRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(BookingStatusUiState())
    val uiState: StateFlow<BookingStatusUiState> = _uiState.asStateFlow()
    
    init {
        loadBooking()
    }
    
    private fun loadBooking() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            if (bookingId.isBlank()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Missing booking information."
                    )
                }
                return@launch
            }
            
            val result = bookingRepository.getBookingById(bookingId)
            if (result.isSuccess) {
                val booking = result.getOrNull()!!
                val serviceDetails = mapBookingDetails(booking)
                val maidInfo = mapMaidInfo(booking)
                val recurringDetails = if (booking.isRecurring) mapRecurringDetails(booking) else null
                
                _uiState.update { current ->
                    current.copy(
                        isLoading = false,
                        bookingDetails = serviceDetails,
                        currentStatus = booking.status,
                        statusMessage = statusMessageFor(booking.status),
                        maidInfo = maidInfo,
                        isRecurring = booking.isRecurring,
                        recurringDetails = recurringDetails
                    )
                }
                
                if (booking.maidId.isNotBlank()) {
                    loadMaidDetails(booking.maidId)
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message
                            ?: "Unable to load booking."
                    )
                }
            }
        }
    }
    
    private fun mapBookingDetails(booking: Booking): BookingServiceDetails {
        val time = BookingDateUtils.getBookingTime(booking)
        return BookingServiceDetails(
            service = booking.bookingType.displayName(),
            date = BookingDateUtils.formatDate(booking.nextScheduledDate),
            time = if (time.isNotBlank()) time else "Not set"
        )
    }
    
    private fun mapMaidInfo(booking: Booking): MaidInfo {
        val description = if (booking.specialInstructions.isBlank()) {
            "Feel free to share any special instructions with your maid."
        } else {
            booking.specialInstructions
        }
        
        return MaidInfo(
            name = booking.maidFullName.ifBlank { "Assigned Maid" },
            rating = 0f,
            profileImageUrl = booking.maidProfileImageUrl,
            arrivingInMinutes = 15,
            description = description,
            phoneNumber = booking.maidPhoneNumber
        )
    }
    
    private fun mapRecurringDetails(booking: Booking): RecurringBookingDetails {
        return RecurringBookingDetails(
            frequency = formatRecurringType(booking.recurringType),
            day = booking.preferredDay.ifBlank { "Every week" },
            time = booking.preferredHour.ifBlank { BookingDateUtils.getBookingTime(booking) }
        )
    }
    
    private fun loadMaidDetails(maidId: String) {
        viewModelScope.launch {
            val result = maidRepository.getMaidById(maidId)
            if (result.isSuccess) {
                val maid = result.getOrNull()!!
                _uiState.update { state ->
                    state.copy(
                        maidInfo = state.maidInfo.copy(
                            name = maid.fullName.ifBlank { state.maidInfo.name },
                            rating = maid.averageRating.toFloat(),
                            profileImageUrl = maid.profileImageUrl.ifBlank { state.maidInfo.profileImageUrl },
                            description = maid.bio.ifBlank { state.maidInfo.description },
                            phoneNumber = maid.phoneNumber.ifBlank { state.maidInfo.phoneNumber }
                        )
                    )
                }
            }
        }
    }
    
    fun onCancelOrderClick() {
        _uiState.update { it.copy(showCancelDialog = true) }
    }

    fun onDismissCancelDialog() {
        _uiState.update { it.copy(showCancelDialog = false) }
    }

    fun onConfirmCancelOrder() {
        viewModelScope.launch {
            _uiState.update { it.copy(showCancelDialog = false, isLoading = true) }
            val result = bookingRepository.cancelBooking(bookingId)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentStatus = BookingStatus.CANCELLED,
                        statusMessage = statusMessageFor(BookingStatus.CANCELLED)
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message
                            ?: "Failed to cancel booking."
                    )
                }
            }
        }
    }
    
    fun onContactMaid() {
        println("☎️ BookingStatusViewModel: Contact maid tapped for bookingId=$bookingId")
        val phoneNumber = _uiState.value.maidInfo.phoneNumber
        if (phoneNumber.isNotBlank()) {
            _uiState.update { it.copy(shouldOpenDialer = phoneNumber) }
        } else {
            println("⚠️ BookingStatusViewModel: No phone number available for maid")
        }
    }

    fun onDialerHandled() {
        _uiState.update { it.copy(shouldOpenDialer = null) }
    }
    
    fun onSOSClicked() {
        println("🚨 BookingStatusViewModel: SOS tapped for bookingId=$bookingId")
        _uiState.update { it.copy(shouldNavigateToSOS = true) }
    }

    fun onSOSNavigationHandled() {
        _uiState.update { it.copy(shouldNavigateToSOS = false) }
    }
    
    fun onRateMaid() {
        println("⭐ BookingStatusViewModel: Rate maid tapped for bookingId=$bookingId")
    }

    // DEBUG: Change booking status (to be removed later)
    fun changeBookingStatus(newStatus: BookingStatus) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Use completeBooking function for COMPLETED status
            val result = if (newStatus == BookingStatus.COMPLETED) {
                bookingRepository.completeBooking(bookingId)
            } else {
                bookingRepository.updateBookingStatus(bookingId, newStatus)
            }

            if (result.isSuccess) {
                // Reload booking to get updated data (especially for recurring bookings)
                loadBooking()
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message
                            ?: "Failed to update status."
                    )
                }
            }
        }
    }
    
    private fun statusMessageFor(status: BookingStatus): String {
        return when (status) {
            BookingStatus.PENDING -> "Your booking request is pending."
            BookingStatus.CONFIRMED -> "Your booking is confirmed."
            BookingStatus.ON_THE_WAY -> "Your maid is on the way."
            BookingStatus.IN_PROGRESS -> "Service in progress."
            BookingStatus.COMPLETED -> "Service completed successfully."
            BookingStatus.CANCELLED -> "This booking has been cancelled."
        }
    }
    
    private fun formatRecurringType(type: RecurringType?): String {
        return when (type) {
            RecurringType.WEEKLY -> "Weekly"
            RecurringType.BIWEEKLY -> "Every 2 weeks"
            RecurringType.MONTHLY -> "Monthly"
            else -> "Recurring"
        }
    }
}
