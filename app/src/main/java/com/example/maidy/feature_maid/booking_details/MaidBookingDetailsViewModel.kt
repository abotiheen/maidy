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
            is MaidBookingDetailsEvent.ShowRejectDialog -> showRejectDialog()
            is MaidBookingDetailsEvent.DismissRejectDialog -> dismissRejectDialog()
            is MaidBookingDetailsEvent.ConfirmRejectBooking -> rejectBooking()
            is MaidBookingDetailsEvent.ShowCancelDialog -> showCancelDialog()
            is MaidBookingDetailsEvent.DismissCancelDialog -> dismissCancelDialog()
            is MaidBookingDetailsEvent.ConfirmCancelBooking -> cancelBooking()
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
            successMessage = "Booking accepted successfully",
            isPrimaryAction = true
        )
    }

    /**
     * Show reject confirmation dialog
     */
    private fun showRejectDialog() {
        _uiState.value = _uiState.value.copy(showRejectDialog = true)
    }

    /**
     * Dismiss reject confirmation dialog
     */
    private fun dismissRejectDialog() {
        _uiState.value = _uiState.value.copy(showRejectDialog = false)
    }

    /**
     * Reject a pending booking (after confirmation)
     */
    private fun rejectBooking() {
        _uiState.value = _uiState.value.copy(showRejectDialog = false)
        updateBookingStatus(
            newStatus = BookingStatus.CANCELLED,
            successMessage = "Booking rejected",
            isPrimaryAction = false
        )
    }

    /**
     * Show cancel confirmation dialog
     */
    private fun showCancelDialog() {
        _uiState.value = _uiState.value.copy(showCancelDialog = true)
    }

    /**
     * Dismiss cancel confirmation dialog
     */
    private fun dismissCancelDialog() {
        _uiState.value = _uiState.value.copy(showCancelDialog = false)
    }

    /**
     * Cancel a confirmed booking (after confirmation)
     */
    private fun cancelBooking() {
        _uiState.value = _uiState.value.copy(showCancelDialog = false)
        updateBookingStatus(
            newStatus = BookingStatus.CANCELLED,
            successMessage = "Booking cancelled",
            isPrimaryAction = false
        )
    }

    /**
     * Mark booking as on the way
     */
    private fun markOnTheWay() {
        updateBookingStatus(
            newStatus = BookingStatus.ON_THE_WAY,
            successMessage = "Status updated to On the Way",
            isPrimaryAction = true
        )
    }

    /**
     * Mark booking as in progress
     */
    private fun markInProgress() {
        updateBookingStatus(
            newStatus = BookingStatus.IN_PROGRESS,
            successMessage = "Job started",
            isPrimaryAction = true
        )
    }

    /**
     * Mark booking as completed
     */
    private fun markCompleted() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdatingPrimary = true)

            val result = bookingRepository.completeBooking(bookingId)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isUpdatingPrimary = false,
                    successMessage = "Job completed successfully"
                )
                // Reload booking to get updated data
                loadBooking()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isUpdatingPrimary = false,
                    errorMessage = "Failed to complete booking: ${error.message}"
                )
            }
        }
    }

    /**
     * Update booking status
     */
    private fun updateBookingStatus(
        newStatus: BookingStatus,
        successMessage: String,
        isPrimaryAction: Boolean
    ) {
        viewModelScope.launch {
            if (isPrimaryAction) {
                _uiState.value = _uiState.value.copy(isUpdatingPrimary = true)
            } else {
                _uiState.value = _uiState.value.copy(isUpdatingSecondary = true)
            }

            val result = bookingRepository.updateBookingStatus(bookingId, newStatus)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isUpdatingPrimary = false,
                    isUpdatingSecondary = false,
                    successMessage = successMessage
                )
                // Reload booking to get updated data
                loadBooking()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isUpdatingPrimary = false,
                    isUpdatingSecondary = false,
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
