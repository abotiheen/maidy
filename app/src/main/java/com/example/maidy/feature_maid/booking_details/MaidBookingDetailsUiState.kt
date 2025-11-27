package com.example.maidy.feature_maid.booking_details

import com.example.maidy.core.model.Booking

/**
 * UI State for Maid Booking Details Screen
 */
data class MaidBookingDetailsUiState(
    val booking: Booking? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isUpdatingPrimary: Boolean = false,  // Loading for primary action button
    val isUpdatingSecondary: Boolean = false, // Loading for secondary action button
    val successMessage: String? = null,
    val showCancelDialog: Boolean = false,    // Show cancel confirmation dialog
    val showRejectDialog: Boolean = false     // Show reject confirmation dialog
)

/**
 * Events for Maid Booking Details Screen
 */
sealed class MaidBookingDetailsEvent {
    data object LoadBooking : MaidBookingDetailsEvent()
    data object AcceptBooking : MaidBookingDetailsEvent()
    data object ShowRejectDialog : MaidBookingDetailsEvent()
    data object DismissRejectDialog : MaidBookingDetailsEvent()
    data object ConfirmRejectBooking : MaidBookingDetailsEvent()
    data object ShowCancelDialog : MaidBookingDetailsEvent()
    data object DismissCancelDialog : MaidBookingDetailsEvent()
    data object ConfirmCancelBooking : MaidBookingDetailsEvent()
    data object MarkOnTheWay : MaidBookingDetailsEvent()
    data object MarkInProgress : MaidBookingDetailsEvent()
    data object MarkCompleted : MaidBookingDetailsEvent()
    data object ClearMessages : MaidBookingDetailsEvent()
}
