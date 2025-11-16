package com.example.maidy.feature.booking

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class BookingStatus {
    CONFIRMED,
    ON_THE_WAY,
    IN_PROGRESS,
    COMPLETED
}

data class MaidInfo(
    val name: String = "Jane Doe",
    val rating: Float = 4.9f,
    val profileImageUrl: String = "", // Placeholder for later
    val arrivingInMinutes: Int = 15,
    val description: String = "Experienced professional cleaner with 5+ years expertise in residential cleaning."
)

data class BookingDetails(
    val service: String = "Standard Cleaning",
    val date: String = "Nov 18, 2023",
    val time: String = "10:00 AM"
)

data class BookingStatusUiState(
    val bookingDetails: BookingDetails = BookingDetails(),
    val currentStatus: BookingStatus = BookingStatus.ON_THE_WAY,
    val maidInfo: MaidInfo = MaidInfo(),
    val statusMessage: String = "Your maid is on the way."
)

class BookingStatusViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(BookingStatusUiState())
    val uiState: StateFlow<BookingStatusUiState> = _uiState.asStateFlow()
    
    fun onCancelOrder() {
        // TODO: Implement cancel order logic
        // This will connect to API later
    }
    
    fun onContactMaid() {
        // TODO: Implement contact maid logic
        // This will connect to phone/messaging later
    }
    
    fun onSOSClicked() {
        // TODO: Implement SOS emergency logic
        // This will connect to emergency service later
    }
    
    fun onRateMaid() {
        // TODO: Implement rating logic
        // This will navigate to rating screen later
    }
    
    fun updateStatus(newStatus: BookingStatus) {
        val newMessage = when (newStatus) {
            BookingStatus.CONFIRMED -> "Your booking is confirmed."
            BookingStatus.ON_THE_WAY -> "Your maid is on the way."
            BookingStatus.IN_PROGRESS -> "Service in progress."
            BookingStatus.COMPLETED -> "Service completed successfully."
        }
        
        _uiState.value = _uiState.value.copy(
            currentStatus = newStatus,
            statusMessage = newMessage
        )
    }
}

