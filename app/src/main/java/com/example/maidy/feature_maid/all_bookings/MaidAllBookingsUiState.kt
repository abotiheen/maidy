package com.example.maidy.feature_maid.all_bookings

import com.example.maidy.core.model.Booking

data class MaidBookingFilterOption(
    val id: String,
    val label: String,
    val isSelected: Boolean = false
)

data class MaidAllBookingsUiState(
    val allBookings: List<Booking> = emptyList(),
    val filteredBookings: List<Booking> = emptyList(),
    val statusFilters: List<MaidBookingFilterOption> = listOf(
        MaidBookingFilterOption("all", "All", isSelected = true),
        MaidBookingFilterOption("pending", "Pending", isSelected = false),
        MaidBookingFilterOption("confirmed", "Confirmed", isSelected = false),
        MaidBookingFilterOption("on_the_way", "On The Way", isSelected = false),
        MaidBookingFilterOption("in_progress", "In Progress", isSelected = false),
        MaidBookingFilterOption("completed", "Completed", isSelected = false),
        MaidBookingFilterOption("cancelled", "Cancelled", isSelected = false)
    ),
    val recurringFilter: MaidBookingFilterOption = MaidBookingFilterOption(
        "recurring",
        "Recurring Only",
        isSelected = false
    ),
    val startDateMillis: Long? = null,
    val endDateMillis: Long? = null,
    val showDateRangePicker: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class MaidAllBookingsEvent {
    data class OnStatusFilterClick(val filterId: String) : MaidAllBookingsEvent()
    data object OnRecurringFilterToggle : MaidAllBookingsEvent()
    data class OnBookingClick(val bookingId: String) : MaidAllBookingsEvent()
    data object OnDateFilterClick : MaidAllBookingsEvent()
    data class OnDateRangeSelected(val startMillis: Long, val endMillis: Long) :
        MaidAllBookingsEvent()

    data object OnClearDateFilter : MaidAllBookingsEvent()
    data object OnDismissDatePicker : MaidAllBookingsEvent()
}
