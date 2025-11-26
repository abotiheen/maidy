package com.example.maidy.feature_maid.booking_details

import com.example.maidy.core.model.Booking

/**
 * UI State for Maid Booking Details Screen
 */
data class MaidBookingDetailsUiState(
    val booking: Booking? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isUpdatingStatus: Boolean = false,
    val successMessage: String? = null
)

/**
 * Events for Maid Booking Details Screen
 */
sealed class MaidBookingDetailsEvent {
    data object LoadBooking : MaidBookingDetailsEvent()
    data object AcceptBooking : MaidBookingDetailsEvent()
    data object RejectBooking : MaidBookingDetailsEvent()
    data object CancelBooking : MaidBookingDetailsEvent()
    data object MarkOnTheWay : MaidBookingDetailsEvent()
    data object MarkInProgress : MaidBookingDetailsEvent()
    data object MarkCompleted : MaidBookingDetailsEvent()
    data object ClearMessages : MaidBookingDetailsEvent()
}
