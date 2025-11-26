package com.example.maidy.feature_maid.booking_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maidy.core.data.BookingRepository
import com.example.maidy.core.model.BookingStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Maid Booking Details Screen
 * Handles booking status updates and data loading
 */
class MaidBookingDetailsViewModel(
    private val bookingId: String,
    private val bookingRepository: BookingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MaidBookingDetailsUiState())
    val uiState: StateFlow<MaidBookingDetailsUiState> = _uiState.asStateFlow()

    init {
        loadBooking()
    }

    fun onEvent(event: MaidBookingDetailsEvent) {
        when (event) {
            is MaidBookingDetailsEvent.LoadBooking -> loadBooking()
            is MaidBookingDetailsEvent.AcceptBooking -> acceptBooking()
            is MaidBookingDetailsEvent.RejectBooking -> rejectBooking()
            is MaidBookingDetailsEvent.CancelBooking -> cancelBooking()
            is MaidBookingDetailsEvent.MarkOnTheWay -> markOnTheWay()
            is MaidBookingDetailsEvent.MarkInProgress -> markInProgress()
            is MaidBookingDetailsEvent.MarkCompleted -> markCompleted()
            is MaidBookingDetailsEvent.ClearMessages -> clearMessages()
        }
    }

    /**
     * Load booking details
     */
    private fun loadBooking() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = bookingRepository.getBookingById(bookingId)
            result.onSuccess { booking ->
                _uiState.value = _uiState.value.copy(
                    booking = booking,
                    isLoading = false
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load booking: ${error.message}"
                )
            }
        }
    }

    /**
     * Accept a pending booking
     */
    private fun acceptBooking() {
        updateBookingStatus(
            newStatus = BookingStatus.CONFIRMED,
            successMessage = "Booking accepted successfully"
        )
    }

    /**
     * Reject a pending booking
     */
    private fun rejectBooking() {
        updateBookingStatus(
            newStatus = BookingStatus.CANCELLED,
            successMessage = "Booking rejected"
        )
    }

    /**
     * Cancel a confirmed booking
     */
    private fun cancelBooking() {
        updateBookingStatus(
            newStatus = BookingStatus.CANCELLED,
            successMessage = "Booking cancelled"
        )
    }

    /**
     * Mark booking as on the way
     */
    private fun markOnTheWay() {
        updateBookingStatus(
            newStatus = BookingStatus.ON_THE_WAY,
            successMessage = "Status updated to On the Way"
        )
    }

    /**
     * Mark booking as in progress
     */
    private fun markInProgress() {
        updateBookingStatus(
            newStatus = BookingStatus.IN_PROGRESS,
            successMessage = "Job started"
        )
    }

    /**
     * Mark booking as completed
     */
    private fun markCompleted() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdatingStatus = true)

            val result = bookingRepository.completeBooking(bookingId)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isUpdatingStatus = false,
                    successMessage = "Job completed successfully"
                )
                // Reload booking to get updated data
                loadBooking()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isUpdatingStatus = false,
                    errorMessage = "Failed to complete booking: ${error.message}"
                )
            }
        }
    }

    /**
     * Update booking status
     */
    private fun updateBookingStatus(newStatus: BookingStatus, successMessage: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdatingStatus = true)

            val result = bookingRepository.updateBookingStatus(bookingId, newStatus)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isUpdatingStatus = false,
                    successMessage = successMessage
                )
                // Reload booking to get updated data
                loadBooking()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isUpdatingStatus = false,
                    errorMessage = "Failed to update status: ${error.message}"
                )
            }
        }
    }

    /**
     * Clear success and error messages
     */
    private fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }
}
